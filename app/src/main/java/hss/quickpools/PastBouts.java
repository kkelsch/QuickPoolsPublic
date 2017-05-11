package hss.quickpools;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kat on 4/29/2015.
 */
public class PastBouts extends Activity {

    //Create Adapter
    private BoutDbAdapter mDbHelper;
    private String fencersName;
    private String Case = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bouts_stats);

        //Setup Adapter
        mDbHelper = new BoutDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Grab the fencer based off the Row ID
        if (fencersName == null) {
            Bundle extras = getIntent().getExtras();
            Case = extras.getString("Case");
            fencersName = extras != null ? extras.getString("FencerFullName")
                    : null;
        }

        if (Case.equals("PastPools")) showPastPoolBouts();
        else if (Case.equals("PastDEs")) showPastDEBouts();
        else if (Case.equals("FencerHistory")) {
            Bundle extras = getIntent().getExtras();

            boolean isDes = extras.getBoolean("DEsOnly");
            String fencerA = extras.getString("FencerA");
            String fencerB = extras.getString("FencerB");

            if (isDes) {
                showPastDEBoutsPair(fencerA, fencerB);
            } else {
                showPastPoolBoutsPair(fencerA, fencerB);

            }

        }

    }


    //Past Bouts
    public void showPastPoolBouts() {
        //setup Bout Result Data point List
        ArrayList<Integer> ptList = new ArrayList<Integer>();
        ArrayList<boutListing> boutListings = new ArrayList<>();

        if (fencersName != null) {
            Cursor boutCursor = null;
            try {
                boutCursor = mDbHelper.fetchAllPastPoolBouts(fencersName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            startManagingCursor(boutCursor);

            boutCursor.moveToFirst();
            int numBouts = boutCursor.getCount();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();


            // loop through bouts
            for (int i = 0; i < numBouts; i++) {

                //get bout Data
                Bout currentBout = convertBout(boutCursor);

                //figure out if current fencer is A or B
                char selectedFencer = 'A';
                if (currentBout.FencerB.equals(fencersName)) selectedFencer = 'B';

                // format score
                // show if opponent is lefty
                // show cards in proper order
                String score = "";
                String opponent = "";
                boolean showLefty = false;


                if (selectedFencer == 'A') {
                    opponent = currentBout.FencerB;
                    if (currentBout.Lefty == 2 || currentBout.Lefty == 3) showLefty = true;

                    //add to Graph
                    if (currentBout.FencerAScore.equals("V")) {
                        ptList.add(5);
                    } else ptList.add(Integer.parseInt(currentBout.FencerAScore.replace("V", "")));

                    //cards
                    switch (currentBout.cardA) {
                        case YELLOW:
                            score = currentBout.FencerAScore + " [Y] - ";
                            break;
                        case RED:
                            score = currentBout.FencerAScore + " [R] - ";
                            break;

                        default:
                            score = currentBout.FencerAScore + " - ";
                            break;

                    }
                    switch (currentBout.cardB) {
                        case YELLOW:
                            score = score + currentBout.FencerBScore + " [Y]";
                            break;
                        case RED:
                            score = score + currentBout.FencerBScore + " [R]";
                            break;

                        default:
                            score = score + currentBout.FencerBScore;
                            break;

                    }

                } else {
                    opponent = currentBout.FencerA;
                    score = currentBout.FencerBScore + "-" + currentBout.FencerAScore;

                    //add to graph
                    if (currentBout.FencerBScore.equals("V")) {
                        ptList.add(5);
                        //  series.appendData(new DataPoint(i,
                        //        5), true, 100);

                    } else
                        ptList.add(Integer.parseInt(currentBout.FencerBScore.replace("V", "")));
                    //series.appendData(new DataPoint(i,Integer.parseInt(currentBout.FencerBScore.replace("V",""))
                    //  ), true, 100);

                    if (currentBout.Lefty == 1 || currentBout.Lefty == 3) showLefty = true;


                    //cards
                    switch (currentBout.cardB) {
                        case YELLOW:
                            score = currentBout.FencerBScore + " [Y] - ";
                            break;
                        case RED:
                            score = currentBout.FencerBScore + " [R] - ";
                            break;

                        default:
                            score = currentBout.FencerBScore + " - ";
                            break;

                    }

                    switch (currentBout.cardA) {
                        case YELLOW:
                            score = score + currentBout.FencerAScore + " [Y]";
                            break;
                        case RED:
                            score = score + currentBout.FencerAScore + " [R]";
                            break;

                        default:
                            score = score + currentBout.FencerAScore;
                            break;

                    }
                }

                //create String

                String boutDisplay = score + " , " + opponent;
                if (showLefty) boutDisplay = boutDisplay + "(L)";
                boutListings.add(new boutListing(boutDisplay, currentBout.Date));

                //go to Next bout
                boutCursor.moveToNext();
            }


            GraphView graph = (GraphView) findViewById(R.id.graph);
            Collections.reverse(ptList);

            for (int j = 0; j < ptList.size(); j++) {
                series.appendData(new DataPoint(j, ptList.get(j)), true, 30);
            }

            graph.addSeries(series);
            graph.setTitle(fencersName + getString(R.string.pool_scores));


        } //end of for loop

        //create String Array List
        ArrayList<String> visibleList = new ArrayList<String>();
        ListView lv = (ListView) findViewById(R.id.lvBoutStats);

        lv.setAdapter(new boutListingAdapter(PastBouts.this, boutListings));


    }

    public void showPastDEBouts() {
        //setup Bout Result Data point List
        ArrayList<Integer> ptList = new ArrayList<Integer>();
        ArrayList<boutListing> boutListings = new ArrayList<>();

        if (fencersName != null) {
            Cursor boutCursor = null;
            try {
                boutCursor = mDbHelper.fetchAllPastDEBouts(fencersName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            startManagingCursor(boutCursor);

            boutCursor.moveToFirst();
            int numBouts = boutCursor.getCount();

            //create String Array List
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();


            for (int i = 0; i < numBouts; i++) {

                //get bout Data
                Bout currentBout = convertBout(boutCursor);

                //figure out if current fencer is A or B
                char selectedFencer = 'A';
                if (currentBout.FencerB.equals(fencersName)) selectedFencer = 'B';

                // format score
                // show if opponent is lefty
                // show cards in proper order
                String score = "";
                String opponent = "";
                boolean showLefty = false;

                if (selectedFencer == 'A') {
                    opponent = currentBout.FencerB;
                    if (currentBout.Lefty == Bout.FencerALefty ||
                            currentBout.Lefty == Bout.FencerBLefty) showLefty = true;

                    //add to Graph
                    if (currentBout.FencerAScore.equals("V")) {
                        ptList.add(15);
                    } else ptList.add(Integer.parseInt(currentBout.FencerAScore.replace("V", "")));

                    //cards
                    switch (currentBout.cardA) {
                        case YELLOW:
                            score = currentBout.FencerAScore + " [Y] - ";
                            break;
                        case RED:
                            score = currentBout.FencerAScore + " [R] - ";
                            break;
                        default:
                            score = currentBout.FencerAScore + " - ";
                            break;

                    }
                    switch (currentBout.cardB) {
                        case YELLOW:
                            score = score + currentBout.FencerBScore + " [Y]";
                            break;
                        case RED:
                            score = score + currentBout.FencerBScore + " [R]";
                            break;
                        default:
                            score = score + currentBout.FencerBScore;
                            break;

                    }

                } else {
                    opponent = currentBout.FencerA;
                    score = currentBout.FencerBScore + "-" + currentBout.FencerAScore;

                    //add to graph
                    if (currentBout.FencerBScore.equals("V")) {
                        ptList.add(15);
                        //  series.appendData(new DataPoint(i,
                        //        5), true, 100);

                    } else
                        ptList.add(Integer.parseInt(currentBout.FencerBScore.replace("V", "")));

                    if (currentBout.Lefty == Bout.FencerALefty ||
                            currentBout.Lefty == Bout.FencerBLefty) showLefty = true;

                    //cards
                    switch (currentBout.cardB) {
                        case YELLOW:
                            score = currentBout.FencerBScore + " [Y] - ";
                            break;
                        case RED:
                            score = currentBout.FencerBScore + " [R] - ";
                            break;

                        default:
                            score = currentBout.FencerBScore + " - ";
                            break;

                    }

                    switch (currentBout.cardA) {
                        case YELLOW:
                            score = score + currentBout.FencerAScore + " [Y]";
                            break;
                        case RED:
                            score = score + currentBout.FencerAScore + " [R]";
                            break;

                        default:
                            score = score + currentBout.FencerAScore;
                            break;

                    }
                }

                //create String

                String boutDisplay = score + " , " + opponent;
                if (showLefty) boutDisplay = boutDisplay + "(L)";

                boutListings.add(new boutListing(boutDisplay, currentBout.Date));

                //go to Next bout
                boutCursor.moveToNext();
            }


            GraphView graph = (GraphView) findViewById(R.id.graph);
            Collections.reverse(ptList);

            for (int j = 0; j < ptList.size(); j++) {
                series.appendData(new DataPoint(j, ptList.get(j)), true, 30);
            }

            graph.addSeries(series);
            graph.setTitle(fencersName + getString(R.string.DE_scores));

        }

        //create String Array List
        ArrayList<String> visibleList = new ArrayList<String>();
        ListView lv = (ListView) findViewById(R.id.lvBoutStats);

        lv.setAdapter(new boutListingAdapter(PastBouts.this, boutListings));

    }

    //Past History of Two Fencers
    public void showPastPoolBoutsPair(String FencerA, String FencerB) {

        //setup Bout Result Data point List
        ArrayList<Integer> ptList = new ArrayList<Integer>();
        ArrayList<boutListing> boutListings = new ArrayList<>();

        //for calculations
        ArrayList<Integer> scores = new ArrayList<>();
        int totalBouts = 0;
        int totalVs = 0;

        if (fencersName != null) {
            Cursor boutCursor = null;
            try {
                boutCursor = mDbHelper.fetchAllPastPoolBoutsPair(FencerA, FencerB);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            startManagingCursor(boutCursor);

            boutCursor.moveToFirst();
            int numBouts = boutCursor.getCount();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

            // loop through bouts
            for (int i = 0; i < numBouts; i++) {

                //get bout Data
                Bout currentBout = convertBout(boutCursor);

                if (currentBout.FencerB == (null) || currentBout.isDE == true) {
                    continue;
                }

                //figure out if current fencer is A or B
                char selectedFencer = 'A';
                if (currentBout.FencerB.equals(fencersName)) selectedFencer = 'B';

                // format score
                // show if opponent is lefty
                // show cards in proper order
                String score = "";
                String opponent = "";
                boolean showLefty = false;

                //Selected Fencer = FencerA of this bout
                if (selectedFencer == 'A') {
                    opponent = currentBout.FencerB;
                    if (currentBout.Lefty == 2 || currentBout.Lefty == 3) showLefty = true;

                    //add to Graph
                    if (currentBout.FencerAScore.equals("V")) {
                        ptList.add(5);
                        scores.add(5);

                    } else {
                        ptList.add(Integer.parseInt(currentBout.FencerAScore.replace("V", "")));
                        scores.add(Integer.parseInt(currentBout.FencerAScore.replace("V", "")));
                    }

                    //cards
                    switch (currentBout.cardA) {
                        case YELLOW:
                            score = currentBout.FencerAScore + " [Y] - ";
                            break;
                        case RED:
                            score = currentBout.FencerAScore + " [R] - ";
                            break;

                        default:
                            score = currentBout.FencerAScore + " - ";
                            break;

                    }
                    switch (currentBout.cardB) {
                        case YELLOW:
                            score = score + currentBout.FencerBScore + " [Y]";
                            break;
                        case RED:
                            score = score + currentBout.FencerBScore + " [R]";
                            break;

                        default:
                            score = score + currentBout.FencerBScore;
                            break;
                    }

                } else {
                    opponent = currentBout.FencerA;

                    //add to graph
                    if (currentBout.FencerBScore.equals("V")) {
                        ptList.add(5);
                        scores.add(5);

                    } else {
                        scores.add(Integer.parseInt(currentBout.FencerBScore.replace("V", "")));
                        ptList.add(Integer.parseInt(currentBout.FencerBScore.replace("V", "")));
                    }


                    if (currentBout.Lefty == 1 || currentBout.Lefty == 3) showLefty = true;

                    //cards
                    switch (currentBout.cardB) {
                        case YELLOW:
                            score = currentBout.FencerBScore + " [Y] - ";
                            break;
                        case RED:
                            score = currentBout.FencerBScore + " [R] - ";
                            break;

                        default:
                            score = currentBout.FencerBScore + " - ";
                            break;

                    }

                    switch (currentBout.cardA) {
                        case YELLOW:
                            score = score + currentBout.FencerAScore + " [Y]";
                            break;
                        case RED:
                            score = score + currentBout.FencerAScore + " [R]";
                            break;

                        default:
                            score = score + currentBout.FencerAScore;
                            break;

                    }
                }

                //create String Bout display
                String boutDisplay = score + " , " + opponent;
                if (showLefty) boutDisplay = boutDisplay + "(L)";
                boutListings.add(new boutListing(boutDisplay, currentBout.Date));

                //add bout scores
                totalBouts++;
                if (boutDisplay.charAt(0) == 'V') totalVs++;

                //go to Next bout
                boutCursor.moveToNext();
            }


            GraphView graph = (GraphView) findViewById(R.id.graph);
            Collections.reverse(ptList);

            for (int j = 0; j < ptList.size(); j++) {
                series.appendData(new DataPoint(j, ptList.get(j)), true, 30);
            }

            graph.addSeries(series);
            graph.setTitle(FencerA + " v " + FencerB);


        } //end of for loop

        // Add calculations
        if (totalBouts != 0) {
            //calculate average Score
            Double average = 0.0;
            for (int i = 0; i < scores.size(); i++) {
                average += scores.get(i);
            }
            average = average / scores.size();

            boutListings.add(0,
                    new boutListing(" " + new DecimalFormat("##.##").format(average).toString(), getString(R.string.average_score)));
            Double perVictory = ((double) totalVs / totalBouts);
            boutListings.add(1,
                    new boutListing(" " + new DecimalFormat("##.##").format(perVictory * 100).toString() + "%", getString(R.string.percentage_Victory)));
        } else {
            boutListings.add(new boutListing(getString(R.string.zero_bouts_fenced), ""));
        }


        //List view & Adapter
        ListView lv = (ListView) findViewById(R.id.lvBoutStats);
        lv.setAdapter(new boutListingAdapter(PastBouts.this, boutListings));


    }

    public void showPastDEBoutsPair(String FencerA, String FencerB) {
        //setup Bout Result Data point List
        ArrayList<Integer> ptList = new ArrayList<Integer>();
        ArrayList<boutListing> boutListings = new ArrayList<>();

        //for calculations
        ArrayList<Integer> scores = new ArrayList<>();
        int totalBouts = 0;
        int totalVs = 0;


        if (fencersName != null) {
            Cursor boutCursor = null;
            try {
                boutCursor = mDbHelper.fetchAllPastDEBoutsPair(FencerA, FencerB);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            startManagingCursor(boutCursor);

            boutCursor.moveToFirst();
            int numBouts = boutCursor.getCount();

            //create String Array List
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();


            for (int i = 0; i < numBouts; i++) {

                //get bout Data
                Bout currentBout = convertBout(boutCursor);

                //  make sure it is valid
                if (currentBout.FencerB == (null) || currentBout.isDE == false) {
                    continue;
                }

                //figure out if current fencer is A or B
                char selectedFencer = 'A';
                if (currentBout.FencerB.equals(fencersName)) selectedFencer = 'B';

                // format score
                // show if opponent is lefty
                // show cards in proper order
                String score = "";
                String opponent = "";
                boolean showLefty = false;

                if (selectedFencer == 'A') {
                    opponent = currentBout.FencerB;
                    if (currentBout.Lefty == Bout.FencerALefty ||
                            currentBout.Lefty == Bout.FencerBLefty) showLefty = true;

                    //add to Graph
                    if (currentBout.FencerAScore.equals("V")) {
                        ptList.add(15);
                        scores.add(15);
                    } else {
                        ptList.add(Integer.parseInt(currentBout.FencerAScore.replace("V", "")));
                        scores.add(Integer.parseInt(currentBout.FencerAScore.replace("V", "")));
                    }

                    //cards
                    switch (currentBout.cardA) {
                        case YELLOW:
                            score = currentBout.FencerAScore + " [Y] - ";
                            break;
                        case RED:
                            score = currentBout.FencerAScore + " [R] - ";
                            break;
                        default:
                            score = currentBout.FencerAScore + " - ";
                            break;

                    }
                    switch (currentBout.cardB) {
                        case YELLOW:
                            score = score + currentBout.FencerBScore + " [Y]";
                            break;
                        case RED:
                            score = score + currentBout.FencerBScore + " [R]";
                            break;
                        default:
                            score = score + currentBout.FencerBScore;
                            break;

                    }

                } else {
                    opponent = currentBout.FencerA;
                    score = currentBout.FencerBScore + "-" + currentBout.FencerAScore;

                    //add to graph
                    if (currentBout.FencerBScore.equals("V")) {
                        ptList.add(15);
                        scores.add(15);

                    } else {
                        ptList.add(Integer.parseInt(currentBout.FencerBScore.replace("V", "")));
                        scores.add(Integer.parseInt(currentBout.FencerBScore.replace("V", "")));
                    }


                    if (currentBout.Lefty == Bout.FencerALefty ||
                            currentBout.Lefty == Bout.FencerBLefty) showLefty = true;

                    //cards
                    switch (currentBout.cardB) {
                        case YELLOW:
                            score = currentBout.FencerBScore + " [Y] - ";
                            break;
                        case RED:
                            score = currentBout.FencerBScore + " [R] - ";
                            break;

                        default:
                            score = currentBout.FencerBScore + " - ";
                            break;

                    }

                    switch (currentBout.cardA) {
                        case YELLOW:
                            score = score + currentBout.FencerAScore + " [Y]";
                            break;
                        case RED:
                            score = score + currentBout.FencerAScore + " [R]";
                            break;

                        default:
                            score = score + currentBout.FencerAScore;
                            break;

                    }
                }

                //create String

                String boutDisplay = score + " , " + opponent;
                if (showLefty) boutDisplay = boutDisplay + "(L)";

                boutListings.add(new boutListing(boutDisplay, currentBout.Date));

                //go to Next bout
                boutCursor.moveToNext();
                // increase counters
                totalBouts++;
                if (boutDisplay.charAt(0) == 'V') totalVs++;
            }

            if (totalBouts != 0) {
                //calculate average Score
                Double average = 0.0;
                for (int i = 0; i < scores.size(); i++) {
                    average += scores.get(i);
                }
                average = average / scores.size();

                boutListings.add(0,
                        new boutListing(" " + new DecimalFormat("##.##").format(average).toString(), getString(R.string.average_score)));
                Double perVictory = ((double) totalVs / totalBouts);
                boutListings.add(1,
                        new boutListing(" " + new DecimalFormat("##.##").format(perVictory * 100).toString() + "%", getString(R.string.percentage_Victory)));
            } else {
                boutListings.add(new boutListing(getString(R.string.zero_bouts_fenced), ""));
            }

            // Graph Items
            GraphView graph = (GraphView) findViewById(R.id.graph);
            Collections.reverse(ptList);

            for (int j = 0; j < ptList.size(); j++) {
                series.appendData(new DataPoint(j, ptList.get(j)), true, 30);
            }

            graph.addSeries(series);
            graph.setTitle(FencerA + " v " + FencerB);

        }

        //create String Array List

        ListView lv = (ListView) findViewById(R.id.lvBoutStats);

        lv.setAdapter(new boutListingAdapter(PastBouts.this, boutListings));

    }

    private class boutListingAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<boutListing> listings;

        public boutListingAdapter(Context context, ArrayList<boutListing> persons) {
            this.context = context;
            this.listings = persons;
        }

        @Override
        public int getCount() {
            return listings.size();
        }

        @Override
        public Object getItem(int position) {
            return listings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TwoLineListItem twoLineListItem;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                twoLineListItem = (TwoLineListItem) inflater.inflate(
                        android.R.layout.simple_list_item_2, null);
            } else {
                twoLineListItem = (TwoLineListItem) convertView;
            }

            TextView text1 = twoLineListItem.getText1();
            TextView text2 = twoLineListItem.getText2();

            text1.setText(listings.get(position).topInfo);
            text2.setText("" + listings.get(position).subInfo);

            if (text1.getText().charAt(0) == 'V' || text1.getText().charAt(0) == '5') {
                text1.setTextColor(Color.rgb(8, 138, 8));
            } else if (Character.isDigit(text1.getText().charAt(0))) {
                text1.setTextColor(Color.rgb(180, 4, 4));
            }

            return twoLineListItem;
        }
    }

    public Bout convertBout(Cursor cursor) {
        try {
            Bout tempBout = new Bout();
            tempBout.FencerA = cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_FENCERA));
            tempBout.FencerB = cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_FENCERB));
            tempBout.Winner = cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_WINNER));
            tempBout.cardA = Bout.Card.fromString(cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_CARDA)));
            tempBout.cardB = Bout.Card.fromString(cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_CARDB)));
            tempBout.FencerAScore = cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_ASCORE));
            tempBout.FencerBScore = cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_BSCORE));
            tempBout.timeRemaining = cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_TIMELEFT));
            tempBout.Lefty = cursor.getInt(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_LEFTY));
            tempBout.Date = cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_DATE));

            if (cursor.getString(cursor.getColumnIndexOrThrow(BoutDbAdapter.KEY_isDE)).equals("0")) {
                tempBout.isDE = false;
            } else tempBout.isDE = true;


            return tempBout;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Bout();
    }

    // for visuals
    public class boutListing {
        String topInfo;
        String subInfo;

        boutListing(String top, String sub) {
            this.subInfo = sub;
            this.topInfo = top;
        }

    }

}

