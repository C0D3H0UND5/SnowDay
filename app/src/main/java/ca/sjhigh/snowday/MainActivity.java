package ca.sjhigh.snowday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /** UI components **/
    private Button viewDelays;
    private Button viewClosures;
    private TextView userInfo;

    /** Logic variables **/
    private String user;
    private long tweetId;
    // I want to have the app store all of the delays for the current day in a database. It will
    // delete all entries at the end of the day or individually if there is a correction tweet
    private DatabaseHelper myDatabase;
    // I want to allow multiple buses to be added so I'll probably have to make a preference object
    // and then store them in a List. The only problem will be naming the groups or the keys
    private final String PREFERENCES_FILE_NAME = "my_preferences";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = "ASD_South";
        // user = "ASD_West";
        // ASD-W structures their tweets differently so in order to support that this would require
        // a filter overhaul
        myDatabase = new DatabaseHelper(MainActivity.this);

        // Prepare shared preferences
        preferences = getApplicationContext().getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);

        // Marry UI components in XML to their corresponding Java variable
        viewDelays = (Button)findViewById(R.id.delays_main_button);
        viewClosures = (Button)findViewById(R.id.closures_main_button);
        userInfo = (TextView)findViewById(R.id.personal_main_textView);

        int busNumber = preferences.getInt("key_busNumber", 0);
        String pickupTime = preferences.getString("key_pickupTime", "not set");
        userInfo.setText(
                "Current bus: " + ((busNumber == 0)? "not set" : busNumber) +
                "\n\nCurrent pickup time: " + pickupTime + "\n\nExpected pickup time: " +
                (pickupTime.equals("not set")? "not availabale" :
                TwitterHelper.addTime(pickupTime, myDatabase.retrieveDelay(busNumber).getDelay())));

        /** Add click listeners **/
        viewDelays.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Start BusDelays.class
                Intent delays = new Intent(MainActivity.this, BusDelays.class);
                startActivity(delays);
            }
        });
        viewClosures.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Start SchoolClosures.class
                Intent closures = new Intent(MainActivity.this, SchoolClosures.class);
                startActivity(closures);
            }
        });
    }

    /**
     * Executes when focus is regained on this activity (i.e. returning from another activity)
     */
    @Override
    public void onResume(){
        super.onResume();
        preferences = getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        int busNumber = preferences.getInt("key_busNumber", 0);
        String pickupTime = preferences.getString("key_pickupTime", "not set");
        userInfo.setText(
                "Current bus: " + ((busNumber == 0)? "not set" : busNumber) +
                "\n\nCurrent pickup time: " + pickupTime + "\n\nExpected pickup time: " +
                (pickupTime.equals("not set")? "not availabale" :
                TwitterHelper.addTime(pickupTime, myDatabase.retrieveDelay(busNumber).getDelay())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Executes when an item in the actionbar is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.refresh_main_action:
                new GetTweetsAsync(MainActivity.this, myDatabase, preferences).execute(user);
                userInfo.setText(String.valueOf(preferences.getLong("key_tweetId", 1)));
                return true;
            case R.id.settings_main_action:
                Intent settings = new Intent(MainActivity.this, Settings.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
