package ca.sjhigh.snowday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BusDelays extends AppCompatActivity {

    /** UI components **/
    private Button clearList;
    private TextView tweetList;

    /** Logic variables **/
    private DatabaseHelper myDatabase;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_delays);

        /* Sets the default preferences to avoid situations where the user has not set them */
        PreferenceManager.setDefaultValues(this, R.xml.preference_settings, false);
        /* Gets shared preferences */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        /* Gets a copy of the database */
        myDatabase = DatabaseHelper.getSingletonInstance(BusDelays.this);

        clearList = (Button)findViewById(R.id.clear_delays_button);
        tweetList = (TextView)findViewById(R.id.tweets_delays_textView);

        tweetList.setMovementMethod(new ScrollingMovementMethod());

        /** Add click listeners **/
        clearList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                deleteRecords();
                tweetList.setText(null);
            }
        });
        displayRecords();
    }

    /**
     * Retrieves all of the records from the database and displays them
     */
    private void displayRecords(){
        TwitterHelper.cullDelays(myDatabase);
        String string = "";
        Delay[] delays = myDatabase.retrieveDelays();
        if (delays.length > 0){
            for(Delay delay : delays){
                string += delay.toString() + "\n\n";
            }
        }
        else{
            string = getString(R.string.no_delays);
        }
        tweetList.setText(string);
    }

    /**
     * Deletes all of the records in the database
     */
    private void deleteRecords(){
        myDatabase.deleteAllDelays();
    }

    /**
     * Adds the custom menu to this activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.child, menu);
        return true;
    }

    /**
     * Executes when an item in the actionbar is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.refresh_child_action:
                new GetTweetsAsync(BusDelays.this, myDatabase, preferences)
                        .execute(preferences.getString("key_schoolDistrict", "ASD_South"));
                return true;
            case R.id.settings_child_action:
                Intent settings = new Intent(BusDelays.this, Settings.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
