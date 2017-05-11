package hss.quickpools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
public class TableofSixteen extends Activity {

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
    TextView brackNine;
    TextView brackTen;
    TextView brackEleven;
    TextView brackTwelve;
    TextView brackThirteen;
    TextView brackFourteen;
    TextView brackFifteen;
    TextView brackSixteen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tableofsixteen);

        //assign brackets
        brackOne = (TextView) findViewById(R.id.tb16pos1);
        brackTwo = (TextView) findViewById(R.id.tb16pos2);
        brackThree = (TextView) findViewById(R.id.tb16pos3);
        brackFour = (TextView) findViewById(R.id.tb16pos4);
        brackFive = (TextView) findViewById(R.id.tb16pos5);
        brackSix = (TextView) findViewById(R.id.tb16pos6);
        brackSeven = (TextView) findViewById(R.id.tb16pos7);
        brackEight = (TextView) findViewById(R.id.tb16pos8);

        //halfway point
        brackNine = (TextView) findViewById(R.id.tb16pos9);
        brackTen = (TextView) findViewById(R.id.tb16pos10);
        brackEleven = (TextView) findViewById(R.id.tb16pos11);
        brackTwelve = (TextView) findViewById(R.id.tb16pos12);
        brackThirteen = (TextView) findViewById(R.id.tb16pos13);
        brackFourteen = (TextView) findViewById(R.id.tb16pos14);
        brackFifteen = (TextView) findViewById(R.id.tb16pos15);
        brackSixteen = (TextView) findViewById(R.id.tb16pos16);


        //get fencers
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fencerData = getIntent().getParcelableArrayListExtra("fencers");
            currentTableFencers = getIntent().getParcelableArrayListExtra("currentTableFencers");
            //fromNewTable = getIntent().getBooleanExtra("fromNewTable", true);
            boutList = getIntent().getParcelableArrayListExtra("boutList");
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
                case 9:
                    brackNine.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 10:
                    brackTen.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 11:
                    brackEleven.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 12:
                    brackTwelve.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 13:
                    brackThirteen.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 14:
                    brackFourteen.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 15:
                    brackFifteen.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                case 16:
                    brackSixteen.setText("(" + tempFencer.seed + ") " + tempFencer.getFencingLabel());
                    break;
                default:
                    break;

            }
        }


        //take care of BYEs

        // mim of 8 fencers
        // concerns seeds 9,10,11,12,13,14,15,16
        // they have list positions 4,14,10,6,8,12,16,2
        //
        // (9,4) (10,14) (11,10) (12,6) (13,8) (14,12) (15,16) (16,2)

        //seed 16, position 2
        if (brackTwo.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb16win1v16);
            winTV.setText(brackOne.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 1) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 15, position 16
        if (brackSixteen.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb16win2v15);
            winTV.setText(brackFifteen.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 15) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 14, position 12
        if (brackTwelve.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb16win3v14);
            winTV.setText(brackEleven.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 11) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 13, position 8
        if (brackEight.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb16win4v13);
            winTV.setText(brackSeven.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 7) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 12, position 6
        if (brackSix.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb16win5v12);
            winTV.setText(brackFive.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 5) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 11, position 10
        if (brackTen.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb16win6v11);
            winTV.setText(brackNine.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 9) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 10, position 14
        if (brackFourteen.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb16win7v10);
            winTV.setText(brackThirteen.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 13) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

        //seed 9, position 4
        if (brackFour.getText().toString().contains("BYE")) {
            TextView winTV = (TextView) findViewById(R.id.tb16win8v9);
            winTV.setText(brackThree.getText().toString());

            //show as a winner
            for (Fencer tempFencer : currentTableFencers) {
                if (tempFencer.listPosition == 3) {
                    tempFencer.wonDE = true;
                    break;
                }
            }
        }

    }// end of onCreate()

    //on Back Pressed
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

    //Bout Functions
    public void oneVersusSixteen(View v) {

        return; //can't support yet
        /*
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
        String[] boutData = {"1", names[1], "16", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
        */
    }

    public void eightVersusNine(View v) {

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

        //check for BYE
        if (fencerA.firstName.equals("") || fencerB.firstName.equals("")) {
            //not real fencers, or a BYE
            return;
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"8", names[1], "9", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
    }

    public void fiveVersusTwelve(View v) {

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
        if (fencerA.firstName.equals("") || fencerB.firstName.equals("")) {
            //not real fencers, or a BYE
            return;
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"5", names[1], "12", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
    }

    public void fourVersusThirteen(View v) {

        return; //can't support yet
        /*

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
        String[] boutData = {"4", names[1], "13", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
        */
    }

    public void sixVersusEleven(View v) {

        //get fencer A & B
        Fencer fencerA = new Fencer();
        Fencer fencerB = new Fencer();

        for (Fencer currentFencer : currentTableFencers) {
            if (currentFencer.listPosition == 9) {
                fencerA = currentFencer;
            } else if (currentFencer.listPosition == 10) {
                fencerB = currentFencer;
            }
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"6", names[1], "11", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
    }

    public void threeVersusFourteen(View v) {

        //can't support yet
        return;

        /*
        //get fencer A & B
        Fencer fencerA = new Fencer();
        Fencer fencerB = new Fencer();

        for (Fencer currentFencer : currentTableFencers) {
            if (currentFencer.listPosition == 11) {
                fencerA = currentFencer;
            } else if (currentFencer.listPosition == 12) {
                fencerB = currentFencer;
            }
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"3", names[1], "14", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
        */
    }

    public void sevenVersusTen(View v) {

        //get fencer A & B
        Fencer fencerA = new Fencer();
        Fencer fencerB = new Fencer();

        for (Fencer currentFencer : currentTableFencers) {
            if (currentFencer.listPosition == 13) {
                fencerA = currentFencer;
            } else if (currentFencer.listPosition == 14) {
                fencerB = currentFencer;
            }
        }

        if (fencerA.firstName.equals("") || fencerB.firstName.equals("")) {
            //not real fencers, or a BYE
            return;
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"7", names[1], "10", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);
    }

    public void twoVersusFifteen(View v) {

        //can't support fifteen yet
        return;

        /*


        //get fencer A & B
        Fencer fencerA = new Fencer();
        Fencer fencerB = new Fencer();

        for (Fencer currentFencer : currentTableFencers) {
            if (currentFencer.listPosition == 13) {
                fencerA = currentFencer;
            } else if (currentFencer.listPosition == 14) {
                fencerB = currentFencer;
            }
        }

        //Get names
        String[] names = {"(" + fencerA.seed + ") " + fencerA.firstName + " " + fencerA.lastName,
                "(" + fencerB.seed + ") " + fencerB.firstName + " " + fencerB.lastName};

        //setup bout Sheet data
        String[] boutData = {"7", names[1], "10", names[0]};

        //create new bout sheet
        Intent intent = new Intent(this, BoutSheet.class);
        intent.putExtra("Source", "DE");
        intent.putExtra("boutData", boutData);
        startActivityForResult(intent, 1);

         */
    }

    //on Activity Result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)

        {
            //get bout data
            String[] boutResult = data.getStringArrayExtra("boutResult");
            String boutTag = boutResult[Bout.Key_R_ID] + "-" + boutResult[Bout.Key_L_ID];
            String scoreA = boutResult[Bout.Key_FencerAScore];
            String scoreB = boutResult[Bout.Key_FencerBScore];

            //add bout to list
            addBoutToList(boutTag, boutResult);

            //save results
            switch (boutTag) {
                case "1-16":
                    TextView winnerTV1_16 = (TextView) findViewById(R.id.tb16win1v16);
                    TextView scoreTV1_16 = (TextView) findViewById(R.id.tb16_1v16score);

                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV1_16.setText(brackOne.getText().toString());
                        scoreTV1_16.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 1) {
                                tempFencer.wonDE = true;
                            } else if (tempFencer.listPosition == 2) {
                                tempFencer.wonDE = false;
                            }
                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV1_16.setText(brackTwo.getText().toString());
                        scoreTV1_16.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 1) {
                                tempFencer.wonDE = false;
                            } else if (tempFencer.listPosition == 2) {
                                tempFencer.wonDE = true;
                            }
                        }
                    }


                    break;
                case "8-9":
                    TextView winnerTV89 = (TextView) findViewById(R.id.tb16win8v9);
                    TextView scoreTV89 = (TextView) findViewById(R.id.tb16_8v9score);

                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV89.setText(brackThree.getText().toString());
                        scoreTV89.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 3) {
                                tempFencer.wonDE = true;
                            } else if (tempFencer.listPosition == 4) {
                                tempFencer.wonDE = false;
                            }
                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV89.setText(brackFour.getText().toString());
                        scoreTV89.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 3) {
                                tempFencer.wonDE = false;
                            } else if (tempFencer.listPosition == 4) {
                                tempFencer.wonDE = true;
                            }
                        }
                    }

                    break;

                case "5-12":
                    TextView winnerTV5_12 = (TextView) findViewById(R.id.tb16win5v12);
                    TextView scoreTV5_12 = (TextView) findViewById(R.id.tb16_5v12score);

                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV5_12.setText(brackFive.getText().toString());
                        scoreTV5_12.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 5) {
                                tempFencer.wonDE = true;
                            } else if (tempFencer.listPosition == 6) {
                                tempFencer.wonDE = false;
                            }
                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV5_12.setText(brackSix.getText().toString());
                        scoreTV5_12.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 5) {
                                tempFencer.wonDE = false;
                            } else if (tempFencer.listPosition == 6) {
                                tempFencer.wonDE = true;
                            }
                        }
                    }

                    break;

                case "4-13":
                    //won't happen yet
                    break;

                case "6-11":
                    TextView winnerTV6_11 = (TextView) findViewById(R.id.tb16win6v11);
                    TextView scoreTV6_11 = (TextView) findViewById(R.id.tb16_6v11score);

                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV6_11.setText(brackNine.getText().toString());
                        scoreTV6_11.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 9) {
                                tempFencer.wonDE = true;
                            } else if (tempFencer.listPosition == 10) {
                                tempFencer.wonDE = false;
                            }
                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV6_11.setText(brackTen.getText().toString());
                        scoreTV6_11.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 9) {
                                tempFencer.wonDE = false;
                            } else if (tempFencer.listPosition == 10) {
                                tempFencer.wonDE = true;
                            }
                        }
                    }

                    break;

                case "3-14":
                    //not supported yet

                    break;
                case "7-10":
                    TextView winnerTV7_10 = (TextView) findViewById(R.id.tb16win7v10);
                    TextView scoreTV7_10 = (TextView) findViewById(R.id.tb16_7v10score);

                    if (scoreA.contains("V")) {
                        //1 won
                        winnerTV7_10.setText(brackThirteen.getText().toString());
                        scoreTV7_10.setText(scoreA.replace("V", "") + "-" + scoreB);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {
                            if (tempFencer.listPosition == 13) {
                                tempFencer.wonDE = true;
                            } else if (tempFencer.listPosition == 14) {
                                tempFencer.wonDE = false;
                            }
                        }
                    } else if (scoreB.contains("V")) {
                        //8 won
                        winnerTV7_10.setText(brackFourteen.getText().toString());
                        scoreTV7_10.setText(scoreB.replace("V", "") + "-" + scoreA);

                        //find winner and set DE status
                        for (Fencer tempFencer : currentTableFencers) {

                            if (tempFencer.listPosition == 13) {
                                tempFencer.wonDE = false;
                            } else if (tempFencer.listPosition == 14) {
                                tempFencer.wonDE = true;
                            }
                        }
                    }

                    break;


                case "2-15":
                    //not supported yet

                    break;

            }
        }
    } //end of onActivityResult

    //onto the next one
    public void onNextTableofSixteen(View V) {

        //make sure all bouts are finished
        TextView winnerTV18 = (TextView) findViewById(R.id.tb16win8v9);
        TextView winnerTV72 = (TextView) findViewById(R.id.tb16win6v11);
        TextView winnerTV63 = (TextView) findViewById(R.id.tb16win7v10);
        TextView winnerTV54 = (TextView) findViewById(R.id.tb16win5v12);

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

        // Table 16  = places 9-16
        int counter = 9;
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
        Intent intent = new Intent(this, TableofEight.class);
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
