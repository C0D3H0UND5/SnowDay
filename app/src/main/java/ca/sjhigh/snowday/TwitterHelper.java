package ca.sjhigh.snowday;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Created by Jason on 2016-12-20.
 *
 * This class will likely contain the code to parse incoming tweets
 */

public class TwitterHelper{

    /** User to get tweets from **/
    private String user;
    /** User's transportation information **/
    // private Tweet tweet;
    // private int busNumber;
    // private String pickupTime;
    /** Twitter object using credentials in twitter4j.properties **/
    private Twitter twitter;
    /** Database helper **/
    // private DatabaseHelper myDatabase;

    public TwitterHelper(Context context, String user) {
        // myDatabase = new DatabaseHelper(context);
    }

    private static String getDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
    }

    /**
     * Turns a status into a Tweet object and returns its textual representation
     * @param status The tweet from Twitter
     * @return The textual representation of the important tweet information
     */
    public static String filterTweet(Status status){
        /** Regex filtering stuff **/
        Pattern pattern = Pattern.compile("\\d+|one");
        Matcher matcher;
        ArrayList<Integer> numbers = new ArrayList<>();

        Tweet tweet = new Tweet();

        // Gets the body of the tweet
        String text = status.getText().toLowerCase();

        // If the tweet contains bus then the bus is either late or being corrected as on time
        if(text.contains("bus")){
            matcher = pattern.matcher(text);
            // If the tweet contains 'late' or 'delay' then a bus is running late
            if(text.contains("late") || text.contains("delay")){
                while(matcher.find()){
                    // If the delay is equal to one hour, change to 60 minutes
                    int value = (matcher.group().equals("one") || matcher.group().equals("1"))? 60 : Integer.valueOf(matcher.group());
                    numbers.add(value);
                }
                int number = numbers.get(0);
                int delay = numbers.get(1);
                String date = getDate(status.getCreatedAt());
                tweet = new Tweet(number, delay, date);
                numbers.clear();
            }
            // If the tweet contains 'on time' then the bus is back on time
            else if(text.contains("on time")){
                while(matcher.find()){
                    numbers.add(Integer.valueOf(matcher.group()));
                }
                int number = numbers.get(0);
                int delay = 0;
                String date = getDate(status.getCreatedAt());
                tweet = new Tweet(number, delay, date);
            }
            return tweet.toString();
        }
        return "Irrelevant tweet";


    }
}
