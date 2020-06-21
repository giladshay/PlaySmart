/*
  NewBand activity file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewBand extends AppCompatActivity implements View.OnClickListener {

    EditText etBandName;
    Button btnSubmitNewBand, btnCancelNewBand;
    TextView genre;
    ListView instrumentList;
    SharedPreferences sp;
    String playerInstrument, playerName;
    Help help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_band);

        sp = getSharedPreferences("details1", 0);
        playerInstrument = sp.getString("instrument", null);
        String playerGenre = sp.getString("genre", null);
        playerName = sp.getString("username", null);
        etBandName = findViewById(R.id.etBandName);
        btnSubmitNewBand = findViewById(R.id.btnSubmitNewBand);
        btnCancelNewBand = findViewById(R.id.btnCancelNewBand);
        genre = findViewById(R.id.tvBandGenre);
        // set genre textview text
        if (playerGenre != null)
            genre.setText(playerGenre);

        // creates instrument list
        instrumentList = findViewById(R.id.lvInstruments);
        InstrumentAdapter instrumentAdapter = new InstrumentAdapter();
        instrumentList.setAdapter(instrumentAdapter);

        // create new instrument array using help class
        help = new Help();
        help.createInstrumentArray(getResources().getStringArray(R.array.instruments));

        btnSubmitNewBand.setOnClickListener(this);
        btnCancelNewBand.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v == btnSubmitNewBand) { // if btnSubmitNewBand is clicked
            int l = help.getInstrumentArray().length;
            for (int i=0; i<l; i++) {
                // put number of player for each instrument and put it to intent extras
                Instrument instrument = (Instrument) instrumentList.getAdapter().getItem(i);
                intent.putExtra(instrument.getName(), instrument.getNumber());
            }
            // put band info into intent extras
            intent.putExtra("BandName",etBandName.getText().toString());
            intent.putExtra("BandGenre", genre.getText().toString());
            intent.putExtra("BandAdmin", playerName);

            setResult(RESULT_OK, intent); // send result to OK
        }
        else if (v == btnCancelNewBand) { // if btnCancelNewBand is clicked
            setResult(RESULT_CANCELED, intent);
        }
        finish(); // go back to bands fragment
    }

    private class InstrumentAdapter extends BaseAdapter {
        // instrument adapter
        @Override
        public int getCount() {
            // return length of instrument list
            return getResources().getStringArray(R.array.instruments).length;
        }

        @Override
        public Instrument getItem(int position) {
            // return instrument
            return help.getInstrumentArray()[position];
        }

        @Override
        public long getItemId(int position) {
            // return position
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // return instruments list item view
            final int min; // minimum number of players (0 - normal, 1 - for the user's instrument [-user must be in his band])
            View view = getLayoutInflater().inflate(R.layout.instrument_layout, null);
            final TextView tvInstrumentName = view.findViewById(R.id.tvInstrumentName);
            final EditText tvNumberOfPlayers = view.findViewById(R.id.tvNumberOfPlayers);
            final Instrument currentInstrument = help.getInstrumentArray()[position];
            tvInstrumentName.setText(getResources().getStringArray(R.array.instruments)[position]); // get the name by the resource file
            // select minimun number of players
            if (tvInstrumentName.getText().toString().equals(playerInstrument))
                min = 1;
            else
                min = 0;
            Button btnAddPlayer = view.findViewById(R.id.btnAddPlayer);
            Button btnSubPlayer = view.findViewById(R.id.btnSubPlayer);
            // create Instrument that keeps the number
            currentInstrument.setNumber(min);
            tvNumberOfPlayers.setText(Integer.toString(currentInstrument.getNumber()));
            // 'increase' and 'decrease' are in the Instrument class
            btnAddPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentInstrument.increase();
                    tvNumberOfPlayers.setText(Integer.toString(currentInstrument.getNumber()));
                }
            });
            btnSubPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentInstrument.decrease(min);
                    tvNumberOfPlayers.setText(Integer.toString(currentInstrument.getNumber()));
                }
            });
            tvNumberOfPlayers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) // when player clicks on something else, the number updates
                        currentInstrument.setNumber(Integer.valueOf(tvNumberOfPlayers.getText().toString()));
                }
            });
            return view;
        }
    }
}
