package com.example.divya.demoapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.divya.demoapplication.AppSingleton;
import com.example.divya.demoapplication.R;
import com.example.divya.demoapplication.UserListActivity;
import com.example.divya.demoapplication.models.Application;
import com.example.divya.demoapplication.models.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.MyViewHolder>{

    private List<UserActivity> activityList;
    private Context ctx;

    private static final String TAG = "UserActivityAdapter";

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView appName, appVersion, tvActivity, tvCreatedAt;
        private String appId;

        public MyViewHolder(View view) {
            super(view);
            appName = view.findViewById(R.id.tv_app_name);
            appVersion = view.findViewById(R.id.tv_app_version);
            tvActivity = view.findViewById(R.id.tv_activity);
            tvCreatedAt = view.findViewById(R.id.tv_date);
        }

    }


    public UserActivityAdapter(List<UserActivity> activityList, Context ctx) {
        this.activityList = activityList;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_activity_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserActivity activity = activityList.get(position);
        holder.appName.setText(activity.getApp().getAppName());
        holder.appVersion.setText(activity.getApp().getAppVersion());
        holder.tvActivity.setText(activity.getActivity());
        holder.tvCreatedAt.setText(activity.getCreatedAt());
        holder.appId=activity.getAppId();
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
}
