/*
  BandsFragment file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart.ui.bands;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.playsmart.BandActivity;
import com.example.playsmart.NewBand;
import com.example.playsmart.R;
import com.example.playsmart.Writer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static android.app.Activity.RESULT_OK;

public class BandsFragment extends Fragment implements View.OnClickListener {

    ListView bandList;
    SharedPreferences sp;
    JSONArray jsonBands;
    Button newBand;
    JSONObject bandInfo, instrumentJSON, band;
    BandAdapter bandAdapter;
    Writer w;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bands, container, false);
        bandList = root.findViewById(R.id.lsvBandList);
        newBand = root.findViewById(R.id.btnNewBand);
        newBand.setOnClickListener(this);
        sp = this.getActivity().getSharedPreferences("details1", 0);
        try {
            // send username to server and wait for response
            JSONObject username = new JSONObject();
            username.put("username", sp.getString("username", null));
            w = new Writer();
            String response = w.execute(getString(R.string.server_host) + "/bands", username.toString()).get();
            JSONObject jsonResponse = new JSONObject(response);

            // put all the bands that have been received from the server into jsonBands
            jsonBands = jsonResponse.getJSONArray("bands");
        } catch (Exception e) {
            Toast.makeText(this.getContext(), "Error!", Toast.LENGTH_SHORT).show();
        }

        // create bands list
        bandAdapter = new BandAdapter();
        bandList.setAdapter(bandAdapter);
        return root;
    }

    @Override
    public void onClick(View v) {
        if (v == newBand) { // if newBand Button is clicked
            // start activity NewBand and get result
            Intent intent = new Intent(this.getActivity(), NewBand.class);
            startActivityForResult(intent, 2);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get the result from activity
        super.onActivityResult( requestCode, resultCode, data);

        if (requestCode == 2) { // if the activity was NewBand activity
            if (resultCode == RESULT_OK) { // if the result was OK
                // get band information
                String bandName = data.getStringExtra("BandName");
                String bandGenre = data.getStringExtra("BandGenre");
                String bandAdmin = data.getStringExtra("BandAdmin");

                bandInfo = new JSONObject(); // all the information
                instrumentJSON = new JSONObject(); // the number of each instrument
                band = new JSONObject(); // the band information

                try {
                    // put band information in band (JSONObject)
                    band.put("name", bandName);
                    band.put("genre", bandGenre);
                    band.put("adminUser", bandAdmin);
                }
                catch (JSONException e) {
                    Toast.makeText(this.getContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
                // get number of instruments for each instrument
                for (int i = 0; i < getResources().getStringArray(R.array.instruments).length; i ++) {
                    try {
                        instrumentJSON.put(getResources().getStringArray(R.array.instruments)[i],
                                data.getIntExtra(getResources().getStringArray(R.array.instruments)[i], 0));
                    }
                    catch (JSONException e) {
                        Toast.makeText(this.getContext(), "Error!", Toast.LENGTH_SHORT).show();
                    }
                }

                // unite the band info with the instruments info
                try {
                    bandInfo.put("bandInfo", band);
                    bandInfo.put("instruments", instrumentJSON);
                } catch (JSONException e) {
                    Toast.makeText(this.getContext(), "Error!", Toast.LENGTH_SHORT).show();
                }

                createNewBandInServer(bandInfo); // sends new band information to the server and creates it
            }
        }
    }

    private void createNewBandInServer(JSONObject band) {
        // sends new band information to the server and creates it
        w = new Writer();
        try {
            // get response from the server
            String response = w.execute(getString(R.string.server_host) + "/new_band", band.toString()).get();
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getString("Status").equals("OK")) { // if response status is OK
                // update bands list
                jsonBands.put(new JSONObject(jsonResponse.getJSONObject("Band").toString()));
                bandAdapter.notifyDataSetChanged();
                bandList.setAdapter(bandAdapter);
            }
        } catch (Exception e) {
            Toast.makeText(this.getContext(), "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    class BandAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // return the length of bands list
            return jsonBands.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // return item view from list
            View view = getLayoutInflater().inflate(R.layout.band_layout, null);
            Button btnBandName = view.findViewById(R.id.btnBand);
            try {
                btnBandName.setText(jsonBands.getJSONObject(position).getString("name"));
                // if btnBandName is clicked - move to BandActivity
                btnBandName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), BandActivity.class);
                        try {
                            // put band information in the intent extras
                            intent.putExtra("bandInfo", jsonBands.get(position).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return view;
        }
    }
}