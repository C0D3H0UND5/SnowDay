package ca.sjhigh.snowday;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private TwitterHelper twitterHelper;

    private Button viewAllDelays;

    private String user;
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = "ASD_South";
        viewAllDelays = (Button)findViewById(R.id.viewDelays_button);
        viewAllDelays.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("Button Click", "Button clicked");
                if(num % 2 == 0){
                    viewAllDelays.setText("You pressed me!");
                    num++;
                }
                else{
                    viewAllDelays.setText("I'm back!");
                    num++;
                }
            }
        });

        Log.d("1", "Made twitter helper");
        twitterHelper = new TwitterHelper(MainActivity.this, user);
        Log.d("2", "Got tweets");
        //twitterHelper.getTweets();
        Log.d("3", "Done");
    }
}
