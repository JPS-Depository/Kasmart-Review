package com.jps.kasmart_review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    SessionManager sessionManager;

    private Button login;
    private EditText inputUsername, inputPassword;
    private ProgressBar loading;
    private static String URL_LOGIN="https://www.kasmart-review.com.dpmd-bengkalis.com/login.php";

    private String name,email,role,username,id;
    private String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        inputUsername=(EditText) findViewById(R.id.username);
        inputPassword=(EditText) findViewById(R.id.password);

        loading=(ProgressBar)findViewById(R.id.loading);

        login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storedUsername = inputUsername.getText().toString().trim();
                String storedPassword = inputPassword.getText().toString();
                checkInput(storedUsername,storedPassword);
            }
        });

    }

    public void checkInput(String storedUsername, String storedPassword) {
        if(storedUsername.equals("")||storedPassword.equals("")){
            Snackbar.make(login, "Kolom harus diisi", Snackbar.LENGTH_LONG)
                    .show();
        }else{
            login(storedUsername,storedPassword);
        }
    }

    private void login(String storedUsername, String storedPassword) {
        loading.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("Message",response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray =jsonObject.getJSONArray("login");
                            if(success.equals("1")) {
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    name = object.getString("fullname").trim();
                                    role = object.getString("name").trim();
                                    username = object.getString("username").trim();
                                    email = object.getString("email").trim();
                                    id = object.getString("id");
                                    loading.setVisibility(View.GONE);
                                    if(!hasPermissions(Login.this,PERMISSIONS)){
                                        ActivityCompat.requestPermissions(Login.this,PERMISSIONS,1);
                                    }else{
                                        openHome();
                                    }
                                }
                            }else{
                                Toast.makeText(Login.this,
                                        "Password Salah",Toast.LENGTH_SHORT).show();

                                loading.setVisibility(View.GONE);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            Toast.makeText(Login.this,"Username Tidak ada",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    Toast.makeText(Login.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", storedUsername);
                params.put("password",storedPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private boolean hasPermissions(Context context, String... PERMISSIONS){
        if(context !=null && PERMISSIONS !=null){
            for(String permission: PERMISSIONS){
                if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return  true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Storage Permission is granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Storage Permission is denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void openHome() {
        sessionManager.createSession(name, role, email,id);
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("ID",id);
        intent.putExtra("NAME", name);
        intent.putExtra("ROLE", role);
        intent.putExtra("EMAIL",email);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}