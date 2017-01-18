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

    private NotificationManager notificationManager;

    /**
     * Creates a new Notification object and tracks the necessary values to build and display it
     * @param context The context to display in
     * @param c The class for the notification to direct to
     * @param title The title for this notification
     * @param body The body / description text of this notification
     * @param ticker The text to be displayed in the status bar when the notification is issued
     * @param notificationId The unique identifier of this notification used by the system
     */
    NotificationHelper(Context context, Class c, String title, String body, String ticker,
                       int notificationId) {
        this.context = context;
        this.c = c;
        this.title = title;
        this.body = body;
        this.ticker = ticker;
        this.notificationId = notificationId;
    }

    /**
     * This method overloads the other method creating a pseudo-optional parameter
     */
    public void displayNotification(){
        displayNotification(false);
    }

    /**
     * Displays a notification to the android system
     * @param isOngoing tells the system whether the notification should be persistent
     */
    public void displayNotification(boolean isOngoing) {
        try{
            Intent intent = new Intent(context, c);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
             notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_tweet_refresh)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setTicker(ticker)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setOngoing(isOngoing);
            if(!isOngoing){
                notificationBuilder.setAutoCancel(true).setSmallIcon(R.mipmap.ic_notification);
            }
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void clearNotification(){
        notificationManager.cancel(notificationId);
    }
}
