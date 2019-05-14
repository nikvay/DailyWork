package com.nikvay.daily_work.module;

public class WorkModule {
    private String name;
    private String mst_dailyid;
    private String mst_projectname;
    private String mst_empid;
    private String project_datetime;
    private String mst_projecttask;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMst_dailyid() {
        return mst_dailyid;
    }

    public void setMst_dailyid(String mst_dailyid) {
        this.mst_dailyid = mst_dailyid;
    }

    public String getMst_projectname() {
        return mst_projectname;
    }

    public void setMst_projectname(String mst_projectname) {
        this.mst_projectname = mst_projectname;
    }

    public String getMst_empid() {
        return mst_empid;
    }

    public void setMst_empid(String mst_empid) {
        this.mst_empid = mst_empid;
    }

    public String getProject_datetime() {
        return project_datetime;
    }

    public void setProject_datetime(String project_datetime) {
        this.project_datetime = project_datetime;
    }

    public String getMst_projecttask() {
        return mst_projecttask;
    }

    public void setMst_projecttask(String mst_projecttask) {
        this.mst_projecttask = mst_projecttask;
    }
}
