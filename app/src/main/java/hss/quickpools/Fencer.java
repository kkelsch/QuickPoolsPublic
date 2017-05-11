package hss.quickpools;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Fencer implements Parcelable {

    public String firstName, lastName;
    public int listPosition;
    public Hand hand;
    public long dbRowID;
    public int V, TR, TS, IND, place, seed;

    public boolean wonDE;
    public int totalV, numRCards, numYCards, totalBouts;
    public Double avgBoutScore, perVictory;
    public Weapon weapon;
    public Gender gender;
    public String club;
    public int birthYear;

    //DE Info
    public int totalV_DE, numRCardsDE, numYCardsDE, totalBoutsDE;
    public Double avgBoutScoreDE, perVictoryDE;

    public enum Weapon {
        SABER, FOIL, EPEE;

        public static Weapon fromInteger(int x) {
            switch (x) {
                case 0:
                    return SABER;
                case 1:
                    return FOIL;
                case 2:
                    return EPEE;
            }
            return null;
        }
    }

    public enum Gender {
        MALE, FEMALE;

        public static Gender fromInteger(int x) {
            switch (x) {
                case 0:
                    return MALE;
                case 1:
                    return FEMALE;
            }
            return null;
        }
    }

    public enum Hand {
        RIGHT, LEFT;

        public static Hand fromInteger(int x) {
            switch (x) {
                case 0:
                    return RIGHT;
                case 1:
                    return LEFT;
            }
            return null;
        }
    }

    //parcel part
    public Fencer(Parcel in) {
        String[] data = new String[18];

        in.readStringArray(data);
        this.firstName = data[0];
        this.lastName = data[1];
        this.listPosition = Integer.parseInt(data[2]);

        this.hand = hand.fromInteger(Integer.valueOf(data[3]));
        this.dbRowID = Long.valueOf(data[4]);

        this.V = Integer.parseInt(data[5]);
        this.TR = Integer.parseInt(data[6]);
        this.TS = Integer.parseInt(data[7]);
        this.IND = Integer.parseInt(data[8]);
        this.place = Integer.parseInt(data[9]);

        this.totalV = Integer.parseInt(data[10]);
        this.numRCards = Integer.parseInt(data[11]);
        this.numYCards = Integer.parseInt(data[12]);
        this.totalBouts = Integer.parseInt(data[13]);
        this.avgBoutScore = Double.valueOf(data[14]);
        this.perVictory = Double.valueOf(data[15]);

        //DE add ons
        this.seed = Integer.parseInt(data[16]);
        this.wonDE = Boolean.parseBoolean(data[17]);


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.firstName, this.lastName,
                String.valueOf(this.listPosition), String.valueOf(hand.ordinal()),
                String.valueOf(dbRowID), String.valueOf(this.V), String.valueOf(this.TR),
                String.valueOf(this.TS), String.valueOf(this.IND), String.valueOf(this.place),
                String.valueOf(this.totalV), String.valueOf(this.numRCards), String.valueOf(this.numYCards),
                String.valueOf(this.totalBouts), String.valueOf(this.avgBoutScore),
                String.valueOf(this.perVictory), String.valueOf(this.seed),
                String.valueOf(this.wonDE)});
    }

    public static final Creator<Fencer> CREATOR = new Creator<Fencer>() {
        @Override
        public Fencer createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new Fencer(source);  //using parcelable constructor
        }

        @Override
        public Fencer[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Fencer[size];
        }
    };


    //public new constructor
    public Fencer() {
        this.firstName = "";
        this.lastName = "";
        this.listPosition = 0;
        this.hand = Hand.RIGHT;
        this.dbRowID = Long.valueOf(0);
        this.V = 0;
        this.TR = 0;
        this.TS = 0;
        this.IND = 0;
        this.place = 0;

        this.totalV = 0;
        this.numRCards = 0;
        this.numYCards = 0;
        this.totalBouts = 0;
        this.avgBoutScore = 0.0;
        this.perVictory = 0.0;
        this.seed = 0;
        this.wonDE = true;

        //DEs
        this.totalV_DE = 0;
        this.numRCardsDE = 0;
        this.numYCardsDE = 0;
        this.totalBoutsDE = 0;


    }

    public void setListPos(int pos) {
        this.listPosition = pos;
    }

    public String getFencingLabel() {
        if (this.firstName != "" && this.lastName != "") {
            return this.lastName + "," + this.firstName.charAt(0) + ".";
        } else {
            return "";
        }
    }

    public void setSeed(int newSeed) {
        this.seed = newSeed;
    }

    public void setPlace(int Place) {
        this.place = Place;
    }

    public Fencer copyFencer(Fencer oldfencer) {
        Fencer newFencer = new Fencer();
        newFencer.firstName = oldfencer.firstName;
        newFencer.lastName = oldfencer.lastName;

        //Pools
        newFencer.listPosition = oldfencer.listPosition;
        newFencer.V = oldfencer.V;
        newFencer.TR = oldfencer.TR;
        newFencer.TS = oldfencer.TS;
        newFencer.IND = oldfencer.IND;

        //DEs
        newFencer.wonDE = oldfencer.wonDE;
        newFencer.place = oldfencer.place;
        newFencer.seed = oldfencer.seed;

        return newFencer;
    }

    public static ArrayList<Fencer> redoListPositions(ArrayList<Fencer> oldList) {

        //sort by highest to lowest
        Collections.sort(oldList, new ListPosComparator());

        //give new positions
        for (int i = 0; i < oldList.size(); i++) {
            Fencer tempFencer = oldList.get(i);
            tempFencer.listPosition = i + 1;
        }

        return oldList;

    }

    public static Fencer getFencerByListPosition(ArrayList<Fencer> list, int listPost) {

        for (Fencer f : list) {
            if (f.listPosition == listPost)
                return f;
        }
        return null;
    }

    //Comparators
    static class ListPosComparator implements Comparator<Fencer> {

        @Override
        public int compare(Fencer lhs, Fencer rhs) {

            //return fencer with higher place
            //1 is higher than 2 here
            if (lhs.listPosition > rhs.listPosition) return 1;
            else return -1;


        }
    }


    static class DESeedComparator implements Comparator<Fencer> {
        public int compare(Fencer c1, Fencer c2) {
            if (c1.seed > c2.seed) return 1;
            else return -1;
        }
    }

    static class PlaceComparator implements Comparator<Fencer> {

        @Override
        public int compare(Fencer lhs, Fencer rhs) {

            //return fencer with higher place
            //1 is higher than 2 here
            if (lhs.place > rhs.place) return 1;
            else return -1;


        }
    }

    static class FencerComparator implements Comparator<Fencer> {
        public int compare(Fencer c1, Fencer c2) {

            int victory = c1.V - c2.V;

            //return fencer with higher victories
            if (victory > 0) return 1;
            else if (victory < 0) return -1;

            else {
                //if higher indicator, return that one
                if (c1.IND > c2.IND) return 1;
                else return -1;
            }
        }

    }
}