package com.example.weatherforcast.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weatherforcast.R;
import com.example.weatherforcast.model.Note;
import com.example.weatherforcast.model.main;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {


    private List<main> categoryList = new ArrayList<>();
    private Context mContext;
    String ori_type;
    onItemClick listner;

    public NoteAdapter(Context context, List<main> categoryList) {
        this.categoryList = categoryList;
        mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_pressure)
        TextView tvPressure;
        @BindView(R.id.tv_humidity)
        TextView tvHumidity;
        @BindView(R.id.tv_speed)
        TextView tvSpeed;
        @BindView(R.id.tv_deg)
        TextView tvDeg;
        @BindView(R.id.tv_clouds)
        TextView tvClouds;
        @BindView(R.id.tv_temp)
        TextView tvTemp;
        @BindView(R.id.tv_weather)
        TextView tvWeather;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final main gsCategory = categoryList.get(position);
        holder.tvDate.setText(convertDate(gsCategory.getDate(),
                "dd/MM/yyyy"));
        holder.tvDate.setVisibility(View.GONE);
        holder.tvPressure.setText("Pressure : " + gsCategory.getPressure());
        holder.tvHumidity.setText("Humidity : " + gsCategory.getHumidity());
        holder.tvSpeed.setText("Speed : " + gsCategory.getSpeed());
        holder.tvDeg.setText("Degree : " + gsCategory.getDeg());
        holder.tvClouds.setText("Clouds : " + gsCategory.getClouds());
        holder.tvTemp.setText("day : " + gsCategory.getTemp().getDay() + " min : " + gsCategory.getTemp().getMin() + " max : " +
                gsCategory.getTemp().getMax() + " night : " + gsCategory.getTemp().getNight() + " eve : " + gsCategory.getTemp().getEve()
                + " morn : " + gsCategory.getTemp().getMorn());
        holder.tvWeather.setText(gsCategory.getWeather().getDesc());
        holder.tvClouds.setText(gsCategory.getClouds());
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public interface onItemClick {
        void onItemClicked(Note note);
    }

    public void setOnItemClick(onItemClick listner) {
        this.listner = listner;
    }
}