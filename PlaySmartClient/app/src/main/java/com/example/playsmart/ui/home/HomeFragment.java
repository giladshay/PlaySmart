/*
  HomeFragment file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.playsmart.R;
import com.example.playsmart.Writer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("ALL")
public class HomeFragment extends Fragment {
    ImageButton yes, no;
    ListView messages;
    SharedPreferences sp;
    JSONObject user;
    JSONArray bandJson;
    TextView name, noNew;
    Writer w;
    String response;
    MessageAdapter messageAdapter;
    boolean chosen;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        messages = root.findViewById(R.id.lvMessages);
        noNew = root.findViewById(R.id.tvNoMessages);

        // send username to server and wait for response
        sp = this.getActivity().getSharedPreferences("details1", 0);
        String username = sp.getString("username", null);
        if (username != null) {
            w = new Writer();
            try {
                user = new JSONObject();
                user.put("username", username);
                response = w.execute(getString(R.string.server_host) + "/", user.toString()).get();
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.get("Status").equals("OK")) { // if resposne status is OK
                    bandJson = jsonResponse.getJSONArray("Offers"); // put offers list in bandJson (JSONArray)
                }
                else
                    Toast.makeText(this.getContext(), "Error!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this.getContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        }
        // set offers list
        messageAdapter = new MessageAdapter();
        messages.setAdapter(messageAdapter);
        if (bandJson.length() == 0)
            noNew.setVisibility(View.VISIBLE);

        return root;
    }

    public void onBackPressed() {
        // not allow to return to previous screen
        return;
    }

    public JSONObject getUserBandInfo(int position) {
        // get user information and band information
        JSONObject data = new JSONObject();
        try {
            data.put("username", sp.getString("username", null));
            data.put("bandname", messageAdapter.getItem(position).getString("name"));
        } catch (JSONException e) {
            Toast.makeText(this.getContext(), "Error!", Toast.LENGTH_SHORT).show();
        }
        return data;
    }

    private class MessageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // return the length of messages list
            return bandJson.length();
        }

        @Override
        public JSONObject getItem(int position) {
            // return the list item (Band) as json
            try {
                return bandJson.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // return the list item view
            View view = getLayoutInflater().inflate(R.layout.message_layout, null);
            chosen = false;
            name = view.findViewById(R.id.btnBandOrUserName);
            yes = view.findViewById(R.id.btnYes);
            no = view.findViewById(R.id.btnNo);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // if yes is clicked
                    if (!chosen) { // if chosen is false
                        // accept joining band
                        w = new Writer();
                        try {
                            response = w.execute(getString(R.string.server_host) + "/accept", getUserBandInfo(position).toString()).get();
                            JSONObject jsonResponse = new JSONObject(response);
                            chosen = true;
                            // update messages list
                            bandJson.remove(position);
                            messageAdapter.notifyDataSetChanged();
                            messages.setAdapter(messageAdapter);
                        } catch (Exception e) {
                            Log.e("server", "Error");
                        }
                    }
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // if no is clicked
                    if (!chosen) { // if chosen is false
                        // decline joining band
                        w = new Writer();
                        try {
                            response = w.execute(getString(R.string.server_host) + "/decline", getUserBandInfo(position).toString()).get();
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("Status").equals("OK")) {
                                chosen = true;
                                // update messages list
                                bandJson.remove(position);
                                messageAdapter.notifyDataSetChanged();
                                messages.setAdapter(messageAdapter);
                            }

                        } catch (Exception e) {
                            Log.e("server", "Error");
                        }
                    }
                }
            });
            // set button name to band name
            try {
                name.setText(bandJson.getJSONObject(position).getString("name"));
            } catch (JSONException e) {
                Log.e("server", "Error");
            }
            return view;
        }
    }
}