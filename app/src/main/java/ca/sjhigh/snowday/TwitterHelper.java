package ca.sjhigh.snowday;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by Jason on 2016-12-20.
 */

public class TwitterHelper{

    /** User to get tweets from **/
    private String user;
    /** User's transportation information **/
    // private Tweet tweet;
    // private int busNumber;
    // private String pickupTime;
    private int delay;
    private int number;
    /** Twitter object using credentials in twitter4j.properties **/
    private Twitter twitter;
    /** Regex filtering stuff **/
    private Pattern pattern = Pattern.compile("\\d|one");
    private Matcher matcher;
    /** Database helper **/
    private DatabaseHelper myDatabase;

    public TwitterHelper(Context context, String user) {
        myDatabase = new DatabaseHelper(context);
        this.user = user;

        twitter = TwitterFactory.getSingleton();
    }

    public String getDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH) + 1);
    }

    /**
     * Gets the 20 latest tweets from a users timeline
     * @return A list of the tweets
     */
    public List<Status> getTweets(){
        List<Status> statuses;
        try{
            statuses = twitter.getUserTimeline(user);
            return statuses;
        }
        catch(TwitterException e) {
            e.printStackTrace();
            System.out.println("Failed to get timeline: " + e.getMessage());
            return null;
        }
    }

    public void retrieveLateBuses(){
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        List<Status> statuses = getTweets();
        for(Status status : statuses){
            // Gets the body of the tweet
            String text = status.getText().toLowerCase();
            // If the tweet contains bus then the bus is either late or being corrected as on time
            if(text.contains("bus") /*&& text.contains(busNumber)*/){
                matcher = pattern.matcher(text);
                // If the tweet contains 'late' or 'delay' then a bus is running late
                if(text.contains("late") || text.contains("delay")){
                    // System.out.println("\nThis tweet contains a late bus");
                    while(matcher.find()){
                        // If the delay is equal to one hour, change to 60 minutes
                        int value = (matcher.group().equals("one") || matcher.group().equals("1"))? 60 : Integer.valueOf(matcher.group());
                        numbers.add(value);
                    }
                    number = numbers.get(0);
                    delay = numbers.get(1);
                    String date = getDate(status.getCreatedAt());
                    Tweet tweet = new Tweet(number, delay, date);
                    myDatabase.insertRecord(tweet);
                    numbers.clear();
                }
                // If the tweet contains 'on time' then the bus is back on time
                else if(text.contains("on time")){
                    // System.out.println("\n - UPDATE: BUS IS NOW RUNNING ON TIME");
                    while(matcher.find()){
                        // If the bus is on time, remove the record
                        myDatabase.deleteRecord(Integer.valueOf(matcher.group()));
                    }
                }
                System.out.println(" - @" + status.getUser().getScreenName() + " - " + status.getText());
            }
        }
    }
}
