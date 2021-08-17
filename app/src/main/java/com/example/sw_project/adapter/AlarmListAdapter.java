package com.example.sw_project.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.AlarmInfo;
import com.example.sw_project.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder>{

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

    public AlarmListAdapter(ArrayList<AlarmInfo> dataSet) {
        mDataset = dataSet;
    }

    @Override
    public AlarmListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        CardView view = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alarm, viewGroup, false);
        AlarmListAdapter.ViewHolder viewHolder = new AlarmListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmListAdapter.ViewHolder viewHolder, int position) {

        TextView alarmKindText = viewHolder.cardView.findViewById(R.id.alarmKindText);
        TextView alarmMessageText = viewHolder.cardView.findViewById(R.id.alarmMessageText);
        TextView alarmTimeText = viewHolder.cardView.findViewById(R.id.alarmTimeText);
        LinearLayout layout = viewHolder.cardView.findViewById(R.id.alarmCardLayout);

        //카드뷰 색상 초기화(다른 카드뷰 색상 영향 받지 않기 위해)
        layout.setBackgroundColor(Color.parseColor("#FFFFFF"));

        AlarmInfo alarmTextDeco = mDataset.get(position);

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

        if(mDataset.get(position).getIsRead()){
            // #7CA6A1A1 로 배경 변경
            layout.setBackgroundColor(Color.parseColor("#7CA6A1A1"));
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
