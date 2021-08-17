package com.example.sw_project.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.Activity.PostingActivity;
import com.example.sw_project.ContestInfo;
import com.example.sw_project.ParticipateInfo;
import com.example.sw_project.R;
import com.example.sw_project.StudentInfo;
import com.example.sw_project.WriteInfo;
import com.example.sw_project.adapter.PostListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;

public class ContestDetailListFragment extends Fragment {

    public View view;
    public FirebaseFirestore db;
    private FirebaseUser user;
    private String TAG = "ContestDetailListFragment";
    public ContestInfo contestInfo;
    private StudentInfo info;
    private boolean alreadyPost;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_contest_scrap_postlist, container, false);

        Bundle bundle = getArguments();
        contestInfo = (ContestInfo)bundle.getSerializable("contestInfo");

        view.findViewById(R.id.movePostingButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.alreadyPartiButton).setOnClickListener(onClickListener);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("participate")
                .whereEqualTo("contestId",contestInfo.getContestId())
                .whereEqualTo("userUid",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final ArrayList<WriteInfo> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                view.findViewById(R.id.alreadyPartiButton).setEnabled(false);
                            }
                        } else {
                            Log.w(TAG, "Error => ", task.getException());
                        }
                    }
                });

        getContestPost();

        return view;
    }

    public void getContestPost(){

        //초기화
        alreadyPost = false;
        db.collection("posts")
                .whereEqualTo("contestId",contestInfo.getContestId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            final ArrayList<WriteInfo> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                arrayList.add(document.toObject(WriteInfo.class));
                                if(document.getData().get("userUid").toString().equals(user.getUid()))
                                    alreadyPost = true;
                            }
                            if(arrayList.size() == 0)
                                view.findViewById(R.id.isPostZeroText).setVisibility(View.VISIBLE);
                            else
                                view.findViewById(R.id.isPostZeroText).setVisibility(View.INVISIBLE);

                            RecyclerView recyclerView = view.findViewById(R.id.postlistRecycle);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            RecyclerView.Adapter mAdapter = new PostListAdapter(arrayList, getActivity());
                            recyclerView.setAdapter(mAdapter);

                        }else {
                            Log.w(TAG, "Error => ", task.getException());
                        }
                    }
                });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() { //버튼 클릭시
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.movePostingButton:
                    checkAndMovePosting();
                    break;
                case R.id.alreadyPartiButton:
                    builder = new AlertDialog.Builder(getContext());
                    dialog = builder.setMessage("해당 공모전에 참여하셨습니다.\n\n* 참여 여부는 수정이 불가능합니다")
                            .setNegativeButton("NO", null)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    participateContest();
                                }
                            })
                            .create();
                    dialog.show();
                    break;
            }
        }
    };

    private void checkAndMovePosting(){

        if(alreadyPost) {
            builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("이미 팀원 모집글을 작성했습니다.\n\n새로운 모집글 작성을 원할 경우 기존 모집글을 삭제해주세요.")
                    .setNegativeButton("확인", null)
                    .create();
            dialog.show();
        } else{
            Intent intent = new Intent(getActivity(), PostingActivity.class);
            intent.putExtra("contestDetail", contestInfo);
            getActivity().startActivity(intent);
        }

    }

    private void participateContest(){

        ParticipateInfo participate = new ParticipateInfo();
        participate.setContestId(contestInfo.getContestId());
        participate.setUserUid(user.getUid());

        db.collection("participate")
                .add(participate)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        final DocumentReference sfDocRef = db.collection("users").document(user.getUid());
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                info = snapshot.toObject(StudentInfo.class);

                int newParticipateNum = Integer.parseInt(snapshot.getString("contestParticipate")) + 1;
                transaction.update(sfDocRef, "contestParticipate", String.valueOf(newParticipateNum));
                updateStatisticsDB();
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
                startToast("해당 공모전을 참여했습니다.");
                view.findViewById(R.id.alreadyPartiButton).setEnabled(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    private void updateStatisticsDB(){

        final DocumentReference sfDocRef = db.collection("statistics").document("contestParticipateDocument");
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                HashMap<String, ArrayList> major = (HashMap<String, ArrayList>) snapshot.getData().get("major");
                HashMap<String, ArrayList> schoolNum = (HashMap<String, ArrayList>) snapshot.getData().get("schoolNum");

                Long preCollege = (Long) major.get(info.getCollege()).get(0);
                major.get(info.getCollege()).set(0,preCollege.intValue() + 1);

                Long preNum = (Long) schoolNum.get(info.getStudentId()).get(0);
                schoolNum.get(info.getStudentId()).set(0,preNum.intValue() + 1);

                transaction.update(sfDocRef, "major", major);
                transaction.update(sfDocRef, "schoolNum", schoolNum);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e){
                Log.w(TAG, "Transaction failure.", e);
            }
        });

    }

    private void startToast(String msg){
        Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
    }



}
