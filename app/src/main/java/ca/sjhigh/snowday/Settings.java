package ca.sjhigh.snowday;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity {

    /** UI components **/
    private EditText bus;
    private EditText pickup;
    private Button getBus;
    private Button getPickup;
    private Button clearPreferences;
    private Button resetTweet;

    /** Logic variables **/
    private int busNumber;
    private String pickupTime;
    private final String MY_PREFERENCES = "my_preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Prepare shared preferences
        preferences = getApplicationContext().getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();

        // Marry UI components in XML to their corresponding Java variable
        bus = (EditText)findViewById(R.id.bus_settings_editText);
        pickup = (EditText) findViewById(R.id.pickup_settings_editText);
        getBus = (Button)findViewById(R.id.bus_settings_button);
        getPickup = (Button)findViewById(R.id.pickup_settings_button);
        clearPreferences = (Button)findViewById(R.id.clear_settings_button);
        resetTweet = (Button)findViewById(R.id.reset_settings_button);

        bus.setHint("Bus currently: " + preferences.getInt("key_busNumber", 0));
        pickup.setHint("Pickup time currently: " + preferences.getString("key_pickupTime", "not set"));

        /** Add click listeners **/
        getBus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Store in shared preferences
                busNumber = Integer.valueOf(bus.getText().toString());
                editor.putInt("key_busNumber", busNumber);
                editor.apply();
                // Clear and update
                bus.setText("");
                bus.setHint("Bus now: " + busNumber);
            }
        });
        getPickup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Store in shared preferences
                pickupTime = pickup.getText().toString();
                editor.putString("key_pickupTime", pickupTime);
                editor.apply();
                // Clear and update
                pickup.setText("");
                pickup.setHint("Pickup time now: " + pickupTime);
            }
        });
        clearPreferences.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                editor.clear();
                editor.apply();
                bus.setHint("Bus currently not set");
                pickup.setHint("Pickup time currently not set");
            }
        });
        resetTweet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                editor.remove("key_tweetId");
                editor.apply();
            }
        });
    }
}
