package com.example.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mine.model.CalendarData;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class CalendarFragment extends Fragment {
    private static final String ARG_YEAR_MONTH = "ARG_YEAR_MONTH";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static CalendarFragment newInstance(LocalDate yearMonth) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_YEAR_MONTH, yearMonth);
        fragment.setArguments(args);
        return fragment;
    }

    private LocalDate yearMonth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            yearMonth = (LocalDate) getArguments().getSerializable(ARG_YEAR_MONTH);
        }

        if (savedInstanceState != null) {
            yearMonth = (LocalDate) savedInstanceState.getSerializable(ARG_YEAR_MONTH);
        }

        assert (yearMonth != null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<CalendarData> dayList = daysInMonthArray();

        CalendarAdapter adapter = new CalendarAdapter(dayList);
        adapter.setOnItemClickListener(position -> {
            try {
                LocalDate selectedDate = adapter.dayList.get(position).localDate;

                //Intent intent = new Intent(requireContext(), UserDiary.class);
                Intent intent = new Intent(requireContext(), MultiImageActivity.class);
                intent.putExtra("localDate", selectedDate);
                System.out.println(selectedDate);
                startActivity(intent);
            } catch (Exception ignore) {
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7));
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<CalendarData> daysInMonthArray() {
        ArrayList<CalendarData> dayList = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(this.yearMonth);

        //마지막&첫날 날짜 가져오기
        int lastDay = yearMonth.lengthOfMonth();
        LocalDate firstDay = this.yearMonth.withDayOfMonth(1);

        //첫날 요일 월1~일7
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        //날짜 생성
        for (int i = 1; i < 42; i++) {
            if (i <= dayOfWeek || i > lastDay + dayOfWeek) {
                dayList.add(null);
            } else {
                LocalDate day = LocalDate.of(this.yearMonth.getYear(), this.yearMonth.getMonth(), i - dayOfWeek);
                dayList.add(new CalendarData(day));
            }
        }
        return dayList;
    }
}