package com.aaronnebbs.peersplitandroidapplication.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.CryptoHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.JobHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.BottomNavBarAdapter;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkLink;
import com.aaronnebbs.peersplitandroidapplication.Model.Confirm;
import com.aaronnebbs.peersplitandroidapplication.Model.HomePageRow;
import com.aaronnebbs.peersplitandroidapplication.Model.JobType;
import com.aaronnebbs.peersplitandroidapplication.Model.PSFile;
import com.aaronnebbs.peersplitandroidapplication.Model.PrivateKeyPair;
import com.aaronnebbs.peersplitandroidapplication.Model.User;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

public class HomeFragment extends Fragment {

    private ArrayList<HomePageRow> dataModels;
    private ListView listView;
    private static BottomNavBarAdapter adapter;
    private boolean downloadCheck;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = getView().findViewById(R.id.home_listview);
        dataModels = new ArrayList<>();
        adapter = new BottomNavBarAdapter(dataModels, getContext());
        listView.setAdapter(adapter);

        // Retrieve all of the files that the user has uploaded to the cloud.
        getAllFilesBeingStored();

        // On lick listener for the files on the home page.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomePageRow dataModel= dataModels.get(position);
                downloadFile(dataModel);
            }
        });
    }

    // Sets a job in Firebase to tell device to upload chunk.
    private void setJobList(final ArrayList<ChunkLink> chunkLinks){
        System.out.println("Attempting to download " + chunkLinks.size() + " chunks.");
        for (ChunkLink link : chunkLinks) {
            JobHelper.addJob(JobType.UPLOAD_CHUNK, link.getChunkName(), link.getUserID(), link.getFileID());
        }

        downloadCheck = false;

        final ArrayList<String> chunksGot = new ArrayList<>();

        JobHelper.confirmReference.child(UserManager.user.getUid()).child(chunkLinks.get(0).getFileID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Check if already downloaded the files
                if (downloadCheck == false) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        // Get confirmation for each chunk.
                        Confirm c = d.getValue(Confirm.class);
                        // Check if the chunk is not already added.
                        if (!chunksGot.contains(c.getChunkName())){
                            // Add to the download list.
                            chunksGot.add(c.getChunkName());
                            System.out.println("Got Chunk: " + c.getChunkName());
                            // Delete the confirmation message.
                            JobHelper.confirmReference.child(UserManager.user.getUid()).child(chunkLinks.get(0).getFileID()).child(d.getKey()).removeValue();
                            // Download the chunk.

                        }
                    }

                    if (chunksGot.size() == chunkLinks.size()) {
                        downloadCheck = true;
                        System.out.println("GOT ALL CHUNKS");
                        JobHelper.confirmReference.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    // Gets a list of available devices to get the chunks from.
    private void downloadFile(final HomePageRow fileToCheck){
        // Get information about users.
        final ArrayList<ChunkLink> availableToDownloadFrom = new ArrayList<>();
        final ArrayList<ChunkLink> finalDevicesToDownloadFrom = new ArrayList<>();

        // Get all users that have a chunk for the file stored on their device.
        ChunkHelper.ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    for (DataSnapshot file : user.getChildren()) {
                        if(file.getKey().equals(fileToCheck.getUid())) {
                            for (DataSnapshot chunk : file.getChildren()) {
                                ChunkLink link = chunk.getValue(ChunkLink.class);
                                if (link.isBeingStored()) {
                                    availableToDownloadFrom.add(link);
                                }
                            }
                        }
                    }
                }
                // Get all of the users and only add them if they are online.
                final ArrayList<User> users = new ArrayList<>();
                UserManager.userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Loop through each user.
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            User u = user.getValue(User.class);
                            u.setUserID(user.getKey());
                            // Only get users that are online
                            if(UserManager.getIfOnline(u)){
                                users.add(u);
                            }
                        }
                        // Only select chunks if the user is online and the chunk has not been added already.
                        // Check for multiple chunks.
                        for (ChunkLink chunk : availableToDownloadFrom) {
                            // Check if the chunk has already been added.
                            if(!checkDevices(chunk.getChunkName(), finalDevicesToDownloadFrom)){
                                // Check if the user is available.
                                if(getUserByID(chunk.getUserID(), users)){
                                    finalDevicesToDownloadFrom.add(chunk);
                                }
                            }
                        }
                        // Tell the selected devices to upload the chunks so they can be downloaded.
                        setJobList(finalDevicesToDownloadFrom);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Searches for a user by UID in a list.
    private boolean getUserByID(String uid, ArrayList<User> users){
        for (User u : users) {
            if(uid.equals(u.getUserID())){
                return true;
            }
        }
        return false;
    }

    // Check if the list already contains the same chunk.
    private boolean checkDevices(String str, ArrayList<ChunkLink> arr){
        for(ChunkLink l : arr){
            if (l.getChunkName().equals(str)){
                return true;
            }
        }
        return false;
    }

    // Get all files that are being stored on the network by the user and update the ui.
    private void getAllFilesBeingStored(){
        FileHelper.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataModels.clear();
                // For each file that is the users, add to the home page.
                for (DataSnapshot file : dataSnapshot.getChildren()){
                    PSFile f = file.getValue(PSFile.class);
                    if(f.getOwnerID().equals(UserManager.user.getUid())){
                        dataModels.add(new HomePageRow(f.getFileName(), FileHelper.getFileSizeString(f.getTotalSize()), file.getKey()));
                    }
                }
                adapter.notifyDataSetChanged();
                // Remove unused keys
                CryptoHelper.removeUnusedKeys(dataModels);
            }
            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUME");
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

}
