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
import com.nikvay.daily_work.activity.DailyTaskActivity;
import com.nikvay.daily_work.activity.LeaveDetailsActivity;
import com.nikvay.daily_work.module.AttendanceModule;
import com.nikvay.daily_work.module.WorkModule;
import com.nikvay.daily_work.shared_pref.SharedPreference;

import java.util.ArrayList;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    private ArrayList<WorkModule> workModuleArrayList;
    private OnItemClickListener listener;
    private ArrayList<WorkModule> arrayListFiltered;


    public WorkAdapter(Context mContext, ArrayList<WorkModule> workModuleArrayList) {
        this.mContext = mContext;
        this.workModuleArrayList = workModuleArrayList;
        this.arrayListFiltered = workModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_work_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        String uType = SharedPreference.getUserType(mContext);
        if (uType.equals("0")) {
            holder.tv_work_name.setVisibility(View.GONE);
        }

        final WorkModule workModule = workModuleArrayList.get(position);

        String name = workModule.getName();
        String title = workModule.getMst_projectname();
        String desc = workModule.getMst_projecttask();
        String dateTime = workModule.getProject_datetime();

        holder.tv_work_name.setText("Name : " + name);
        holder.tv_work_title.setText("Project Name : " + title);
        holder.tv_work_desc.setText("Description : " + desc);
        holder.tv_task_date.setText("Updated : " + dateTime);

        //========== Adapter onClick() ===========
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DailyTaskActivity.class);
                Bundle b = new Bundle();
            //    b.putParcelable("LEAVE_MODULE", leaveModule);
                intent.putExtras(b); //pass bundle to your intent
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return workModuleArrayList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s","").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    workModuleArrayList = arrayListFiltered;
                } else {
                    ArrayList<WorkModule> filteredList = new ArrayList<>();
                    for (int i = 0; i < workModuleArrayList.size(); i++) {

                        String name=workModuleArrayList.get(i).getName().replaceAll("\\s","").toLowerCase().trim();
                        if (name.contains(charString)) {
                            filteredList.add(workModuleArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        workModuleArrayList = filteredList;
                    } else {
                        workModuleArrayList = arrayListFiltered;
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = workModuleArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                workModuleArrayList = (ArrayList<WorkModule>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_work_name, tv_work_title, tv_work_desc, tv_task_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_work_name = itemView.findViewById(R.id.tv_work_name);
            tv_work_title = itemView.findViewById(R.id.tv_work_title);
            tv_work_desc = itemView.findViewById(R.id.tv_work_desc);
            tv_task_date = itemView.findViewById(R.id.tv_task_date);
        }
    }

    //=====================================================
    public interface OnItemClickListener {
        void onAdapterClick(WorkModule workModule, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}