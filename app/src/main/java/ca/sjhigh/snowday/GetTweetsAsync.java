package ca.sjhigh.snowday;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Jason on 2016-12-27.
 *
 * Android won't let us search Twitter on the main thread so we need to use a background thread
 * Some reading for the astute http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
 *
 */

class GetTweetsAsync extends AsyncTask<String, Void, Integer> {

    private Context context;
    private DatabaseHelper databaseHelper;
    private SharedPreferences preferences;

    private NotificationHelper newDelays;
    private NotificationHelper newClosures;

    private final int DELAY_NOTIFICATION_ID = 1;
    private final int CLOSURE_NOTIFICATION_ID = 2;
    private final int SUCCESS = 0;
    private final int FAILURE = SUCCESS + 1;
    private ProgressDialog dialog;

    GetTweetsAsync(Context context, DatabaseHelper databaseHelper, SharedPreferences preferences){
        this.context = context;
        this.databaseHelper = databaseHelper;
        this.preferences = preferences;
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.searching));
        dialog.setCancelable(true);

        newDelays = new NotificationHelper(context, BusDelays.class,
                context.getString(R.string.delay_notification_title),
                context.getString(R.string.delay_notification_body),
                context.getString(R.string.delay_notification_ticker), DELAY_NOTIFICATION_ID);
        newClosures = new NotificationHelper(context, SchoolClosures.class,
                context.getString(R.string.closure_notification_title),
                context.getString(R.string.closure_notification_body),
                context.getString(R.string.closure_notification_ticker), CLOSURE_NOTIFICATION_ID);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        try{
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setApplicationOnlyAuthEnabled(true);
            builder.setOAuthConsumerKey(context.getString(R.string.twitter_consumer_key));
            builder.setOAuthConsumerSecret(context.getString(R.string.twitter_consumer_secret));

            OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();

            builder = new ConfigurationBuilder();
            builder.setApplicationOnlyAuthEnabled(true);
            builder.setOAuthConsumerKey(context.getString(R.string.twitter_consumer_key));
            builder.setOAuthConsumerSecret(context.getString(R.string.twitter_consumer_secret));
            builder.setOAuth2TokenType(token.getTokenType());
            builder.setOAuth2AccessToken(token.getAccessToken());

            Twitter twitter = new TwitterFactory(builder.build()).getInstance();

            // Gets the tweets from newest to oldest
            long tweetId = preferences.getLong("key_tweetId", 1);
            List<twitter4j.Status> tweets = twitter.getUserTimeline(params[0], new Paging().sinceId(tweetId));
            // The first tweet is the latest so it will be stored as the most recently checked
            if(tweets.size() > 0){
                preferences.edit().putLong("key_tweetId", tweets.get(0).getId()).apply();

            }
            // Changes the tweet order to be oldest to newest
            Collections.reverse(tweets);
            if(tweets.size() > 0){
                for(twitter4j.Status tweet : tweets){
                    TwitterHelper.storeTweet(tweet, databaseHelper);
                }
            }
            return SUCCESS;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return FAILURE;
    }

    @Override
    protected void onPostExecute(Integer result){
        super.onPostExecute(result);
        if(result == SUCCESS){
            if(databaseHelper.retrieveDelays().length > databaseHelper.getLatestDelay()){
                databaseHelper.setLatestDelay(databaseHelper.retrieveDelays().length);
                TwitterHelper.cullDelays(databaseHelper);
                if(databaseHelper.retrieveDelays().length > 0)
                    newDelays.displayNotification();
            }
            if(databaseHelper.retrieveClosures().length > databaseHelper.getLatestClosure()){
                databaseHelper.setLatestClosure(databaseHelper.retrieveClosures().length);
                TwitterHelper.cullClosures(databaseHelper);
                if(databaseHelper.retrieveClosures().length > 0)
                    newClosures.displayNotification();
            }
        }
        else{
            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_LONG).show();
        }
    }
}
