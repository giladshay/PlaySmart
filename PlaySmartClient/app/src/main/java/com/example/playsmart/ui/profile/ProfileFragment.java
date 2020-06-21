/*
  ProfileFragment file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.playsmart.R;

public class ProfileFragment extends Fragment {

    TextView tvUsername, tvEmail, tvInstrument, tvGenre;
    SharedPreferences sp;
    String username;
    String email;
    String instrument;
    String genre;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = root.findViewById(R.id.tvProfileUsername);
        tvEmail = root.findViewById(R.id.tvProfileEmail);
        tvInstrument = root.findViewById(R.id.tvProfileInstrument);
        tvGenre = root.findViewById(R.id.tvProfileGenre);

        // get user information
        sp = this.getActivity().getSharedPreferences("details1", 0);
        username = sp.getString("username", null);
        email = sp.getString("email", null);
        instrument = sp.getString("instrument", null);
        genre = sp.getString("genre", null);

        // if user information is not null
        if (username != null && email != null && instrument != null && genre != null) {
            // set text views to user information
            tvUsername.setText(username);
            tvEmail.setText(email);
            tvInstrument.setText(instrument);
            tvGenre.setText(genre);
        }
        return root;
    }
}