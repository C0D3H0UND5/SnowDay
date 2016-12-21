package ca.sjhigh.snowday;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {

    // private TwitterHelper twitterHelper;

    /** UI components **/
    private Button viewAllDelays;
    private TextView tweetList;

    /** Logic variables **/
    private String user;
    // private String lateBuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = "ASD_South";

        // Marry UI components in XML to their corresponding Java variable
        viewAllDelays = (Button)findViewById(R.id.viewDelays_button);
        tweetList = (TextView) findViewById(R.id.tweets_textView);
        tweetList.setMovementMethod(new ScrollingMovementMethod());

        //twitterHelper = new TwitterHelper(MainActivity.this, user);

        viewAllDelays.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                new SearchOnTwitter().execute(user);
            }
        });
    }

    /**
     * Android won't let us search Twitter on the main thread so we need to use a background thread
     */
    class SearchOnTwitter extends AsyncTask<String, Void, Integer> {
        private StringBuilder stringBuilder;
        private ArrayList<Tweet> tweets;
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

                // I'll leave this here incase we want to do queries in the future
                // Query query = new Query(params[0]);
                // QueryResult result = twitter.search(query);
                // List<twitter4j.Status> tweets = result.getTweets();

                List<twitter4j.Status> tweets = twitter.getUserTimeline(params[0]);
                stringBuilder = new StringBuilder();
                if(tweets != null){
                    for(twitter4j.Status tweet : tweets){
                        stringBuilder.append("@" + tweet.getUser().getScreenName() + " - " + tweet.getText() + "\n\n");
                        // this.tweets.add(new Tweet("@" + tweet.getUser().getScreenName(), tweet.getText()));
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
                tweetList.setText(stringBuilder);
            }
            else{
                Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        }
    }
}
