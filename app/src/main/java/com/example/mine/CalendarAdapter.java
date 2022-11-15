package com.example.mine;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.calendarviewHolder> {
    ArrayList<LocalDate> dayList;

    public CalendarAdapter(ArrayList<LocalDate> dayList){
        this.dayList=dayList;
    }
    @NonNull
    @Override
    public calendarviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.calendar_cube, parent,false);

        return new calendarviewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull calendarviewHolder holder, int position) {

        LocalDate day = dayList.get(position);

//날짜 세팅
        if (day == null) {
            holder.dayText.setText("");
        } else {
            holder.dayText.setText(String.valueOf(day.getDayOfMonth()));

            //오늘 날짜 카메라 이미지
            if (day.equals(CalendarUtil.selectedDate)&&CalendarUtil.selectedYear==day.getYear()&&(CalendarUtil.selectedMonth.equals(day.getMonth()))){
                holder.cube_parentView.setBackgroundResource(R.drawable.camera);
                holder.dayText.setText("");
            }
        }

  /* holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //카메라 연동
        }
    });*/
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class  calendarviewHolder extends RecyclerView.ViewHolder {

        TextView dayText;
        View cube_parentView;
         public calendarviewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            cube_parentView=itemView.findViewById(R.id.cube_parentView);

         }
    }


}
