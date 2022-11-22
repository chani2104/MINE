package com.example.mine;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Calendar extends AppCompatActivity {

  //  ImageView photo;
    private FirebaseStorage storage;

    TextView monthYearText;
    RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        storage= FirebaseStorage.getInstance();
        monthYearText = findViewById(R.id.monthYearText);
        ImageButton album = findViewById(R.id.album);
        ImageButton set = findViewById(R.id.setting);
        recyclerView = findViewById(R.id.recycle_view);

        //현재 날짜
        CalendarUtil.selectedDate = LocalDate.now();
        CalendarUtil.selectedYear = LocalDate.now().getYear();
        CalendarUtil.selectedMonth =LocalDate.now().getMonth();
        setMonthview();
        ///스와이프 화면 전환
        recyclerView.setItemAnimator(null);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override public boolean isLongPressDragEnabled(){
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1);
                    setMonthview();
                } else if (direction == ItemTouchHelper.LEFT) {
                    CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1);
                    setMonthview();
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    //연도, 월 출력
    @RequiresApi(api = Build.VERSION_CODES.O)
    private  String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy MM월");
        return date.format(formatter);
    }

    //화면 세팅
    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void setMonthview(){

        //년월 텍스트뷰
        monthYearText.setText(monthYearFromDate(CalendarUtil.selectedDate));
        //월 가져옴
        ArrayList<LocalDate> dayList = daysInMonthArray(CalendarUtil.selectedDate);

        CalendarAdapter adapter = new CalendarAdapter(dayList);

        // 일~월 열 레이아웃
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(),7);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> dayList = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        //마지막&첫날 날짜 가져오기
        int lastDay = yearMonth.lengthOfMonth();
        LocalDate firstDay = CalendarUtil.selectedDate.withDayOfMonth(1);

        //첫날 요일 월1~일7
        int dayofweek = firstDay.getDayOfWeek().getValue();
        //날짜 생성
        for (int i = 1; i < 42; i++) {
            if (i <= dayofweek || i > lastDay + dayofweek) {
                dayList.add(null);
            } else {
                dayList.add(LocalDate.of(CalendarUtil.selectedDate.getYear(),CalendarUtil.selectedDate.getMonth(),i - dayofweek));
            }
        }
        return dayList;
    }

}