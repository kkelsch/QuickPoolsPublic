package hss.quickpools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Kat on 4/26/2015.
 */

public class PoolSheet extends ActionBarActivity {

    //create a list of bout pools
    ArrayList<Bout> boutList = new ArrayList<Bout>();
    public ArrayList<Fencer> pFencerData = new ArrayList<Fencer>();
    public int numOfFencers = 0;
    private static final int NOTES_ID = Menu.FIRST;
    private static final int EMAIL_ID = Menu.FIRST + 1;
    private static final int CHART_ID = Menu.FIRST + 2;


    //Create Adapters
    private BoutDbAdapter boutDbAdapter;
    private FencersDbAdapter fencerDbAdapter;

    //array of bouts displayed
    public CustomAdapter lvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pool_sheet);

        //create adapters
        boutDbAdapter = new BoutDbAdapter(this);
        fencerDbAdapter = new FencersDbAdapter(this);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        //Grab data from main activity
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            pFencerData = getIntent().getParcelableArrayListExtra("fencers");
        }

        //Save Size
        numOfFencers = pFencerData.size();


        //draw Pool table
        setupPoolTable();

        //Setup List view
        ListView lw = (ListView) findViewById(R.id.lvBouts);


        //adapter to use on ListView
        String[] chosenArray = {""};

        //Show Bout Order
        switch (numOfFencers) {
            case 2:
                chosenArray = poolOfTwo;
                break;
            case 3:
                chosenArray = poolOfThree;
                break;
            case 4:
                chosenArray = poolOfFour;
                break;
            case 5:
                chosenArray = poolOfFive;
                break;
            case 6:
                chosenArray = poolOfSix;
                break;
            case 7:
                chosenArray = poolOfSeven;
                break;
            case 8:
                chosenArray = poolOfEight;
                break;
            case 9:
                chosenArray = poolOfNine;
                break;
            case 10:
                chosenArray = poolOfTen;
                break;
            case 11:
                chosenArray = poolOfEleven;
                break;
            case 12:
                chosenArray = poolofTwelve;
                break;

        }


        //set Adapter
        lvAdapter = new CustomAdapter(chosenArray);
        lw.setAdapter(lvAdapter);

        //set position IDs


        //List view onClick listener
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //get IDs
                String data = (String) parent.getItemAtPosition(position);


                //remove names
                int endIndex = data.lastIndexOf(",");
                if (endIndex != -1) {
                    data = data.substring(0, endIndex).trim();
                }

                //get IDs
                String[] IDs = data.split("-");


                //get fencer A& B
                Fencer fencerA = pFencerData.get(Integer.parseInt(IDs[0]) - 1);
                Fencer fencerB = pFencerData.get(Integer.parseInt(IDs[1]) - 1);

                for (Fencer currentFencer : pFencerData) {
                    if (currentFencer.listPosition == Integer.valueOf(IDs[0])) {
                        fencerA = currentFencer;
                    } else if (currentFencer.listPosition == Integer.valueOf(IDs[1])) {
                        fencerB = currentFencer;
                    }

                }


                //Get names
                String[] names = {fencerA.firstName + " " + fencerA.lastName,
                        fencerB.firstName + " " + fencerB.lastName};

                //setup bout Sheet data
                String[] boutData = {IDs[1], names[1], IDs[0], names[0]};

                //create new bout sheet
                Intent intent = new Intent(PoolSheet.this, BoutSheet.class);
                intent.putExtra("Source", "PoolSheet");
                intent.putExtra("boutData", boutData);

                startActivityForResult(intent, 1);

            }
        });


        //update list of Bouts
        for (int i = 0; i < lvAdapter.getCount(); i++) {

            //create a new bout
            Bout tempBout = new Bout();
            tempBout.setTag(chosenArray[i]);

            //Add to List
            boutList.add(tempBout);

            try {
                //add name on bout list
                String[] ids = chosenArray[i].split("-");
                int ID_A = Integer.valueOf(ids[0]);
                int ID_B = Integer.valueOf(ids[1]);

                //get fencer names
                String nameA = pFencerData.get(ID_A - 1).lastName;
                String nameB = pFencerData.get(ID_B - 1).lastName;

                chosenArray[i] = chosenArray[i] + " , " + nameA + " v " + nameB;

            } catch (Exception x) {

            }

        }

        //update adapter
        lvAdapter.notifyDataSetChanged();
    }

    //Collect data once bout is finished
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            //get bout data
            String[] boutResult = data.getStringArrayExtra("boutResult");
            String boutTag = boutResult[Bout.Key_L_ID] + "-" + boutResult[Bout.Key_R_ID];
            Bout tempB = new Bout();
            int i;
            String ID_A = boutResult[Bout.Key_L_ID];
            String ID_B = boutResult[Bout.Key_R_ID];

            //cross off list
            //strike through text
            for (i = 0; i < lvAdapter.getCount(); i++) {
                if (lvAdapter.getItem(i).contains(boutTag)) {
                    if (!lvAdapter.selectedStrings.contains(lvAdapter.getItem(i))) {
                        lvAdapter.selectedStrings.add(lvAdapter.getItem(i));
                        lvAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }

            //Save to list
            for (i = 0; i < boutList.size(); i++) {

                if (boutList.get(i).getTag().equals(boutTag)) {
                    //set Bout Data
                    tempB = boutList.get(i);
                    tempB.FencerA = boutResult[Bout.Key_FencerA];
                    tempB.FencerAScore = boutResult[Bout.Key_FencerAScore];

                    tempB.FencerB = boutResult[Bout.Key_FencerB];
                    tempB.FencerBScore = boutResult[Bout.Key_FencerBScore];
                    tempB.timeRemaining = boutResult[Bout.Key_TimeRemaining];

                    tempB.cardA = Bout.Card.fromString(boutResult[Bout.Key_CardA]);
                    tempB.cardB = Bout.Card.fromString(boutResult[Bout.Key_CardB]);

                    tempB.pID_FencerA = Integer.valueOf(ID_A);
                    tempB.pID_FencerB = Integer.valueOf(ID_B);

                    if (tempB.FencerAScore.contains("V")) {
                        tempB.Winner = "A";
                    } else tempB.Winner = "B";

                    //save
                    boutList.set(i, tempB);

                    //exit for loop
                    break;
                }

            }

            //Grab TextViews
            TableLayout tlPool = (TableLayout) findViewById(R.id.tlPool);
            TextView tvScoreA = (TextView) tlPool.findViewWithTag(ID_A + "-" + ID_B);
            TextView tvScoreB = (TextView) tlPool.findViewWithTag(ID_B + "-" + ID_A);

            //display Scores
            tvScoreA.setText(tempB.FencerAScore);
            tvScoreB.setText(tempB.FencerBScore);

            //show bout results for scores
            if (tempB.Winner.equals("A")) {

                //A fencer won
                tvScoreA.setTextColor(Color.rgb(34, 139, 34));
                tvScoreB.setTextColor(Color.BLACK);

            } else { //B fencer won
                tvScoreB.setTextColor(Color.rgb(34, 139, 34));
                tvScoreA.setTextColor(Color.BLACK);

            }

            //Show Cards
            switch (tempB.cardA) {
                case YELLOW:
                    tvScoreA.setBackgroundResource(R.drawable.customyellow);
                    break;
                case RED:
                    tvScoreA.setBackgroundResource(R.drawable.customred);
                    break;
                default:
                    tvScoreA.setBackgroundResource(R.drawable.customblackborder);
                    break;
            }
            switch (tempB.cardB) {
                case YELLOW:
                    tvScoreB.setBackgroundResource(R.drawable.customyellow);
                    break;
                case RED:
                    tvScoreB.setBackgroundResource(R.drawable.customred);
                    break;
                default:
                    tvScoreB.setBackgroundResource(R.drawable.customblackborder);
                    break;
            }

            //Calculate End Results (TS,TR, etc)
            calculateEndResults(ID_A);
            calculateEndResults(ID_B);

            //check to see if places need to be done
            ArrayList<String> crossedOff = lvAdapter.getSelectedStrings();

            //if all bouts are finished, calculate places
            if (crossedOff.size() >= boutList.size()) {
                calculatePlaces();
            }


        }
    }

    //Calculate End Columns
    public void calculateEndResults(String listPosition) {
        //declare variables
        int victories = 0;
        int TS = 0;
        int TR = 0;
        int ind;

        TableLayout tlPool = (TableLayout) findViewById(R.id.tlPool);
        //get row of scores

        ArrayList<String> rowOfScores = new ArrayList<String>();
        ArrayList<String> colOfScores = new ArrayList<String>();

        //go through columns and put data into list
        for (int k = 1; k <= numOfFencers; k++) {

            //Find text view
            TextView tempCell = (TextView) tlPool.findViewWithTag(listPosition + "-" + Integer.toString(k));

            //get data
            rowOfScores.add(tempCell.getText().toString());

            //repeat with columns
            tempCell = (TextView) tlPool.findViewWithTag(Integer.toString(k) + "-" + listPosition);
            colOfScores.add(tempCell.getText().toString());


        }

        //Count number of victories & TS
        for (String word : rowOfScores) {
            if (word.contains("V")) {
                //won
                victories++;

                String data = word.replace("V", "");
                if (data.equals("")) TS = TS + 5;
                else TS = TS + Integer.valueOf(data);
            } else if (!word.equals("") && !word.contains("_") && !word.contains("-")) {

                //Lost
                TS = TS + Integer.valueOf(word);
            }

        }

        //TR Calc
        for (String word : colOfScores) {
            if (word.contains("V")) {

                String data = word.replace("V", "");
                if (data.equals("")) TR = TR + 5;
                else TR = TR + Integer.valueOf(data);
            } else if (!word.equals("") && !word.contains("_") && !word.contains("-")) {

                //Lost
                TR = TR + Integer.valueOf(word);
            }
        }

        //Indicator
        ind = TS - TR;

        //get fencer
        Fencer tempFencer = new Fencer();
        for (Fencer f : pFencerData) {
            if (f.listPosition == Integer.valueOf(listPosition)) {
                tempFencer = f;
                break;

            }
        }

        tempFencer.V = victories;
        tempFencer.IND = ind;
        tempFencer.TR = TR;
        tempFencer.TS = TS;

        //Show on pool sheet
        TextView vCell = (TextView) tlPool.findViewWithTag(listPosition + "-V");
        vCell.setText(Integer.toString(victories));

        TextView tsCell = (TextView) tlPool.findViewWithTag(listPosition + "-TS");
        tsCell.setText(Integer.toString(TS));

        TextView trCell = (TextView) tlPool.findViewWithTag(listPosition + "-TR");
        trCell.setText(Integer.toString(TR));

        TextView indCell = (TextView) tlPool.findViewWithTag(listPosition + "-Ind");
        indCell.setText(Integer.toString(ind));

    }

    //Calculate Places
    public void calculatePlaces() {

        //sort the list
        Collections.sort(pFencerData, new Fencer.FencerComparator());
        Collections.reverse(pFencerData);

        //get table Layout
        TableLayout tlPool = (TableLayout) findViewById(R.id.tlPool);

        //show on table
        for (int i = 0; i < numOfFencers; i++) {
            //get cell
            Fencer tempFencer = pFencerData.get(i);
            TextView plCell = (TextView) tlPool.findViewWithTag(tempFencer.listPosition + "-Pl");

            //set data on table and on fencer's list
            plCell.setText(Integer.toString(i + 1));
            pFencerData.get(i).setPlace(i + 1);

        }
    }

    @Override
    public void onBackPressed() {
        //warn of discarding
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.discard_pool))
                .setMessage(getString(R.string.discard_pool) + "?")
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

    //on finish
    public void onPoolFinished(View v) {
        //check to see if completed
        ArrayList<String> crossedOff = lvAdapter.getSelectedStrings();

        if (crossedOff.size() < lvAdapter.getCount()) {
            //not all fencers have fenced
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.unfinished_pool))
                    .setMessage(getString(R.string.not_all_bouts_fenced))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            //finished pool sheet

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.finished_pool))
                    .setMessage(getString(R.string.save_to_database))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //save to database then exit
                            saveBoutsToDataBase();
                            goToPoolResults();

                        }

                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish pool
                            goToPoolResults();

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        }

    }

    // Go to Pool Results
    public void goToPoolResults() {
        //add fencer data to intent
        Intent intent = new Intent().setClass(PoolSheet.this, PoolResults.class);
        intent.putExtra("fencers", pFencerData);

        //Start pool result Activity
        startActivityForResult(intent, 1);
    }

    //draw pool sheet
    public void setupPoolTable() {
        //grab Table Layout
        TableLayout tlPool = (TableLayout) findViewById(R.id.tlPool);

        //create Column Headers
        //Create Row
        TableRow newRow = new TableRow(this);

        //Create name column
        TextView colName = new TextView(this);
        colName.setText(getString(R.string.name));
        colName.setGravity(Gravity.CENTER);
        colName.setBackgroundResource(R.drawable.customblackborder);

        // colName.setBackgroundResource(R.drawable.cell_shape);
        newRow.addView(colName);

        //create # column
        TextView colNum = new TextView(this);
        colNum.setText("#");
        colNum.setGravity(Gravity.CENTER);
        colNum.setBackgroundResource(R.drawable.customblackborder);
        newRow.addView(colNum);

        //create number columns
        for (int j = 0; j < numOfFencers; j++) {
            TextView colTemp = new TextView(this);
            colTemp.setText(Integer.toString(j + 1));
            colTemp.setGravity(Gravity.CENTER);
            colTemp.setBackgroundResource(R.drawable.customblackborder);
            newRow.addView(colTemp);

        }

        //Victories
        TextView colV = new TextView(this);
        colV.setText("V");
        colV.setGravity(Gravity.CENTER);
        colV.setBackgroundResource(R.drawable.customblackborder);
        newRow.addView(colV);

        //Touches Scored
        TextView colTS = new TextView(this);
        colTS.setText("TS");
        colTS.setGravity(Gravity.CENTER);
        colTS.setBackgroundResource(R.drawable.customblackborder);
        newRow.addView(colTS);


        //Touches received
        TextView colTR = new TextView(this);
        colTR.setText("TR");
        colTR.setGravity(Gravity.CENTER);
        colTR.setBackgroundResource(R.drawable.customblackborder);
        newRow.addView(colTR);

        //Indicator
        TextView colInd = new TextView(this);
        colInd.setText("Ind");
        colInd.setGravity(Gravity.CENTER);
        colInd.setBackgroundResource(R.drawable.customblackborder);
        newRow.addView(colInd);


        //Place
        TextView colPl = new TextView(this);
        colPl.setText("Pl");
        colPl.setGravity(Gravity.CENTER);
        colPl.setBackgroundResource(R.drawable.customblackborder);
        newRow.addView(colPl);


        //Add row
        tlPool.addView(newRow, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

        //Write other rows
        for (int j = 0; j < numOfFencers; j++) {
            //Create Row
            TableRow newFencer = new TableRow(this);

            //Create name column
            TextView colFencerName = new TextView(this);

            //Format Name
            Fencer tempFencer = pFencerData.get(j);
            colFencerName.setText(tempFencer.lastName + "," + tempFencer.firstName.charAt(0) + ".");
            colFencerName.setGravity(Gravity.CENTER);
            colFencerName.setBackgroundResource(R.drawable.customblackborder);
            newFencer.addView(colFencerName);

            //add number cell
            TextView colNumberCell = new TextView(this);
            colNumberCell.setText(Integer.toString(j + 1));
            colNumberCell.setGravity(Gravity.CENTER);
            colNumberCell.setBackgroundResource(R.drawable.customblackborder);
            newFencer.addView(colNumberCell);


            //get current row count
            int currentID = tlPool.getChildCount();
            int currentRow = currentID - 1;

            //constant values
            int vColumn = numOfFencers;
            int tsColumn = numOfFencers + 1;
            int trColumn = numOfFencers + 2;
            int indColumn = numOfFencers + 3;
            int plColumn = numOfFencers + 4;


            //add bout cells
            for (int k = 0; k < numOfFencers + 5; k++) {

                //if currently have 5 rows, this is row 6
                if (k == currentRow) {
                    //set to black
                    TextView tempCell = new TextView(this);
                    tempCell.setBackgroundResource(R.drawable.customblackbox);
                    tempCell.setText("-");
                    tempCell.setTag(currentID + "-" + (k + 1));

                    //add it to the table row
                    newFencer.addView(tempCell);

                } else {

                    TextView tempCell = new TextView(this);

                    tempCell.setText("__");
                    tempCell.setBackgroundResource(R.drawable.customblackborder);
                    tempCell.setGravity(Gravity.CENTER);

                    if (k < numOfFencers) {
                        tempCell.setClickable(true);
                        tempCell.setTag(currentID + "-" + (k + 1));
                    } else {
                        tempCell.setClickable(false);

                        //add on tag
                        if (k == vColumn) {
                            tempCell.setTag(currentID + "-V");
                            tempCell.setText("0");
                        } else if (k == tsColumn) {
                            tempCell.setTag(currentID + "-TS");
                            tempCell.setText("0");
                        } else if (k == trColumn) {
                            tempCell.setTag(currentID + "-TR");
                            tempCell.setText("0");
                        } else if (k == indColumn) {
                            tempCell.setTag(currentID + "-Ind");
                            tempCell.setText("0");
                        } else if (k == plColumn) {
                            tempCell.setTag(currentID + "-Pl");


                        }
                    }
                    //add to table row
                    newFencer.addView(tempCell);
                }

            }
            //add row to table layout
            tlPool.addView(newFencer, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));


        } //continue for

    }

    //Menu Items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, NOTES_ID, 0, R.string.notes);
        menu.add(0, EMAIL_ID, 0, R.string.export_pool);
        menu.add(0, CHART_ID, 0, R.string.event_chart);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NOTES_ID:
                //open Notepad
                Intent i = new Intent(this, Notepad.class);
                startActivity(i);
                break;
            case EMAIL_ID:
                //send
                //emailPool();
                emailScreenShot();
                break;
            case CHART_ID:
                Intent it = new Intent(this, EventClassChart.class);
                startActivity(it);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void emailScreenShot() {

        //get bitmap of activity
        View v = findViewById(R.id.tlPool);
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(),
                v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);

        //save bitmap externally
        String path = Environment.getExternalStorageDirectory().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyy");
        String currentDateandTime = sdf.format(new Date());
        File file = new File(path, "Pool" + currentDateandTime + ".JPEG");
        Uri pngUri = Uri.fromFile(file);
        FileOutputStream fos;

        //compress
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("BoutSlip", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("BoutSlip", e.getMessage(), e);
        }

        //email intent
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("image/png");
        emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, pngUri);
        startActivity(Intent.createChooser(emailIntent, "Send using:"));

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

                    //Get fencers
                    String[] ids = currentBout.getTag().split("-");

                    //find fencers in list
                    long fencerA_ID = 0;
                    long fencerB_ID = 0;

                    //get fencer IDs
                    for (int j = 0; j < pFencerData.size(); j++) {
                        Fencer tempFencer = pFencerData.get(j);

                        if (currentBout.FencerA.equals(tempFencer.firstName + " " +
                                tempFencer.lastName)) {
                            fencerA_ID = tempFencer.dbRowID;
                        } else if (currentBout.FencerB.equals(tempFencer.firstName + " " +
                                tempFencer.lastName)) {
                            fencerB_ID = tempFencer.dbRowID;
                        }
                    }

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

                    Fencer fencerA;
                    Fencer fencerB;
                    int isLefty = 0;


                    //grab DB data
                    if (A_Fencer != null && B_Fencer != null) {

                        fencerA = populateFencer(A_Fencer);
                        fencerB = populateFencer(B_Fencer);

                        //update Cards
                        if (currentBout.cardA == Bout.Card.YELLOW)
                            fencerA.numYCards = fencerA.numYCards + 1;
                        if (currentBout.cardA == Bout.Card.RED)
                            fencerA.numRCards = fencerA.numRCards + 1;

                        if (currentBout.cardB == Bout.Card.YELLOW)
                            fencerB.numYCards = fencerB.numYCards + 1;
                        if (currentBout.cardB == Bout.Card.RED)
                            fencerB.numRCards = fencerB.numRCards + 1;

                        String currAScore = currentBout.FencerAScore;
                        String currBScore = currentBout.FencerBScore;

                        //add victory
                        if (currentBout.Winner.equals("A")) {
                            fencerA.totalV++;
                            currAScore = currentBout.FencerAScore.replace("V", "");
                            if (currAScore.equals("")) currAScore = "5";
                        } else {
                            fencerB.totalV++;
                            currBScore = currentBout.FencerBScore.replace("V", "");
                            if (currBScore.equals("")) currBScore = "5";
                        }

                        //add bouts
                        fencerA.totalBouts++;
                        fencerB.totalBouts++;


                        //weighted Average
                        double a_score = Double.valueOf(currAScore);
                        double b_score = Double.valueOf(currBScore);
                        double a_totalBouts = Double.valueOf(fencerA.totalBouts);
                        double b_totalBouts = Double.valueOf(fencerB.totalBouts);

                        //Average Bout Score using running average
                        fencerA.avgBoutScore -= fencerA.avgBoutScore / a_totalBouts;
                        fencerA.avgBoutScore += a_score / a_totalBouts;

                        fencerB.avgBoutScore -= fencerB.avgBoutScore / b_totalBouts;
                        fencerB.avgBoutScore += b_score / b_totalBouts;


                        //Percentage Victory
                        fencerA.perVictory = Double.valueOf(fencerA.totalV) / a_totalBouts;
                        fencerB.perVictory = Double.valueOf(fencerB.totalV) / b_totalBouts;


                        //put back into database
                        fencerDbAdapter.updateFencerPoolData(fencerA.dbRowID, fencerA.totalBouts, fencerA.numRCards,
                                fencerA.numYCards, fencerA.totalV, fencerA.avgBoutScore,
                                fencerA.perVictory);

                        fencerDbAdapter.updateFencerPoolData(fencerB.dbRowID, fencerB.totalBouts, fencerB.numRCards,
                                fencerB.numYCards, fencerB.totalV, fencerB.avgBoutScore,
                                fencerB.perVictory);


                        //set Left ness
                        //A left B right
                        if (fencerA.hand == Fencer.Hand.LEFT &&
                                fencerB.hand == Fencer.Hand.RIGHT)
                            isLefty = Bout.FencerALefty;
                            // A right B Left
                        else if (fencerB.hand == Fencer.Hand.LEFT &&
                                fencerA.hand == Fencer.Hand.RIGHT)
                            isLefty = Bout.FencerBLefty;
                            //A left B Right
                        else if (fencerB.hand == Fencer.Hand.LEFT &&
                                fencerA.hand == Fencer.Hand.LEFT)
                            isLefty = Bout.FencerABLefty;
                        else isLefty = 0;


                    }

                    //get today's date
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    String currentDate = sdf.format(new Date());

                    //update database
                    boutDbAdapter.addBout(currentBout.FencerA, currentBout.FencerB, currentBout.Winner,
                            Bout.Card.toString(currentBout.cardA), Bout.Card.toString(currentBout.cardB),
                            currentBout.FencerAScore, currentBout.FencerBScore,
                            currentBout.timeRemaining, currentDate, isLefty, false);
                }


            }
        });
        initBkgdThread.start();

    }


    //Arrays of bout orders
    public String[] poolOfTwo = {"1-2"};
    public String[] poolOfThree = {"1-2", "2-3", "1-3"};
    public String[] poolOfFour =
            {"1-4", "2-3", "1-3", "2-4", "3-4", "1-2"};
    public String[] poolOfFive =
            {"1-2", "3-4", "5-1", "2-3", "5-4", "1-3", "2-5", "4-1", "3-5", "4-2"};
    public String[] poolOfSix =
            {"1-2", "4-5", "2-3", "5-6", "3-1", "6-4", "2-5", "1-4", "5-3", "1-6", "4-2", "3-6", "5-1", "3-4",
                    "6-2"};
    public String[] poolOfSeven =
            {"1-4", "2-5", "3-6", "7-1", "5-4", "2-3", "6-7", "5-1", "4-3", "6-2", "5-7", "3-1", "4-6", "7-2", "3-5",
                    "1-6", "2-4", "7-3", "6-5", "1-2", "4-7"};
    public String[] poolOfEight =
            {"2-3", "1-5", "7-4", "6-8", "1-2", "3-4", "5-6", "8-7", "4-1", "5-2", "8-3", "6-7", "4-2",
                    "8-1", "7-5", "3-6", "2-8", "5-4", "6-1", "3-7", "4-8", "2-6", "3-5", "1-7", "4-6",
                    "8-5", "7-2", "1-3"};
    public String[] poolOfNine =
            {"1-9", "2-8", "3-7", "4-6", "1-5", "2-9", "8-3", "7-4", "6-5", "1-2", "9-3", "8-4", "7-5",
                    "6-1", "3-2", "9-4", "5-8", "7-6", "3-1", "2-4", "5-9", "8-6", "7-1", "4-3", "5-2",
                    "6-9", "8-7", "4-1", "5-3", "6-2", "9-7", "1-8", "4-5", "3-6", "2-7", "9-8"};
    public String[] poolOfTen =
            {"1-4", "6-9", "2-5", "7-10", "3-1", "8-6", "4-5", "9-10", "2-3", "7-8", "5-1", "10-6", "4-2",
                    "9-7", "5-3", "10-8", "1-2", "6-7", "3-4", "8-9", "5-10", "1-6", "2-7", "3-8", "4-9",
                    "6-5", "10-2", "8-1", "7-4", "9-3", "2-6", "5-8", "4-10", "1-9", "3-7", "8-2", "6-4",
                    "9-5", "10-3", "7-1", "4-8", "2-9", "3-6", "5-7", "1-10"};
    public String[] poolOfEleven =
            {"1-2", "7-8", "4-5", "10-11", "2-3", "8-9", "5-6", "3-1", "9-7", "6-4", "2-5", "8-11", "1-4",
                    "7-10", "5-3", "11-9", "1-6", "4-2", "10-8", "3-6", "5-1", "11-7", "3-4", "9-10", "6-2",
                    "1-7", "3-9", "10-4", "8-2", "5-11", "1-8", "9-2", "3-10", "4-11", "6-7", "9-1", "2-10",
                    "11-3", "7-5", "6-8", "10-1", "11-2", "4-7", "8-5", "6-9", "11-1", "7-3", "4-8", "9-5",
                    "6-10", "2-7", "8-3", "4-9", "10-5", "6-11"};
    public String[] poolofTwelve =
            {"1-2", "7-8", "4-5", "10-11", "2-3", "8-9", "5-6", "11-12", "3-1", "9-7", "6-4", "12-10", "2-5",
                    "8-11", "1-4", "7-10", "5-3", "11-9", "1-6", "7-12", "4-2", "10-8", "3-6", "9-12", "5-1",
                    "11-7", "3-4", "9-10", "6-2", "12-8", "1-7", "3-9", "10-4", "8-2", "5-11", "12-6", "1-8",
                    "9-2", "3-10", "4-11", "12-5", "6-7", "9-1", "2-10", "11-3", "4-12", "7-5", "6-8", "10-1",
                    "11-2", "12-3", "4-7", "8-5", "6-9", "11-1", "2-12", "7-3", "4-8", "9-5", "6-10", "12-1",
                    "2-7", "8-3", "4-9", "10-5", "6-11"};

    private class CustomAdapter extends BaseAdapter {

        private String[] strings;
        private ArrayList<String> selectedStrings;

        public CustomAdapter(String[] strings) {
            this.strings = strings;
            selectedStrings = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public String getItem(int i) {
            return strings[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                holder.text = (TextView) view;
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.text.setText(getItem(i));
            if (selectedStrings.contains(getItem(i))) {
                holder.text.setPaintFlags(holder.text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.text.setPaintFlags(0);
            }
            return view;
        }

        private class ViewHolder {
            TextView text;
        }

        public ArrayList<String> getSelectedStrings() {
            return selectedStrings;
        }
    }

    // Fill out fencer object
    public Fencer populateFencer(Cursor cursor) {
        try {
            Fencer tempFencer = new Fencer();
            tempFencer.dbRowID = cursor.getLong(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_ROWID));
            tempFencer.firstName = cursor.getString(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_FIRSTNAME));
            tempFencer.lastName = cursor.getString(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_LASTNAME));
            tempFencer.hand =
                    Fencer.Hand.fromInteger(cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_HAND)));
            tempFencer.totalV = cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_TOTALVICT));
            tempFencer.totalBouts = cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_TOTALBOUTS));
            tempFencer.numRCards = cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_NUMRCARDS));
            tempFencer.numYCards = cursor.getInt(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_NUMYCARDS));

            String tempP = cursor.getString(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_PERVICT));
            String tempX = cursor.getString(cursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_AVGBOUTSCORE));

            if (tempP == null) {
                tempFencer.perVictory = 0.0;
            } else tempFencer.perVictory = Double.valueOf(tempP);

            if (tempP == null) {
                tempFencer.avgBoutScore = 0.0;
            } else tempFencer.avgBoutScore = Double.valueOf(tempX);


            return tempFencer;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Fencer();
    }
}
