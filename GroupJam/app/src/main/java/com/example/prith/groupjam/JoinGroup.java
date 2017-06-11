package com.example.prith.groupjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class JoinGroup extends AppCompatActivity {
    FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Join Group Options");
        setSupportActionBar(toolbar);

        //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        authentication = FirebaseAuth.getInstance();

    }


    //gotten from stack overflow. For back button
    @Override
    public boolean onSupportNavigateUp(){
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logoutButton){
            authentication.signOut();
            Intent restart = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(restart);
        }
        return true;
    }

}
