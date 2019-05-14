package com.nikvay.daily_work.module;

import android.os.Parcel;
import android.os.Parcelable;

public class LeaveModule implements Parcelable {
    private String name;
    private String endDate;
    private String mst_leaveid;
    private String remark;
    private String title;
    private String startDate;
    private String emp_id;
    private String desc;
    private String status;

    protected LeaveModule(Parcel in) {
        name = in.readString();
        endDate = in.readString();
        mst_leaveid = in.readString();
        remark = in.readString();
        title = in.readString();
        startDate = in.readString();
        emp_id = in.readString();
        desc = in.readString();
        status = in.readString();
    }

    public static final Creator<LeaveModule> CREATOR = new Creator<LeaveModule>() {
        @Override
        public LeaveModule createFromParcel(Parcel in) {
            return new LeaveModule(in);
        }

        @Override
        public LeaveModule[] newArray(int size) {
            return new LeaveModule[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMst_leaveid() {
        return mst_leaveid;
    }

    public void setMst_leaveid(String mst_leaveid) {
        this.mst_leaveid = mst_leaveid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(endDate);
        dest.writeString(mst_leaveid);
        dest.writeString(remark);
        dest.writeString(title);
        dest.writeString(startDate);
        dest.writeString(emp_id);
        dest.writeString(desc);
        dest.writeString(status);
    }
}
