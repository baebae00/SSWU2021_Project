package com.example.sw_project.fragment.SocialMajor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sw_project.R;
import com.example.sw_project.RecyclerViewEmptySupport;
import com.example.sw_project.SSWUDTO;
import com.example.sw_project.adapter.SSWURecyclerAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

// 경영학과
public class SocialMajorBusiness extends Fragment {

    // private RecyclerView recyclerView;
    private RecyclerViewEmptySupport recyclerView;
    private SSWURecyclerAdapter adapter;
    String contest_chart_url = "https://www.sungshin.ac.kr/bizadm/17486/subview.do";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.sswu_contest_recycler,container,false);

        // recyclerview
        // recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_chart);
        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recyclerView_chart);
        // recyclerView.setHasFixedSize(true);
        adapter = new SSWURecyclerAdapter(getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new SSWURecyclerAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        // empty 경우 텍스트
        recyclerView.setEmptyView(v.findViewById(R.id.empty_view));
        getData();

        return v;
    }

    private void getData(){
        SocialMajorBusiness.MelonJsoup jsoupAsyncTask = new SocialMajorBusiness.MelonJsoup();
        jsoupAsyncTask.execute();
    }

    public class MelonJsoup extends AsyncTask<Void, Void, Void> {
        ArrayList<String> listTitle = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();
        ArrayList<String> reportDate = new ArrayList<>();
        ArrayList<String> listUrl = new ArrayList<>();
        ArrayList<String> listAlbumID = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(contest_chart_url).get();

                final Elements rank_list1 = doc.select("td._artclTdNum");
                final Elements rank_list_name = doc.select("td._artclTdTitle a.artclLinkView strong");

                final Elements report_date = doc.select("td._artclTdRdate");
                final Elements albumId_list_1 = doc.select("td._artclTdTitle a");

                Handler handler = new Handler(Looper.getMainLooper()); // 객체생성
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 공지 or not
                        for(Element element: rank_list1) {
                            listTitle.add(element.text());
                        }
                        // 공모전 제목
                        for (Element element : rank_list_name) {
                            listName.add(element.text());
                        }
                        // 작성일
                        for (Element element : report_date) {
                            reportDate.add(element.text());
                        }
                        // 공모전 정보 링크
                        for (Element element : albumId_list_1) {
                            String tmp = element.attr("href");

                            String result = tmp.substring(tmp.indexOf("bs/")+3);

                            listAlbumID.add(result);

                        }

                        for (int i = 0; i < listName.size(); i++) {
                            if (listTitle.get(i).contains("공지")!=true && ( listName.get(i).contains("대회") || listName.get(i).contains("공모") )){
                                SSWUDTO data = new SSWUDTO();
                                data.setName(listName.get(i));
                                data.setReportDate(reportDate.get(i));
                                data.setAlbumID(listAlbumID.get(i));

                                adapter.addItem(data);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}