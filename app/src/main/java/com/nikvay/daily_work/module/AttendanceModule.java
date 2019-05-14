package com.nikvay.daily_work.module;

public class AttendanceModule {
    private String name;
    private String date;
    private String inTime;
    private String location;
    private String type;
    private String atd_id;
    private String outTime;
    private String emp_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getInTime ()
    {
        return inTime;
    }

    public void setInTime (String inTime)
    {
        this.inTime = inTime;
    }

    public String getLocation ()
    {
        return location;
    }

    public void setLocation (String location)
    {
        this.location = location;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getAtd_id ()
    {
        return atd_id;
    }

    public void setAtd_id (String atd_id)
    {
        this.atd_id = atd_id;
    }

    public String getOutTime ()
    {
        return outTime;
    }

    public void setOutTime (String outTime)
    {
        this.outTime = outTime;
    }

    public String getEmp_id ()
    {
        return emp_id;
    }

    public void setEmp_id (String emp_id)
    {
        this.emp_id = emp_id;
    }

}
