package com.aaronnebbs.peersplitandroidapplication.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Controllers.FileInfoPageController;
import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.CryptoHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.JobHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.ConnectivityHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.BottomNavBarAdapter;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
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
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                final HomePageRow dataModel= dataModels.get(position);
                // Get the file from online.
                FileHelper.ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot file : dataSnapshot.getChildren()) {
                            if (file.getKey().equals(dataModel.getUid())) {
                                PSFile f = file.getValue(PSFile.class);

                                Intent i = new Intent(getContext(), FileInfoPageController.class);

                                String out = new Gson().toJson(f);
                                String out2 = new Gson().toJson(dataModel);
                                i.putExtra("psfile", out);
                                i.putExtra("homepagerow", out2);
                                startActivity(i);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }
        });
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
