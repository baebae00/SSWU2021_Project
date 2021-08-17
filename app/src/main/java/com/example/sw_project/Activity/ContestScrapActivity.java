package com.example.sw_project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw_project.BackPressCloseHandler;
import com.example.sw_project.ContestInfo;
import com.example.sw_project.R;
import com.example.sw_project.ScrapInfo;
import com.example.sw_project.adapter.ContestScrapAdapter;
import com.example.sw_project.fragment.Fragment_Tab_3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.StringTokenizer;

public class ContestScrapActivity extends Fragment {

    View view;

    public static ContestScrapActivity newInstance() {
        return new ContestScrapActivity();
    }

    private Activity activity;
    private MaterialCalendarView calendarView;
    public RelativeLayout layout;
    public RecyclerView recycleView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;
    private BackPressCloseHandler backPress;

    public FirebaseUser user;
    public FirebaseFirestore db;
    public static final String TAG = "ContestScrapActivity";
    public ArrayList<String> temporaryList;
    public ArrayList<CalendarDay> scrapContestDateList;
    public ArrayList<ContestInfo> scrapContestList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_my_page_contest_scrap,container,false);

        view.findViewById(R.id.showCalendarButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.showListButton).setOnClickListener(onClickListener);

        calendarView = view.findViewById(R.id.calendarMaterial);
        recycleView = view.findViewById(R.id.contestListView);
        calendarView.setVisibility(View.INVISIBLE);
        layout = view.findViewById(R.id.listContainLayout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getDataLoad();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int year, month, day;
                year = date.getYear() - 2000;
                month = date.getMonth()+1;
                day = date.getDay();
                String value = String.format("%d-%02d-%02d",year,month,day);
                String dateTitle = month+"월 "+day+"일";

                mOnPopupClick(value, dateTitle);

            }
        });

        backPress = new BackPressCloseHandler(getActivity());
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                ((MainActivity)getActivity()).replaceFragment(Fragment_Tab_3.newInstance(), "Fragment_Tab_3");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return view;
    }

    public void initRecycler(){

        calendarView.removeDecorators();
        calendarView.addDecorators(new SundayRed(), new SaturdayBlue());
        layoutManager = new LinearLayoutManager(activity);
        recycleView.setLayoutManager(layoutManager);
        mAdapter = new ContestScrapAdapter(new ArrayList<ContestInfo>(), getActivity());
        recycleView.setAdapter(mAdapter);
    }

    public void getDataLoad(){

        initRecycler();

        db.collection("scrapContest")
                .whereEqualTo("scrapUserUid",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<ScrapInfo> scrapArray = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                scrapArray.add(document.toObject(ScrapInfo.class));
                            }

                            if(scrapArray.size() == 0)
                                view.findViewById(R.id.isContestZeroText).setVisibility(View.VISIBLE);
                            else
                                view.findViewById(R.id.isContestZeroText).setVisibility(View.INVISIBLE);

                            // contestId에 해당하는 contest 불러오기
                            temporaryList = new ArrayList<>();
                            scrapContestList = new ArrayList<>();

                            for(ScrapInfo findPost : scrapArray){
                                DocumentReference docRef = db.collection("contests").document(findPost.getContestId());
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        scrapContestList.add(documentSnapshot.toObject(ContestInfo.class));
                                        try {
                                            temporaryList.add(documentSnapshot.getData().get("endDate").toString());
                                        }catch (NullPointerException e){
                                        }
                                        if(scrapContestList.size() == scrapArray.size()) {
                                            convertDateType();
                                            recycleView.setHasFixedSize(true);
                                            layoutManager = new LinearLayoutManager(activity);
                                            recycleView.setLayoutManager(layoutManager);
                                            mAdapter = new ContestScrapAdapter(scrapContestList, getActivity());
                                            recycleView.setAdapter(mAdapter);
                                        }
                                    }
                                });
                            }

                        }else {
                            Log.w(TAG, "Error => ", task.getException());
                        }
                    }
                });
    }

    public void mOnPopupClick(String value, String dateTitle){

        ArrayList<ContestInfo> dateSameList = new ArrayList<>();

        for(ContestInfo contest : scrapContestList){
            if(value.equals(contest.getEndDate()))
                dateSameList.add(contest);
        }

        Intent intent = new Intent(getActivity(), PopUpActivity.class);
        intent.putExtra("contestDate", value);
        intent.putExtra("dateTextTitle", dateTitle);
        intent.putExtra("list",dateSameList);
        startActivity(intent);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.showCalendarButton:
                    calendarView.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                    break;
                case R.id.showListButton:
                    layout.setVisibility(View.VISIBLE);
                    calendarView.setVisibility(View.INVISIBLE);
                    break;
            }

        }
    };

    public void convertDateType(){

        String[] dateStorage = new String[3];
        scrapContestDateList = new ArrayList<>();

        for (String string : temporaryList) {
            StringTokenizer skn = new StringTokenizer(string, "-");
            for (int i = 0; skn.hasMoreTokens(); i++) {
                dateStorage[i] = skn.nextToken();
            }
            CalendarDay contestDay = CalendarDay.from(Integer.parseInt("20"+dateStorage[0])
                    , Integer.parseInt(dateStorage[1]) - 1, Integer.parseInt(dateStorage[2]));
            scrapContestDateList.add(contestDay);
        }
        calendarView.addDecorator(new ContestEndLineDayDecorate(scrapContestDateList));
    }

    class SaturdayBlue implements DayViewDecorator {
        private final Calendar calendar = Calendar.getInstance();

        public SaturdayBlue(){
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);

            return weekDay == calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    class SundayRed implements DayViewDecorator{
        private final Calendar calendar = Calendar.getInstance();

        public SundayRed(){
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    class ContestEndLineDayDecorate implements DayViewDecorator{

        private final ArrayList<CalendarDay> dates;

        public ContestEndLineDayDecorate(Collection<CalendarDay> dates) {
            this.dates = new ArrayList<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(15,Color.parseColor("#6B6FAE")));
        }
    }


}
