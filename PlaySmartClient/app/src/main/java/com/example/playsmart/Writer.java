/*
  Writer class file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Writer extends AsyncTask <String, Integer, String> {
    // Writes messages to server
    // Extends from AsyncTask
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        // Send messages to server
        // Return response as String

        try {
            URL myURL = new URL(strings[0]); // get URL from the parameters
            HttpURLConnection con;
            try {
                // create connection to server
                con = (HttpURLConnection) myURL.openConnection();
                con.setRequestMethod("POST"); // set method to POST - sending info
                con.setRequestProperty("Content-Type", "application/json; utf-8"); // transferring JSON
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true); // set output to be true

                // send message
                OutputStream os = con.getOutputStream();
                byte[] input = strings[1].getBytes("UTF-8"); // convert message to bytes array
                os.write(input, 0, input.length);

                // get response
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        // reading line after line until done
                        response.append(responseLine.trim());
                }
                return response.toString();
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

