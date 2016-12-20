package ca.sjhigh.snowday;

/**
 * Created by Jason on 2016-12-20.
 *
 * This class will be used to allow easy transfer of data between the database and any components
 * of the app that need that information
 */

public class Person {

    private int busNumber;
    private String pickupTime;
    private int delay;
    private String date;

    // Takes in a HH:MM string and adds the delay time in minutes
    private String addTime(String time, int delay){
        String[] split = time.split(":");
        int hour = Integer.valueOf(split[0]);
        int minute = Integer.valueOf(split[1]);
        int total = hour*60 + minute;
        total += delay;
        hour = total/60;
        minute = total%60;
        return hour + ":" + ((minute < 10)? ("0" + minute) : minute);
    }

    public Person(){}

    public Person(int busNumber, String pickupTime){
        this.busNumber = busNumber;
        this.pickupTime = pickupTime;
        this.delay = 0;
        this.date = null;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(int busNumber) {
        this.busNumber = busNumber;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return date + " - Bus " + busNumber + " is running " + delay + "minutes late and should arrive at " + addTime(pickupTime, delay);
    }
}
