package com.example.pari.usersapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pari on 01-04-2017.
 */

public class SplashScreen extends Activity{
    private static int SPLASH_TIME_OUT = 2000;
    SharedPreferences loginstate;
    JsonObject loginjson;
    private static final String URL_FOR_LOGIN = Constants.SERVER+"/auth/user_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loginstate = getSharedPreferences("LOGIN",MODE_PRIVATE);



        new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if(loginstate.getString("email"," ").equals(" ")) {

                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();

                }else{

                    try{

                        String email= loginstate.getString("email"," ");
                        String pass = loginstate.getString("password"," ");
                        loginUser(email,pass);

                    }catch (Exception e){

                    }


                }
            }
        }, SPLASH_TIME_OUT);


    }


    private void loginUser( final String email, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(response);
                   String status = jObj.getString("status");
                    if (status != null && status.equals("success")) {
                        String accessToken = null;
                        accessToken = jObj.getString("access_token");
                        String secretKey = jObj.getString("secret_key");
                        String user = jObj.getString("user_name");
                        boolean aadhaar_verified = jObj.getBoolean("aadhar_verified");
                        boolean phone_no_verified = jObj.getBoolean("phone_no_verified");
                        Intent intent = new Intent(
                                SplashScreen.this,
                                HomePage.class);



                        SharedPreferences.Editor edit = loginstate.edit();
                        edit.putString("email",email);
                        edit.putString("password",password);
                        edit.apply();
                        edit.commit();


                        Bundle b = new Bundle();
                        b.putString("name", user);
                        b.putString("email", email);
                        b.putString("accessToken",accessToken);
                        b.putString("secretKey",secretKey);
                        b.putString("password",password);
                        b.putBoolean("aadhar_verified",aadhaar_verified);
                        b.putBoolean("phone_no_verified",phone_no_verified);

                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    }

                    else {

                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Please Check Your Internet Connection!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq,cancel_req_tag);
    }
}
