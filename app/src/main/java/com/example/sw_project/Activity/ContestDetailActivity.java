package com.example.sw_project.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
import com.example.sw_project.ScrapInfo;
import com.example.sw_project.StatisticsInfo;
import com.example.sw_project.StudentInfo;
import com.example.sw_project.adapter.FragmentAdapter;
import com.example.sw_project.fragment.ContestDetailListFragment;
import com.example.sw_project.fragment.ContestStatisticsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;

public class ContestDetailActivity extends AppCompatActivity {

    private ContestInfo contestInfo;
    private TabLayout tabLayout;
    private ViewPager detailViewPager;
    private FragmentAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ScrapInfo alreadyScrap;
    private ContestDetailListFragment fragment1;
    private ContestStatisticsFragment fragment2;
    private Button scrap;
    private Button scrapCancel;
    private String TAG = "ContestDetailActivity";
    private StatisticsInfo statistics;
    private StudentInfo studentInfo;
    private String userDepartment;
    private String userStudentId;
    private String scrapDocumnetId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_detail);

        //????????? ?????????
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();// ????????? ????????????
        contestInfo = (ContestInfo) intent.getSerializableExtra("contestDetail");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        tabLayout = findViewById(R.id.contestDetailTab);
        detailViewPager = findViewById(R.id.contestDetailViewPager);

        findViewById(R.id.moveWebButton).setOnClickListener(onClickListener);
        findViewById(R.id.contestScrapCheckButton).setOnClickListener(onClickListener);
        findViewById(R.id.contestScrapCheckCancelButton).setOnClickListener(onClickListener);

        scrap = findViewById(R.id.contestScrapCheckButton);
        scrapCancel = findViewById(R.id.contestScrapCheckCancelButton);

        //getFragmentManager??? getSupportFragmentManager?????? ??????
        adapter = new FragmentAdapter(getSupportFragmentManager(),1);

        fragment1 = new ContestDetailListFragment();
        fragment2 = new ContestStatisticsFragment();

        adapter.addFragment(fragment1);
        adapter.addFragment(fragment2);

        detailViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(detailViewPager);
        tabLayout.getTabAt(0).setText("?????? ??????");
        tabLayout.getTabAt(1).setText("?????? ??????");

        //contest ?????? Fragment?????? ????????????
        Bundle bundle = new Bundle();
        bundle.putSerializable("contestInfo",contestInfo);
        fragment1.setArguments(bundle);
        fragment2.setArguments(bundle);

        scrapCancel.setVisibility(View.INVISIBLE);
        scrap.setVisibility(View.VISIBLE);

        db.collection("scrapContest")
                .whereEqualTo("contestId", contestInfo.getContestId())
                .whereEqualTo("scrapUserUid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                alreadyScrap = document.toObject(ScrapInfo.class);
                                scrap.setVisibility(View.INVISIBLE);
                                scrapCancel.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() { //?????? ?????????
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.moveWebButton:
                    moveWebPage();
                    break;
                case R.id.contestScrapCheckButton:
                    scrapDbUpload();
                    break;
                case R.id.contestScrapCheckCancelButton:
                    scrapDbDelete();
                    break;
            }
        }
    };


    private void moveWebPage(){
        //?????? ??????
        String contestUrl = contestInfo.getDetailUrl();
        String detail_url;

        //?????? ????????? ?????? melon_detail_url + albumID
        if(contestInfo.getInOut().equals("??????"))
            detail_url = "https://www.sungshin.ac.kr/bbs/";
        else
            detail_url = "https://www.jungle.co.kr/contest";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(detail_url + contestUrl));
        this.startActivities(new Intent[]{intent});
    }

    private void scrapDbUpload(){

        ScrapInfo scrapInfo = new ScrapInfo();
        scrapInfo.setContestId(contestInfo.getContestId());
        scrapInfo.setScrapUserUid(user.getUid());

        db.collection("scrapContest")
                .add(scrapInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                        scrapDocumnetId = documentReference.getId();

                        //contestId ?????????
                        DocumentReference washingtonRef = db.collection("scrapContest").document(documentReference.getId());
                        washingtonRef
                                .update("scrapId", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        scrapNumberIncrease();

                                        // statistcs
                                        getStatisticsData();
                                        startToast("???????????? ????????????????????????.");
                                        scrap.setVisibility(View.INVISIBLE);
                                        scrapCancel.setVisibility(View.VISIBLE);
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

    private void getStatisticsData(){

        //????????? ???????????? ???????????????... ??????...
        DocumentReference docRef = db.collection("statistics").document(contestInfo.getContestId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                         statistics = document.toObject(StatisticsInfo.class);

                        //?????????????????? user ?????? ????????????
                        DocumentReference docRef = db.collection("users").document(user.getUid());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        userDepartment = document.getData().get("department").toString();
                                        userStudentId = document.getData().get("studentId").toString();
                                        statisticsUpdate();
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void statisticsUpdate(){

        HashMap<String, Integer> numDataStorage = statistics.getSchoolNumCount();
        HashMap<String, Integer> majorDataStorage = statistics.getMajorCount();

        int newSchoolId, newMajor;

        if(numDataStorage == null) {
            numDataStorage = new HashMap<>();
            majorDataStorage = new HashMap<>();
        }

        //data ????????? ?????? ???????????? ?????? ????????????
        if(numDataStorage.containsKey(userStudentId))
            newSchoolId = numDataStorage.get(userStudentId) + 1;
        else
            newSchoolId = 1;

        if(majorDataStorage.containsKey(userDepartment))
            newMajor = majorDataStorage.get(userDepartment) + 1;
        else
            newMajor = 1;


        numDataStorage.put(userStudentId,newSchoolId);
        majorDataStorage.put(userDepartment,newMajor);

        DocumentReference washingtonRef = db.collection("statistics").document(contestInfo.getContestId());
        washingtonRef
                .update("schoolNumCount", numDataStorage)
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
        washingtonRef
                .update("majorCount", majorDataStorage)
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

    private void scrapNumberIncrease(){

        DocumentReference sfDocRef = db.collection("statistics").document(contestInfo.getContestId());
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

        // ???????????? ?????? ????????? ?????? ?????? ?????? -> ?????? ?????? try/catch ??? ??????
        try {
            db.collection("scrapContest").document(alreadyScrap.getScrapId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            scrapNumberDecrease();
                            startToast("?????? ????????? ???????????? ?????????????????????.");
                            scrap.setVisibility(View.VISIBLE);
                            scrapCancel.setVisibility(View.INVISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        } catch (NullPointerException e){
            db.collection("scrapContest").document(scrapDocumnetId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            scrapNumberDecrease();
                            startToast("?????? ????????? ???????????? ?????????????????????.");
                            scrap.setVisibility(View.VISIBLE);
                            scrapCancel.setVisibility(View.INVISIBLE);
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

    private void scrapNumberDecrease(){

        final DocumentReference sfDocRef = db.collection("statistics").document(contestInfo.getContestId());
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

    @Override
    protected void onRestart() {

        super.onRestart();

        ContestDetailListFragment mf = (ContestDetailListFragment) adapter.getItem(0);
        mf.getContestPost();

    }

    private void startToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }
}
