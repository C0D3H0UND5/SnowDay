package ca.sjhigh.snowday;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by Jason on 2017-01-11.
 *
 * TODO Test this some more. I don't think it is fully working
 */

public class PullTweetsService extends Service {

    private SharedPreferences preferences;
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
        /* Gets shared preferences */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get interval in milliseconds
        UPDATE_INTERVAL = Integer.valueOf(preferences.getString("key_refreshInterval", "0"))*MINUTES_TO_MILLISECONDS;

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

    /**
     * Runs the asynchronous task to gather tweets
     */
    private void executeTweetsTask() {
        Toast.makeText(getApplicationContext(), "Executing task", Toast.LENGTH_SHORT).show();
        new GetTweetsAsync(getApplicationContext(), DatabaseHelper
                .getSingletonInstance(getApplicationContext()), preferences)
                .execute(preferences.getString("key_schoolDistrict", "ASD_South"));
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