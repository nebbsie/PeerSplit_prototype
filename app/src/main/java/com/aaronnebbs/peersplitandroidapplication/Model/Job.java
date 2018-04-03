package com.aaronnebbs.peersplitandroidapplication.Model;

public class Job {

    private JobType job;
    private String data;

    public Job(){}

    public Job(JobType job, String data){
        this.job = job;
        this.data = data;
    }

    public JobType getJob() {
        return job;
    }

    public void setJob(JobType job) {
        this.job = job;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
