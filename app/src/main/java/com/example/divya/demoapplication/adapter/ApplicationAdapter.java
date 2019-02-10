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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.divya.demoapplication.AppSingleton;
import com.example.divya.demoapplication.R;
import com.example.divya.demoapplication.models.Application;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.MyViewHolder>{

    private List<Application> appsList;
    private Context ctx;
    private String userId;

    private static final String TAG = "ApplicationAdapter";
    private static final String URL_FOR_PERSIST_ACTIVITY = "http://192.168.0.22:3000/persist-user-activity";

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected Button btnInstall, btnUninstall;
        private TextView appName, appVersion, appDescription;
        private String appId;

        public MyViewHolder(View view) {
            super(view);
            appName = view.findViewById(R.id.tv_app_name);
            appVersion = view.findViewById(R.id.tv_app_version);
            appDescription = view.findViewById(R.id.tv_app_desc);
            btnInstall = view.findViewById(R.id.btn_install);
            btnUninstall = view.findViewById(R.id.btn_uninstall);

            btnInstall.setOnClickListener(this);
            btnUninstall.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == btnInstall.getId()){
                saveActivity("install");

            } else if(v.getId() == btnUninstall.getId()) {
                saveActivity("uninstall");
            }
        }

        public void saveActivity(String activity) {
            String req_tag = "save_activity";
            JSONObject jsonBody = new JSONObject();
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                jsonBody.put("appId", appId);
                jsonBody.put("userId", userId);
                jsonBody.put("activity", activity);
                jsonBody.put("createdAt", formatter.format(date));
                jsonBody.put("updatedAt", formatter.format(date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String requestBody = jsonBody.toString();
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    URL_FOR_PERSIST_ACTIVITY, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response: " + response.toString());
                    try {
                        JSONObject jObj = new JSONObject(response);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(ctx,
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

            };
            AppSingleton.getInstance(ctx).addToRequestQueue(strReq,req_tag);
        }
    }


    public ApplicationAdapter(List<Application> appsList, Context ctx, String userId) {
        this.appsList = appsList;
        this.ctx = ctx;
        this.userId = userId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Application app = appsList.get(position);
        holder.appName.setText(app.getAppName());
        holder.appVersion.setText(app.getAppVersion());
        holder.appDescription.setText(app.getAppDescription());
        holder.appId=app.getAppId();
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }
}
