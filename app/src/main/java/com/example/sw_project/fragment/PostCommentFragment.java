package com.example.sw_project.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.AlarmInfo;
import com.example.sw_project.CommentInfo;
import com.example.sw_project.R;
import com.example.sw_project.WriteInfo;
import com.example.sw_project.adapter.CommentListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostCommentFragment extends Fragment {

    public View view;
    private WriteInfo writeinfo;
    private CommentInfo commentInfo;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String TAG = "PostCommentFragment";

    private EditText addcomment;
    private TextView post;
    private String postid;

    private RecyclerView recyclerView;
    private CommentListAdapter commentAdapter;
    private LinearLayoutManager llm;
    private ArrayList<CommentInfo> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_viewpost_comment, container, false);

        Bundle bundle = getArguments();
        writeinfo = (WriteInfo) bundle.getSerializable("writeInfo");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //COMMENTVIEW
        recyclerView = view.findViewById(R.id.commentlistView);
        postid = writeinfo.getPostid(); //addcomment에 사용
        addcomment = view.findViewById(R.id.add_comment);
        post = view.findViewById(R.id.post); //댓글등록 btn

        readComments();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "댓글을 입력하시오", Toast.LENGTH_SHORT).show();
                } else {
                    commentInfoUpdate();
                }
            }
        });

        readComments();

        return view;
    }

    @Override
    public void onStart() { //댓글 실시간
        super.onStart();

        db.collection("comments")
                .whereEqualTo("postid", writeinfo.getPostid())
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "RealTimeLoadText Error=" + error);
                            return;
                        }
                        if (value != null) {
                            for (DocumentChange doc : value.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Log.e(TAG, "addSnapshotListner ADDED");
                                    readComments();
                                } else if (doc.getType() == DocumentChange.Type.REMOVED) {
                                    Log.e(TAG, "addSnapshotListner REMOVED");
                                    readComments();
                                }
                            }
                            if (commentAdapter != null) {
                                commentAdapter.notifyDataSetChanged();
                                Log.e(TAG, "commentAdapter not null");
                            }
                        }
                    }
                });
    }

    private void commentInfoUpdate() {

        commentInfo = new CommentInfo();

        final String addcomment = ((EditText) view.findViewById(R.id.add_comment)).getText().toString();
        commentInfo.setContents(addcomment);
        commentInfo.setPostid(postid);
        commentInfo.setCreatedAt(System.currentTimeMillis());
        commentInfo.setUserUid(user.getUid());

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName = documentSnapshot.getData().get("userName").toString();
                commentInfo.setCommentwriter(userName);
                addcomment(commentInfo);
            }
        });

    }

    private void readComments() {

        db.collection("comments")
                .whereEqualTo("postid", writeinfo.getPostid())
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) { // 데이터를 가져오는 작업이 잘 동작했을 떄
                            final ArrayList<CommentInfo> arrayList = new ArrayList<>(); //리스트 초기화
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                arrayList.add(document.toObject(CommentInfo.class));
                            }
                            recyclerView.setHasFixedSize(true);
                            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(llm);
                            commentAdapter = new CommentListAdapter(arrayList, getActivity());
                            recyclerView.setAdapter(commentAdapter);

                        } else {
                            Log.w(TAG, "Error => ", task.getException());
                        }
                    }

                });

    }

    private void addcomment(final CommentInfo commentInfo) { //댓글 입력

        db.collection("comments")
                .add(commentInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        DocumentReference washingtonRef = db.collection("comments").document(documentReference.getId());
                        washingtonRef
                                .update("commentid", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        commentInfo.setCommentid(documentReference.getId());
                                        Log.d(TAG, "DocumentSnapshot successfully updated!"+commentInfo.getCommentid());

                                        if(!commentInfo.getUserUid().equals(writeinfo.getUserUid()))
                                            postingAlarm(commentInfo);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                        addcomment.setText(""); //등록후 댓글창

                    }
                });

    }

    private void postingAlarm(final CommentInfo commentInfo){

        AlarmInfo alarmInfo = new AlarmInfo();

        alarmInfo.setDestinationUid(writeinfo.getUserUid());
        alarmInfo.setUid(user.getUid());
        alarmInfo.setUserNickname(commentInfo.getCommentwriter());
        alarmInfo.setKind(0);
        alarmInfo.setTimeStamp(System.currentTimeMillis());
        alarmInfo.setIsRead(false);
        alarmInfo.setPostId(writeinfo.getPostid());
        alarmInfo.setMessage(commentInfo.getContents());

        db.collection("alarms").add(alarmInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                        //postId 업데이
                        DocumentReference washingtonRef = db.collection("alarms").document(documentReference.getId());
                        washingtonRef
                                .update("alarmDocument", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
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

}

