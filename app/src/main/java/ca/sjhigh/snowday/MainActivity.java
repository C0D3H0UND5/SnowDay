package ca.sjhigh.snowday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {

    /** UI components **/
    private Button getTweets;
    private Button viewDelays;
    private Button viewClosures;

    /** Logic variables **/
    private String user;
    // I want to have the app store all of the delays for the current day in a database. It will
    // delete all entries at the end of the day or individually if there is a correction tweet
    private DatabaseHelper myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = "ASD_South";
        myDatabase = new DatabaseHelper(MainActivity.this);

        // Marry UI components in XML to their corresponding Java variable
        getTweets = (Button)findViewById(R.id.twitter_main_button);
        viewDelays = (Button)findViewById(R.id.delays_main_button);
        viewClosures = (Button)findViewById(R.id.closures_main_button);

        /** Add click listeners **/
        getTweets.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                new GetTweets().execute(user);
            }
        });
        viewDelays.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Send intent to BusDelays.class with database helper
                Intent delays = new Intent(MainActivity.this, BusDelays.class);
                startActivity(delays);
            }
        });
        viewClosures.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Send intent to SchoolClosures.class with database helper
                Intent closures = new Intent(MainActivity.this, SchoolClosures.class);
                startActivity(closures);
            }
        });
    }

    /**
     * Android won't let us search Twitter on the main thread so we need to use a background thread
     * Some reading for the astute http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
     */
    class GetTweets extends AsyncTask<String, Void, Integer> {
        private final int SUCCESS = 0;
        private final int FAILURE = SUCCESS + 1;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.searching));
        }

        @Override
        protected Integer doInBackground(String... params) {
            try{
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setApplicationOnlyAuthEnabled(true);
                builder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
                builder.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));

                OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();

                builder = new ConfigurationBuilder();
                builder.setApplicationOnlyAuthEnabled(true);
                builder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
                builder.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));
                builder.setOAuth2TokenType(token.getTokenType());
                builder.setOAuth2AccessToken(token.getAccessToken());

                Twitter twitter = new TwitterFactory(builder.build()).getInstance();

                // I'll leave this here in case we want to do queries in the future
                // Query query = new Query(params[0]);
                // QueryResult result = twitter.search(query);
                // List<twitter4j.Status> tweets = result.getTweets();

                // Gets the tweets from newest to oldest
                List<twitter4j.Status> tweets = twitter.getUserTimeline(params[0]);
                // Changes the tweet order to be oldest to newest
                Collections.reverse(tweets);
                if(tweets != null){
                    for(twitter4j.Status tweet : tweets){
                        TwitterHelper.storeTweet(tweet, myDatabase);
                    }
                    return SUCCESS;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return FAILURE;
        }

        @Override
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);
            dialog.dismiss();
            if(result == SUCCESS){
                Toast.makeText(MainActivity.this, getString(R.string.success), Toast.LENGTH_LONG).show();
                // dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.success));
            }
            else{
                Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                // dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.error));
            }
        }
    }
}
