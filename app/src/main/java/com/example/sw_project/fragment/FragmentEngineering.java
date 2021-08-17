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

import com.example.sw_project.fragment.EngineeringMajor.EngineeringMajorAiot;
import com.example.sw_project.fragment.EngineeringMajor.EngineeringMajorCe;
import com.example.sw_project.fragment.EngineeringMajor.EngineeringMajorCse;
import com.example.sw_project.fragment.EngineeringMajor.EngineeringMajorInfosys;
import com.example.sw_project.fragment.EngineeringMajor.EngineeringMajorSerdesign;
import com.google.android.material.tabs.TabLayout;

// 공과대학
public class FragmentEngineering extends Fragment {


    private static final String TAG = "SSWU_Engineering_Fragment";
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
        adapter.addFragment(new EngineeringMajorCse());
        adapter.addFragment(new EngineeringMajorCe());
        adapter.addFragment(new EngineeringMajorInfosys());
        adapter.addFragment(new EngineeringMajorSerdesign());
        adapter.addFragment(new EngineeringMajorAiot());

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("융합보안공학과");
        tabLayout.getTabAt(1).setText("컴퓨터공학과");
        tabLayout.getTabAt(2).setText("정보시스템공학과");
        tabLayout.getTabAt(3).setText("서비스디자인공학과");
        tabLayout.getTabAt(4).setText("AI융합학부");

        return v;
    }
}