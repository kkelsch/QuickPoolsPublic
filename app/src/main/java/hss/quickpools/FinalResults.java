package hss.quickpools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Kat on 6/4/2015.
 */
public class FinalResults extends Activity {

    public ArrayList<Fencer> fencerData = new ArrayList<Fencer>();
    public ArrayList<Bout> boutList = new ArrayList<Bout>();
    public boolean isFinished = false;
    public boolean savedToDB = false;


    //Create Adapters
    private BoutDbAdapter boutDbAdapter;
    private FencersDbAdapter fencerDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finalresults);

        Bundle extras = getIntent().getExtras();

        //get extras
        if (extras != null) {
            fencerData = getIntent().getParcelableArrayListExtra("fencers");
            isFinished = getIntent().getBooleanExtra("finished", false);
            boutList = getIntent().getParcelableArrayListExtra("boutList");
        }

        if (isFinished == false) {
            Button btn = (Button) findViewById(R.id.btnFinishedDEs);
            btn.setEnabled(false);
            Button btnDB = (Button) findViewById(R.id.btnSaveDEtoDB);
            btnDB.setEnabled(false);
        } else {
            //create adapters
            boutDbAdapter = new BoutDbAdapter(this);
            fencerDbAdapter = new FencersDbAdapter(this);
        }

        //Sort by place
        Collections.sort(fencerData, new Fencer.PlaceComparator());

        //setup string list
        ArrayList<String> finalResults = new ArrayList<>();

        //add to list
        for (Fencer f : fencerData) {
            finalResults.add("(" + f.place + ") " + f.firstName + " " +
                    f.lastName);
        }

        //display on listview
        ListView lvResults = (ListView) findViewById(R.id.lvFinalResults);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, finalResults);

        //save adapter
        lvResults.setAdapter(adapter);
    }


    public void finishFinalResults(View v) {
        if (savedToDB == false) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.finished_DEs))
                    .setMessage(getString(R.string.save_to_database))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //save to database then exit
                            // saveBoutsToDataBase();
                            //go home
                            saveBoutsToDataBase();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }

                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish pool
                            //go home
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            //finish pool
            //go home
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


    }


    //Save to database
    public void btnSaveDEtoDB(View v) {
        if (savedToDB) {
            Toast.makeText(getApplicationContext(), getString(R.string.bouts_already_saved),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            savedToDB = true;
            saveBoutsToDataBase();
            Toast.makeText(getApplicationContext(), getString(R.string.saved),
                    Toast.LENGTH_SHORT).show();

        }
    }

    //Save bouts to database in background thread
    public void saveBoutsToDataBase() {
        Thread initBkgdThread = new Thread(new Runnable() {
            public void run() {
//
                /* Save bouts to database*/
                //open up connections
                try {
                    boutDbAdapter.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    fencerDbAdapter.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //get each bout
                for (int i = 0; i < boutList.size(); i++) {
                    //sort data
                    Bout currentBout = boutList.get(i);

                    //find fencers in list
                    long fencerA_ID = 0;
                    long fencerB_ID = 0;

                    //get fencer IDs of
                    for (Fencer tempFencer : fencerData) {
                        if (currentBout.FencerA.contains(tempFencer.firstName + " " +
                                tempFencer.lastName)) {
                            fencerA_ID = tempFencer.dbRowID;
                        } else if (currentBout.FencerB.contains(tempFencer.firstName + " " +
                                tempFencer.lastName)) {
                            fencerB_ID = tempFencer.dbRowID;
                        }
                    }

                    // Get Database cursors
                    Cursor A_Fencer = null;
                    Cursor B_Fencer = null;

                    try {
                        A_Fencer = fencerDbAdapter.fetchFencer(fencerA_ID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        B_Fencer = fencerDbAdapter.fetchFencer(fencerB_ID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

            /*Calculate Fencer Stats*/

                    Fencer fencerA = new Fencer();
                    Fencer fencerB = new Fencer();
                    int isLefty = 0;

                    //grab DB data
                    if (A_Fencer != null && B_Fencer != null) {

                        fencerA = populateFencer(A_Fencer);
                        fencerB = populateFencer(B_Fencer);

                        //update Cards
                        if (currentBout.cardA == Bout.Card.YELLOW)
                            fencerA.numYCardsDE = fencerA.numYCardsDE + 1;
                        if (currentBout.cardA == Bout.Card.RED)
                            fencerA.numRCardsDE = fencerA.numRCardsDE + 1;

                        if (currentBout.cardB == Bout.Card.YELLOW)
                            fencerB.numYCardsDE = fencerB.numYCardsDE + 1;
                        if (currentBout.cardB == Bout.Card.RED)
                            fencerB.numRCardsDE = fencerB.numRCardsDE + 1;

                        String currAScore = currentBout.FencerAScore;
                        String currBScore = currentBout.FencerBScore;

                        //add victory
                        if (currentBout.Winner.equals("A")) {
                            fencerA.totalV_DE++;
                            currAScore = currentBout.FencerAScore.replace("V", "");

                        } else {
                            fencerB.totalV_DE++;
                            currBScore = currentBout.FencerBScore.replace("V", "");
                        }

                        //add bouts
                        fencerA.totalBoutsDE++;
                        fencerB.totalBoutsDE++;

                        //weighted Average
                        double a_score = Double.valueOf(currAScore);
                        double b_score = Double.valueOf(currBScore);
                        double a_totalBouts = Double.valueOf(fencerA.totalBoutsDE);
                        double b_totalBouts = Double.valueOf(fencerB.totalBoutsDE);

                        //Average Bout Score using running average
                        fencerA.avgBoutScoreDE -= fencerA.avgBoutScoreDE / a_totalBouts;
                        fencerA.avgBoutScoreDE += a_score / a_totalBouts;

                        fencerB.avgBoutScoreDE -= fencerB.avgBoutScoreDE / b_totalBouts;
                        fencerB.avgBoutScoreDE += b_score / b_totalBouts;


                        //Percentage Victory
                        fencerA.perVictoryDE = Double.valueOf(fencerA.totalV_DE) / a_totalBouts;
                        fencerB.perVictoryDE = Double.valueOf(fencerB.totalV_DE) / b_totalBouts;


                        //put back into database
                        fencerDbAdapter.updateFencerDEData(fencerA.dbRowID, fencerA.totalBoutsDE,
                                fencerA.numRCardsDE,
                                fencerA.numYCardsDE, fencerA.totalV_DE, fencerA.avgBoutScoreDE,
                                fencerA.perVictoryDE);

                        fencerDbAdapter.updateFencerDEData(fencerB.dbRowID,
                                fencerB.totalBoutsDE, fencerB.numRCardsDE,
                                fencerB.numYCardsDE, fencerB.totalV_DE, fencerB.avgBoutScoreDE,
                                fencerB.perVictoryDE);


                        //see Left ness
                        if (fencerA.hand == Fencer.Hand.LEFT && fencerB.hand == Fencer.Hand.RIGHT)
                            isLefty = Bout.FencerALefty;
                        else if (fencerB.hand == Fencer.Hand.LEFT && fencerA.hand == Fencer.Hand.RIGHT)
                            isLefty = Bout.FencerBLefty;
                        else if (fencerB.hand == Fencer.Hand.LEFT &&
                                fencerA.hand == Fencer.Hand.LEFT)
                            isLefty = Bout.FencerABLefty;
                        else isLefty = 0;

                    }

                    //get today's date
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    String currentDate = sdf.format(new Date());

                    //update database
                    boutDbAdapter.addBout(fencerA.firstName + " " + fencerA.lastName, fencerB.firstName + " " +
                                    fencerB.lastName, currentBout.Winner,
                            Bout.Card.toString(currentBout.cardA), Bout.Card.toString(currentBout.cardB),
                            currentBout.FencerAScore, currentBout.FencerBScore,
                            currentBout.timeRemaining, currentDate, isLefty, true);
                }

            }
        });
        initBkgdThread.start();

    }

    public Fencer populateFencer(Cursor cursor) {
        try {
            Fencer tempFencer = new Fencer();
            tempFencer.dbRowID = cursor.getLong(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_ROWID));
            tempFencer.firstName = cursor.getString(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_FIRSTNAME));
            tempFencer.lastName = cursor.getString(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_LASTNAME));
            tempFencer.hand =
                    Fencer.Hand.fromInteger(cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_HAND)));
            tempFencer.totalV_DE = cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_TOTALVICT_DE));
            tempFencer.totalBoutsDE = cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_TOTALBOUTS_DE));
            tempFencer.numRCardsDE = cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_NUMRCARDS_DE));
            tempFencer.numYCardsDE = cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_NUMYCARDS_DE));

            String tempP = cursor.getString(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_PERVICT_DE));
            String tempX = cursor.getString(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_AVGBOUTSCORE_DE));

            if (tempP == null) {
                tempFencer.perVictoryDE = 0.0;
            } else tempFencer.perVictoryDE = Double.valueOf(tempP);

            if (tempP == null) {
                tempFencer.avgBoutScoreDE = 0.0;
            } else tempFencer.avgBoutScoreDE = Double.valueOf(tempX);


            return tempFencer;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Fencer();
    }

}

