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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterGroupCode extends AppCompatActivity {
    FirebaseAuth auth;
    EditText key;
    Button join;
    FirebaseDatabase mDatabase;
    DatabaseReference allGroupsReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_group_code);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Access Code");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        join = (Button) findViewById(R.id.joinThisGroup);
        key = (EditText) findViewById(R.id.codeField);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        allGroupsReference = mDatabase.getReference("Groups");

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String code = key.getText().toString();
                Log.d("Code enter", "" + code.length());
                if(code.length()<  6 || (code.length() > 6)){
                    Toast.makeText(getApplicationContext(), "Access Codes are 6 characters long", Toast.LENGTH_SHORT).show();;
                    return;
                }
                allGroupsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean valid = false;
                        for(DataSnapshot group: dataSnapshot.getChildren()){
                            if(code.equals(group.child("accessCode").getValue())){
                                valid = true;
                                //now check if creator

                                String userID = auth.getCurrentUser().getUid();
                                String creatorID = "" + group.child("creator").getValue();
                                if(userID.equals(creatorID)){
                                    Toast.makeText(getApplicationContext(), "Joined as admin", Toast.LENGTH_SHORT).show();
                                    Intent admin = new Intent(getApplicationContext(), GroupForAdmin.class);
                                    admin.putExtra(GroupForAdmin.mAccessCode, code);
                                    startActivity(admin);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Joined Group", Toast.LENGTH_SHORT).show();
                                    Intent normal = new Intent(getApplicationContext(), GroupForNormals.class);
                                    normal.putExtra(GroupForNormals.mAccessCode, code);
                                    startActivity(normal);
                                }
                            }
                        }
                        if(!valid){
                            Toast.makeText(getApplicationContext(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });




    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //log out button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflator = getMenuInflater();
        menuInflator.inflate(R.menu.menu, menu);
        return true;
    }

}
