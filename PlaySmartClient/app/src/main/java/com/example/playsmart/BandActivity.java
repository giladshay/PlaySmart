/*
  BandActivity file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class BandActivity extends AppCompatActivity {

    TextView name, genre;
    ListView instruments;
    InstrumentAdapter instrumentAdapter;
    Intent intent;
    JSONObject JSONInstrument, JSONBand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band);

        intent = getIntent();
        try {
            JSONBand = new JSONObject(intent.getExtras().getString("bandInfo")); // get band info from intent extras
            // get band info from server
            Writer w = new Writer();
            String response = w.execute(getString(R.string.server_host) + "/get_band_info", JSONBand.toString()).get();
            JSONObject jsonResponse = new JSONObject(response);
            // get instrument number from response
            JSONInstrument = jsonResponse.getJSONObject("instruments");
        } catch (Exception e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }

        name = findViewById(R.id.tvBandActivityName);
        genre = findViewById(R.id.tvBandActivityGenre);
        instruments = findViewById(R.id.lvInstrumentsActivityBand);

        // set textview to information
        try {
            name.setText(JSONBand.getString("name"));
            genre.setText(JSONBand.getString("genre"));

            // create instruments list
            instrumentAdapter = new InstrumentAdapter();
            instruments.setAdapter(instrumentAdapter);

        } catch (Exception e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    class InstrumentAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // get length of instrument list
            return JSONInstrument.length();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            // get item view in list
            View view = getLayoutInflater().inflate(R.layout.instrument_layout, null);
            final TextView instrumentName = view.findViewById(R.id.tvInstrumentName);
            final EditText instrumentCount = view.findViewById(R.id.tvNumberOfPlayers);
            Button btnAdd = view.findViewById(R.id.btnAddPlayer);
            Button btnSub = view.findViewById(R.id.btnSubPlayer);
            btnAdd.setVisibility(View.INVISIBLE);
            btnSub.setVisibility(View.INVISIBLE);
            instrumentName.setText(getResources().getStringArray(R.array.instruments)[position]); // set text to instrument
            try {
                // set amount of instrument players
                Integer temp=JSONInstrument.getInt(getResources().getStringArray(R.array.instruments)[position]);
                instrumentCount.setText(temp.toString());
            } catch (Exception e) {
                Toast.makeText(BandActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
            instrumentCount.setEnabled(false);

            return view;
        }
    }
}
