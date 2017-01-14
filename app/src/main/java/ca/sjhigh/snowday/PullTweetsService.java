package ca.sjhigh.snowday;

import android.app.Service;
import android.content.Intent;
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
        new NotificationHelper(this, BusDelays.class,
                this.getString(R.string.delay_notification_ticker),
                this.getString(R.string.delay_notification_title),
                this.getString(R.string.delay_notification_body), 7)
                .displayNotification();
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