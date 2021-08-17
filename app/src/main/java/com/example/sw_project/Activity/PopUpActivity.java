package com.example.sw_project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
import com.example.sw_project.adapter.CalendarListAdapter;

import java.util.ArrayList;

public class PopUpActivity extends Activity {

    private static final String TAG = "PopUpActivity";
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        //데이터 가져오기
        Intent intent = getIntent();
        String dateTitle = intent.getStringExtra("dateTextTitle");
        ArrayList<ContestInfo> dateSameList = (ArrayList<ContestInfo>)intent.getSerializableExtra("list");

        findViewById(R.id.popUpCloseButton).setOnClickListener(onClickListener);
        RecyclerView recycleViewInCalendar = findViewById(R.id.popUpRecycleView);

        TextView contestEndDateCalendarText = findViewById(R.id.popUpDateText);
        contestEndDateCalendarText.setText(dateTitle);

        recycleViewInCalendar.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(PopUpActivity.this);
        recycleViewInCalendar.setLayoutManager(layoutManager);
        mAdapter = new CalendarListAdapter(dateSameList);
        recycleViewInCalendar.setAdapter(mAdapter);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popUpCloseButton:
                    Log.e("click","click");
                    finish();
                    break;
            }

        }
    };

}
