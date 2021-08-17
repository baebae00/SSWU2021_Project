package com.example.sw_project.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.AlarmInfo;
import com.example.sw_project.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlarmSettingAdapter extends RecyclerView.Adapter<AlarmSettingAdapter.ViewHolder>{

    private ArrayList<AlarmInfo> mDataset;
    private final String TAG = "AlarmListAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder(CardView view) {
            super(view);
            // Define click listener for the ViewHolder's View
            cardView = view;
        }
    }

    public AlarmSettingAdapter(ArrayList<AlarmInfo> dataSet) {
        mDataset = dataSet;
    }

    @Override
    public AlarmSettingAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        CardView view = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alarm_setting, viewGroup, false);
        AlarmSettingAdapter.ViewHolder viewHolder = new AlarmSettingAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmSettingAdapter.ViewHolder viewHolder, int position) {

        AlarmInfo alarmTextDeco = mDataset.get(position);

        CheckBox checkBox = viewHolder.cardView.findViewById(R.id.alarmCheckBox);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmTextDeco.setIsCheck(checkBox.isChecked());
            }
        });

        TextView alarmKindText = viewHolder.cardView.findViewById(R.id.alarmKindText);
        TextView alarmMessageText = viewHolder.cardView.findViewById(R.id.alarmMessageText);
        TextView alarmTimeText = viewHolder.cardView.findViewById(R.id.alarmTimeText);
        LinearLayout layout = viewHolder.cardView.findViewById(R.id.alaramSettingCardLayout);

        //카드뷰 색상 초기화(다른 카드뷰 색상 영향 받지 않기 위해)
        layout.setBackgroundColor(Color.parseColor("#FFFFFF"));

        // 알림 시간 텍스트 코드
        Long currentTime = System.currentTimeMillis();
        Long alarmTime = alarmTextDeco.getTimeStamp();
        Long currentDiffer = currentTime - alarmTime;

        String calcuratedTime;

        if(currentDiffer / 1000 < 60){
            //초단위
            calcuratedTime = String.format("%d초 전",currentDiffer / 1000);
        }else if(currentDiffer / (1000 * 60) < 60) {
            //분단위
            calcuratedTime = String.format("%d분 전",currentDiffer / (1000 * 60));
        }else if(currentDiffer / (1000 * 60 * 60) < 24){
            //시단위
            calcuratedTime = String.format("%d시간 전",currentDiffer / (1000 * 60 * 60));
        }else if(currentDiffer / (1000 * 60 * 60 * 24) < 7){
            //일단위
            calcuratedTime = String.format("%d일 전",currentDiffer / (1000 * 60 * 60 * 24));
        }else{
            //이외에는 날짜
            SimpleDateFormat timeFormat = new SimpleDateFormat("M월 d일");
            calcuratedTime = timeFormat.format(new Date(alarmTime));
        }


        switch (alarmTextDeco.getKind()){
            case 0:
                String kind0 = String.format("%s님이 회원님의 게시글에 댓글을 남기셨습니다.",alarmTextDeco.getUserNickname());
                alarmKindText.setText(kind0);
                String message0 = String.format("\"%s\"",alarmTextDeco.getMessage());
                alarmMessageText.setText(message0);
                alarmTimeText.setText(calcuratedTime);
        }

        if(alarmTextDeco.getIsRead())
            layout.setBackgroundColor(Color.parseColor("#7CA6A1A1"));


        //checkbox 설정
        if(alarmTextDeco.getIsCheck())
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<AlarmInfo> getAlarmList(){
        return mDataset;
    }
    

}

