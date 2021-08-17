package com.example.sw_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.example.sw_project.R;
import com.example.sw_project.adapter.SSWUFragmentAdapter;

import com.example.sw_project.fragment.ArtsMajor.ArtsMajorCarving;
import com.example.sw_project.fragment.ArtsMajor.ArtsMajorCrafts;
import com.example.sw_project.fragment.ArtsMajor.ArtsMajorIndusdesign;
import com.example.sw_project.fragment.ArtsMajor.ArtsMajorOrient;
import com.example.sw_project.fragment.ArtsMajor.ArtsMajorWestern;

import com.google.android.material.tabs.TabLayout;

// 예체능대학
public class FragmentArts extends Fragment {


    private static final String TAG = "SSWU_Arts_Fragment";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SSWUFragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.sswu_activity_sub, container, false);

        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        // adapter = new SSWUFragmentAdapter(getSupportFragmentManager(), 1);
        adapter = new SSWUFragmentAdapter(getChildFragmentManager(),1);

        //FragmentAdapter에 컬렉션 담기
        adapter.addFragment(new ArtsMajorOrient());
        adapter.addFragment(new ArtsMajorWestern());
        adapter.addFragment(new ArtsMajorCarving());
        adapter.addFragment(new ArtsMajorCrafts());
        adapter.addFragment(new ArtsMajorIndusdesign());
        // adapter.addFragment(new ArtsMajorCultureart());

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("동양학과");
        tabLayout.getTabAt(1).setText("서양학과");
        tabLayout.getTabAt(2).setText("조소과");
        tabLayout.getTabAt(3).setText("공예과");
        tabLayout.getTabAt(4).setText("산업디자인과");
        // tabLayout.getTabAt(5).setText("문화예술경영학과");

        return v;
    }
}
