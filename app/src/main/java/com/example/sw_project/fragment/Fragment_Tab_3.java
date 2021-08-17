package com.example.sw_project.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sw_project.Activity.ContestScrapActivity;
import com.example.sw_project.Activity.MainActivity;
import com.example.sw_project.Activity.RecruitScrapActivity;
import com.example.sw_project.BackPressCloseHandler;
import com.example.sw_project.R;
import com.example.sw_project.StudentInfo;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fragment_Tab_3 extends Fragment {

    View view;
    TextView totalStatisticNumber, totalStatisticsText, collegeStatisticsNumber, collegeStatisticsText;
    PieChart pieChartTotal, pieChartCollege;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private static final String TAG = "MyPageData";
    private RelativeLayout loaderLayout;
    private StudentInfo info;
    private HashMap<String, ArrayList> major;
    private BackPressCloseHandler backPress;

    public static Fragment_Tab_3 newInstance() {
        return new Fragment_Tab_3();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_my_page,container,false);
        view.findViewById(R.id.contestScrapButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.recruitButton).setOnClickListener(onClickListener);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        loaderLayout = view.findViewById(R.id.loaderLayout);

        userParticipateQuery();

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

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.contestScrapButton:
                    ((MainActivity)getActivity()).replaceFragment(ContestScrapActivity.newInstance(), "contestScrap");
                    break;
                case R.id.recruitButton:
                    ((MainActivity)getActivity()).replaceFragment(RecruitScrapActivity.newInstance(), "recruitScrap");
                    break;
            }

        }
    };

    private void collegeStatisticQuery(){

        totalStatisticNumber = view.findViewById(R.id.totalStatisticNumber);
        totalStatisticsText = view.findViewById(R.id.totalStatisticsText);
        collegeStatisticsNumber = view.findViewById(R.id.collegeStatisticsNumber);
        collegeStatisticsText = view.findViewById(R.id.collegeStatisticsText);

        //전체 데이터
        double totalContest = 0;
        double totalStudent = 0;
        for(Map.Entry<String, ArrayList> each : major.entrySet()){
            Long convert = (Long) each.getValue().get(0);
            double contest = convert.doubleValue();
            totalContest += contest;

            Long change = (Long) each.getValue().get(1);
            double student = change.doubleValue();
            totalStudent += student;
        }

        totalContest /= totalStudent;

        String number = String.format("%.1f vs %d",totalContest,Integer.parseInt(info.getContestParticipate()));
        String what = totalContest <= Integer.parseInt(info.getContestParticipate()) ? "많이" : "적게";
        String text = String.format("수정이들보다 평균적으로 %s 참여했습니다",what);
        totalStatisticNumber.setText(number);
        totalStatisticsText.setText(text);
        setTotalGraph(totalContest);

        // 단과대 데이터
        try{
            Long a = (Long) major.get(info.getCollege()).get(0);
            Long b = (Long) major.get(info.getCollege()).get(1);

            double contest = a.doubleValue();
            double student = b.doubleValue();
            contest /= student;

            String number2 = String.format("%.1f vs %d",contest, Integer.parseInt(info.getContestParticipate()));
            String what2 = contest <= Integer.parseInt(info.getContestParticipate()) ? "많이" : "적게";
            String text2 = String.format("%s 수정이들보다 평균적으로 %s 참여했습니다",info.getCollege(),what2);
            collegeStatisticsNumber.setText(number2);
            collegeStatisticsText.setText(text2);

            setCollegeGraph(contest);
        } catch (NullPointerException e){
            System.out.println("no");
        }
        loaderLayout.setVisibility(View.INVISIBLE);

    }

    public void userParticipateQuery(){

        loaderLayout.setVisibility(View.VISIBLE);
        String userUid = user.getUid();

        TextView myPageUserText = view.findViewById(R.id.myPageUserText);
        TextView userInformationText = view.findViewById(R.id.userInformationText);

        DocumentReference docRef = db.collection("users").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("contestParticipate"));

                        info = document.toObject(StudentInfo.class);
                        myPageUserText.setText(info.getUserName() + "의 공모전 현황");
                        userInformationText.setText(info.getCollege() + " " + info.getDepartment() + " " + info.getStudentId()+"학번");
                    } else {
                        Log.d(TAG, "No such document");
                    }

                    DocumentReference docRef = db.collection("statistics").document("contestParticipateDocument");
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            major = (HashMap<String, ArrayList>) documentSnapshot.getData().get("major");
                            collegeStatisticQuery();
                        }
                    });
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setTotalGraph(final double average){

        //first pieChart
        pieChartTotal = (PieChart)view.findViewById(R.id.totalStatisticGraph);
        pieChartTotal.setUsePercentValues(false);
        pieChartTotal.getDescription().setEnabled(false);
        pieChartTotal.setExtraOffsets(5,10,5,5);
        pieChartTotal.setDragDecelerationFrictionCoef(0.95f);
        pieChartTotal.setDrawHoleEnabled(true);
        pieChartTotal.setHoleColor(Color.WHITE);
        pieChartTotal.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        yValues.add(new PieEntry(Integer.parseInt(info.getContestParticipate()),"My"));
        yValues.add(new PieEntry((int)average,"SSWU"));

        PieDataSet dataSet = new PieDataSet(yValues,"Count");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setLabel(null);

        PieData data = new PieData((dataSet));
        pieChartTotal.setData(data);
        pieChartTotal.invalidate();

    }

    private void setCollegeGraph(final double average){

        //second pieChart
        pieChartCollege = (PieChart)view.findViewById(R.id.collegeStatisticGraph);
        pieChartCollege.setUsePercentValues(false);
        pieChartCollege.getDescription().setEnabled(false);
        pieChartCollege.setExtraOffsets(5,10,5,5);
        pieChartCollege.setDragDecelerationFrictionCoef(0.95f);
        pieChartCollege.setDrawHoleEnabled(true);
        pieChartCollege.setHoleColor(Color.WHITE);
        pieChartCollege.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValuesSecond = new ArrayList<PieEntry>();
        yValuesSecond.add(new PieEntry(Integer.parseInt(info.getContestParticipate()),"My"));
        yValuesSecond.add(new PieEntry((int)(average),"College"));

        PieDataSet dataSetSecond = new PieDataSet(yValuesSecond,"Count");
        dataSetSecond.setSliceSpace(3f);
        dataSetSecond.setSelectionShift(5f);
        dataSetSecond.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSetSecond.setLabel(null);

        PieData dataSecond = new PieData((dataSetSecond));
        pieChartCollege.setData(dataSecond);
        pieChartCollege.invalidate();

    }

}