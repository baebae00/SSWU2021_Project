package com.example.sw_project.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sw_project.MySharedPreferences;
import com.example.sw_project.R;
import com.example.sw_project.StudentInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "LogInActivity";
    private MySharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = new MySharedPreferences(LogInActivity.this);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        findViewById(R.id.logInButton).setOnClickListener(onClickListener);
        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logInButton:
                    Log.e("click","click");
                    LogIn();
                    break;
                case R.id.signUpButton:
                    Log.e("cli","cli");
                    startSignUpActivity();
                    break;

            }
        }
    };

    //임시 계정 생성
    private void create(){
        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setCollege("공과대학");
        studentInfo.setContestParticipate("6");
        studentInfo.setDepartment("컴퓨터공학과");
        studentInfo.setEmail("");
        studentInfo.setId("tt");
        studentInfo.setStudentId("19");
        studentInfo.setUid("");
        studentInfo.setUserName("tt");
    }

    private void LogIn(){

        String email = ((EditText)findViewById(R.id.idText)).getText().toString();
        email += "@proj.com";
        String password = ((EditText)findViewById(R.id.passwdText)).getText().toString();
        if(email.length() > 0 && password.length() > 0) {
            String finalEmail = email;
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
//                                FirebaseUser user = mAuth.getCurrentUser();
                                sharedPreferences.setUserId(finalEmail);
                                sharedPreferences.setUserPw(password);
                                startToast("로그인에 성공하였습니다");
                                startMainActivity();
                            } else {
                                if (task.getException() != null) {
                                    startToast("아이디 또는 비밀번호를 다시 확인해주세요");
                                }

                            }
                        }
                    });
        }else{
            startToast("이메일 또는 비밀번호를 입력해주세요");
        }
    }

    private void startSignUpActivity(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void startMainActivity(){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}