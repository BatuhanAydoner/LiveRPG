package com.moonturns.liverpg.DatabaseModel;

//Plans that user maked
public class UserPlans {

    private String calendar;
    private String finish_calendar;
    private String time;
    private String finish_time;
    private String text_content;
    private String xp;
    private String plan_id;
    private String kind_of_plan;

    public UserPlans() {
    }

    public UserPlans(String calendar, String finish_calendar, String time, String finish_time, String text_content, String xp, String plan_id, String kind_of_plan) {
        this.calendar = calendar;
        this.finish_calendar = finish_calendar;
        this.time = time;
        this.finish_time = finish_time;
        this.text_content = text_content;
        this.xp = xp;
        this.plan_id = plan_id;
        this.kind_of_plan = kind_of_plan;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public String getFinish_calendar() {
        return finish_calendar;
    }

    public void setFinish_calendar(String finish_calendar) {
        this.finish_calendar = finish_calendar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText_content() {
        return text_content;
    }

    public String getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(String finish_time) {
        this.finish_time = finish_time;
    }

    public void setText_content(String text_content) {
        this.text_content = text_content;
    }

    public String getXp() {
        return xp;
    }

    public void setXp(String xp) {
        this.xp = xp;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getKind_of_plan() {
        return kind_of_plan;
    }

    public void setKind_of_plan(String kind_of_plan) {
        this.kind_of_plan = kind_of_plan;
    }
}
