package com.example.sw_project.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;

import java.util.ArrayList;

public class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.ViewHolder> {

    private ArrayList<ContestInfo> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder(CardView view) {
            super(view);
            // Define click listener for the ViewHolder's View
            cardView = view;
        }
    }

    public CalendarListAdapter(ArrayList<ContestInfo> dataSet) {
        mDataset = dataSet;
    }

    @Override
    public CalendarListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        CardView view = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_calendar_list, viewGroup, false);
        CalendarListAdapter.ViewHolder viewHolder = new CalendarListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CalendarListAdapter.ViewHolder viewHolder, int position) {
        Log.d("tag1" , "onBind");
        TextView contestNameText = viewHolder.cardView.findViewById(R.id.CalendarContestListTextView);
        contestNameText.setText(mDataset.get(position).getContestName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}