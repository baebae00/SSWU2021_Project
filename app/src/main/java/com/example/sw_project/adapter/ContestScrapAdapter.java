package com.example.sw_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sw_project.Activity.ContestDetailActivity;
import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ContestScrapAdapter extends RecyclerView.Adapter<ContestScrapAdapter.ViewHolder>{

    private ArrayList<ContestInfo> mDataset;
    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }

    public ContestScrapAdapter(ArrayList<ContestInfo> dataSet, Activity activity) {
        this.mDataset = dataSet;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        CardView view = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contest_scrap, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ContestDetailActivity.class);
                intent.putExtra("contestDetail", mDataset.get(viewHolder.getAdapterPosition()));
                activity.startActivity(intent);

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Log.d("tag1" , "onBind");
        TextView contestNameText = viewHolder.cardView.findViewById(R.id.contestListTextView);
        TextView contestEndLineText = viewHolder.cardView.findViewById(R.id.contestEndLineText);
        ImageView img_chart = viewHolder.cardView.findViewById(R.id.contestImageView);

        int[] storage = new int[3];
        String startOrEndDateText;
        StringTokenizer stk;
        if(mDataset.get(position).getInOut().equals("교내")) {

            stk = new StringTokenizer(mDataset.get(position).getStartDate(), ".");
            for (int i = 0; stk.hasMoreTokens(); i++)
                storage[i] = Integer.parseInt(stk.nextToken());
            startOrEndDateText = String.format("%d년 %d월 %d일부터 ~", storage[0], storage[1], storage[2]);

        }else{
            stk = new StringTokenizer(mDataset.get(position).getEndDate(), "-");
            for (int i = 0; stk.hasMoreTokens(); i++)
                storage[i] = Integer.parseInt(stk.nextToken());
            startOrEndDateText = String.format("~ %d년 %d월 %d일까지", storage[0] + 2000, storage[1], storage[2]);
        }

        contestEndLineText.setText(startOrEndDateText);
        contestNameText.setText(mDataset.get(position).getContestName());
        Glide.with(viewHolder.cardView.getContext()).load(mDataset.get(position).getImageUrl()).into(img_chart);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}



