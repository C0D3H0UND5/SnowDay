package ca.sjhigh.snowday;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Jason on 2017-01-11.
 *
 * 1. Retrieve tweets periodically
 * 2. Filter tweets
 * 3. Update database
 * 4. Send notification
 */

public class PullTweetsService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences("my_preferences", MODE_PRIVATE);
        new GetTweetsAsync(this, DatabaseHelper.getSingletonInstance(this), preferences)
                .execute(preferences.getString("key_district", "ASD_South"));
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }
}