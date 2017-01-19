package ca.sjhigh.snowday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Jason on 2017-01-11.
 *
 * This app is hopefully going to be used to start the tweet retrieval service on device startup
 * TODO Get the service working reliably, then get this to work
 */

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
    }
}
