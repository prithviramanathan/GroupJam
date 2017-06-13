package com.example.prith.groupjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;


public class CreateGroup extends AppCompatActivity {
    FirebaseAuth authentication;
    FirebaseDatabase mDatabase;
    DatabaseReference allGroupsReference;

    EditText groupName;
    EditText genre;
    EditText maxCapacity;
    Button generateGroup;
    RadioGroup privacyPreferred;
    RadioButton privacySelected;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Create Group");
        //set up authentication and database
        authentication = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        allGroupsReference = mDatabase.getReference("Groups");



        //link buttons and edit texts
        generateGroup = (Button) findViewById(R.id.generateGroup);
        groupName = (EditText) findViewById(R.id.groupName);
        genre = (EditText) findViewById(R.id.genrePreferred);
        maxCapacity = (EditText) findViewById(R.id.maxCapacity);
        privacyPreferred = (RadioGroup) findViewById(R.id.toggle);

        //when generate is pressed
        generateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gName = groupName.getText().toString();
                String gGenre = genre.getText().toString();
                String cap = maxCapacity.getText().toString();
                if(cap.length() == 0){
                    Toast.makeText(getApplicationContext(), "Please enter a valid capacity", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cap.length() > 3){
                    Toast.makeText(getApplicationContext(), "Please enter a smaller capacity", Toast.LENGTH_SHORT).show();
                    return;
                }

                int capacity = Integer.parseInt(cap);

                privacySelected = (RadioButton) findViewById(privacyPreferred.getCheckedRadioButtonId());
                String privacy = privacySelected.getText().toString();
                boolean isPrivate;
                if(privacy.equals("Private")){
                    isPrivate = true;
                }
                else{
                    isPrivate = false;
                }

                Date now = new Date(System.currentTimeMillis());



                //for access code
                int numLetters = (int)(Math.random()*5);
                int numNums = (int) (Math.random() * 3);
                int numSymbols = 6 - numLetters - numNums;
                String accessCode = GenerateAccessCode.generate(numLetters, numSymbols, numNums);

                Group mGroup = new Group(authentication.getCurrentUser().getUid(), gGenre, gName, isPrivate, accessCode, now);




                allGroupsReference.push().setValue(mGroup);

                Intent startGroup = new Intent(getApplicationContext(), GroupForAdmin.class);
                startGroup.putExtra(GroupForAdmin.mAccessCode, accessCode);
                startActivity(startGroup);


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
