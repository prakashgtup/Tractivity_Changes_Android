package com.trac.android.tractivity.W2S.Model;

/**
 * Created by krishna on 6/28/2016.
 */
public class JobDetailList
{
    private String jobid,jobnumber,jobdescription,jobqulity,jobarea,jobstatus;
    private boolean selected;



    public String getJobid() {return jobid; }

    public String getJobnumber() {
        return jobnumber;
    }

    public String getJobdescription() {
        return jobdescription;
    }

    public String getJobqulity() {
        return jobqulity;
    }

    public String getJobarea() {
        return jobarea;
    }

    public String getJobstatus() {
        return jobstatus;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public void setJobdescription(String jobdescription) {
        this.jobdescription = jobdescription;
    }

    public void setJobqulity(String jobqulity) {
        this.jobqulity = jobqulity;
    }

    public void setJobarea(String jobarea) {
        this.jobarea = jobarea;
    }

    public void setJobstatus(String jobstatus) {
        this.jobstatus = jobstatus;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
