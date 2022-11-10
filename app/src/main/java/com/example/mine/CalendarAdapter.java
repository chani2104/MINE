package com.example.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.calendarviewHolder  > {
    ArrayList<String> dayList;

    public CalendarAdapter(ArrayList<String> dayList){
        this.dayList=dayList;
    }
    @NonNull
    @Override
    public calendarviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.calendar_cube, parent,false);

        return new calendarviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull calendarviewHolder holder, int position) {
        holder.dayText.setText(dayList.get(position));
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class  calendarviewHolder extends RecyclerView.ViewHolder {

        TextView dayText;

         public calendarviewHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(R.id.dayText);

         }
    }


}
