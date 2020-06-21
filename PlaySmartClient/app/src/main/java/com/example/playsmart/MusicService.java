/*
  MusicService file
  Created by: Gil-Ad Shay
  Last edited: 03.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

@SuppressLint("Registered")
public class MusicService extends Service {
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        // when service is created
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.jazz);
        player.setLooping(true); // Set looping true
    }

    // when service is started
    public int onStartCommand(Intent intent, int flags, int startId) {
        String music = intent.getStringExtra("music");
        if (music.equals("Jazz")) // if music equals to Jazz
            player = MediaPlayer.create(this, R.raw.jazz); // play jazz music
        else if (music.equals("Rock")) // if music equals to Rock
            player = MediaPlayer.create(this, R.raw.rock); // play rock music
        else if (music.equals("Metal")) // if music equals to Metal
            player = MediaPlayer.create(this, R.raw.metal); // play metal music
        player.setLooping(true); // set looping true (music will loop)
        player.start(); // start playing the music
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }
}
