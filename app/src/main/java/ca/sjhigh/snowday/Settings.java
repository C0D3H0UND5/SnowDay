package ca.sjhigh.snowday;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * TODO Try and tidy up the interface and make it more user-friendly
 */
public class Settings extends AppCompatActivity {

    private static final String BUS_NUMBER = "key_busNumber";
    private static final String PICKUP_TIME = "key_pickupTime";
    private static final String SCHOOL_DISTRICT = "key_schoolDistrict";
    private static final String REFRESH_INTERVAL = "key_refreshInterval";
    private static final String TWEET_SERVICE = "key_runTweetService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
