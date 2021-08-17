package com.example.sw_project.fragment;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.ContestDTO;
import com.example.sw_project.R;
import com.example.sw_project.adapter.AllRecyclerAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentAll extends Fragment {

    private RecyclerView recyclerView;
    private AllRecyclerAdapter adapter;
    String contest_chart_url = "https://www.jungle.co.kr/contest";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.contest_recycler,container,false);

        // recyclerview
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_chart);
        // recyclerView.setHasFixedSize(true);
        adapter = new AllRecyclerAdapter(getActivity());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new AllRecyclerAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        getData();

        return v;
    }

    private void getData(){
        FragmentAll.MelonJsoup jsoupAsyncTask = new FragmentAll.MelonJsoup();
        jsoupAsyncTask.execute();
    }

    public class MelonJsoup extends AsyncTask<Void, Void, Void> {
        ArrayList<String> listTitle = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();
        ArrayList<String> listDue = new ArrayList<>();
        ArrayList<String> listUrl = new ArrayList<>();
        ArrayList<String> listAlbumID = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(contest_chart_url).get();
                final Elements rank_list1 = doc.select("ul#contest-list.thumb_list05 li div a.thumb_title05");
                final Elements rank_list_name = doc.select("ul#contest-list.thumb_list05 li div p.list_date span");

                final Elements rank_list_due = doc.select("ul#contest-list.thumb_list05 li div p.list_date");

                final Elements image_list1 = doc.select("ul#contest-list.thumb_list05 li a span.zoom img");

                //앨범 아이디 추출하기
                final Elements albumId_list_1 = doc.select("ul#contest-list.thumb_list05 div a.thumb_title05");

                Handler handler = new Handler(Looper.getMainLooper()); // 객체생성
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 공모전 이름
                        for(Element element: rank_list1) {
                            listTitle.add(element.text());
                        }
                        // d-day
                        for (Element element : rank_list_name) {
                            listName.add(element.text());
                        }
                        // 기간
                        for (Element element : rank_list_due) {
                            String date = element.ownText();
                            listDue.add(date);
                            // listDue.add(element.text());
                        }
                        // 이미지정보
                        for (Element element : image_list1){
                            // 주소에서 /contest 지우고 그 자리에 src 추가
                            listUrl.add("https://www.jungle.co.kr" + element.attr("src"));
                        }

                        for (Element element : albumId_list_1) {
                            // 앨범 아이디만 나올 수 있도록 문자열 추출 작업
                            // <a href="javascript:melon.link.goAlbumDetail('10427559');" 에서 href 속성만 떼어내기
                            String tmp = element.attr("href");
/*
                            //그중에서도 앨범 아이디(숫자부분)만 뗴어내기
                            int tmp_num = tmp.indexOf("('") + 2;
                            String result = tmp.substring(tmp_num, (tmp.substring(tmp_num).indexOf("');") + tmp_num));
                            //앨범 아이디만 리스트에 추가
                            listAlbumID.add(result);
*/
                           /* String result = tmp.substring(tmp.indexOf("=")+1);
                            //앨범 아이디만 리스트에 추가
                            listAlbumID.add(result); */

                            String result = tmp.substring(tmp.indexOf("st")+2);
                            //앨범 아이디만 리스트에 추가
                            listAlbumID.add(result);

                        }

                        for (int i = 0; i < listTitle.size() ; i++) {
                            ContestDTO data = new ContestDTO();
                            if(listTitle.size()!= 0) {data.setTitle(listTitle.get(i));}
                            if(listUrl.size()!= 0) {data.setImageUrl(listUrl.get(i));}
                            if(listName.size()!= 0) {data.setName(listName.get(i));}
                            if(listDue.size()!= 0) {data.setDue(listDue.get(i));}
                            // data.setRankNum(String.valueOf(i+1));
                            // if(listTitle.size()!= 0) {data.setTitle(listTitle.get(i));}
                            if(listAlbumID.size()!= 0) {data.setAlbumID(listAlbumID.get(i));}

                            adapter.addItem(data);
                        }
                        System.out.println(listAlbumID);
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
