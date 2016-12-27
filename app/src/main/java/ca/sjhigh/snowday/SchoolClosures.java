package ca.sjhigh.snowday;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SchoolClosures extends AppCompatActivity {

    /** UI components **/
    private Button clearList;
    private TextView tweetList;

    /** Logic variables **/
    private DatabaseHelper myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_closures);

        myDatabase = new DatabaseHelper(SchoolClosures.this);

        // Marry UI components in XML to their corresponding Java variable
        clearList = (Button)findViewById(R.id.clear_closures_button);
        tweetList = (TextView)findViewById(R.id.tweets_closures_textView);

        // Set TextView to be scrollable
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
        for(Closure closure : closures){
            string += closure.toString() + "\n\n";
        }
        tweetList.setText(string);
    }

    /**
     * Deletes all of the records in the database
     */
    private void deleteRecords(){
        myDatabase.deleteAllClosures();
    }
}
