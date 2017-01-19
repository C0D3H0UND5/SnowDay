package ca.sjhigh.snowday;

/**
 * Created by Jason on 2016-12-20.
 *
 * Creates an object to contain the information for any tweets containing bus delays
 */

class Delay {

    private int busNumber;
    private int time;
    private String date;

    Delay(){}

    Delay(int busNumber, int delay, String date){
        this.busNumber = busNumber;
        this.time = delay;
        this.date = date;
    }

    int getBusNumber() {
        return busNumber;
    }

    void setBusNumber(int busNumber) {
        this.busNumber = busNumber;
    }

    int getDelay() {
        return time;
    }

    void setDelay(int time) {
        this.time = time;
    }

    String getDate() {
        return date;
    }

    void setDate(String date) {
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
