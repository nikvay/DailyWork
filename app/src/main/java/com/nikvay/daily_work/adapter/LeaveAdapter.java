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
import com.nikvay.daily_work.activity.LeaveDetailsActivity;
import com.nikvay.daily_work.module.LeaveModule;
import com.nikvay.daily_work.module.WorkModule;
import com.nikvay.daily_work.shared_pref.SharedPreference;

import java.util.ArrayList;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<LeaveModule> leaveModuleArrayList;
    private ArrayList<LeaveModule> arrayListFiltered;

    public LeaveAdapter(Context mContext, ArrayList<LeaveModule> leaveModuleArrayList) {
        this.mContext = mContext;
        this.leaveModuleArrayList = leaveModuleArrayList;
        this.arrayListFiltered = leaveModuleArrayList;
    }

    public void removeItem(int position) {
        leaveModuleArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(LeaveModule item, int position) {
        leaveModuleArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<LeaveModule> getData() {
        return leaveModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_leave_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String uType = SharedPreference.getUserType(mContext);
        if (uType.equals("0")) {
            holder.tv_leave_adapter_name.setVisibility(View.GONE);
        }

        final LeaveModule leaveModule = leaveModuleArrayList.get(position);

        String name = leaveModule.getName();
        String title = leaveModule.getTitle();
        String startDate = leaveModule.getStartDate();
        String endDate = leaveModule.getEndDate();
        String status = leaveModule.getStatus();

        holder.tv_leave_adapter_name.setText(name);
        holder.tv_leave_adapter_status.setText(status);
        holder.tv_leave_adapter_title.setText(title);
        holder.tv_leave_adapter_date.setText(startDate + "  Up to  " + endDate);
        holder.tv_leave_adapter_cDate.setText("20-04-2019");

//        SharedPreferences sharedpreferences = mContext.getSharedPreferences("Daily Work", MODE_PRIVATE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LeaveDetailsActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("LEAVE_MODULE", leaveModule);
                intent.putExtras(b); //pass bundle to your intent
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaveModuleArrayList.size();
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s","").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    leaveModuleArrayList = arrayListFiltered;
                } else {
                    ArrayList<LeaveModule> filteredList = new ArrayList<>();
                    for (int i = 0; i < leaveModuleArrayList.size(); i++) {

                        String name=leaveModuleArrayList.get(i).getName().replaceAll("\\s","").toLowerCase().trim();
                        if (name.contains(charString)) {
                            filteredList.add(leaveModuleArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        leaveModuleArrayList = filteredList;
                    } else {
                        leaveModuleArrayList = arrayListFiltered;
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = leaveModuleArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                leaveModuleArrayList = (ArrayList<LeaveModule>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_leave_adapter_name, tv_leave_adapter_status, tv_leave_adapter_title, tv_leave_adapter_date, tv_leave_adapter_cDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_leave_adapter_name = itemView.findViewById(R.id.tv_leave_adapter_name);
            tv_leave_adapter_status = itemView.findViewById(R.id.tv_leave_adapter_status);
            tv_leave_adapter_title = itemView.findViewById(R.id.tv_leave_adapter_title);
            tv_leave_adapter_date = itemView.findViewById(R.id.tv_leave_adapter_date);
            tv_leave_adapter_cDate = itemView.findViewById(R.id.tv_leave_adapter_cDate);
        }
    }
}
