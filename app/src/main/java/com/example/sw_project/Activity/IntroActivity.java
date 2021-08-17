package com.example.sw_project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.sw_project.MySharedPreferences;
import com.example.sw_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends Activity {

    private FirebaseAuth mAuth;
    private MySharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = new MySharedPreferences(IntroActivity.this);

        //자동 로그인
        try {
            sharedPreferences = new MySharedPreferences(IntroActivity.this);
            autoLogInFunc();
        }catch (NullPointerException e){
            moveLogInAfter();
        }catch (IllegalArgumentException e){
            moveLogInAfter();
        }

    }

    private void moveLogInAfter(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent (getApplicationContext(), LogInActivity.class);
                startActivity(intent); //다음화면으로 넘어감
                finish();
            }
        },2000); //2초 뒤에 LogInActivity 실행하도록 함
    }

    private void autoLogInFunc(){

        mAuth.signInWithEmailAndPassword(sharedPreferences.getUserId(),sharedPreferences.getUserPw())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startMainActivity();
                        } else {
                            Intent intent = new Intent (getApplicationContext(), LogInActivity.class);
                            startActivity(intent); //다음화면으로 넘어감
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }

    private void startMainActivity(){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
