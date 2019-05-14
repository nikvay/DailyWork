package com.nikvay.daily_work.module;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SuccessModule {
    private String msg;
    private String error_code;


    @SerializedName("user_details")
    ArrayList<LoginModule> loginModuleArrayList;

    @SerializedName("task_list")
    ArrayList<WorkModule> workModuleArrayList;

    @SerializedName("leave_list")
    ArrayList<LeaveModule> leaveModuleArrayList;

    @SerializedName("Attendence_list")
    ArrayList<AttendanceModule> attendanceModuleArrayList;

    public ArrayList<AttendanceModule> getAttendanceModuleArrayList() {
        return attendanceModuleArrayList;
    }

    public void setAttendanceModuleArrayList(ArrayList<AttendanceModule> attendanceModuleArrayList) {
        this.attendanceModuleArrayList = attendanceModuleArrayList;
    }

    public ArrayList<LeaveModule> getLeaveModuleArrayList() {
        return leaveModuleArrayList;
    }

    public void setLeaveModuleArrayList(ArrayList<LeaveModule> leaveModuleArrayList) {
        this.leaveModuleArrayList = leaveModuleArrayList;
    }

    public ArrayList<WorkModule> getWorkModuleArrayList() {
        return workModuleArrayList;
    }

    public void setWorkModuleArrayList(ArrayList<WorkModule> workModuleArrayList) {
        this.workModuleArrayList = workModuleArrayList;
    }

    public ArrayList<LoginModule> getLoginModuleArrayList() {
        return loginModuleArrayList;
    }

    public void setLoginModuleArrayList(ArrayList<LoginModule> loginModuleArrayList) {
        this.loginModuleArrayList = loginModuleArrayList;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }
}
