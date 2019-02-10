package com.example.divya.demoapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.divya.demoapplication.adapter.ApplicationAdapter;
import com.example.divya.demoapplication.adapter.UserActivityAdapter;
import com.example.divya.demoapplication.models.Application;
import com.example.divya.demoapplication.models.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private List<UserActivity> activityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserActivityAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private String url = "http://192.168.0.22:3000/fetch-user-activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        String userId = getIntent().getStringExtra("userId");
        url = url+"/"+userId;

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new UserActivityAdapter(activityList,this);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mAdapter);
        getData();
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        UserActivity activity = new UserActivity();
                        activity.setActivity(jsonObject.getString("activity"));
                        activity.setAppId(jsonObject.getString("appId"));
                        activity.setCreatedAt(jsonObject.getString("createdAt"));
                        activity.setUpdatedAt(jsonObject.getString("updatedAt"));
                        JSONObject appObj = jsonObject.getJSONObject("app");
                        Application app = new Application();
                        app.setAppName(appObj.getString("appName"));
                        app.setAppDescription(appObj.getString("appDescription"));
                        app.setAppVersion(appObj.getString("appVersion"));
                        app.setApkFile(appObj.getString("apkFile"));
                        activity.setApp(app);

                        activityList.add(activity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
