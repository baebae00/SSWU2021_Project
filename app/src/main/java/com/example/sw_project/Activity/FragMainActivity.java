package com.example.sw_project.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sw_project.BackPressCloseHandler;
import com.example.sw_project.ContestDTO;
import com.example.sw_project.R;
import com.example.sw_project.adapter.FragmentAdapter;
import com.example.sw_project.fragment.FragmentAll;
import com.example.sw_project.fragment.FragmentDesign;
import com.example.sw_project.fragment.FragmentIT;
import com.example.sw_project.fragment.FragmentMarketing;
import com.example.sw_project.fragment.FragmentPlan;
import com.example.sw_project.fragment.Fragment_Tab_2;
import com.google.android.material.tabs.TabLayout;

import android.content.Intent;
import android.net.Uri;
import android.widget.SearchView;

import java.util.ArrayList;

public class FragMainActivity extends Fragment {

    View view;

    private static final String TAG = "Main_Activity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentAdapter adapter;
    private BackPressCloseHandler backPress;

    private SearchView searchView;
    private ArrayList<ContestDTO> groupItemList = new ArrayList<>(); // 서버에서 가져온 원본 데이 리스트

    public static FragMainActivity newInstance() {
        return new FragMainActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.frag_activity_main, container, false);

        searchView = view.findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                // 검색 버튼 누를 때 호출
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                return false;
            }

            void search(String query) {
//                Toast.makeText(FragMainActivity.this, "공모전 정보 검색 : " + query, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jungle.co.kr/search?q="+ query));
                startActivity(intent);
            }

        });

        tabLayout=view.findViewById(R.id.tabs);
        viewPager=view.findViewById(R.id.view_pager);
        adapter=new FragmentAdapter(getChildFragmentManager(),1);

        //FragmentAdapter에 컬렉션 담기
        adapter.addFragment(new FragmentAll());
        adapter.addFragment(new FragmentPlan());
        adapter.addFragment(new FragmentMarketing());
        adapter.addFragment(new FragmentIT());
        adapter.addFragment(new FragmentDesign());

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("전체");
        tabLayout.getTabAt(1).setText("기획");
        tabLayout.getTabAt(2).setText("마케팅");
        tabLayout.getTabAt(3).setText("IT");
        tabLayout.getTabAt(4).setText("디자인");

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
