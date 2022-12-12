package com.example.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Calendar extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewPager2 viewPager;
    private TextView monthYearText;

    private AppPassWordActivity appPassWordActivity;
    static boolean isPassword = false;
    static boolean lock = true;
    static boolean login = true;
    private long pressedTime = 0;
    SharedPreferences sharedPref = LogInActivity.context_login.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        appPassWordActivity = new AppPassWordActivity();
        if (login) {
            // 잠금설정
            isPassLock();
        }
        onInit();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onInit() {
        monthYearText = findViewById(R.id.monthYearText);
        ImageButton set = findViewById(R.id.setting);
        viewPager = findViewById(R.id.view_pager);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                try {
                    LocalDate yearMonth = ((CalendarViewPagerAdapter) Objects.requireNonNull(viewPager.getAdapter()))
                            .yearMonthList
                            .get(position);
                    setMonthView(yearMonth);
                } catch (Exception ignore) {
                }
            }
        });

        // 달력이 깜빡거리는게 싫으시면 아래 코드를 주석 처리 하시면 됩니다.
        // 아래 코드 주석처리시, Firestore 에서 데이터를 읽기 전까지는 화면에 달력이 표시되지 않습니다.
        viewPager.setAdapter(new CalendarViewPagerAdapter(this, Collections.singletonList(LocalDate.now())));

        String id = sharedPref.getString("inputID", "");

        db.collection("user_info")
                .document(id)
                .get()
                .addOnCompleteListener(this, task -> {
                    LocalDate now = LocalDate.now();
                    LocalDate oldestDate = now;

                    if (task.getException() != null) {
                        task.getException().printStackTrace();
                    }

                    try {
                        String source = task.getResult().getString("가입날짜");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.KOREA);
                        Date date = dateFormat.parse(source);
                        oldestDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    ArrayList<LocalDate> yearMonthList = new ArrayList<>();
                    LocalDate start = LocalDate.of(oldestDate.getYear(), oldestDate.getMonth(), 1);
                    LocalDate end = LocalDate.of(now.getYear(), now.getMonth(), 1);
                    while (!start.isAfter(end)) {
                        yearMonthList.add(start);
                        // Log.d("Calendar", start.toString());

                        start = start.plusMonths(1);
                    }

                    viewPager.setAdapter(new CalendarViewPagerAdapter(this, yearMonthList));
                    viewPager.setCurrentItem(yearMonthList.size() - 1, false);
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView(LocalDate yearMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM월");
        monthYearText.setText(yearMonth.format(formatter));
    }

    // 잠금기능
    protected boolean isPassLock() {
        System.out.println("///////////////////////////////////");
        appPassWordActivity.database(4, null);
        return isPassword;
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("login : "+login);
        System.out.println("lock : "+lock);
        System.out.println("isPassword : "+isPassword);
        if (login) {
            if (lock && isPassLock()) {
                Intent intent = new Intent(this, AppPassWordActivity.class);
                intent.putExtra(AppLockConst.TYPE, AppLockConst.UNLOCK_PASSWORD);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (login) {
            if (isPassLock()) {
                lock = true;
            }
        }
    }



    private static class CalendarViewPagerAdapter extends FragmentStateAdapter {
        private final List<LocalDate> yearMonthList;

        public CalendarViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                        List<LocalDate> yearMonthList) {
            super(fragmentActivity);
            this.yearMonthList = yearMonthList;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return CalendarFragment.newInstance(yearMonthList.get(position));
        }

        @Override
        public int getItemCount() {
            return yearMonthList.size();
        }
    }

    @Override
    public void onBackPressed() {
        if ( pressedTime == 0 ) {
            Toast.makeText(Calendar.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
            pressedTime = System.currentTimeMillis();
        }
        else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if ( seconds > 2000 ) {
                Toast.makeText(Calendar.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
                pressedTime = 0 ;
            }
            else {
                finish(); // app 종료 시키기
            }
        }
    }

}