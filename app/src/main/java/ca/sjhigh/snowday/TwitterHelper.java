package ca.sjhigh.snowday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;

/**
 * Created by Jason on 2016-12-20.
 *
 * This class will likely contain the code to parse incoming tweets
 * ToDo - Fix this shit to optimize regex statements
 */

class TwitterHelper {

    /** Logic variables **/
    /** Any numbers 0-999 | The character string 'one' **/
    private static final Pattern PATTERN = Pattern.compile("\\d+|one");
    private static Matcher matcher;

    /**
     * Takes in a HH:MM string and adds the delay time in minutes
     * @param time The normal pickup time
     * @param delay The time in minutes that the bus is delayed
     * @return The new arrival time of the bus
     */
    public static String addTime(String time, int delay){
        String[] split = time.split(":");
        int hour = Integer.valueOf(split[0]);
        int minute = Integer.valueOf(split[1]);
        int total = hour*60 + minute;
        total += delay;
        hour = total/60;
        minute = total%60;
        return hour + ":" + ((minute < 10)? ("0" + minute) : minute);
    }

    /**
     * Strips a date down into the desired components
     * @param date A Java Date object
     * @return The modified time consisting of | Day/Month Hour:Minute |
     */
    private static String getDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        return  day + "/" + month + " " + hour + ":" + ((minute < 10)? ("0" + minute): minute);
    }

    /**
     * Analyzes tweet and stores it in the database if it contains a late bus or a closure.
     * If a delay record contains a correction then it will delete the corresponding delay
     * @param status The status(tweet) from Twitter
     * @param myDatabase the DatabaseHelper object to allow database access
     */
    public static void storeTweet(Status status, DatabaseHelper myDatabase){
        ArrayList<Integer> numbers = new ArrayList<>();

        // Gets the body of the tweet
        String text = status.getText().toLowerCase();

        // What if all buses are running on a delay (i.e. buses run one hour delay in bad weather)
        // What if a bus isn't running at all? (GitHub issue)
        // What if students are being dismissed but no close statement in tweet? (GitHub issue)
        // What if ...

        // If the tweet contains 'clos' or 'cancel' then it means there is a closure
        // Try to differentiate between 'closed', school or schools are closed, and 'closing', a
        // school or school(s) are closing
        if(text.contains("clos") || text.contains("cancel")){
            if(!myDatabase.isClosureStored(text)){
                String date = getDate(status.getCreatedAt());
                Closure closure = new Closure(text, date);
                myDatabase.insertClosure(closure);
            }
        }
        // If the tweet contains bus then the bus is either late or being corrected as on time
        if(text.contains("bus")){
            matcher = PATTERN.matcher(text);
            // If the tweet contains 'late' or 'delay' then a bus is running late, add to database
            if(text.contains("late") || text.contains("delay")){
                while(matcher.find()){
                    // If the delay is equal to one hour, change to 60 minutes
                    int value = (matcher.group().equals("one") || matcher.group().equals("1"))? 60 : Integer.valueOf(matcher.group());
                    numbers.add(value);
                }
                int number = numbers.get(0);
                int time = numbers.get(1);
                String date = getDate(status.getCreatedAt());
                Delay delay = new Delay(number, time, date);

                if(myDatabase.isDelayStored(numbers.get(0))){
                    myDatabase.updateDelay(delay);
                }
                else{
                    myDatabase.insertDelay(delay);
                }
                numbers.clear();
            }
            // If the tweet contains 'on time' then the bus is back on time, remove from database
            else if(text.contains("on time")){
                while(matcher.find()){
                    numbers.add(Integer.valueOf(matcher.group()));
                }
                myDatabase.deleteDelay(numbers.get(0));
            }
        }
    }
}
