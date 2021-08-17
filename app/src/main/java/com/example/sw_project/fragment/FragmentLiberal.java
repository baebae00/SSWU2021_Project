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

import com.example.sw_project.fragment.LiberalMajor.LiberalMajorChinese;
import com.example.sw_project.fragment.LiberalMajor.LiberalMajorEnglish;
import com.example.sw_project.fragment.LiberalMajor.LiberalMajorFrance;
import com.example.sw_project.fragment.LiberalMajor.LiberalMajorGerman;
import com.example.sw_project.fragment.LiberalMajor.LiberalMajorHistory;
import com.example.sw_project.fragment.LiberalMajor.LiberalMajorJapanese;
import com.example.sw_project.fragment.LiberalMajor.LiberalMajorKorean;
import com.google.android.material.tabs.TabLayout;

// 인문대학
public class FragmentLiberal extends Fragment {


    private static final String TAG = "SSWU_Liberal_Fragment";
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
        adapter.addFragment(new LiberalMajorKorean());
        adapter.addFragment(new LiberalMajorEnglish());
        adapter.addFragment(new LiberalMajorGerman());
        adapter.addFragment(new LiberalMajorFrance());
        adapter.addFragment(new LiberalMajorJapanese());
        adapter.addFragment(new LiberalMajorChinese());
        adapter.addFragment(new LiberalMajorHistory());

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("국어국문학과");
        tabLayout.getTabAt(1).setText("영어영문학과");
        tabLayout.getTabAt(2).setText("독일어문ㆍ문화학과");
        tabLayout.getTabAt(3).setText("프랑스어문ㆍ문화학과");
        tabLayout.getTabAt(4).setText("일본어문ㆍ문화학과");
        tabLayout.getTabAt(5).setText("중국어문ㆍ문화학과");
        tabLayout.getTabAt(6).setText("사학과");

        return v;
    }
}