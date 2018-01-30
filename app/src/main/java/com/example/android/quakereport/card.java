package com.example.android.quakereport;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by khali on 12/14/2017.
 */
public class card {
    private double mag;
    private String[] place = new String[2];
    private long time;
    private String Url;

    public card(Double mag, String place, long time, String url) {
        this.mag = mag;

        this.Url = url;

        if (place.contains("of")) {
            this.place = place.split("(?<=of)");
        } else {
            this.place = place.split("-");
        }

        this.time = time;

    }

    public double getMag() {
        return mag;
    }

    public String[] getPlace() {

        return place;
    }

    public String[] getTime() {

        String[] stime = new String[2];

        Date dateObjet = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM DD, yyyy");

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        stime[0] = dateFormat.format(dateObjet);
        stime[1] = timeFormat.format(dateObjet);
        return stime;
    }


    public int getMagnitude() {
        int Magn = (int) mag;
        switch (Magn) {
            case 1:
                return R.color.magnitude1;

            case 2:
                return R.color.magnitude2;

            case 3:
                return R.color.magnitude3;

            case 4:
                return R.color.magnitude4;

            case 5:
                return R.color.magnitude5;

            case 6:
                return R.color.magnitude6;

            case 7:
                return R.color.magnitude7;

            case 8:
                return R.color.magnitude8;

            case 9:
                return R.color.magnitude9;

            default:
                return R.color.magnitude10plus;


        }


    }

    public String getUrl() {

        return Url;
    }
}
