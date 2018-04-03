package com.aaronnebbs.peersplitandroidapplication.Helpers;

import com.aaronnebbs.peersplitandroidapplication.Model.Job;
import com.aaronnebbs.peersplitandroidapplication.Model.JobType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class JobHelper {

    private static DatabaseReference jobReference;

    public static void setup() {
        jobReference = UserManager.database.getReference().child("jobs");
    }

    public static void addJob(JobType jt, String data, String userID){
        jobReference.child(userID).push().setValue(new Job(jt, data));
    }

    public static void setupJobListener() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                jobReference.child(UserManager.user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot job : dataSnapshot.getChildren()) {
                            Job j = job.getValue(Job.class);
                            // Do upload value
                            if(j.getJob() == JobType.UPLOAD_CHUNK){
                                doJob(j, job.getKey());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        t.start();
    }

    private static void doJob(Job job, String jobID){
        jobReference.child(UserManager.user.getUid()).child(jobID).removeValue();
        System.out.println("Doing job: " + job.getJob());

        if (job.getJob() == JobType.UPLOAD_CHUNK) {

        }

    }

}
