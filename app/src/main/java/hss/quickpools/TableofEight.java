package hss.quickpools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Kat on 6/3/2015.
 */
public class TableofEight extends ActionBarActivity {


    //list of fencers
    public ArrayList<Fencer> fencerData = new ArrayList<Fencer>();
    public ArrayList<Fencer> currentTableFencers = new ArrayList<Fencer>();
    public ArrayList<Bout> boutList = new ArrayList<Bout>();
    public boolean fromNewTable = true;

    //get brackets
    TextView brackOne;
    TextView brackTwo;
    TextView brackThree;
    TextView brackFour;
    TextView brackFive;
    TextView brackSix;
    TextView brackSeven;
    TextView brackEight;

    //menu
    private static final int PLACES_ID = Menu.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tableofeight);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //assign brackets
        brackOne = (TextView) findViewById(R.id.tb8pos1);
        brackTwo = (TextView) findViewById(R.id.tb8pos2);
        brackThree = (TextView) findViewById(R.id.tb8pos3);
        brackFour = (TextView) findViewById(R.id.tb8pos4);
        brackFive = (TextView) findViewById(R.id.tb8pos5);
        brackSix = (TextView) findViewById(R.id.tb8pos6);
        brackSeven = (TextView) findViewById(R.id.tb8pos7);
        brackEight = (TextView) findViewById(R.id.tb8pos8);

        //get fencers
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fencerData = getIntent().getParcelableArrayListExtra("fencers");
            currentTableFencers = getIntent().getParcelableArrayListExtra("currentTableFencers");
            boutList = getIntent().getParcelableArrayListExtra("boutList");
            fromNewTable = getIntent().getBooleanExtra("fromNewTable", true);

            if (boutList == null) {
                boutList = new ArrayList<Bout>();
            }
        }

        //upload fencers to table
        for (int i = 0; i < currentTableFencers.size(); i++) {
            Fencer tempFencer = currentTableFencers.get(i);

            //switch based on seeding
            switch (tempFencer.listPosition) {
                case 1:
                    brackOne.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 2:
                    brackTwo.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 3:
                    brackThree.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 4:
                    brackFour.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 5:
                    brackFive.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 6:
                    brackSix.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 7:
                    brackSeven.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 8:
                    brackEight.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                default:
                    break;

            }
        }


        // Account for BYEs
        // minimum of 4 fencers required, so 1-4 shouldn't
        // be a concern, only 8,5,6, and 7

        //seed 8, 1 has a bye
        if (brackTwo.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb8win1v8);
            winTV.setText(brackOne.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 1) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 5, 4 has a bye
        if (brackThree.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb8win5v4);
            winTV.setText(brackFour.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 4) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }


        //seed 6, seed 3 has a bye (3 in list position 6)
        if (brackFive.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb8win3v6);
            winTV.setText(brackSix.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 6) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 7, seed 2 has a bye (2 in list position 8)
        if (brackSeven.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb8win7v2);
            winTV.setText(brackEight.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 8) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }


    } //end of onCreate()

    @Override
    public void onBackPressed() {
        //warn of discarding
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.discard_table))
                .setMessage(getString(R.string.discard_table) + "?")
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //save to database then exit
                        finish();

                    }

                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    //Context menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, PLACES_ID, 0, R.string.see_current_standings);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case PLACES_ID:


                //create list of finished fencers
                ArrayList<Fencer> finishedFencers = new ArrayList<>();

                for (Fencer f : fencerData) {
                    if (f.wonDE == false) {
                        finishedFencers.add(f);
                    }
                }

                //create intent
                Intent intent = new Intent(this, FinalResults.class);
                intent.putExtra("fencers", finishedFencers);
                intent.putExtra("finished", false);
                startActivity(intent);

                break;

        }
        return super.onOptionsItemSelected(item);
    }

//bout functions

    public void oneVersusEight(View v) {

        //get fencer A & B
        Fencer fencerA = new Fencer();
        Fencer fencerB = new Fencer();

        for (Fencer currentFencer : currentTableFencers) {
            if (currentFencer.listPosition == 1) {
                fencerA = currentFencer;
            } else if (currentFencer.listPosition == 2) {
                fencerB = currentFencer;
            }
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"1", names[1], "8", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
    }
    public void fiveVersusFour(View v) {

        //get fencer A & B
        Fencer fencerA = new Fencer();
        Fencer fencerB = new Fencer();

        for (Fencer currentFencer : currentTableFencers) {
            if (currentFencer.listPosition == 3) {
                fencerA = currentFencer;
            } else if (currentFencer.listPosition == 4) {
                fencerB = currentFencer;
            }
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"5", names[1], "4", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
    }
    public void sixVersusThree(View v) {
        //get fencer A & B
        Fencer fencerA = new Fencer();
        Fencer fencerB = new Fencer();

        for (Fencer currentFencer : currentTableFencers) {
            if (currentFencer.listPosition == 5) {
                fencerA = currentFencer;
            } else if (currentFencer.listPosition == 6) {
                fencerB = currentFencer;
            }
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"6", names[1], "3", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
    }
    public void sevenVersusTwo(View v) {
        //get fencer A & B
        Fencer fencerA = new Fencer();
        Fencer fencerB = new Fencer();

        for (Fencer currentFencer : currentTableFencers) {
            if (currentFencer.listPosition == 7) {
                fencerA = currentFencer;
            } else if (currentFencer.listPosition == 8) {
                fencerB = currentFencer;
            }
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"7", names[1], "2", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);

    }

    //on Activity Result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)

        {   //get bout data
            String[] boutResult = data.getStringArrayExtra("boutResult");
            String boutTag = boutResult[Bout.Key_R_ID] + "-" + boutResult[Bout.Key_L_ID];
            String scoreA = boutResult[Bout.Key_FencerAScore];
            String scoreB = boutResult[Bout.Key_FencerBScore];

            //add bout to list
            addBoutToList(boutTag, boutResult);

            //save results
            switch (boutTag) {
                case "1-8":
                    // Text Views
                    TextView winnerTV18 = (TextView) findViewById(R.id.tb8win1v8);
                    TextView scoreTV18 = (TextView) findViewById(R.id.tb8_1v8score);

                    //show on screen score info
                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV18.setText(brackOne.getText().toString());
                        scoreTV18.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 1) tempFencer.wonDE = true;
                            else if (tempFencer.listPosition == 2) tempFencer.wonDE = false;
                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV18.setText(brackTwo.getText().toString());
                        scoreTV18.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 1) tempFencer.wonDE = false;
                            else if (tempFencer.listPosition == 2) tempFencer.wonDE = true;

                        }
                    }

                    break;

                case "5-4":
                    TextView winnerTV54 = (TextView) findViewById(R.id.tb8win5v4);
                    TextView scoreTV54 = (TextView) findViewById(R.id.tb8_5v4score);

                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV54.setText(brackThree.getText().toString());
                        scoreTV54.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 3) tempFencer.wonDE = true;
                            else if (tempFencer.listPosition == 4) tempFencer.wonDE = false;

                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV54.setText(brackFour.getText().toString());
                        scoreTV54.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 3) tempFencer.wonDE = false;
                            else if (tempFencer.listPosition == 4) tempFencer.wonDE = true;
                        }
                    }

                    break;

                case "6-3":
                    TextView winnerTV63 = (TextView) findViewById(R.id.tb8win3v6);
                    TextView scoreTV63 = (TextView) findViewById(R.id.tb8_6v3score);

                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV63.setText(brackFive.getText().toString());
                        scoreTV63.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 5) tempFencer.wonDE = true;
                            else if (tempFencer.listPosition == 6) tempFencer.wonDE = false;
                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV63.setText(brackSix.getText().toString());
                        scoreTV63.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 5) tempFencer.wonDE = false;
                            else if (tempFencer.listPosition == 6) tempFencer.wonDE = true;
                        }
                    }

                    break;

                case "7-2":

                    TextView winnerTV78 = (TextView) findViewById(R.id.tb8win7v2);
                    TextView scoreTV78 = (TextView) findViewById(R.id.tb8_7v2score);

                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV78.setText(brackSeven.getText().toString());
                        scoreTV78.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 7) tempFencer.wonDE = true;
                            else if (tempFencer.listPosition == 8) tempFencer.wonDE = false;

                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV78.setText(brackEight.getText().toString());
                        scoreTV78.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 7) tempFencer.wonDE = false;
                            else if (tempFencer.listPosition == 8) tempFencer.wonDE = true;

                        }
                    }

                    break;


            }
        }
    } //end of onActivityResult

    //On End of Table
    public void onTableEightNext(View v) {

        //make sure all bouts are finished
        TextView winnerTV18 = (TextView) findViewById(R.id.tb8win1v8);
        TextView winnerTV72 = (TextView) findViewById(R.id.tb8win7v2);
        TextView winnerTV63 = (TextView) findViewById(R.id.tb8win3v6);
        TextView winnerTV54 = (TextView) findViewById(R.id.tb8win5v4);

        //warn if not
        if (!winnerTV18.getText().toString().contains("(") ||
                !winnerTV72.getText().toString().contains("(") ||
                !winnerTV63.getText().toString().contains("(") ||
                !winnerTV54.getText().toString().contains("(")) {
            Toast.makeText(getApplicationContext(), getString(R.string.table_not_finished),
                    Toast.LENGTH_SHORT).show();
            return;

        }
        //Update places on main list
        ArrayList<Fencer> winners = new ArrayList<Fencer>();
        ArrayList<Fencer> losers = new ArrayList<Fencer>();

        //get list of losers & winners
        //in pool of 4 losers tie for third
        for (Fencer fdFencer : fencerData) {
            for (Fencer ctFencer : currentTableFencers) {
                //if same fencer
                if (ctFencer.firstName.equals(fdFencer.firstName) &&
                        ctFencer.lastName.equals(fdFencer.lastName)) {

                    if (ctFencer.wonDE == false) { //losers
                        //both are 3rd place
                        fdFencer.wonDE = false;
                        losers.add(fdFencer);

                    } else { //winners
                        fdFencer.wonDE = true;

                        //copying to avoid list computation issues onResume();
                        //preserving original list positions
                        Fencer tempFencer = ctFencer.copyFencer(ctFencer);
                        winners.add(tempFencer);
                    }
                }

            } //end of second for loop

        }// end of first for loop


        //set places for losing fencers
        //sort by highest to lowest seed
        Collections.sort(losers, new Fencer.DESeedComparator());

        // Table 8 = places 5th -8th
        int counter = 5;
        for (Fencer losingFencer : losers) {
            for (Fencer fdFencer : fencerData) {
                if (losingFencer.firstName.equals(fdFencer.firstName) &&
                        losingFencer.lastName.equals(fdFencer.lastName)) {

                    fdFencer.place = counter;
                    counter++;


                }
            }
        }

        //get new list positions
        winners = Fencer.redoListPositions(winners);

        //send winners off to next intent
        Intent intent = new Intent(this, TableofFour.class);
        intent.putExtra("fencers", fencerData);
        intent.putExtra("currentTableFencers", winners);
        intent.putExtra("boutList", boutList);
        intent.putExtra("fromNewTable", false);
        startActivity(intent);


    }

    //Bout List for Database Saving Later
    public void addBoutToList(String boutTag, String[] boutData) {

        // Bout info
        Bout tempBout = getBoutFromList(boutTag);

        // check if already on list
        if (tempBout == null) {
            //create new bout
            tempBout = new Bout();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

            tempBout.Date = sdf.format(new Date()).toString();
            tempBout.Tag = boutTag;

            tempBout.FencerA = boutData[Bout.Key_FencerA];
            tempBout.FencerAScore = boutData[Bout.Key_FencerAScore];

            tempBout.FencerB = boutData[Bout.Key_FencerB];
            tempBout.FencerBScore = boutData[Bout.Key_FencerBScore];
            tempBout.timeRemaining = boutData[Bout.Key_TimeRemaining];

            tempBout.cardA = Bout.Card.fromString(boutData[Bout.Key_CardA]);
            tempBout.cardB = Bout.Card.fromString(boutData[Bout.Key_CardB]);

            tempBout.isDE = true;

            if (tempBout.FencerAScore.contains("V")) {
                tempBout.Winner = "A";
            } else tempBout.Winner = "B";

            //save
            boutList.add(tempBout);

        } else {
            // re-fencing bout
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            tempBout.Date = sdf.format(new Date()).toString();
            tempBout.Tag = boutTag;
            tempBout.FencerA = boutData[Bout.Key_FencerA];
            tempBout.FencerAScore = boutData[Bout.Key_FencerAScore];
            tempBout.FencerB = boutData[Bout.Key_FencerB];
            tempBout.FencerBScore = boutData[Bout.Key_FencerBScore];
            tempBout.timeRemaining = boutData[Bout.Key_TimeRemaining];
            tempBout.cardA = Bout.Card.fromString(boutData[Bout.Key_CardA]);
            tempBout.cardB = Bout.Card.fromString(boutData[Bout.Key_CardB]);
            tempBout.isDE = true;

        }

    }

    public Bout getBoutFromList(String tag) {
        for (Bout b : boutList) {
            if (b.Tag.equals(tag)) {
                return b;
            }
        }
        return null;

    }



}
