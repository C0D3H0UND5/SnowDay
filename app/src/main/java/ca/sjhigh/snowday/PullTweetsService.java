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
    private final int SERVICE_NOTIFICATION_ID = 0;
    private final int MINUTES_TO_MILLISECONDS = 60000;
    private int UPDATE_INTERVAL;
    private Handler taskHandler;
    private Runnable runnable;
    private NotificationHelper serviceNotification;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceNotification = new NotificationHelper(getApplicationContext(), Settings.class,
                getApplicationContext().getString(R.string.service_notification_title),
                getApplicationContext().getString(R.string.service_notification_body),
                getApplicationContext().getString(R.string.service_notification_ticker),
                SERVICE_NOTIFICATION_ID);
         preferences = getApplicationContext()
                 .getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Get interval in milliseconds
        UPDATE_INTERVAL = preferences.getInt("key_interval", 0)*MINUTES_TO_MILLISECONDS;

        taskHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (UPDATE_INTERVAL > 0) {
                    // Run the repeated task
                    executeTweetsTask();
                    taskHandler.postDelayed(runnable, UPDATE_INTERVAL);
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
        super.onCreate();
        Toast.makeText(this, "Service created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    private void executeTweetsTask() {
        Toast.makeText(getApplicationContext(), "Executing task", Toast.LENGTH_SHORT).show();
        new GetTweetsAsync(getApplicationContext(), DatabaseHelper
                .getSingletonInstance(getApplicationContext()), preferences)
                .execute(preferences.getString("key_district", "ASD_South"));
    }

    /**
     * Starts the periodical update routine (taskStatusChecker adds the callback to the handler).
     */
     private synchronized void startRepeatingTask(){
        taskHandler.post(runnable);
        serviceNotification.displayNotification(true);
     }

    /**
     * Stops the periodical update routine from running, by removing the callback.
     */
    private synchronized void stopRepeatingTask(){
        taskHandler.removeCallbacks(runnable);
        serviceNotification.clearNotification();
    }
}