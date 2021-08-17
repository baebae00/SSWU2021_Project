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

import com.example.sw_project.fragment.SocialMajor.SocialMajorBusiness;
import com.example.sw_project.fragment.SocialMajor.SocialMajorEconomic;
import com.example.sw_project.fragment.SocialMajor.SocialMajorGeo;
import com.example.sw_project.fragment.SocialMajor.SocialMajorMediacomm;
import com.example.sw_project.fragment.SocialMajor.SocialMajorPolitics;
import com.example.sw_project.fragment.SocialMajor.SocialMajorPsy;
import com.example.sw_project.fragment.SocialMajor.SocialMajorWelfare;
import com.google.android.material.tabs.TabLayout;

// 사회대학
public class FragmentSocial extends Fragment {


    private static final String TAG = "SSWU_Social_Fragment";
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
        adapter.addFragment(new SocialMajorPolitics());
        adapter.addFragment(new SocialMajorPsy());
        adapter.addFragment(new SocialMajorGeo());
        adapter.addFragment(new SocialMajorEconomic());
        adapter.addFragment(new SocialMajorBusiness());
        adapter.addFragment(new SocialMajorMediacomm());
        adapter.addFragment(new SocialMajorWelfare());

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("정치외교학과");
        tabLayout.getTabAt(1).setText("심리학과");
        tabLayout.getTabAt(2).setText("지리학과");
        tabLayout.getTabAt(3).setText("경제학과");
        tabLayout.getTabAt(4).setText("경영학과");
        tabLayout.getTabAt(5).setText("미디어커뮤니케이션학과");
        tabLayout.getTabAt(6).setText("사회복지학과");

        return v;
    }
}