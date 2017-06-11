package com.example.prith.groupjam;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<AuthUI.IdpConfig> signInOptions;
    FirebaseAuth authentication;
    final int SIGN_IN_REQUEST_CODE = 1;
    final int FIND_IMAGE_REQUEST_CODE = 2;
    private static final int REQUEST_READ_PERMISSION = 100;
    Intent i;
    private FirebaseDatabase mDatabase;

    Button joinGroup;
    Button createGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //sign in options
        authentication = FirebaseAuth.getInstance();

        //create all possible sign in options
        signInOptions = new ArrayList<>();
        signInOptions.add(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
        signInOptions.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
        //check if already signed in
        if(authentication.getCurrentUser() == null){
            Log.d("No authentication yet", "got here");
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setProviders(signInOptions).build(), SIGN_IN_REQUEST_CODE);
        }

        joinGroup = (Button) findViewById(R.id.joinGroup);
        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(getApplicationContext(), JoinGroup.class);
                startActivity(joinIntent);
            }
        });

        createGroup = (Button) findViewById(R.id.createGroup);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(getApplicationContext(), CreateGroup.class);
                startActivity(createIntent);
            }
        });

    }


    //all the activity results possible for this activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //sign in request
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            //we signed in
            Log.d("on activityResult", "" + resultCode);
            Log.d("Desired result", "" + RESULT_OK);
            if (resultCode == RESULT_OK){
                Log.d("Signed in", "got here");
                Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
            }
            else{
                //could not sign in
                i = getIntent();
                finish();
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Could not sign in", Toast.LENGTH_SHORT).show();
            }
        }

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
            Intent restart = getIntent();
            finish();
            startActivity(restart);
        }
        return true;
    }
}

