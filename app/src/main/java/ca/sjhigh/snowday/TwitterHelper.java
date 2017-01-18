package ca.sjhigh.snowday;

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
    private static final Pattern busPattern = Pattern.compile("bus");
    private static final Pattern closurePattern = Pattern.compile("clos");
    private static final Pattern lateDelayPattern = Pattern.compile("late|delay");
    // Regex used to properly get tweets from ASD-South
    private static final Pattern southBusPattern = Pattern.compile("\\d+");
    private static final Pattern southDelayPattern = Pattern.compile("\\s(\\d\\d?|one)\\s");
    private static final Pattern southClosurePattern = Pattern.compile("");
    // Regex used to properly get tweets from ASD-West
    private static final Pattern westBusPattern = Pattern.compile("#\\s?\\d\\d\\d?|\\d\\d\\d?\\s\\(");
    private static final Pattern westDelayPattern = Pattern.compile("\\s(\\d\\d?|one)\\s");

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
     * Removes any tweets that are not from the current day
     * ToDO Modify this to remove delays after the bus should arrive
     */
    public static void cullDelays(DatabaseHelper myDatabase){
        Delay[] delays = myDatabase.retrieveDelays();
        String day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        for (Delay delay : delays) {
            if(!delay.getDate().substring(0, 2).equals(day)){
                myDatabase.deleteDelay(delay.getBusNumber());
            }
        }
    }
    /**
     * Removes any tweets that are not from the current day
     */
    public static void cullClosures(DatabaseHelper myDatabase){
        Closure[] closures = myDatabase.retrieveClosures();
        String day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        for (Closure closure : closures) {
            if(!closure.getDate().substring(0, 2).equals(day)){
                myDatabase.deleteClosure(closure.getText().substring(2,11));
            }
        }
    }

    /**
     * Analyzes tweet and stores it in the database if it contains a late bus or a closure.
     * If a delay record contains a correction then it will delete the corresponding delay
     * @param status The status(tweet) from Twitter
     * @param myDatabase the DatabaseHelper object to allow database access
     */
    public static void storeTweet(Status status, DatabaseHelper myDatabase){
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
        if(busPattern.matcher(text).find()){
            // If the tweet contains 'late' or 'delay' then a bus is running late, add to database
            if(lateDelayPattern.matcher(text).find()){
                System.out.println(text);
                /* Get bus number */
                Matcher busMatcher = southBusPattern.matcher(text);
                busMatcher.find();
                int number = Integer.valueOf(busMatcher.group());
                /* Get delay time */
                Matcher delayMatcher = southDelayPattern.matcher(text);
                delayMatcher.find();
                String time = delayMatcher.group();
                time = time.substring(1, time.length()-1);
                // Convert to an integer
                int intTime = (time.equals("one") || time.equals("1"))? 60 : Integer.valueOf(time);
                /* Get date */
                String date = getDate(status.getCreatedAt());

                System.out.println("Bus: '" + number + "'");
                System.out.println("Delay: '" + time + "'");
                System.out.println("Date: '" + date + "'");

                /* Add to database */
                Delay delay = new Delay(number, intTime, date);
                if(myDatabase.isDelayStored(number)){
                    myDatabase.updateDelay(delay);
                }
                else {
                    myDatabase.insertDelay(delay);
                }
            }
            // If the tweet contains 'on time' then the bus is back on time, remove from database
            else if(closurePattern.matcher(text).find()){
                Matcher busMatcher = southBusPattern.matcher(text);
                int number = Integer.valueOf(busMatcher.group());
                myDatabase.deleteDelay(number);
            }
            // If the tweet contains a bus and not a delay or correction then it must not be running
            else {
                /* Get bus number */
                Matcher busMatcher = southBusPattern.matcher(text);
                busMatcher.find();
                int number = Integer.valueOf(busMatcher.group());
                System.out.println("Bus " + number + " isn't running today");
                /* Get date */
                String date = getDate(status.getCreatedAt());
                /* Add to database */
                Delay delay = new Delay(number, 24, date);
                if(myDatabase.isDelayStored(number)){
                    myDatabase.updateDelay(delay);
                }
                else {
                    myDatabase.insertDelay(delay);
                }
            }
        }
    }
}
