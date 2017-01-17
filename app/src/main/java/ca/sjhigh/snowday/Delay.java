package ca.sjhigh.snowday;

/**
 * Created by Jason on 2016-12-20.
 *
 * This class will be used to allow easy transfer of data between the database and any components
 * of the app that need that information
 */

class Delay {

    /** Logic variables **/
    private int busNumber;
    private int time;
    private String date;

    public Delay(){}

    public Delay(int busNumber, int delay, String date){
        this.busNumber = busNumber;
        this.time = delay;
        this.date = date;
    }

    public int getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(int busNumber) {
        this.busNumber = busNumber;
    }

    public int getDelay() {
        return time;
    }

    public void setDelay(int time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        if(time == 24)
            return date + " - Bus " + busNumber + " is not running today";
        else
            return date + " - Bus " + busNumber + " is running " + ((time > 0) ? (time + " minutes late") : ("on time now"));
    }
}
