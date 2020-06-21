/*
  LoginActivity file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSubmit, btnRegister;
    EditText etUsername, etPassword;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        sp = getSharedPreferences("details1", 0);
        String username = sp.getString("username", null);

        if (username != null) { // if username is not null
            // set etUsername text to username
            etUsername.setText(username);
        }
        btnSubmit = findViewById(R.id.btnLoginSubmit);
        btnRegister = findViewById(R.id.btnRegister);

        btnSubmit.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        boolean correct = true;
        if (v == btnSubmit) { // if btnSubmit is checked
            if ( etPassword.getText().length() == 0) { // if password is not given
                etPassword.setError("Password is required!");
                correct = false;
            }
            if ( etUsername.getText().length() == 0) { // if username is not given
                etUsername.setError("Username is required!");
                correct = false;
            }
            if (etUsername.getText().toString().contains(" ")) { // if username contains spaces
                etUsername.setError("Username must not contain spaces!");
                correct = false;
            }
            if ( !correct ) return ; // if correct is false - do not continue
            boolean status = login(getUserLoginInfo()); // do login
            if (status) // if login went well
                startActivity(intent);
        }
        else if (v == btnRegister) { // if btnRegister is clicked
            startActivity(new Intent(this, RegisterActivity.class)); // go to RegisterActivity
        }
    }
    private boolean login(JSONObject userInfo) {
        // login
        // return true if went well
        // else false
        SharedPreferences.Editor editor = sp.edit();
        // send login to server with userInfo and wait for response
        Writer w = new Writer();
        try {
            String response = w.execute(getString(R.string.server_host) + "/login", userInfo.toString()).get();
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getString("Status").equals("OK")) { // if response status is OK
                // put user information into shared preferences
                JSONObject user = jsonResponse.getJSONObject("User");
                editor.putString("username", user.getString("Username"));
                editor.putString("email", user.getString("Email"));
                editor.putString("number", user.getString("Number"));
                editor.putString("instrument", user.getString("Instrument"));
                editor.putString("genre", user.getString("Genre"));
                editor.apply();
                return true;
            }
            else { // if response status is not OK
                // create AlertDialog
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Error in Login!");
                if (jsonResponse.getString("Detail").contains("ARE NOT MATCHED"))
                    alertDialog.setMessage("Username and password do not match!");
                else
                    alertDialog.setMessage("Unknown Error!!!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private JSONObject getUserLoginInfo() {
        // get user login info
        // return JSONObject with user information

        // create JSONObject with user information
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", etUsername.getText().toString());
            jsonObject.put("password", etPassword.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }
}
