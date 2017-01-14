package ca.sjhigh.snowday;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Jason on 2017-01-12.
 *
 * This class pushes a notification to the system using the passed parameters
 */

class NotificationHelper {

    private Context context;
    private Class c;
    private String title;
    private String body;
    private String ticker;
    private int notificationId;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private Intent intent;
    private PendingIntent pendingIntent;

    NotificationHelper(Context context, Class c, String title, String body, String ticker,
                       int notificationId) {
        this.context = context;
        this.c = c;
        this.title = title;
        this.body = body;
        this.ticker = ticker;
        this.notificationId = notificationId;
    }

    public void displayNotification() {
        try{
            intent = new Intent(context, c);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setTicker(ticker)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
