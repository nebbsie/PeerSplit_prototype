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
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.BottomNavBarAdapter;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkLink;
import com.aaronnebbs.peersplitandroidapplication.Model.HomePageRow;
import com.aaronnebbs.peersplitandroidapplication.Model.PSFile;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = getView().findViewById(R.id.home_listview);
        dataModels = new ArrayList<>();
        adapter = new BottomNavBarAdapter(dataModels, getContext());
        listView.setAdapter(adapter);

        getAllFilesBeingStored();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomePageRow dataModel= dataModels.get(position);
                getChunkData(dataModel);
            }
        });
    }

    private void attemptDownload(ArrayList<ChunkLink> chunkLinks){
        System.out.println("Attempting to download " + chunkLinks.size() + " chunks.");
    }

    private void getChunkData(final HomePageRow fileToCheck){
        // Get information about users.
        final ArrayList<ChunkLink> availableToDownloadFrom = new ArrayList<>();
        final ArrayList<ChunkLink> finalDevices = new ArrayList<>();
        ChunkHelper.ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Each user
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    // Each file for each user
                    for (DataSnapshot file : user.getChildren()) {
                        // Check if correct file
                        if(file.getKey().equals(fileToCheck.getUid())) {
                            // Each chunk for the file.
                            for (DataSnapshot chunk : file.getChildren()) {
                                ChunkLink link = chunk.getValue(ChunkLink.class);
                                if (link.isBeingStored()) {
                                    availableToDownloadFrom.add(link);
                                }
                            }
                        }
                    }
                }
                // Get all users
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
                        // Check for multiple chunks.
                        for (ChunkLink chunk : availableToDownloadFrom) {
                            // Check if the chunk has already been added.
                            if(!checkDevices(chunk.getChunkName(), finalDevices)){
                                // Check if the user is available.
                                if(getUserByID(chunk.getUserID(), users)){
                                    finalDevices.add(chunk);
                                }
                            }
                        }
                        attemptDownload(finalDevices);
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

    private boolean getUserByID(String uid, ArrayList<User> users){
        for (User u : users) {
            System.out.println(u.getUserID());
            if(uid.equals(u.getUserID())){
                System.out.println();
                return true;
            }
        }
        return false;
    }

    private boolean checkDevices(String str, ArrayList<ChunkLink> arr){
        for(ChunkLink l : arr){
            if (l.getChunkName().equals(str)){
                return true;
            }
        }
        return false;
    }

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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
