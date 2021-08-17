package com.example.sw_project.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.AlarmInfo;
import com.example.sw_project.R;
import com.example.sw_project.adapter.AlarmSettingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class AlarmSettingActivity extends AppCompatActivity {

    private CheckBox checkBox;
    private RecyclerView recycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "AlarmSettingActivity";
    private ArrayList<AlarmInfo> alarmList;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        recycleView = findViewById(R.id.alarmSettingRecyclerView);
        checkBox = findViewById(R.id.alarmCheckBox);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        findViewById(R.id.alarmAllSelectButton).setOnClickListener(onClickListener);
        findViewById(R.id.alarmUnSelectButton).setOnClickListener(onClickListener);

        findViewById(R.id.floatingChangeReadButton).setOnClickListener(onClickListener);
        findViewById(R.id.floatingDeleteButton).setOnClickListener(onClickListener);
        findViewById(R.id.floatingPrestateButton).setOnClickListener(onClickListener);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        dataListReload();
    }

    private void dataListReload(){

        alarmList = new ArrayList<>();

        db.collection("alarms")
                .whereEqualTo("destinationUid",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                alarmList.add(document.toObject(AlarmInfo.class));
                            }
                            Collections.sort(alarmList);
                            recycleView.setHasFixedSize(true);
                            layoutManager = new LinearLayoutManager(AlarmSettingActivity.this);
                            recycleView.setLayoutManager(layoutManager);
                            mAdapter = new AlarmSettingAdapter(alarmList);
                            recycleView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() { //버튼 클릭시
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.alarmAllSelectButton:
                    allSelectButtonClick();
                    break;
                case R.id.alarmUnSelectButton:
                    allUnSelectButtonClick();
                    break;
                case R.id.floatingChangeReadButton:
                    builder = new AlertDialog.Builder(AlarmSettingActivity.this);
                    dialog = builder.setMessage("선택한 알림들을 읽음 표시로 전환합니다")
                            .setNegativeButton("CANCEL", null)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    changeToReadButtonClick();
                                }
                            })
                            .create();
                    dialog.show();
                    break;
                case R.id.floatingDeleteButton:
                    builder = new AlertDialog.Builder(AlarmSettingActivity.this);
                    dialog = builder.setMessage("선택한 알림들을 삭제합니다")
                            .setNegativeButton("CANCEL", null)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteButtonClick();
                                }
                            })
                            .create();
                    dialog.show();
                    break;
                case R.id.floatingPrestateButton:
                    finish();
                    break;
            }
        }
    };

    private void allSelectButtonClick(){
        for(AlarmInfo each: alarmList){
            if(!each.getIsCheck()){
                each.setIsCheck(true);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void allUnSelectButtonClick(){
        for(AlarmInfo each: alarmList){
            if(each.getIsCheck()){
                each.setIsCheck(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void changeToReadButtonClick(){
        ArrayList<AlarmInfo> listOfAlarm = ((AlarmSettingAdapter) mAdapter).getAlarmList();
        for(AlarmInfo each : listOfAlarm) {
            if(each.getIsCheck()) {
                String documentId = each.getAlarmDocument();
                DocumentReference washingtonRef = db.collection("alarms").document(documentId);
                washingtonRef
                        .update("isRead", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                //알림 리스트 동기
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        }
        dataListReload();
    }

    private void deleteButtonClick(){
        ArrayList<AlarmInfo> listOfAlarm = ((AlarmSettingAdapter) mAdapter).getAlarmList();
        for(AlarmInfo each : listOfAlarm) {
            if(each.getIsCheck()) {

                String documentId = each.getAlarmDocument();
                db.collection("alarms").document(documentId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        }
        dataListReload();
    }

}