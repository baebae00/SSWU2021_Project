package com.example.sw_project.fragment;

import android.app.Activity;
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
import com.example.sw_project.Activity.MainActivity;
import com.example.sw_project.Activity.MyPostActivity;
import com.example.sw_project.BackPressCloseHandler;
import com.example.sw_project.R;
import com.example.sw_project.WriteInfo;
import com.example.sw_project.adapter.PostListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class Fragment_Tab_1 extends Fragment {

    View view;
    private Activity activity;
    private final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    private FirebaseUser user;
    public String TAG = "activity_postlist";
    public FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private BackPressCloseHandler backPress;

    public static Fragment_Tab_1 newInstance() {
        return new Fragment_Tab_1();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_postlist, container, false);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerview);
        view.findViewById(R.id.moveMyPostButton).setOnClickListener(onClickListener);

        listDbGet();

        backPress = new BackPressCloseHandler(getActivity());
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                backPress.onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.moveMyPostButton:
                    ((MainActivity)getActivity()).replaceFragment(MyPostActivity.newInstance(), "MyPostActivity");
                    break;
            }
        }
    };

    public void init(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mAdapter = new PostListAdapter(new ArrayList<WriteInfo>(), getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    public void listDbGet(){

        init();

        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) { // 데이터를 가져오는 작업이 잘 동작했을 떄
                            final ArrayList<WriteInfo> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                arrayList.add(document.toObject(WriteInfo.class));
                            }
                            Collections.sort(arrayList);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            mAdapter = new PostListAdapter(arrayList, getActivity());
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.w(TAG, "Error => ", task.getException());
                        }
                    }
                });
    }

}
