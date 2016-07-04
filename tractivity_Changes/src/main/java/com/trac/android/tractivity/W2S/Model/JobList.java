package com.trac.android.tractivity.W2S.Model;

/**
 * Created by krishna on 6/27/2016.
 */
public class JobList
{
    private String jobname;
    private String jobitem;
    private String jobstages;
    private String jobDate;
//    public JobList(String jobname, String jobitem, String jobDate)
//    {
//        this.jobname = jobname;
//        this.jobitem = jobitem;
//        this.jobstages = jobstages;
//        this.jobDate=jobDate;
//    }
    public String getJobname()
    {
        return this.jobname;
    }
    public String getJobItem()
    {
        return this.jobitem;
    }
    public String getJobstages()
    {
        return this.jobstages;
    }
    public String getJobDate()
    {
        return this.jobDate;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public void setJobitem(String jobitem) {
        this.jobitem = jobitem;
    }

    public void setJobstages(String jobstages) {
        this.jobstages = jobstages;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }
}
