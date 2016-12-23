package ca.sjhigh.snowday;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BusDelays extends AppCompatActivity {

    /** UI components **/
    private Button clearList;
    private Button deleteAll;
    private TextView tweetList;

    /** Logic variables **/
    private DatabaseHelper myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_delays);

        myDatabase = new DatabaseHelper(BusDelays.this);

        // Marry UI components in XML to their corresponding Java variable
        clearList = (Button)findViewById(R.id.clear_delays_button);
        deleteAll = (Button)findViewById(R.id.delete_delays_button);
        tweetList = (TextView)findViewById(R.id.tweets_delays_textView);

        // Set TextView to be scrollable
        tweetList.setMovementMethod(new ScrollingMovementMethod());

        /** Add click listeners **/
        clearList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Add this button in BusDelay
                tweetList.setText(null);
            }
        });
        deleteAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Add this button in BusDelay
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
        // Change to retrieve delays
        String string = "Current delays\n\n";
        Tweet[] tweets = myDatabase.retrieveAllRecords();
        for(Tweet tweet : tweets){
            string += tweet.toString() + "\n\n";
        }
        tweetList.setText(string);
    }

    /**
     * Deletes all of the records in the database
     */
    private void deleteRecords(){
        // Change to delete delays
        myDatabase.deleteAll();
    }
}