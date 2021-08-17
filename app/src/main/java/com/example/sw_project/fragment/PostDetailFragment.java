package com.example.sw_project.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.sw_project.Activity.ContestDetailActivity;
import com.example.sw_project.Activity.WriterPopUpActivity;
import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
import com.example.sw_project.ScrapInfo;
import com.example.sw_project.StudentInfo;
import com.example.sw_project.WriteInfo;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostDetailFragment extends Fragment {

    public View view;
    public TextView titleView,writerView,dateView,regionView,wantnumView,wantdepView,etcView;
    private Activity activity;
    public WriteInfo writeinfo;
    private StudentInfo studentInfo;
    private ScrapInfo alreadyScrap;
    private String newScrapId;
    private FirebaseUser user;
    private String TAG = "ScrapActivity";
    private FirebaseFirestore db;
    private Button scrapButton, scrapCancelButton, finishRecruitButton;
    public CheckBox meet, zoom;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    public Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_view_post, container, false);

        scrapButton = view.findViewById(R.id.scrapButton);
        scrapButton.setOnClickListener(onClickListener);
        scrapCancelButton = view.findViewById(R.id.scrapCancelButton);
        scrapCancelButton.setOnClickListener(onClickListener);
        finishRecruitButton = view.findViewById(R.id.finishRecruitButton);
        finishRecruitButton.setOnClickListener(onClickListener);
        view.findViewById(R.id.moveContestPageButton).setOnClickListener(onClickListener);

        bundle = getArguments();
        writeinfo = (WriteInfo) bundle.getSerializable("writeInfo");

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (writeinfo.getUserUid().equals(user.getUid())) {
            scrapButton.setVisibility(View.INVISIBLE);
            finishRecruitButton.setVisibility(View.VISIBLE);
            // 모집완료 버튼 추가
            if (writeinfo.getIsFinishRecruit())
                finishRecruitButton.setEnabled(false);
        }

        titleView = view.findViewById(R.id.titleView);
        writerView =view.findViewById(R.id.postWriterText);
        dateView = view.findViewById(R.id.dateView);
        regionView = view.findViewById(R.id.regionView);
        wantnumView = view.findViewById(R.id.wantnumView);
        wantdepView = view.findViewById(R.id.wantdepView);
        etcView =view.findViewById(R.id.etcView);
        meet = view.findViewById(R.id.meetCheckBox);
        zoom = view.findViewById(R.id.zoomCheckBox);

        postDataShow();

        db.collection("scrapsPost")
                .whereEqualTo("postId", writeinfo.getPostid())
                .whereEqualTo("scrapUserUid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                alreadyScrap = document.toObject(ScrapInfo.class);
                                scrapButton.setVisibility(View.INVISIBLE);
                                scrapCancelButton.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        writerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = db.collection("users").document(writeinfo.getUserUid());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        studentInfo = documentSnapshot.toObject(StudentInfo.class);
                        Intent intent = new Intent(getContext(), WriterPopUpActivity.class);

                        intent.putExtra("data", studentInfo.getStudentId());
                        intent.putExtra("data2", studentInfo.getCollege());
                        intent.putExtra("data3", studentInfo.getDepartment());
                        intent.putExtra("data4", studentInfo.getContestParticipate());
                        startActivity(intent);
                    }
                });
            }
        });

        return view;
    }

    public void postDataShow(){

        bundle = getArguments();
        writeinfo = (WriteInfo) bundle.getSerializable("writeInfo");

        titleView.setText(writeinfo.getTitle());

        String writerText = String.format("글쓴이: %s",writeinfo.getWriterName());
        writerView.setText(writerText);

//        SimpleDateFormat sdf = new SimpleDateFormat("작성일: yy.MM.dd");
        SimpleDateFormat sdf = new SimpleDateFormat("20yy년 M월 d일 작성");
        String dateText = sdf.format(new Date(writeinfo.getCreatedAt()));
        dateView.setText(dateText);

        regionView.setText(writeinfo.getRegionScope());
        wantnumView.setText(writeinfo.getWantNum());
        wantdepView.setText(writeinfo.getWantDept());
        etcView.setText(writeinfo.getWantEtc());
        meet.setChecked(writeinfo.isMeetCheck());
        zoom.setChecked(writeinfo.isZoomCheck());

        meet.setEnabled(false);
        zoom.setEnabled(false);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scrapButton:
                    scrapDbUpload();
                    break;
                case R.id.scrapCancelButton:
                    scrapDbDelete();
                    break;
                case R.id.finishRecruitButton:
                    builder = new AlertDialog.Builder(getActivity());
                    dialog = builder.setMessage("팀원 모집을 마감합니다")
                            .setNegativeButton("NO", null)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishRecruit();
                                }
                            })
                            .create();
                    dialog.show();
                    break;

                case R.id.moveContestPageButton:
                    moveContestActivity();
                    break;
            }
        }
    };

    private void finishRecruit(){

        DocumentReference washingtonRef = db.collection("posts").document(writeinfo.getPostid());
        washingtonRef
                .update("isFinishRecruit", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        finishRecruitButton.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void moveContestActivity(){

        DocumentReference docRef = db.collection("contests").document(writeinfo.getContestId());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ContestInfo contestInfo = documentSnapshot.toObject(ContestInfo.class);

                Intent intent = new Intent(getContext(), ContestDetailActivity.class);
                intent.putExtra("contestDetail", contestInfo);
                startActivity(intent);
            }
        });
    }

    private void scrapDbUpload(){

        ScrapInfo scrapInfo = new ScrapInfo();
        scrapInfo.setPostId(writeinfo.getPostid());
        scrapInfo.setScrapUserUid(user.getUid());

        db.collection("scrapsPost")
                .add(scrapInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        DocumentReference washingtonRef = db.collection("scrapsPost").document(documentReference.getId());
                        washingtonRef
                                .update("scrapId", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        newScrapId = documentReference.getId();
                                        scrapNumberIncrease();
                                        startToast("해당 게시글이 스크랩되었습니다.");
                                        scrapButton.setVisibility(View.INVISIBLE);
                                        scrapCancelButton.setVisibility(View.VISIBLE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void scrapNumberIncrease(){

        final DocumentReference sfDocRef = db.collection("posts").document(writeinfo.getPostid());
        db.runTransaction(new Transaction.Function<Double>() {
            @Override
            public Double apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                double newScrapNum = snapshot.getDouble("scrapNum") + 1;
                transaction.update(sfDocRef, "scrapNum", (int)newScrapNum);
                return newScrapNum;
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Double>() {
                    @Override
                    public void onSuccess(Double result) {
                        Log.d(TAG, "Transaction success: " + result);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });
    }

    private void scrapDbDelete(){

        //스크랩 후 바로 스크랩 취소하는 경우
        String document;
        try{
            document = alreadyScrap.getScrapId();
        }catch (NullPointerException e){
            document = newScrapId;
        }

        db.collection("scrapsPost").document(document)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        scrapNumberDecrease();
                        startToast("해당 게시글 스크랩 취소되었습니다.");
                        scrapButton.setVisibility(View.VISIBLE);
                        scrapCancelButton.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void scrapNumberDecrease(){

        final DocumentReference sfDocRef = db.collection("posts").document(writeinfo.getPostid());
        db.runTransaction(new Transaction.Function<Double>() {
            @Override
            public Double apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                double newScrapNum = snapshot.getDouble("scrapNum") - 1;
                transaction.update(sfDocRef, "scrapNum", (int)newScrapNum);
                return newScrapNum;
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Double>() {
                    @Override
                    public void onSuccess(Double result) {
                        Log.d(TAG, "Transaction success: " + result);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });
    }

    private void startToast(String msg){
        Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
    }

}
