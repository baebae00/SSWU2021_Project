package com.example.sw_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sw_project.Activity.FragMainActivity;
import com.example.sw_project.Activity.MainActivity;
import com.example.sw_project.Activity.SSWUMainActivity;
import com.example.sw_project.BackPressCloseHandler;
import com.example.sw_project.R;

public class Fragment_Tab_2 extends Fragment {

    View view;

    private static final String TAG = "Fragment_2";
    private BackPressCloseHandler backPress;

    public static Fragment_Tab_2 newInstance() {
        return new Fragment_Tab_2();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.activity_fragment_2,container,false);

        view.findViewById(R.id.inContestButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.outContestButton).setOnClickListener(onClickListener);

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

    View.OnClickListener onClickListener = new View.OnClickListener() { //버튼 클릭시
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.inContestButton:
                    ((MainActivity)getActivity()).replaceFragment(SSWUMainActivity.newInstance(), "inContest");
                    break;
                case R.id.outContestButton:
                    ((MainActivity)getActivity()).replaceFragment(FragMainActivity.newInstance(), "outContest");
                    break;
            }
        }
    };
}