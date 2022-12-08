package com.example.mine;

import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mine.model.CalendarData;

import java.time.LocalDate;
import java.util.ArrayList;

public class UserDiaryAdapter extends RecyclerView.Adapter<UserDiaryAdapter.UserDiaryViewHolder> {

    ArrayList<CalendarData> dayList;
    private Consumer<Integer> onClickListener;

    public UserDiaryAdapter(ArrayList<CalendarData> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    //뷰홀더를 만들 때 호출되는 함수
    public UserDiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cube, parent, false);
        return new UserDiaryViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    //뷰홀더 안에서 어떤 작업을 할 것이냐
    public void onBindViewHolder(@NonNull UserDiaryViewHolder holder, int position) {

        CalendarData item = dayList.get(position);
        holder.itemView.setOnClickListener(null);

        //날짜 세팅
        if (item == null) {
            holder.dayText.setText("");

        } else {
            LocalDate day = item.localDate;
            holder.dayText.setText(String.valueOf(day.getDayOfMonth()));

            //오늘 날짜 카메라 이미지
            if (day.equals(CalendarUtil.selectedDate) && CalendarUtil.selectedYear == day.getYear() && (CalendarUtil.selectedMonth.equals(day.getMonth()))) {
                holder.cube_parentView.setBackgroundResource(R.drawable.camera);
                holder.dayText.setText("");

                holder.itemView.setOnClickListener(v -> {
                    if (onClickListener != null) {
                        onClickListener.accept(position);
                    }
                });
            }

            if (item.imageUri != null) {
                holder.dayImageView.setImageURI(item.imageUri);
            }
        }
    }

    //뷰홀더를 몇 개나 만들어서 RecyclerView에 넣을 것이냐
    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public void setImage(int position, Uri uri) {
        if (position < 0 || position >= dayList.size()) return;

        dayList.get(position).imageUri = uri;
        notifyItemChanged(position);
    }

    /*public void setOnItemClickListener(Consumer<Integer> listener) {
        this.onClickListener = listener;
    }*/




    public interface OnItemClickListener{
        void onItemClick(View v, UserDiaryData data);
    }

    private UserDiaryAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(UserDiaryAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }


    //ViewHolder를 만들어서 그 안에 있는 View들을 찾아주기
    static class UserDiaryViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        ImageView dayImageView;
        View cube_parentView;

        public UserDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            cube_parentView = itemView.findViewById(R.id.cube_parentView);
            dayImageView = itemView.findViewById(R.id.dayImage);
            dayText = itemView.findViewById(R.id.dayText);
        }
    }
}
