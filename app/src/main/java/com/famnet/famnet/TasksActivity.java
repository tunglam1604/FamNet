package com.famnet.famnet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.famnet.famnet.Model.Task;
import com.famnet.famnet.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksActivity extends AppCompatActivity{

    //Constant
    private String TAG = "TasksActivity";

    //View
    private RecyclerView mRecyclerView;
    private TaskAdapter mTaskAdapter;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseUser mCurrentUser;
    DatabaseReference mUsersRef;
    DatabaseReference mTaskRef;
    DatabaseReference mCurrentUserTaskRef;

    //Properties
    private List<Task> mTaskList; //Store all tasks in board
    private User mUser;

    // Used when user want to take a certain task, we need to know the next index to add a new task
    // to user's task list
    private static int taskCountOfCurrentUser = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        Log.d(TAG, "TasksActivity onCreate");



        //TODO: Create bug when assign family to null

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("Users");
        mTaskRef = mDatabase.getReference("Tasks");
        mCurrentUserTaskRef = mUsersRef.child(mCurrentUser.getUid()).child("tasks");

        // Properties init
        mUser = new User(mCurrentUser.getUid(), mCurrentUser.getDisplayName(), null, mCurrentUser.getEmail());
        mTaskList = new ArrayList<>();

        // Check if user already log in, if not,
        // change user to log in page (MainActivity)
        if (mAuth.getCurrentUser() == null) {
            startActivity(MainActivity.createIntent(this));
            finish();
            return;
        }



        //Get number of current tasks of current user
        mCurrentUserTaskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    Task task = post.getValue(Task.class);
                    taskList.add(task);
                }
                taskCountOfCurrentUser = taskList.size();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // If current user do not have data on Real-time database,
        // add information of user to the database
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //TODO: BUG: Create a bug that set family to null
                if (!dataSnapshot.hasChild(mCurrentUser.getUid())) {
                    // Create a new user
                    User user = new User(mCurrentUser.getUid(),
                                        mCurrentUser.getDisplayName(),
                                        null,
                                        mCurrentUser.getEmail());
                    //Add user to the Real-time database
                    mUsersRef.child(mCurrentUser.getUid()).setValue(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Recycler View
        mRecyclerView = findViewById(R.id.tasks_recyclerView); //Initialize
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); //set LayoutManager

        // Add listener to update task when new task is added, or taken
        mTaskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Task> taskList = new ArrayList<>(); // New List to store task value

                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Task task = postSnapShot.getValue(Task.class);
                    taskList.add(task);
                }

                mTaskList = taskList;
                updateUI(mTaskList);

//                notification();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Failed to read value
                Toast.makeText(TasksActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();

            }
        });


        // Set notification when a new task is added
        mTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                notification();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Navigation bottom
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.navigation_tasks);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_chat:
                        Intent intent1 = new Intent(TasksActivity.this, ChatActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_tasks:
                        break;
                    case R.id.navigation_personal_task:
                        Intent intent3 = new Intent(TasksActivity.this, PersonalTasksActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.navigation_account:
                        Intent intent4 = new Intent(TasksActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });

    }


    //TODO: Bug: always send notification
    // Notification
    private void notification(){
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(TasksActivity.this, 0, intent, 0);

        Notification noti = new Notification.Builder(TasksActivity.this)
                .setTicker("Famnet New Task")
                .setContentTitle("Famnet - New Task In Family TaskBoard")
                .setContentText("Someone in your family just created a new task. Check it now !")
                .setSmallIcon(R.drawable.notification)
                .setVibrate(new long[] { 1000, 1000 })
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent).getNotification();


        noti.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
    }


    //Private methods
    private void updateUI(List<Task> tasks) {
        mTaskAdapter = new TaskAdapter(tasks);
        mTaskAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mTaskAdapter);
    }


    //ViewHolder class for RecyclerView
    public class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Task mTask;
        private TextView mTaskNameTextView;
        private TextView mTaskRewardTextView;
        private TextView mTaskDeadlineTextView;
        private Button mTaskTakeButton;

        public TaskHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTaskNameTextView = itemView.findViewById(R.id.task_name_text_view);
            mTaskRewardTextView = itemView.findViewById(R.id.task_reward_text_view);
            mTaskDeadlineTextView = itemView.findViewById(R.id.task_deadline_text_view);
            mTaskTakeButton = itemView.findViewById(R.id.task_take_button);
        }

        public void bind(Task task) {
            mTask = task;
            mTaskNameTextView.setText(mTask.getName());
            mTaskRewardTextView.setText("Reward: " + mTask.getReward());
            mTaskDeadlineTextView.setText("Deadline: " + mTask.getDeadline());
            mTaskTakeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create task update
                    Map<String, Object> taskUpdates = new HashMap<>();

                    // Add the taken task to user's list task with next index based on taskCount
//                    taskUpdates.put(String.valueOf(taskCountOfCurrentUser), mTask);
                    taskUpdates.put(mTask.getId(), mTask);

                    // Remove task from task board
                    mTaskRef.child(mTask.getId()).removeValue();

                    // Add taken task to user's task list
                    mCurrentUserTaskRef.updateChildren(taskUpdates);

                    // Reload data change in task board
                    mTaskAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent detailIntent = new Intent(TasksActivity.this, TaskDetailActivity.class);
            detailIntent.putExtra("task", mTask);
            detailIntent.putExtra("identifier", "TasksActivity");
            startActivity(detailIntent);
        }
    }


    //Adapter class for RecyclerView
    public class TaskAdapter extends RecyclerView.Adapter<TaskHolder> implements Filterable {

        private List<Task> mTasks;

        public TaskAdapter(List<Task> tasks) {
            mTasks = tasks;
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
            return new TaskHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            Task task = mTasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        //TODO: check the search implementation again
        // SEARCH TASKS BEGINS HERE
        //GET FILTER METHOD
        //TODO: BUG: does not show all list again after search
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    ArrayList<Task> filteredResults;
                    if (constraint == null || constraint.length() == 0) {
                        filteredResults = (ArrayList<Task>) mTasks;
                    } else {
                        filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                    }

                    FilterResults results = new FilterResults();
                    results.values = filteredResults;

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mTasks = (ArrayList<Task>) results.values;
                    TaskAdapter.this.notifyDataSetChanged();
                }
            };
        }

        protected ArrayList<Task> getFilteredResults(String constraint) {
            ArrayList<Task> results = new ArrayList<>();

            for (Task task : mTasks) {
                if (task.getName().toLowerCase().contains(constraint)) {
                    results.add(task);
                }
            }
            return results;
        }
    }

    //SEARCH METHOD
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mTaskAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
//THE END OF SEARCH TASKS

    public static Intent createIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, TasksActivity.class);
        return intent;
    }

}
