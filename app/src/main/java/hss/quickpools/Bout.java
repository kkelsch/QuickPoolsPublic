package hss.quickpools;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kat on 4/23/2015.
 */
public class Bout implements Parcelable {

    //public ints
    public static final int FencerALefty = 1;
    public static final int FencerBLefty = 2;
    public static final int FencerABLefty = 3;

    public static final int Key_FencerB = 1;
    public static final int Key_FencerBScore = 2;
    public static final int Key_CardB = 3;
    public static final int Key_FencerA = 5;
    public static final int Key_FencerAScore = 6;
    public static final int Key_CardA = 7;
    public static final int Key_TimeRemaining = 8;

    public static final int Key_L_ID = 4;
    public static final int Key_R_ID = 0;



    //setup yellow/red cards
    public enum Card {
        NONE, YELLOW, RED, BLACK;

        public static Card fromInteger(int x) {
            switch (x) {
                case 0:
                    return NONE;
                case 1:
                    return YELLOW;
                case 2:
                    return RED;
            }
            return null;
        }
        public static int toInteger(Card x) {
            switch (x) {
                case NONE:
                    return 0;
                case YELLOW:
                    return 1;
                case RED:
                    return 2;
            }
            return -1;
        }
        public static Card fromString(String x) {
            switch (x) {
                case "NONE":
                    return NONE;
                case "YELLOW":
                    return YELLOW;
                case "RED":
                    return RED;
            }
            return NONE;
        }
        public static String toString(Card x) {
            switch (x) {
                case NONE:
                    return "NONE";
                case YELLOW:
                    return "YELLOW";
                case RED:
                    return "RED";
            }
            return "NONE";
        }
    }

    public String FencerA, FencerB, timeRemaining, Winner;
    public Card cardA, cardB;
    public String FencerAScore, FencerBScore;
    public int pID_FencerA, pID_FencerB;
    public String Tag, Date;
    public int Lefty;
    public boolean isDE;

    public Bout() {
        this.FencerAScore = "";
        this.FencerBScore = "";
        this.timeRemaining = "";
        this.cardA = Card.NONE;
        this.cardB = Card.NONE;
        this.Tag = "";
        this.Winner = "";
        this.Lefty = 0;
        this.isDE = false;
        this.Date = "";
    }
    public String getTag() {
        return this.Tag;
    }
    public void setTag(String Tag) {
        this.Tag = Tag;
    }
    public String toString() {
        return this.FencerA + " , " + this.FencerAScore + " , " + this.cardA.toString() + " , " +
                this.FencerB + " , " + this.FencerBScore + " , " + this.cardB.toString();
    }

    //parcel part
    public Bout(Parcel in) {
        String[] data = new String[14];

        in.readStringArray(data);
        this.FencerA = data[0];
        this.FencerB = data[1];
        this.timeRemaining = data[2];
        this.Winner = data[3];
        this.cardA = Card.fromString(data[4]);
        this.cardB = Card.fromString(data[5]);
        this.FencerAScore = data[6];
        this.FencerBScore = data[7];
        this.pID_FencerA = Integer.parseInt(data[8]);
        this.pID_FencerB = Integer.parseInt(data[9]);
        this.Tag = data[10];
        this.Date = data[11];
        this.Lefty = Integer.parseInt(data[12]);
        this.isDE = Boolean.parseBoolean(data[13]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.FencerA, this.FencerB,
                this.timeRemaining, this.Winner,
                Card.toString(this.cardA), Card.toString(this.cardB), this.FencerAScore,
                this.FencerBScore, Integer.toString(this.pID_FencerA),
                Integer.toString(this.pID_FencerB),
                this.Tag, this.Date, Integer.toString(this.Lefty),
                Boolean.toString(this.isDE)});
    }

    public static final Parcelable.Creator<Bout> CREATOR = new Parcelable.Creator<Bout>() {
        @Override
        public Bout createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new Bout(source);  //using parcelable constructor
        }

        @Override
        public Bout[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Bout[size];
        }
    };

}
