package com.example.sw_project.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sw_project.ContestStatisticsInfo;
import com.example.sw_project.MailSend;
import com.example.sw_project.R;
import com.example.sw_project.StudentInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayAdapter collegeadapter;
    private Spinner collegespinner;
    private ArrayAdapter liberalmajoradapter, socialmajoradapter, engineeringmajoradapter, artsmajoradapter;
    private Spinner liberalmajorspinner, socialmajorspinner, engineeringmajorspinner, artsmajorspinner;
    private String certification;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    EditText passwordEditText, passwordCheckEditText, correctEditText, canUseIdText, canEmailUseText, emailCertificationText;
    private String email, userName, college, department, studentId, id, contestParticipate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //????????? ?????????
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.canUseIdButton).setOnClickListener(onClickListener);
        findViewById(R.id.emailPushButton).setOnClickListener(onClickListener);

        // ????????? ?????????
        collegespinner = (Spinner) findViewById(R.id.collegeSpinner);
        collegeadapter = ArrayAdapter.createFromResource(this, R.array.college, android.R.layout.simple_spinner_dropdown_item);
        collegespinner.setAdapter(collegeadapter);

        // ???????????? ?????? ?????????
        collegespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (collegeadapter.getItem(position).equals("????????????")) {
                    liberalmajorspinner = (Spinner) findViewById(R.id.majorSpinner);
                    liberalmajoradapter = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.liberalmajor, android.R.layout.simple_spinner_dropdown_item);
                    liberalmajorspinner.setAdapter(liberalmajoradapter);
                } else if (collegeadapter.getItem(position).equals("????????????")) {
                    socialmajorspinner = (Spinner) findViewById(R.id.majorSpinner);
                    socialmajoradapter = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.socialmajor, android.R.layout.simple_spinner_dropdown_item);
                    socialmajorspinner.setAdapter(socialmajoradapter);
                } else if (collegeadapter.getItem(position).equals("????????????")) {
                    engineeringmajorspinner = (Spinner) findViewById(R.id.majorSpinner);
                    engineeringmajoradapter = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.engineeringmajor, android.R.layout.simple_spinner_dropdown_item);
                    engineeringmajorspinner.setAdapter(engineeringmajoradapter);
                } else if (collegeadapter.getItem(position).equals("???????????????")) {
                    artsmajorspinner = (Spinner) findViewById(R.id.majorSpinner);
                    artsmajoradapter = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.artsmajor, android.R.layout.simple_spinner_dropdown_item);
                    artsmajorspinner.setAdapter(artsmajoradapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        canEmailUseText = (EditText)findViewById(R.id.canEmailUseText);
        emailCertificationText = (EditText)findViewById(R.id.emailCertificationText);
        passwordEditText = (EditText)findViewById(R.id.passwdText);
        passwordCheckEditText = (EditText)findViewById(R.id.passwdCheckText);
        correctEditText = (EditText) findViewById(R.id.correctEditText);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (passwordEditText.getText().toString().length() < 6) {
                    // correct
                    correctEditText.setText("6?????? ???????????? ??????????????????.");
                } else {
                    // incorrect
                    correctEditText.setText("");
                }
            }
        });

        passwordCheckEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (passwordEditText.getText().toString().length() >= 6) {
                    if (passwordEditText.getText().toString().equals(passwordCheckEditText.getText().toString())) {
                        // correct
                        correctEditText.setText("???????????????.");
                    } else {
                        // incorrect
                        correctEditText.setText("???????????? ????????????.");
                    }
                }
            }
        });

        emailCertificationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (emailCertificationText.getText().toString().equals(certification)) {
                    // correct
                    canEmailUseText.setText("?????????????????????.");
                } else {
                    // incorrect
//                    canEmailUseText.setText("??????????????? ???????????? ????????????.");
                }
            }
        });

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.signUpButton:
                    Log.e("click","click");
                    signUp();
                    break;
                case R.id.canUseIdButton:
                    Log.e("id","id");
                    isIdExist();
                    break;
                case R.id.emailPushButton:
                    Log.e("emailPush","emailPush");
                    isSSWUEmail();
                    break;

            }
        }
    };

    private void isSSWUEmail(){
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        StringTokenizer stk = new StringTokenizer(email,"@");
        stk.nextToken();
        if(stk.nextToken().equals("sungshin.ac.kr"))
            emailCertification(email);
        else{
            builder = new AlertDialog.Builder(SignUpActivity.this);

            dialog = builder.setMessage("?????? ?????? ???????????? ??????????????????.\n\n" +
                    "@sungshin.ac.kr")
                    .setNegativeButton("??????", null)
                    .create();
            dialog.show();
        }

    }

    private void emailCertification(String email){
        //?????????
        canEmailUseText.setText("");
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                canEmailUseText.setText("?????? ????????? ????????? ??????????????????.");
                                return;
                            }
                            if(canEmailUseText.getText().toString().equals("")){
                                //???????????? ??????/?????? 5?????? ??????
                                Random random = new Random();
                                StringBuffer sb = new StringBuffer();
                                for(int i = 0; i < 5; i++){
                                    int index = random.nextInt(3);
                                    switch (index){
                                        case 0 :
                                            sb.append((char)(random.nextInt(26)+97));
                                            break;
                                        case 1 :
                                            sb.append((char)(random.nextInt(26)+65));
                                            break;
                                        case 2 :
                                            sb.append(random.nextInt(10));
                                            break;
                                    }
                                }
                                System.out.println(sb);
                                certification = sb.toString();

                                AsyncTask.execute(() -> {
                                    MailSend.mailSend(email, certification);
                                });

                                startToast("?????? ???????????? ?????? ????????? ?????????????????????.");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void isIdExist(){

        canUseIdText = ((EditText)findViewById(R.id.canUseIdText));
        canUseIdText.setText("");
        String willUseIdText = ((EditText)findViewById(R.id.idText)).getText().toString();

        db.collection("users")
                .whereEqualTo("id", willUseIdText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                canUseIdText.setText("?????? ???????????? ??????????????????.");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        if(canUseIdText.getText().toString().equals("")){
            canUseIdText.setText("?????? ????????? ??????????????????.");
        }

    }

    private void signUp(){

        email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        userName = ((EditText)findViewById(R.id.userNameText)).getText().toString();
        Spinner spinner_co = (Spinner)findViewById(R.id.collegeSpinner);
        Spinner spinner_ma = (Spinner)findViewById(R.id.majorSpinner);
        college = spinner_co.getSelectedItem().toString();
        department = spinner_ma.getSelectedItem().toString();
        studentId = ((EditText)findViewById(R.id.studentIdSet)).getText().toString();
        id = ((EditText)findViewById(R.id.idText)).getText().toString();
        contestParticipate = ((EditText)findViewById(R.id.contestCountEditText)).getText().toString();

        if (email.equals("") || userName.equals("") || college.equals("") || department.equals("")
                || studentId.equals("") || id.equals("") || contestParticipate.equals("")
                || !(canEmailUseText.getText().toString().equals("?????????????????????."))
                || !(correctEditText.getText().toString().equals("???????????????."))
                || ! canUseIdText.getText().toString().equals("?????? ????????? ??????????????????.")){

            builder = new AlertDialog.Builder(SignUpActivity.this);
            dialog = builder.setMessage("??? ?????? ???????????????.")
                    .setNegativeButton("??????", null)
                    .create();
            dialog.show();
        }
        else {
            String idToEmail = id + "@proj.com";
            String password = ((EditText) findViewById(R.id.passwdText)).getText().toString();
            mAuth.createUserWithEmailAndPassword(idToEmail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                final String uid = task.getResult().getUser().getUid();
                                profileRegister(uid);
                                moveLogInActivity();

                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });
        }
    }

    private void profileRegister(String uid){
        StudentInfo studentInfo = new StudentInfo(email,userName,college,department,studentId, id, contestParticipate, uid);
        db.collection("users").document(uid).set(studentInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        statisticsAdd();
                        startToast("???????????? ????????? ?????????????????????");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("???????????? ????????? ?????????????????????");
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void statisticsAdd(){

        DocumentReference docRef = db.collection("statistics").document("contestParticipateDocument");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                HashMap<String, ArrayList> major = (HashMap<String, ArrayList>) documentSnapshot.getData().get("major");
                HashMap<String, ArrayList> schoolNum = (HashMap<String, ArrayList>) documentSnapshot.getData().get("schoolNum");

                if(major == null) {
                    major = new HashMap<>();
                    schoolNum = new HashMap<>();
                }

                //{a,b} a: ????????? ????????????, b: ??????
                ArrayList<Integer> newMajor = new ArrayList<>();
                ArrayList<Integer> newSchool = new ArrayList<>();
                Long a, b;

                if(major.containsKey(college)) {
                    a = (Long) major.get(college).get(0);
                    b = (Long) major.get(college).get(1);

                    newMajor.add(0, a.intValue() + Integer.parseInt(contestParticipate));
                    newMajor.add(1, b.intValue() + 1);
                }
                else {
                    newMajor.add(0, Integer.parseInt(contestParticipate));
                    newMajor.add(1, 1);
                }

                if(schoolNum.containsKey(studentId)) {
                    a = (Long) schoolNum.get(studentId).get(0);
                    b = (Long) schoolNum.get(studentId).get(1);

                    newSchool.add(0, a.intValue() + Integer.parseInt(contestParticipate));
                    newSchool.add(1, b.intValue() + 1);
                }
                else {
                    newSchool.add(0, Integer.parseInt(contestParticipate));
                    newSchool.add(1, 1);
                }

                major.put(college, newMajor);
                schoolNum.put(studentId,newSchool);

                ContestStatisticsInfo newClass = new ContestStatisticsInfo();
                newClass.setMajor(major);
                newClass.setSchoolNum(schoolNum);

                db.collection("statistics").document("contestParticipateDocument").set(newClass);
            }
        });
    }

    private void moveLogInActivity(){
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
