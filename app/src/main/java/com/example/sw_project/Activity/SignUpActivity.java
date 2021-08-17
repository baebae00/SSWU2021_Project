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

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.canUseIdButton).setOnClickListener(onClickListener);
        findViewById(R.id.emailPushButton).setOnClickListener(onClickListener);

        // 단과대 스크롤
        collegespinner = (Spinner) findViewById(R.id.collegeSpinner);
        collegeadapter = ArrayAdapter.createFromResource(this, R.array.college, android.R.layout.simple_spinner_dropdown_item);
        collegespinner.setAdapter(collegeadapter);

        // 단과대별 학과 필터링
        collegespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (collegeadapter.getItem(position).equals("인문대학")) {
                    liberalmajorspinner = (Spinner) findViewById(R.id.majorSpinner);
                    liberalmajoradapter = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.liberalmajor, android.R.layout.simple_spinner_dropdown_item);
                    liberalmajorspinner.setAdapter(liberalmajoradapter);
                } else if (collegeadapter.getItem(position).equals("사회대학")) {
                    socialmajorspinner = (Spinner) findViewById(R.id.majorSpinner);
                    socialmajoradapter = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.socialmajor, android.R.layout.simple_spinner_dropdown_item);
                    socialmajorspinner.setAdapter(socialmajoradapter);
                } else if (collegeadapter.getItem(position).equals("공과대학")) {
                    engineeringmajorspinner = (Spinner) findViewById(R.id.majorSpinner);
                    engineeringmajoradapter = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.engineeringmajor, android.R.layout.simple_spinner_dropdown_item);
                    engineeringmajorspinner.setAdapter(engineeringmajoradapter);
                } else if (collegeadapter.getItem(position).equals("예체능대학")) {
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
                    correctEditText.setText("6자리 이상으로 설정해주세요.");
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
                        correctEditText.setText("일치합니다.");
                    } else {
                        // incorrect
                        correctEditText.setText("일치하지 않습니다.");
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
                    canEmailUseText.setText("인증되었습니다.");
                } else {
                    // incorrect
//                    canEmailUseText.setText("인증번호가 일치하지 않습니다.");
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

            dialog = builder.setMessage("학교 계정 이메일로 작성해주세요.\n\n" +
                    "@sungshin.ac.kr")
                    .setNegativeButton("확인", null)
                    .create();
            dialog.show();
        }

    }

    private void emailCertification(String email){
        //초기화
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
                                canEmailUseText.setText("이미 인증이 완료된 이메일입니다.");
                                return;
                            }
                            if(canEmailUseText.getText().toString().equals("")){
                                //인증번호 문자/순자 5자리 생성
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

                                startToast("학교 계정으로 인증 메일이 전송되었습니다.");
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
                                canUseIdText.setText("이미 존재하는 아이디입니다.");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        if(canUseIdText.getText().toString().equals("")){
            canUseIdText.setText("사용 가능한 아이디입니다.");
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
                || !(canEmailUseText.getText().toString().equals("인증되었습니다."))
                || !(correctEditText.getText().toString().equals("일치합니다."))
                || ! canUseIdText.getText().toString().equals("사용 가능한 아이디입니다.")){

            builder = new AlertDialog.Builder(SignUpActivity.this);
            dialog = builder.setMessage("빈 칸이 존재합니다.")
                    .setNegativeButton("확인", null)
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
                        startToast("회원정보 등록을 성공하였습니다");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록을 실패하였습니다");
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

                //{a,b} a: 공모전 참여횟수, b: 학생
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
