package com.example.frank.authexample_webapp_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TasksActivity extends AppCompatActivity {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;

    private Resources res;
    private String HOSTNAME;
    private String API_VERSION_PATH;
    private String TASKS_URL;

    private ProgressBar mProgressView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private TasksActivity.TodoListTask mTodoListTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        res = getResources();
        HOSTNAME = res.getString(R.string.hostname);
        API_VERSION_PATH = res.getString(R.string.api_version_path);
        TASKS_URL = HOSTNAME + API_VERSION_PATH + res.getString(R.string.tasks_path);

        String[] arr = {"Android","IPhone","WindowsMobile","Blackberry",
                "WebOS","Ubuntu","Windows7","Max OS X"};

//        ArrayAdapter adapter = new ArrayAdapter<String>(this,
//                R.layout.task_view, arr);

        mProgressView = (ProgressBar) findViewById(R.id.tasks_progress);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mRecyclerView.setAdapter();

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mTodoListTask = new TodoListTask(mRecyclerView);
        mTodoListTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    // TodoListTask class
    public class TodoListTask extends AsyncTask<Void, Void, Boolean> {

        private RecyclerView recyclerView;

        TodoListTask(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Response response = post(TASKS_URL, null);
                if (response.code() == 200) {
                    // populate list
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                    } catch(JSONException e) {

                    }
                } else {
                    Log.d("Tasks Response:", response.body().string());
                    return false;
                }
            } catch (IOException e) {
                return false;
            }

            return true;
        }

        private Response post(String url, String json) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mTodoListTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                // catch failure to fetch tasks
            }
        }

        @Override
        protected void onCancelled() {
            mTodoListTask = null;
            showProgress(false);
        }
    }
}
