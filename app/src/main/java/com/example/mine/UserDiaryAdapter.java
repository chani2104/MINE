/*
package com.example.mine;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.example.mine.model.CalendarData;

import java.time.LocalDate;
import java.util.ArrayList;

public class UserDiaryAdapter extends RecyclerView.Adapter<UserDiaryAdapter.ViewHolder> {

    private ArrayList<Uri> mData = null;
    private Context mContext = null;

    public UserDiaryAdapter(ArrayList<Uri> list, Context context) {
        mData = list;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조.
            image = itemView.findViewById(R.id.image);
        }
    }
    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    // LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
    @Override
    public UserDiaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.user_diary_form, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        UserDiaryAdapter.ViewHolder vh = new UserDiaryAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(UserDiaryAdapter.ViewHolder holder, int position) {
        Uri image_uri = mData.get(position) ;

        GlideApp.with(mContext)
                .load(image_uri)
                .into(holder.image);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

}
















*/
/*

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

        *//*

*/
/*//*
/*
/날짜 세팅
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
        }*//*
*/
/*

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

    *//*

*/
/*public void setOnItemClickListener(Consumer<Integer> listener) {
        this.onClickListener = listener;
    }*//*
*/
/*





    public interface OnItemClickListener{
        void onItemClick(View v, UserDiaryData data);
    }

    private UserDiaryAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(UserDiaryAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }


    //ViewHolder를 만들어서 그 안에 있는 View들을 찾아주기
    static class UserDiaryViewHolder extends RecyclerView.ViewHolder {
        TextView diaryText;
        ImageView diaryImageView;

        public UserDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            diaryImageView = itemView.findViewById(R.id.diary_imageView);
            diaryText = itemView.findViewById(R.id.dayText);
        }
    }
}
*/

