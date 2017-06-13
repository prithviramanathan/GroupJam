package com.example.prith.groupjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupForAdmin extends AppCompatActivity {
    public static final String mAccessCode = "";
    String accessCode;
    String groupName;
    String groupID;
    FirebaseAuth auth;
    FirebaseDatabase mDatabase;
    DatabaseReference allGroupsReference;

    TextView accessCodeView;
    TextView capacityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_for_admin);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accessCode = getIntent().getStringExtra(mAccessCode);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        allGroupsReference = mDatabase.getReference("Groups");

        accessCodeView = (TextView) findViewById(R.id.accessCodeDisplay);
        capacityView = (TextView) findViewById(R.id.memberCountDisplay);
        String accessCodeDisplay = "Access Code: " + accessCode;
        accessCodeView.setText(accessCodeDisplay);

        allGroupsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot group: dataSnapshot.getChildren()){
                    Log.d("group", "" + group.child("accessCode").getValue());
                    if(accessCode.equals(group.child("accessCode").getValue())){
                        groupID = group.getRef().getKey();
                        allGroupsReference.child(groupID).child("members").child(auth.getCurrentUser().getUid()).setValue("member");
                        String cap = "Members: " + group.child("members").getChildrenCount();
                        toolbar.setTitle("" + group.child("name").getValue());
                        capacityView.setText(cap);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    //log out button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflator = getMenuInflater();
        menuInflator.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logoutButton){
            auth.signOut();
            Intent restart = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(restart);;
        }
        return true;
    }

}
