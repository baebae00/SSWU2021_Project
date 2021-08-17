package com.example.sw_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sw_project.Activity.ViewPostActivity;
import com.example.sw_project.R;
import com.example.sw_project.WriteInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.postViewHolder> {

    private ArrayList<WriteInfo> arrayList;
    private Activity activity;
    private static final String TAG="postAdapter";


    public PostListAdapter(ArrayList<WriteInfo> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_list, parent, false);
        final postViewHolder holder = new postViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ViewPostActivity.class);
                intent.putExtra("writeInfo", arrayList.get(holder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.postViewHolder holder, int position) {

        //글자 색 초기화
        holder.stillRecruit.setTextColor(Color.GRAY);

        holder.itemView.setTag(position);
        Glide.with(holder.itemView.getContext()).load(arrayList.get(position).getImgUrl()).into(holder.poster);
        holder.title.setText(arrayList.get(position).getTitle());
        holder.scrapNum.setText(String.valueOf(arrayList.get(position).getScrapNum()));

        // 알림 시간 텍스트 코드
        Long currentTime = System.currentTimeMillis();
        Long alarmTime = arrayList.get(position).getCreatedAt();
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

        holder.date.setText(calcuratedTime + " 작성");

        if(arrayList.get(position).getIsFinishRecruit()) {
            holder.stillRecruit.setText("팀원 모집 완료");
        }
        else {
            holder.stillRecruit.setText("팀원 모집중");
            holder.stillRecruit.setTextColor(Color.BLUE);
        }
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class postViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView title, date, scrapNum, stillRecruit;

        public postViewHolder(@NonNull View itemView) {
            super(itemView);
            this.poster = itemView.findViewById(R.id.poster);
            this.title = itemView.findViewById(R.id.titleView);
            this.date = itemView.findViewById(R.id.postWriterText);
            this.scrapNum = itemView.findViewById(R.id.scrapNumView);
            this.stillRecruit = itemView.findViewById(R.id.stillRecruitText);
        }}
}

