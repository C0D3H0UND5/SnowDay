package ca.sjhigh.snowday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
        TODO Allow user to store multiple buses

        TODO Allow user to select between ASD-South and ASD-West
     */
public class MainActivity extends AppCompatActivity {

    /** UI components **/
    private Button viewDelays;
    private Button viewClosures;
    private TextView userInfo;

    /** Logic variables **/
    private DatabaseHelper myDatabase;
    private final String DATABASE_POSITION_FILE_NAME = "database_files";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Sets the default preferences to avoid situations where the user has not set them */
        PreferenceManager.setDefaultValues(this, R.xml.preference_settings, false);
        /* Gets shared preferences */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        /* Gets a copy of the database */
        myDatabase = DatabaseHelper.getSingletonInstance(MainActivity.this);

        // Marry UI components in XML to their corresponding Java variable
        viewDelays = (Button)findViewById(R.id.delays_main_button);
        viewClosures = (Button)findViewById(R.id.closures_main_button);
        userInfo = (TextView)findViewById(R.id.information_main_textView);

        int busNumber = Integer.valueOf(preferences.getString("key_busNumber", "0"));
        String pickupTime = preferences.getString("key_pickupTime", "not set");
        userInfo.setText(
                "Current bus: " + ((busNumber == 0)? "not set" : busNumber) +
                "\n\nNormal pickup time: " + pickupTime + "\n\nExpected pickup time: " +
                (pickupTime.equals("not set")? "not available" :
                TwitterHelper.addTime(pickupTime, myDatabase.retrieveDelay(busNumber).getDelay())));

        /** Add click listeners **/
        viewDelays.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent delays = new Intent(MainActivity.this, BusDelays.class);
                startActivity(delays);
            }
        });
        viewClosures.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int busNumber = Integer.valueOf(preferences.getString("key_busNumber", "0"));
        String pickupTime = preferences.getString("key_pickupTime", "not set");
        userInfo.setText(
                "Current bus: " + ((busNumber == 0)? "not set" : busNumber) +
                "\nCurrent pickup time: " + pickupTime + "\nExpected pickup time: " +
                (pickupTime.equals("not set")? "not availabale" :
                TwitterHelper.addTime(pickupTime, myDatabase.retrieveDelay(busNumber).getDelay())));
    }

    /**
     * Executes when the app is closed permanently
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        getApplicationContext().getSharedPreferences(DATABASE_POSITION_FILE_NAME, MODE_PRIVATE)
                .edit()
                .putInt("key_delay", myDatabase.getLatestDelay())
                .putInt("key_closure", myDatabase.getLatestClosure())
                .apply();
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
            case R.id.settings_main_action:
                Intent settings = new Intent(MainActivity.this, Settings.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
