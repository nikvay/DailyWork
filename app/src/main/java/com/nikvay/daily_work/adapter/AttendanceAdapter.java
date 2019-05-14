package com.nikvay.daily_work.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nikvay.daily_work.R;
import com.nikvay.daily_work.activity.AttendanceActivity;
import com.nikvay.daily_work.activity.LeaveDetailsActivity;
import com.nikvay.daily_work.module.AttendanceModule;
import com.nikvay.daily_work.shared_pref.SharedPreference;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder>implements Filterable {
    Context mContext;
    private ArrayList<AttendanceModule> attendanceModuleArrayList;
    private ArrayList<AttendanceModule> arrayListFiltered;

   public AttendanceAdapter(Context mContext, ArrayList<AttendanceModule> attendanceModuleArrayList) {
        this.mContext = mContext;
        this.attendanceModuleArrayList = attendanceModuleArrayList;
        this.arrayListFiltered = attendanceModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_attendance_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String uType = SharedPreference.getUserType(mContext);
        if (uType.equals("0")) {
            holder.tv_attendance_ada_name.setVisibility(View.GONE);
        }

        final AttendanceModule attendanceModule = attendanceModuleArrayList.get(position);

        String name = attendanceModule.getName();
        String date = attendanceModule.getDate();
        String inTime = attendanceModule.getInTime();
        String outTime = attendanceModule.getOutTime();
        String location = attendanceModule.getLocation();

        holder.tv_attendance_ada_name.setText(name);
        holder.tv_attendance_ada_date.setText(date);
        holder.tv_attendance_ada_location.setText(location);
        holder.tv_attendance_ada_update_date.setText("Updated : "+date);
        holder.tv_attendance_ada_time.setText("In Time : " + inTime +" "+ "Out Time : " + outTime);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AttendanceActivity.class);
                Bundle b = new Bundle();
             //   b.putParcelable("ATTENDANCE_MODULE", attendanceModule);
                intent.putExtras(b); //pass bundle to your intent
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return attendanceModuleArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s","").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    attendanceModuleArrayList = arrayListFiltered;
                } else {
                    ArrayList<AttendanceModule> filteredList = new ArrayList<>();
                    for (int i = 0; i < attendanceModuleArrayList.size(); i++) {

                        String name=attendanceModuleArrayList.get(i).getName().replaceAll("\\s","").toLowerCase().trim();
                        if (name.contains(charString)) {
                            filteredList.add(attendanceModuleArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        attendanceModuleArrayList = filteredList;
                    } else {
                        attendanceModuleArrayList = arrayListFiltered;
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = attendanceModuleArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                attendanceModuleArrayList = (ArrayList<AttendanceModule>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_attendance_ada_name, tv_attendance_ada_date, tv_attendance_ada_time,tv_attendance_ada_location,tv_attendance_ada_update_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_attendance_ada_name = itemView.findViewById(R.id.tv_attendance_ada_name);
            tv_attendance_ada_date = itemView.findViewById(R.id.tv_attendance_ada_date);
            tv_attendance_ada_time = itemView.findViewById(R.id.tv_attendance_ada_time);
            tv_attendance_ada_location = itemView.findViewById(R.id.tv_attendance_ada_location);
            tv_attendance_ada_update_date = itemView.findViewById(R.id.tv_attendance_ada_update_date);
        }
    }
}
