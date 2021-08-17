package com.example.sw_project.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.sw_project.BackPressCloseHandler;
import com.example.sw_project.R;
import com.example.sw_project.adapter.SSWUFragmentAdapter;
import com.example.sw_project.fragment.FragmentArts;
import com.example.sw_project.fragment.FragmentEngineering;
import com.example.sw_project.fragment.FragmentLiberal;
import com.example.sw_project.fragment.FragmentSocial;
import com.example.sw_project.fragment.Fragment_Tab_2;
import com.google.android.material.tabs.TabLayout;

public class SSWUMainActivity extends Fragment {

    View view;

    private static final String TAG = "SSWU_Main_Activity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SSWUFragmentAdapter adapter;
    private BackPressCloseHandler backPress;

    public static SSWUMainActivity newInstance() {
        return new SSWUMainActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.sswu_activity_main, container, false);

        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.view_pager);
        adapter = new SSWUFragmentAdapter(getChildFragmentManager(), 1);

        //FragmentAdapter에 컬렉션 담기
        adapter.addFragment(new FragmentLiberal());
        adapter.addFragment(new FragmentSocial());
        adapter.addFragment(new FragmentEngineering());
        adapter.addFragment(new FragmentArts());

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("인문대학");
        tabLayout.getTabAt(1).setText("사회대학");
        tabLayout.getTabAt(2).setText("공과대학");
        tabLayout.getTabAt(3).setText("예체능대학");

        backPress = new BackPressCloseHandler(getActivity());
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                ((MainActivity)getActivity()).replaceFragment(Fragment_Tab_2.newInstance(), "Fragment_Tab_2");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return view;
    }
}

