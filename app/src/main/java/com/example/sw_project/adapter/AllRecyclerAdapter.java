package com.example.sw_project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sw_project.Activity.ContestDetailActivity;
import com.example.sw_project.ContestDTO;
import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
import com.example.sw_project.StatisticsInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class AllRecyclerAdapter extends RecyclerView.Adapter<AllRecyclerAdapter.ItemViewHolder> {
    private String TAG = "AllRecyclerAdapter";
    private ArrayList<ContestDTO> listData = new ArrayList<>(); //adapter에 들어갈 list
    private Context context;
    private Intent intent;
    private Activity activity;

    public AllRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킴
        // return 인자는 ViewHolder
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contest_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllRecyclerAdapter.ItemViewHolder itemViewHolder, int position) {
        // item을 하나씩 보여주는(bind 되는) 함수
        itemViewHolder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        //RecyclerView의 총 개수
        return listData.size();
    }

    public void addItem(ContestDTO data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView txt_chartName, txt_chartTitle, txt_chartDue;
        private ImageView img_chart;
        private ContestDTO data;
        private String albumID;

        private ContestInfo contestDetail;
        private boolean dbExist;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_chartTitle = itemView.findViewById(R.id.txt_chartTitle);
            txt_chartName = itemView.findViewById(R.id.txt_chartName);
            txt_chartDue = itemView.findViewById(R.id.txt_chartDue);
            img_chart = itemView.findViewById(R.id.img_chart);
            itemView.findViewById(R.id.contestRecruitButton).setOnClickListener(onClickListener);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.contestRecruitButton:
                        checkContestInDB();
                        break;
                }
            }
        };

        private void checkContestInDB() {

            dbExist = false;

            db.collection("contests")
                    .whereEqualTo("contestName", data.getTitle())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    dbExist = true;
                                    contestDetail = document.toObject(ContestInfo.class);

                                    Intent intent = new Intent(activity, ContestDetailActivity.class);
                                    intent.putExtra("contestDetail", contestDetail);
                                    activity.startActivity(intent);
                                }
                                if(!dbExist) {
                                    registerNewContest();
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }

        private void registerNewContest(){

            StringTokenizer stk = new StringTokenizer(data.getDue(),"~ ");
            contestDetail = new ContestInfo();
            contestDetail.setStartDate(stk.nextToken());
            contestDetail.setEndDate(stk.nextToken());

            contestDetail.setContestName(data.getTitle());
            contestDetail.setDetailUrl(data.getAlbumID());
            contestDetail.setImageUrl(data.getImageUrl());
            contestDetail.setInOut("교외");
//            contestDetail.setScrapNum(0);

            db.collection("contests").add(contestDetail)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            //contestId 업데이
                            DocumentReference washingtonRef = db.collection("contests").document(documentReference.getId());
                            washingtonRef
                                    .update("contestId", documentReference.getId())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            contestDetail.setContestId(documentReference.getId());
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            StatisticsInfo statisticsInfo = new StatisticsInfo();
                                            statisticsInfo.setScrapNum(0);
                                            db.collection("statistics").document(documentReference.getId()).set(statisticsInfo);

                                            Intent intent = new Intent(activity, ContestDetailActivity.class);
                                            intent.putExtra("contestDetail", contestDetail);
                                            activity.startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }

        void onBind(ContestDTO data){
            this.data = data;
            txt_chartName.setText(data.getName());
            txt_chartTitle.setText(data.getTitle());
            txt_chartDue.setText(data.getDue());
            Glide.with(itemView.getContext()).load(data.getImageUrl()).into(img_chart);

            itemView.setOnClickListener(this);
            txt_chartTitle.setOnClickListener(this);
            txt_chartName.setOnClickListener(this);
            txt_chartDue.setOnClickListener(this);
            img_chart.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Log.d("click_item!!!!!", String.valueOf(view.getId()));
            Log.d("click_item!!!!!", data.getTitle()  + " / "+ data.getName() + " / " + data.getDue() + " / " + data.getAlbumID());

            Toast.makeText(context, "TITLE : " + data.getTitle() + "\nContent : " + data.getName() + "\nDue : " + data.getDue(), Toast.LENGTH_SHORT).show();

            //앨범 번호
            albumID = data.getAlbumID();

            //앨범 디테일 주소 melon_detail_url + albumID
            String melon_detail_url = "https://www.jungle.co.kr/contest";

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(melon_detail_url + albumID));
            view.getContext().startActivities(new Intent[]{intent});
        }
    }
}