package com.example.sw_project.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.BackPressCloseHandler;
import com.example.sw_project.R;
import com.example.sw_project.ScrapInfo;
import com.example.sw_project.WriteInfo;
import com.example.sw_project.adapter.PostListAdapter;
import com.example.sw_project.fragment.Fragment_Tab_3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecruitScrapActivity extends Fragment {

    View view;

    private final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    public FirebaseFirestore db;
    public String TAG = "RecruitActivity";
    public FirebaseUser user;
    private BackPressCloseHandler backPress;

    public static RecruitScrapActivity newInstance() {
        return new RecruitScrapActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_mypage_recruit, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getScrapData();

        backPress = new BackPressCloseHandler(getActivity());
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                ((MainActivity)getActivity()).replaceFragment(Fragment_Tab_3.newInstance(), "Fragment_Tab_3");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return view;
    }

    public void initRecyclerView(){

        RecyclerView recyclerView = view.findViewById(R.id.recruitRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.Adapter mAdapter = new PostListAdapter(new ArrayList<WriteInfo>(), getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    public void getScrapData(){

        initRecyclerView();

        db.collection("scrapsPost")
                .whereEqualTo("scrapUserUid",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<ScrapInfo> scrapArray = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                scrapArray.add(document.toObject(ScrapInfo.class));
                            }

                            if(scrapArray.size() == 0)
                                view.findViewById(R.id.isScrapPostText).setVisibility(View.VISIBLE);
                            else
                                view.findViewById(R.id.isScrapPostText).setVisibility(View.INVISIBLE);

                            // postId에 해당하는 post 불러오기
                            ArrayList<WriteInfo> writeArray = new ArrayList<>();
                            for(ScrapInfo findPost : scrapArray){
                                db.collection("posts")
                                        .whereEqualTo("postid", findPost.getPostId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        writeArray.add(document.toObject(WriteInfo.class));
                                                    }
                                                    RecyclerView recyclerView = view.findViewById(R.id.recruitRecycler);
                                                    recyclerView.setHasFixedSize(true);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                    RecyclerView.Adapter madapter = new PostListAdapter(writeArray, getActivity());
                                                    recyclerView.setAdapter(madapter);
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }
                        }else {
                            Log.w(TAG, "Error => ", task.getException());
                        }
                    }
                });
    }

}