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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.Activity.ContestDetailActivity;
import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
import com.example.sw_project.SSWUDTO;
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

public class SSWURecyclerAdapter extends RecyclerView.Adapter<SSWURecyclerAdapter.ItemViewHolder> {
    private ArrayList<SSWUDTO> listData = new ArrayList<>(); //adapter에 들어갈 list
    private Context context;
    private Intent intent;
    private Activity activity;

    public SSWURecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public SSWURecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킴
        // return 인자는 ViewHolder
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sswu_contest_item, viewGroup, false);
        return new SSWURecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SSWURecyclerAdapter.ItemViewHolder itemViewHolder, int i) {
        // item을 하나씩 보여주는(bind 되는) 함수
        itemViewHolder.onBind(listData.get(i));
    }

    @Override
    public int getItemCount() {
        //RecyclerView의 총 개수
        return listData.size();
    }

    public void addItem(SSWUDTO data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView txt_chartName, txt_chartTitle, txt_reportDate;
        private ImageView img_chart;
        private SSWUDTO data;
        private String albumID;
        private boolean dbExist;
        private ContestInfo contestDetail;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private String TAG = "SSWURecyclerAdapter";

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // txt_chartTitle = itemView.findViewById(R.id.txt_chartTitle);
            txt_chartName = itemView.findViewById(R.id.txt_chartName);
            txt_reportDate = itemView.findViewById(R.id.txt_reportDate);
            // img_chart     = itemView.findViewById(R.id.img_chart);
            itemView.findViewById(R.id.recruitInContestButton).setOnClickListener(onClickListener);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.recruitInContestButton:
                        checkContestInDB();
                        break;
                }
            }
        };

        private void checkContestInDB() {

            dbExist = false;
            db.collection("contests")
                    .whereEqualTo("contestName", data.getName())
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

            contestDetail = new ContestInfo();
            contestDetail.setStartDate(data.getReportDate());
            contestDetail.setContestName(data.getName());
            contestDetail.setDetailUrl(data.getAlbumID());
            contestDetail.setInOut("교내");

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

        void onBind(SSWUDTO data){
            this.data = data;

            txt_chartName.setText(data.getName());
            // txt_chartTitle.setText(data.getTitle());
            txt_reportDate.setText(data.getReportDate());
            // Glide.with(itemView.getContext()).load(data.getImageUrl()).into(img_chart);

            itemView.setOnClickListener(this);
            // txt_chartTitle.setOnClickListener(this);
            txt_chartName.setOnClickListener(this);
            txt_reportDate.setOnClickListener(this);
            // img_chart.setOnClickListener(this);

            Log.d("RecyclerAdapter!!!!!", data.getName() + "\n");

        }

        @Override
        public void onClick(View view) {
            Log.d("click_item!!!!!", String.valueOf(view.getId()));
            // Log.d("click_item!!!!!", data.getTitle()  + " / "+ data.getName() + " / " + data.getAlbumID());
            Log.d("click_item!!!!!", data.getName() + " / " + data.getAlbumID());

            //앨범 번호
            albumID = data.getAlbumID();

            //앨범 디테일 주소 melon_detail_url + albumID
            String melon_detail_url = "https://www.sungshin.ac.kr/bbs/";

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(melon_detail_url + albumID));
            view.getContext().startActivities(new Intent[]{intent});
        }
    }
}