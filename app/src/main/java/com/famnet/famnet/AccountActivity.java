package com.famnet.famnet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.famnet.famnet.Model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {

    //Constant
    private static final String TAG = "AccountActivity";
    public static final int REQUEST_SETTING = 200;

    // Views
    ImageView mUserSignOut;
    ImageView mUserPhoto;
    TextView mUserName;
    TextView mUserFamily;
    TextView mUserEmail;
    ImageView mUserAddMember;
    ImageView mUserSetting;


    //Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseUser mCurrentUser;
    DatabaseReference mCurrentUserRef;

    // Properties
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mCurrentUserRef = mDatabase.getReference("Users/" + mCurrentUser.getUid());
        mCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                user = dataSnapshot.getValue(User.class);
//                    // User Family
//                    Family family = user.getFamily();
//                    if (family != null) {
//                        mUserFamily.setText(family.getName());
//                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Check if user sign in
        if (mCurrentUser == null) {
            startActivity(MainActivity.createIntent(this));
            finish();
            return;
        }

        // Views init
        mUserSignOut = findViewById(R.id.sign_out_image_view);
        mUserSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        mUserPhoto = findViewById(R.id.user_photo_image_view);

        mUserName = findViewById(R.id.user_name_text_view);
        mUserName.setText(mCurrentUser.getDisplayName());

        mUserFamily = findViewById(R.id.user_family_text_view);

        mUserEmail = findViewById(R.id.user_email_text_view);
        mUserEmail.setText(mCurrentUser.getEmail());

        mUserAddMember = findViewById(R.id.user_add_member_image_view);
        //TODO: Implement add member to family
        mUserAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mUserSetting = findViewById(R.id.user_setting_image_view);
        mUserSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_SETTING);
            }
        });



        //Navigation bar
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.navigation_account);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_chat:
                        Intent intent1 = new Intent(AccountActivity.this, ChatActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_tasks:
                        Intent intent2 = new Intent(AccountActivity.this, TasksActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_personal_task:
                        Intent intent3 = new Intent(AccountActivity.this, PersonalTasksActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.navigation_account:
                        break;
                }
                return false;
            }
        });
        Log.i(TAG, "onCreate");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String name = data.getStringExtra("name");
        mUserName.setText(name);
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(MainActivity.createIntent(AccountActivity.this));
                            finish();
                        } else {
                            Toast.makeText(AccountActivity.this, "Sign out failed", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }



}
