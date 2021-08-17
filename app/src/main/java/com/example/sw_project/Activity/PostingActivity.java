package com.example.sw_project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sw_project.AlarmInfo;
import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
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
import com.google.firebase.firestore.Source;

public class PostingActivity extends AppCompatActivity {

    private  static final String TAG = "Posting";
    private FirebaseUser user;
    public FirebaseFirestore db;
    private ContestInfo contestInfo;
    private WriteInfo writeEdit;
    private CheckBox zoomCheck, meetCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        findViewById(R.id.upLoadButton).setOnClickListener(onClickListener);
        findViewById(R.id.editButton).setOnClickListener(onClickListener);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();// 인텐트 받아오기
        contestInfo = (ContestInfo) intent.getSerializableExtra("contestDetail");
        writeEdit = (WriteInfo) intent.getSerializableExtra("writeInfo");

        //checkBox 선택 여부 처리
        zoomCheck = findViewById(R.id.zoom);
        meetCheck = findViewById(R.id.meet);

        try{
            postEditPrepare();
            findViewById(R.id.editButton).setVisibility(View.VISIBLE);
        }catch (NullPointerException e){
            findViewById(R.id.upLoadButton).setVisibility(View.VISIBLE);
        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() { //버튼 클릭시
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.upLoadButton:
                    //게시글 등록 전 등록에 필요한 정보 추출 함
                    postInfoUpdate();
                    break;
                case R.id.editButton:
                    reUpload();
            }

        }
    };

    private void reUpload(){

        final String wantEtc = ((EditText) findViewById(R.id.writeEtc)).getText().toString();    //기타
        final String regionScope = ((EditText) findViewById(R.id.writeRegionText)).getText().toString();   //선호 지역
        final String wantNum = ((EditText)findViewById(R.id.writeWantNum)).getText().toString();  //모집인원
        final String wantDept = ((EditText)findViewById(R.id.writeDeptText)).getText().toString(); //선호학과
        final String title = ((EditText)findViewById(R.id.postTitleText)).getText().toString();

        writeEdit.setRegionScope(regionScope);
        writeEdit.setTitle(title);
        writeEdit.setWantDept(wantDept);
        writeEdit.setWantEtc(wantEtc);
        writeEdit.setWantNum(wantNum);
        writeEdit.setZoomCheck(zoomCheck.isChecked());
        writeEdit.setMeetCheck(meetCheck.isChecked());

        db.collection("posts").document(writeEdit.getPostid())
                .set(writeEdit)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        finish();;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void postEditPrepare(){

        TextView wantEtc = findViewById(R.id.writeEtc);
        wantEtc.setText(writeEdit.getWantEtc());

        TextView regionScope = findViewById(R.id.writeRegionText);
        regionScope.setText(writeEdit.getRegionScope());

        TextView wantNum = findViewById(R.id.writeWantNum);
        wantNum.setText(writeEdit.getWantNum());

        TextView wantDept = findViewById(R.id.writeDeptText);
        wantDept.setText(writeEdit.getWantDept());

        TextView title = findViewById(R.id.postTitleText);
        title.setText(writeEdit.getTitle());

        zoomCheck.setChecked(writeEdit.isZoomCheck());
        meetCheck.setChecked(writeEdit.isMeetCheck());

    }

    private void postInfoUpdate() {

        WriteInfo writeInfo = new WriteInfo();

        final String wantEtc = ((EditText) findViewById(R.id.writeEtc)).getText().toString();    //기타
        final String regionScope = ((EditText) findViewById(R.id.writeRegionText)).getText().toString();   //선호 지역
        final String wantNum = ((EditText)findViewById(R.id.writeWantNum)).getText().toString();  //모집인원
        final String wantDept = ((EditText)findViewById(R.id.writeDeptText)).getText().toString(); //선호학과
        final String title = ((EditText)findViewById(R.id.postTitleText)).getText().toString();

        writeInfo.setCreatedAt(System.currentTimeMillis());
        writeInfo.setRegionScope(regionScope);
        writeInfo.setScrapNum(0);
        writeInfo.setTitle(title);
        writeInfo.setUserUid(user.getUid());
        writeInfo.setWantDept(wantDept);
        writeInfo.setWantEtc(wantEtc);
        writeInfo.setWantNum(wantNum);
        writeInfo.setImgUrl(contestInfo.getImageUrl());
        writeInfo.setContestId(contestInfo.getContestId());
        writeInfo.setFinishRecruit(false);

        writeInfo.setZoomCheck(zoomCheck.isChecked());
        writeInfo.setMeetCheck(meetCheck.isChecked());

        //userName 정보 가져오기
        DocumentReference docRef = db.collection("users").document(user.getUid());
        Source source = Source.CACHE;
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "Cached document data: " + document.getData());
                    String writerName = document.getData().get("userName").toString();
                    writeInfo.setWriterName(writerName);

                    //post 등록
                    if (wantNum.length() > 0 && wantEtc.length() > 0)
                        uploader(writeInfo);
                    else
                        startToast("모집 내용을 입력하시오.");
                }
                else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });

    }

    private void uploader(final WriteInfo writeInfo){
        db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                        //postId 업데이
                        DocumentReference washingtonRef = db.collection("posts").document(documentReference.getId());
                        washingtonRef
                                .update("postid", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        startToast("게시글이 등록되었습니다.");
                                        finish();
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
                        startToast("게시글이 등록되지 않았습니다.");
                    }
                });

    }

    private void startToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }
}


