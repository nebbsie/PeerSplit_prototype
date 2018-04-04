package com.aaronnebbs.peersplitandroidapplication.Model;

public class Job {

    private JobType job;
    private String data;
    private String senderID;
    private String fileID;

    public Job(){}

    public Job(JobType job, String data, String senderID, String fileID){
        this.job = job;
        this.data = data;
        this.senderID = senderID;
        this.fileID = fileID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
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
