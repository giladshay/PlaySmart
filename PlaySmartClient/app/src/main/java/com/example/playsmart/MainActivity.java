/*
  MainActivity file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.playsmart.ui.bands.BandsFragment;
import com.example.playsmart.ui.home.HomeFragment;
import com.example.playsmart.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import static com.example.playsmart.R.drawable;
import static com.example.playsmart.R.id;
import static com.example.playsmart.R.layout;
import static com.example.playsmart.R.menu.top_menu;

public class MainActivity extends AppCompatActivity {
    Fragment fragment = null;
    SharedPreferences sp;
    Intent svc;
    BroadCastBattery broadCastBattery;
    int battery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("details1", 0);
        setContentView(layout.activity_main);

        // create music service
        svc = new Intent(this, MusicService.class);
        String genre = sp.getString("genre", "jazz");
        svc.putExtra("music", genre);
        startService(svc);

        // create broadcast receiver that listens to battery
        broadCastBattery = new BroadCastBattery();

        // create bottom navigation bar
        final BottomNavigationView navView = findViewById(id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                id.navigation_home, id.navigation_profile, id.navigation_bands)
                .build();
        final NavController navController = Navigation.findNavController(this, id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // replace between fragment when bottom navigation bar item is clicked
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        fragment = null;
                switch (menuItem.getItemId()) {
                    case id.navigation_home:
                        fragment = new HomeFragment();
                        break;
                    case id.navigation_profile:
                        fragment = new ProfileFragment();
                        break;
                    case id.navigation_bands:
                        fragment = new BandsFragment();
                        break;
                }
                if (fragment != null) {
                    Fragment curFrag = getSupportFragmentManager().getPrimaryNavigationFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    if (curFrag != null)
                        ft.detach(curFrag);
                    ft.replace(id.nav_host_fragment, fragment);
                    ft.commit();
                }
                menuItem.setChecked(true);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create menu in top of screen
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // when one of top_menu items is clicked
        switch (item.getItemId()) {
            case id.logout: // if logout is clicked
                // logout
                SharedPreferences.Editor editor = sp.edit();
                editor.clear().apply(); // clear shared preferences
                finish(); // go back to LoginActivity
                return true;
            case id.stopOrPlayMusic: // if stopOrPlayMusic is clicked
                if (item.getIcon().getConstantState().equals(getResources().getDrawable(drawable.ic_stop_black_24dp).getConstantState())) { // if item icon is stop icon
                    // stop service and replace icon to play icon
                    stopService(svc);
                    item.setIcon(getResources().getDrawable(drawable.ic_play_arrow_black_24dp));
                } else { // if item icon is play icon
                    // start service and replace icon to stop icon
                    startService(svc);
                    item.setIcon(getResources().getDrawable(drawable.ic_stop_black_24dp));
                }
                return true;
            case id.battery: // if battery is clicked
                // create Toast with percentage left in battery
                Toast.makeText(this, String.valueOf(battery) + "% left", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class BroadCastBattery extends BroadcastReceiver{
        // broadcast receiver that listens tob battery
        @Override
        public void onReceive(Context context, Intent intent) {
            // when battery is changed
            battery = intent.getIntExtra("level", 0); // get battery level
            invalidateOptionsMenu(); // go to onPrepareOptionsMenu
        }
    }

    @Override
    protected void onResume() {
        // start broadcast receiver listens to battery
        super.onResume();
        registerReceiver(broadCastBattery, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        // stop listens to battery
        super.onPause();
        unregisterReceiver(broadCastBattery);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // when options menu is enabled (when battery level has been changed)
        MenuItem batteryItem = menu.findItem(id.battery);
        if (battery > 50) // if battery level is bigger than 50
            batteryItem.setIcon(drawable.ic_battery_80_black_24dp); // set icon to full battery icon
        else if (battery > 20) // if battery level is between 20 to 50
            batteryItem.setIcon(drawable.ic_battery_50_black_24dp); // set icon to half full battery icon
        else // if less than 20 percent left in battery
            batteryItem.setIcon(drawable.ic_battery_20_black_24dp); // set icon to empty battery icon
        return true;
    }
}