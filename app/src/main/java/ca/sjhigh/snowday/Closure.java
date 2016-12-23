package ca.sjhigh.snowday;

/**
 * Created by Jason on 2016-12-23.
 *
 * This class contains the information for any tweets containing closures
 */

public class Closure {

    private String text;
    private String date;

    public Closure(){}

    public Closure(String text, String date){
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return date + " - " + text;
    }
}
