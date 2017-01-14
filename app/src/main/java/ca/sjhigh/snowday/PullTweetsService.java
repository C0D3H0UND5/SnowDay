package ca.sjhigh.snowday;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
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

    private SharedPreferences preferences;
    // Task repeat interval, in minutes
    private int UPDATE_INTERVAL;
    private final int MINUTES_TO_MILLISECONDS = 60000;
    private Handler taskHandler;
    private Runnable runnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_SHORT).show();
         preferences = getApplicationContext()
                 .getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Get interval in milliseconds (1 minute)
        UPDATE_INTERVAL = preferences.getInt("key_interval", 0)*MINUTES_TO_MILLISECONDS;

        taskHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (UPDATE_INTERVAL > 0) {
                    // Run the repeated task
                    System.out.println("Running task every " + UPDATE_INTERVAL/MINUTES_TO_MILLISECONDS + " minutes");
                    executeTweetsTask();
                    taskHandler.postDelayed(runnable, UPDATE_INTERVAL);
                }
                else {
                    System.out.println("Time is 0 seconds, don't run task");
                }
            }
        };

        startRepeatingTask();
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
        stopRepeatingTask();
    }

    private void executeTweetsTask() {
        new GetTweetsAsync(this, DatabaseHelper.getSingletonInstance(this), preferences)
                .execute(preferences.getString("key_district", "ASD_South"));
    }

    /**
     * Starts the periodical update routine (taskStatusChecker adds the callback to the handler).
     */
     private synchronized void startRepeatingTask(){
        taskHandler.post(runnable);
     }

    /**
     * Stops the periodical update routine from running, by removing the callback.
     */
     private synchronized void stopRepeatingTask(){
        taskHandler.removeCallbacks(runnable);
     }
}