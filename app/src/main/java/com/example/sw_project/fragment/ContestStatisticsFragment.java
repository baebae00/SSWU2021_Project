package com.example.sw_project.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
import com.example.sw_project.StatisticsInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ContestStatisticsFragment extends Fragment {

    View view;
    private FirebaseFirestore db;
    private String TAG = "ContestStatisticsFragment";
    private ContestInfo contestInfo;
    private BarChart majorChart;
    private BarChart numChart;
    private StatisticsInfo statisticsInfo;
    private TextView totalText;
    private ArrayList<Integer> jsonList = new ArrayList<>(); // ArrayList 선언
    private ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
    private ArrayList<Integer> json2List = new ArrayList<>(); // ArrayList 선언
    private ArrayList<String> label2List = new ArrayList<>(); // ArrayList 선언
    private ArrayList<Integer> colorList;
    private XAxis xAxis;
    private XAxis x;
    private ArrayList<Integer> percentageList = new ArrayList<>();
    private ArrayList<Integer> percentage2List = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_contest_statistics, container, false);

        Bundle bundle = getArguments();
        contestInfo = (ContestInfo)bundle.getSerializable("contestInfo");

        majorChart = (BarChart) view.findViewById(R.id.contestMajorChart);
        numChart = (BarChart) view.findViewById(R.id.contestSchoolNumChart);
        totalText = view.findViewById(R.id.totalContestScrapCountText);

        db = FirebaseFirestore.getInstance();

        try {
            DocumentReference docRef = db.collection("statistics").document(contestInfo.getContestId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task){
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            statisticsInfo = document.toObject(StatisticsInfo.class);
                            String text = String.format("%d명의 학생들이 이 공모전을 스크랩했습니다",statisticsInfo.getScrapNum());
                            totalText.setText(text);
                            try {
                                graphSetting();
                                BarChartGraph(labelList, percentageList);
                                BarChartGraph2(label2List,percentage2List);
                            }catch(NullPointerException e) {
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } catch (NullPointerException e){
            //아직 제공할 데이터 없음 나타내는 뷰 만들기...
            System.out.println("nothing...");
        }

        //major chart 설
        majorChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
        majorChart.setDrawBarShadow(true); // 그래프 그림자
        majorChart.setTouchEnabled(false); // 차트 터치 막기
        majorChart.setDrawValueAboveBar(true); // 입력?값이 차트 위or아래에 그려질 건지 (true=위, false=아래)
        majorChart.setPinchZoom(false); // 두손가락으로 줌 설정
        majorChart.setDrawGridBackground(false); // 격자구조
        majorChart.setMaxVisibleValueCount(5); // 그래프 최대 갯수
        majorChart.getDescription().setEnabled(false);// 그래프 오른쪽 하단에 라벨 표시
        majorChart.getLegend().setEnabled(false);
        majorChart.getAxisLeft().setAxisMaxValue(40);

        xAxis = majorChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.BLACK);

        YAxis leftAxis = majorChart.getAxisLeft();
        leftAxis.setEnabled(false);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(100);
        leftAxis.setGranularity(10f);
        leftAxis.setDrawLabels(false);
        leftAxis.setTextColor(Color.RED);
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setGridColor(Color.BLUE);

        YAxis rightAxis = majorChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setTextSize(15f);

        //
        numChart.setTouchEnabled(false);
        numChart.setDrawBarShadow(true); // 그래프 그림자
        numChart.setTouchEnabled(false); // 차트 터치 막기
        numChart.setDrawValueAboveBar(true);
        numChart.setPinchZoom(false);
        numChart.setDrawGridBackground(false); // 격자구조
        numChart.setMaxVisibleValueCount(5); // 그래프 최대 갯수
        numChart.getDescription().setEnabled(false);// 그래프 오른쪽 하단에 라벨 표시
        numChart.getLegend().setEnabled(false);
        numChart.getAxisLeft().setAxisMaxValue(40);

        x = numChart.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setDrawAxisLine(false);
        x.setGranularity(1f);
        x.setTextSize(10f);
        x.setTextColor(Color.BLACK);

        YAxis left = numChart.getAxisLeft();
        left.setEnabled(false);
        left.setAxisMinimum(0);
        left.setAxisMaximum(100);
        left.setGranularity(10f);
        left.setDrawLabels(false);
        left.setTextColor(Color.RED);
        left.setAxisLineColor(Color.BLACK);
        left.setGridColor(Color.BLUE);

        YAxis right = numChart.getAxisRight();
        right.setEnabled(false);
        right.setTextSize(15f);

        colorList = new ArrayList();
        colorList.add(Color.parseColor("#DB6BE5"));
        colorList.add(Color.parseColor("#9267F3"));
        colorList.add(Color.parseColor("#6384F5"));
        colorList.add(Color.parseColor("#56BDD3"));
        colorList.add(Color.parseColor("#61D478"));


        return view;
    }

    public void graphSetting() throws NullPointerException{

        Map<String, Integer> majorMap = statisticsInfo.getMajorCount();
        Map<String, Integer> numMap = statisticsInfo.getSchoolNumCount();
        List<Map.Entry<String, Integer>> majorList = new ArrayList<Map.Entry<String, Integer>>(majorMap.entrySet());
        List<Map.Entry<String, Integer>> numList = new ArrayList<Map.Entry<String, Integer>>(numMap.entrySet());

        Collections.sort(majorList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                // 내림 차순으로 정렬
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });

        Collections.sort(numList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                // 내림 차순으로 정렬
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });

        // 그래프 5개가지만 제공할 것. 5개초과인 경우는 4개는 그대로, 나머지 한개에 대해서 기타로 퉁치기
        if(majorList.size() > 5){

            for (int i = 0; i < 4; i++) {
                labelList.add(majorList.get(i).getKey());
                jsonList.add(majorList.get(i).getValue());
            }
            int etcSum = 0;

            for(int i = 4; i < majorList.size(); i++)
                etcSum += majorList.get(i).getValue();

            labelList.add("기타");
            jsonList.add(etcSum);

        }else {
            for (int i = 0; i < majorList.size(); i++) {
                labelList.add(majorList.get(i).getKey());
                jsonList.add(majorList.get(i).getValue());
            }
        }

        if(numList.size() > 5){

            for (int i = 0; i < 4; i++) {
                label2List.add(numList.get(i).getKey());
                json2List.add(numList.get(i).getValue());
            }
            int etcSum = 0;

            for(int i = 4; i < numList.size(); i++)
                etcSum += numList.get(i).getValue();

            label2List.add("기타");
            json2List.add(etcSum);

        }else {
            for (int i = 0; i < numList.size(); i++) {
                label2List.add(numList.get(i).getKey());
                json2List.add(numList.get(i).getValue());
            }
        }

        percentageCal();

    }

    private void percentageCal(){

        double total = 0;
        for(Integer each : jsonList)
            total += each;

        for(Integer each : jsonList) {
            int convert = (int) (each / total * 100);
            percentageList.add(convert);
        }
        System.out.println(percentageList);

        total = 0;
        for(Integer each : json2List)
            total += each;

        for(Integer each : json2List) {
            int convert = (int) (each / total * 100);
            percentage2List.add(convert);
        }
        System.out.println(percentage2List);
    }

    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Integer> valList) {
        
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry(i, (Integer) valList.get(i)));
        }

        BarDataSet depenses = new BarDataSet(entries, "스크랩"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        depenses.setColors(colorList);

        ArrayList<String> labels = new ArrayList<String>();

        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }

        BarData data = new BarData(depenses);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels.get((int)value);
            }
        };

        xAxis.setValueFormatter(xAxisFormatter);

        majorChart.setData(data);
        majorChart.animateXY(1000, 1000);
        majorChart.invalidate();
    }

    private void BarChartGraph2(ArrayList<String> labelList, ArrayList<Integer> valList) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry(i, (Integer) valList.get(i)));
        }

        BarDataSet depenses = new BarDataSet(entries, "스크랩"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        depenses.setColors(colorList);

        ArrayList<String> labels = new ArrayList<String>();

        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }

        BarData data = new BarData(depenses);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels.get((int)value);
            }
        };

        x.setValueFormatter(xAxisFormatter);

        numChart.setData(data);
        numChart.animateXY(1000, 1000);
        numChart.invalidate();
    }


}
