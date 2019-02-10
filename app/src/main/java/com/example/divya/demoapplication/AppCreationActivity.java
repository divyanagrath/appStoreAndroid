package com.example.divya.demoapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppCreationActivity extends AppCompatActivity {
    private static final String TAG = "AppCreationActivity";
    private static final String URL_FOR_APP_CREATION = "http://192.168.0.22:3000/create-app";
    ProgressDialog progressDialog;

    private EditText etAppName, etAppDesc, etAppVersion;
    private TextView tvFilePath;
    private Button btnCreateApp, btnUpload;

    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_creation_activity);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        etAppName = findViewById(R.id.et_app_name);
        etAppDesc = findViewById(R.id.et_app_desc);
        etAppVersion = findViewById(R.id.et_app_version);
        btnCreateApp = findViewById(R.id.btn_create);
        tvFilePath = findViewById(R.id.tv_filename);
        btnUpload = findViewById(R.id.btn_upload);

        btnCreateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createApp();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    private void createApp() {
        String req_tag = "app-create";

        final String appName = etAppName.getText().toString();
        final String appDesc = etAppDesc.getText().toString();
        final String appVersion = etAppVersion.getText().toString();
        final String filePath = tvFilePath.getText().toString();

        JSONObject jsonBody = new JSONObject();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            jsonBody.put("appName", appName);
            jsonBody.put("appVersion", appVersion);
            jsonBody.put("appDescription", appDesc);
            jsonBody.put("status", "Active");
            jsonBody.put("apkFile", filePath);
            jsonBody.put("createdAt", formatter.format(date));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        progressDialog.setMessage("Creating app ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_APP_CREATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplicationContext(),"Application created",Toast.LENGTH_SHORT).show();
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
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
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    String path = uri.getPath();

                    tvFilePath.setText(path);
                    Log.d(TAG, "File Path: " + path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
