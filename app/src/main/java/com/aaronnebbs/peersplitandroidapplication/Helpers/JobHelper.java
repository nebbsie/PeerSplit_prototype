package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.Confirm;
import com.aaronnebbs.peersplitandroidapplication.Model.Job;
import com.aaronnebbs.peersplitandroidapplication.Model.JobType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobHelper {

    private static DatabaseReference jobReference;
    public static DatabaseReference confirmReference;

    public static void setup() {
        jobReference = UserManager.database.getReference().child("jobs");
        confirmReference = UserManager.database.getReference().child("confirm");
    }

    public static void addJob(JobType jt, String data, String userID, String fileID){
        System.out.println("Setting Job: " + jt + " Data: " + data + " For: "  + userID);
        jobReference.child(userID).push().setValue(new Job(jt, data, UserManager.user.getUid(), fileID));
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
                            doJob(j, job.getKey());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError){}
                });
            }
        });
        t.start();
    }

    // Takes in a job and performs the action.
    private static void doJob(final Job job, final String jobID){
        System.out.println("Doing job: " + job.getJob() + " " + job.getData());

        // Do the upload job.
        if (job.getJob() == JobType.UPLOAD_CHUNK) {
            // Get the chunk from memory.
            final ChunkFile f = ChunkHelper.getChunk(job.getData());
            // If the chunk is valid, attempt to upload.
            if(f!=null){
                ChunkHelper.uploadChunk(f).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        System.out.println("Uploaded Chunk: " + f.getOriginalname() + "  Sizeof: " + FileHelper.getFileSizeString(f.getFile()));
                        confirmReference.child(job.getSenderID()).child(job.getFileID()).push().setValue(new Confirm(f.getOriginalname(), UserManager.user.getUid()));
                        jobReference.child(UserManager.user.getUid()).child(jobID).removeValue();
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { }
                });

            }else{
                System.out.println("No chunk found");
            }
        }

    }

}
