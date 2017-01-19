package ca.sjhigh.snowday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SchoolClosures extends AppCompatActivity {

    /** UI components **/
    private Button clearList;
    private TextView tweetList;

    /** Logic variables **/
    private DatabaseHelper myDatabase;
    private final String PREFERENCES_FILE_NAME = "my_preferences";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_closures);

        preferences = getApplicationContext().getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        myDatabase = DatabaseHelper.getSingletonInstance(SchoolClosures.this);

        clearList = (Button)findViewById(R.id.clear_closures_button);
        tweetList = (TextView)findViewById(R.id.tweets_closures_textView);

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
        String string = "";
        Closure[] closures = myDatabase.retrieveClosures();
        if (closures.length > 0) {
            for (Closure closure : closures) {
                string += closure.toString() + "\n\n";
            }
        }
        else{
            string = getString(R.string.no_closures);
        }
        tweetList.setText(string);
    }

    /**
     * Deletes all of the records in the database
     */
    private void deleteRecords(){
        myDatabase.deleteAllClosures();
    }

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
                new GetTweetsAsync(SchoolClosures.this, myDatabase, preferences)
                        .execute(preferences.getString("key_district", "ASD_South"));
                return true;
            case R.id.settings_child_action:
                Intent settings = new Intent(SchoolClosures.this, Settings.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
