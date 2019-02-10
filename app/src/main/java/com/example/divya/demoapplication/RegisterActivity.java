package com.example.divya.demoapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static final String URL_FOR_REGISTRATION = "http://192.168.0.22:3000/register";
    ProgressDialog progressDialog;

    private EditText etFirstName, etLastName, etContactNumber, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private Button btnLinkLogin;
    private RadioGroup roleRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        etFirstName = (EditText) findViewById(R.id.et_first_name);
        etLastName = (EditText) findViewById(R.id.et_last_name);
        etContactNumber = (EditText) findViewById(R.id.et_contact_number);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnLinkLogin = (Button) findViewById(R.id.btn_link_login);

        roleRadioGroup = (RadioGroup) findViewById(R.id.type_radio_group);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btnLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void submitForm() {

        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
        String role;
        if(selectedId == R.id.pub_radio_btn)
            role = "Publisher";
        else
            role = "User";
        if(!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(),
                    "Passwords doesn't match.", Toast.LENGTH_LONG).show();
            return;
        }

        registerUser(etFirstName.getText().toString(),
                etLastName.getText().toString(),
                etContactNumber.getText().toString(),
                etEmail.getText().toString(),
                role,
                etPassword.getText().toString());
    }

    private void registerUser(final String firstName,  final String lastName, final String contactNum,
                              final String emailId, final String role, final String password) {
        String cancel_req_tag = "register";

        progressDialog.setMessage("Adding you ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_REGISTRATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("user").getString("name");
                        Toast.makeText(getApplicationContext(), "Hi " + user +", You are successfully Added!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                RegisterActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("email", emailId);
                params.put("contactNumber", contactNum);
                params.put("password", password);
                params.put("role", role);
                return params;
            }
        };
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
