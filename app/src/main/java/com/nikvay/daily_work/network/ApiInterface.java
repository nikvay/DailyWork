package com.nikvay.daily_work.network;

import com.nikvay.daily_work.module.SuccessModule;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST(EndApi.LOGIN)
    @FormUrlEncoded
    Call<SuccessModule> loginCall(@Field("name") String name,
                                  @Field("password") String password);

    @POST(EndApi.TASK_LIST)
    @FormUrlEncoded
    Call<SuccessModule> taskListCall(@Field("mst_empid") String mst_empid,
                                     @Field("loginType") String loginType);

    @POST(EndApi.TASK_ADD)
    @FormUrlEncoded
    Call<SuccessModule> taskAddCall(@Field("mst_empid") String mst_empid,
                                    @Field("mst_projectname") String mst_projectname,
                                    @Field("mst_projecttask") String mst_projecttask,
                                    @Field("project_datetime") String project_datetime);

    @POST(EndApi.LEAVE_ADD)
    @FormUrlEncoded
    Call<SuccessModule> leaveAddCall(@Field("emp_id") String emp_id,
                                     @Field("title") String title,
                                     @Field("desc") String desc,
                                     @Field("startDate") String startDate,
                                     @Field("endDate") String endDate,
                                     @Field("status") String status,
                                     @Field("remark") String remark);

    @POST(EndApi.LEAVE_LIST)
    @FormUrlEncoded
    Call<SuccessModule> leaveListCall(@Field("emp_id") String emp_id,
                                      @Field("loginType") String loginType);

    @POST(EndApi.LEAVE_UPDATE)
    @FormUrlEncoded
    Call<SuccessModule> leaveUpdateCall(@Field("emp_id") String emp_id,
                                        @Field("mst_leaveid") String mst_leaveid,
                                        @Field("startDate") String startDate,
                                        @Field("endDate") String endDate,
                                        @Field("status") String status,
                                        @Field("remark") String remark);

    @POST(EndApi.ATTENDANCE_ADD)
    @FormUrlEncoded
    Call<SuccessModule> attendanceAddCall(@Field("emp_id") String emp_id,
                                          @Field("location") String location,
                                          @Field("inTime") String inTime,
                                          @Field("outTime") String outTime,
                                          @Field("date") String date,
                                          @Field("type") String type);
   @POST(EndApi.ATTENDANCE_List)
    @FormUrlEncoded
    Call<SuccessModule> attendanceListCall(@Field("emp_id") String emp_id,
                                           @Field("loginType") String loginType);
}
