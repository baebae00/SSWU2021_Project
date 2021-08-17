package com.example.sw_project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.example.sw_project.R;

public class WriterPopUpActivity extends Activity {

    private TextView txtText, txtText2, txtText3, txtText4;
    private static final String TAG = "PopUpActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup2);

        txtText = (TextView) findViewById(R.id.txtText);
        txtText2 = (TextView) findViewById(R.id.txtText2);
        txtText3 = (TextView) findViewById(R.id.txtText3);
        txtText4 = (TextView) findViewById(R.id.txtText4);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        String data2 = intent.getStringExtra("data2");
        String data3 = intent.getStringExtra("data3");
        String data4 = intent.getStringExtra("data4");


        txtText.setText("학번 : " + data);
        txtText2.setText("단과대학 : " + data2);
        txtText3.setText("학과 : " + data3);
        txtText4.setText("공모전 참가 횟수 : " + data4);


        findViewById(R.id.popupclosebtn).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popupclosebtn:
                    Log.e("click", "click");
                    finish();
                    break;
            }
        }

    };

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}