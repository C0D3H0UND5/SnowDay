package ca.sjhigh.snowday;

/**
 * Created by Jason on 2016-12-23.
 *
 * Creates an object to contain the information for any tweets containing closures
 */

class Closure {

    private String text;
    private String date;

    Closure(){}

    Closure(String text, String date){
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return date + " - " + text;
    }
}
