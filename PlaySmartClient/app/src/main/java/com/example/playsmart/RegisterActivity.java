/*
  RegisterActivity file
  Created by: Gil-Ad Shay
  Last edited: 25.05.2020
  Version: 'checking validity'
 */

package com.example.playsmart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSubmit, btnCancel;
    EditText etUsername, etPassword, etEmail, etPhoneNumber;
    Spinner instrument, genre;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sp = getSharedPreferences("details1", 0);
        etUsername = findViewById(R.id.etRegisterUsername);
        etPassword = findViewById(R.id.etRegisterPassword);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPhoneNumber = findViewById(R.id.etRegisterPhoneNumber);
        instrument = findViewById(R.id.spInstrument);
        genre = findViewById(R.id.spGenre);

        // set instrument spinner
        ArrayAdapter<String> instrumentAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.instruments));
        instrumentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instrument.setAdapter(instrumentAdapter);

        // set genre spinner
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.genres));
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genre.setAdapter(genreAdapter);

        btnSubmit = findViewById(R.id.btnRegisterSubmit);
        btnCancel = findViewById(R.id.btnRegisterCancel);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean correct;
        if (v == btnSubmit) { // if btnSubmit is clicked
            correct = checkUsername() && checkPassword() && checkEmail() && checkPhone();
            if (!correct) return;
            boolean status = register(getUserRegisterInfo()); // register with user register info
            if (status) { // if register went well
                // move to MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(this, "Could not complete the action!", Toast.LENGTH_LONG).show();
        }
        else if (v == btnCancel) { // if btnCancel is clicked
            finish(); // move to LoginActivity
        }
    }

    private boolean checkPhone() {
        boolean correct = true;
        String phone = etPhoneNumber.getText().toString();
        if (!(phone.charAt(0) == '0' && phone.charAt(1) == '5')) { // if phone number does not start with 05
            etPhoneNumber.setError("Please check your phone number (must start with 05-)");
            correct = false;
        }
        else if (phone.length() != 10) { // if phone number length is not 10 digits
            etPhoneNumber.setError("Please check your phone number (must be 10 digits)");
            correct = false;
        }
        return correct;
    }

    private boolean checkEmail() {
        boolean correct = true;
        String mail = etEmail.getText().toString();
        int indexA = mail.indexOf('@');
        int indexDot = mail.indexOf('.');
        if (indexA == -1 || indexDot == -1) { // if . or @ are missing
            etEmail.setError("Please check your email address");
            correct = false;
        }
        else if (isDigit(mail.charAt(0))) { // if the first character is a digit
            etEmail.setError("Please check your email address");
            correct = false;
        }
        else if (indexA == 0 || indexDot == 0) { // if . or @ is the first character
            etEmail.setError("Please check your email address");
            correct = false;
        }
        else if (indexA > indexDot) { // if @ is after .
            etEmail.setError("Please check your email address");
            correct = false;
        }
        return correct;
    }

    private boolean checkPassword() {
        boolean correct = true;
        String password = etPassword.getText().toString();
        if (password.length() <= 6 || password.length() > 20) { // if password is not between 6-20 characters
            etPassword.setError("Password must be between 6 to 20 characters");
            correct = false;
        }
        return correct;
    }

    private boolean checkUsername() {
        boolean correct = true;
        String username = etUsername.getText().toString();
        if (username.length() <= 4) { // if username is not 4 characters long
            etUsername.setError("Username must contain at least 4 characters");
            correct = false;
        }
        else if (username.contains(" ")) { // if username contains space
            etUsername.setError("Username must not contain space!");
            correct = false;
        }
        else if (isDigit(username.charAt(0))) { // if username starts in digit
            etUsername.setError("Username must not start in digit, but in letter");
            correct = false;
        }
        return correct;
    }

    private boolean isDigit(char c) {
        // return true if given character is a digit
        return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' ||
                c == '7' || c == '8' || c == '9';
    }

    private boolean register(JSONObject userInfo) {
        // register
        // return true if went well
        // else return false
        SharedPreferences.Editor editor = sp.edit();
        // sends register to server with user info and wait for response
        Writer w = new Writer();
        try {
            String response = w.execute(getString(R.string.server_host) + "/register", userInfo.toString()).get();
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("Status").equals("OK")) { // if response status is OK
                // save info in shared preferences
                editor.putString("username", etUsername.getText().toString());
                editor.putString("password", etPassword.getText().toString());
                editor.putString("email", etEmail.getText().toString());
                editor.putString("number", etPhoneNumber.getText().toString());
                editor.putString("instrument", instrument.getSelectedItem().toString());
                editor.putString("genre", genre.getSelectedItem().toString());
                editor.apply();
                return true;
            }
            else { // if status is NOT OK
                // create AlertDialog
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("Error in Register!");
                if (jsonResponse.getString("Detail").contains("UNIQUE"))
                    alertDialog.setMessage("Username is already existed");
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
    private JSONObject getUserRegisterInfo() {
        // get user register info
        // return JSONObject with user info

        JSONObject jsonObject = new JSONObject();
        try {
            // create JSONObject with user info
            jsonObject.put("username", etUsername.getText().toString());
            jsonObject.put("password", etPassword.getText().toString());
            jsonObject.put("email", etEmail.getText().toString());
            jsonObject.put("number", etPhoneNumber.getText().toString());
            jsonObject.put("instrument", instrument.getSelectedItem().toString());
            jsonObject.put("genre", genre.getSelectedItem().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }
}
