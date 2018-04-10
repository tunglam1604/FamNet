package com.famnet.famnet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.famnet.famnet.Model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskDetailActivity extends AppCompatActivity {


    // Views
    private TextView mTaskName;
    private TextView mTaskReward;
    private TextView mTaskDeadline;
    private TextView mTaskDescription;
    private Button mDeleteButton;

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mCurrentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mTaskRef = mDatabase.getReference("Tasks");
    DatabaseReference mUserRef = mDatabase.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // View init
        mTaskName = findViewById(R.id.task_name_detail_textView);
        mTaskReward = findViewById(R.id.task_reward_detail_textView);
        mTaskDeadline = findViewById(R.id.task_deadline_detail_textView);
        mTaskDescription = findViewById(R.id.task_detail_description_detail_textView);
        mDeleteButton = findViewById(R.id.task_detail_delete_button);

        final Task task = (Task) getIntent().getSerializableExtra("task");

        // Get identifier to know which Activity start this activity
        String identifier = getIntent().getStringExtra("identifier");

        mTaskName.setText(task.getName());
        mTaskReward.setText(task.getReward());
        mTaskDeadline.setText(task.getDeadline());
        mTaskDescription.setText(task.getDescription());

        // Delete task based on identifier

        if (identifier != null) {
            switch (identifier) {
                // Delete task from tasks list
                case "TasksActivity":
                    mDeleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTaskRef.child(task.getId()).removeValue();

                            finish();
                        }
                    });
                    break;

                // Delete task from personal task list, BUT put it back to task list
                case "PersonalTasksActivity":
                    mDeleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Add task to task list first
                            mTaskRef.child(task.getId()).setValue(task);

                            // Delete task from personal task list
                            mUserRef.child(mCurrentUser.getUid()).child("tasks").child(task.getId()).removeValue();

                            finish();
                        }
                    });
            }
        }
    }
}
