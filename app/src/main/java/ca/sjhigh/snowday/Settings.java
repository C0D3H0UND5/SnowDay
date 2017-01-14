package ca.sjhigh.snowday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings extends AppCompatActivity {

    /** UI components **/
    private EditText bus;
    private EditText pickup;
    private Button setBus;
    private Button setPickup;
    private Button clearPreferences;
    private Button resetTweet;
    private Spinner refreshInterval;
    private ToggleButton backgroundService;

    /** Logic variables **/
    private int busNumber;
    private String pickupTime;
    private String spinnerMessage;
    private Pattern timePattern = Pattern.compile("(\\d\\d?):(\\d\\d)");
    private Pattern intervalPattern  = Pattern.compile("\\d+|Never");
    private Matcher matcher;
    private final String MY_PREFERENCES = "my_preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Intent tweetServiceIntent;

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
        setBus = (Button)findViewById(R.id.bus_settings_button);
        setPickup = (Button)findViewById(R.id.pickup_settings_button);
        clearPreferences = (Button)findViewById(R.id.clear_settings_button);
        resetTweet = (Button)findViewById(R.id.reset_settings_button);
        refreshInterval = (Spinner)findViewById(R.id.interval_settings_spinner);
        backgroundService = (ToggleButton)findViewById(R.id.backgroundService_settings_toggleButton);

        bus.setHint("Bus currently: " + preferences.getInt("key_busNumber", 0));
        pickup.setHint("Pickup time currently: " + preferences.getString("key_pickupTime", "not set"));

        /** Add click listeners **/
        setBus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try{
                    // Store in shared preferences
                    busNumber = Integer.valueOf(bus.getText().toString());
                    editor.putInt("key_busNumber", busNumber);
                    editor.apply();
                }
                catch(NumberFormatException e){
                    // Clear and raise error, don't update
                    bus.setText("");
                    bus.setError("Must enter a numeric value");
                }
                finally{
                    // Clear and update
                    bus.setText("");
                    bus.setHint("Bus currently: " + busNumber);
                }
            }
        });
        setPickup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pickupTime = pickup.getText().toString();
                // Use regex to validate pickup time
                if (timePattern.matcher(pickupTime).find()) {
                    // Store in shared preferences
                    editor.putString("key_pickupTime", pickupTime);
                    editor.apply();
                    // Clear and update
                    pickup.setText("");
                    pickup.setHint("Pickup time currently: " + pickupTime);
                }
                else {
                    // Clear and raise error, don't update
                    bus.setText("");
                    pickup.setError("Please enter a valid time");
                }
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
        refreshInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                spinnerMessage = refreshInterval.getSelectedItem().toString();
                matcher = intervalPattern.matcher(spinnerMessage);
                while(matcher.find()){
                    if(matcher.group().equals("Never")){
                        editor.putInt("key_interval", 0);
                    }
                    else{
                        editor.putInt("key_interval", Integer.valueOf(matcher.group()));
                    }
                    System.out.println("Interval: " + matcher.group());
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        backgroundService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    // Turn on service
                    tweetServiceIntent = new Intent(getApplicationContext(), PullTweetsService.class);
                    startService(tweetServiceIntent);
                }
                else{
                    // Turn off service
                    tweetServiceIntent = new Intent(getApplicationContext(), PullTweetsService.class);
                    stopService(tweetServiceIntent);
                }
            }
        });
    }
}
