package hss.quickpools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Kat on 6/3/2015.
 */
public class PoolResults extends Activity {

    //list of fencers
    public ArrayList<Fencer> fencerData = new ArrayList<Fencer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poolresults);

        //Grab data from pool sheet
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            fencerData = getIntent().getParcelableArrayListExtra("fencers");
        }

        //Create string List
        ArrayList<String> viewingList = new ArrayList<String>();
        Collections.sort(fencerData, new Fencer.PlaceComparator());

        for (int i = 0; i < fencerData.size(); i++) {
            String str = "(" + fencerData.get(i).place + ")" + " " + fencerData.get(i).firstName +
                    " " + fencerData.get(i).lastName;
            viewingList.add(str);

        }

        //setup list view
        ListView lvResults = (ListView) findViewById(R.id.listPoolResults);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PoolResults.this,
                android.R.layout.simple_list_item_1, viewingList);

        //save adapter
        lvResults.setAdapter(adapter);
    }


    //go back to main activity
    public void returnToHome(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void startDEs(View V) {

        int numOfFencers = fencerData.size();

        //setup seeds & list positioning
        for (int i = 0; i < numOfFencers; i++) {
            fencerData.get(i).setSeed(i + 1);
        }


        //setup tableau
        if (numOfFencers > 8) {
            //table of 16

            //setup list position within bracket
            //counting from top of bracket
            for (int i = 0; i < numOfFencers; i++) {
                Fencer tempFencer = fencerData.get(i);
                switch (tempFencer.seed) {
                    case 1:
                        tempFencer.listPosition = 1;
                        break;
                    case 2:
                        tempFencer.listPosition = 15;
                        break;
                    case 3:
                        tempFencer.listPosition = 11;
                        break;
                    case 4:
                        tempFencer.listPosition = 7;
                        break;
                    case 5:
                        tempFencer.listPosition = 5;
                        break;
                    case 6:
                        tempFencer.listPosition = 9;
                        break;
                    case 7:
                        tempFencer.listPosition = 13;
                        break;
                    case 8:
                        tempFencer.listPosition = 3;
                        break;
                    case 9:
                        tempFencer.listPosition = 4;
                        break;
                    case 10:
                        tempFencer.listPosition = 14;
                        break;
                    case 11:
                        tempFencer.listPosition = 10;
                        break;
                    case 12:
                        tempFencer.listPosition = 6;
                        break;
                    case 13:
                        tempFencer.listPosition = 8;
                        break;
                    case 14:
                        tempFencer.listPosition = 12;
                        break;
                    case 15:
                        tempFencer.listPosition = 16;
                        break;
                    case 16:
                        tempFencer.listPosition = 2;
                        break;
                }
            }

            Intent intent = new Intent().setClass(this, TableofSixteen.class);
            intent.putExtra("fencers", fencerData);
            intent.putExtra("currentTableFencers", fencerData);
            intent.putExtra("fromNewTable", true);
            startActivity(intent);
        } else if (numOfFencers <= 8 && numOfFencers > 4) {
            //table of 8

            //setup list position within bracket
            //counting from top of bracket
            for (int i = 0; i < numOfFencers; i++) {
                Fencer tempFencer = fencerData.get(i);
                switch (tempFencer.seed) {
                    case 1:
                        tempFencer.listPosition = 1;
                        break;
                    case 2:
                        tempFencer.listPosition = 8;
                        break;
                    case 3:
                        tempFencer.listPosition = 6;
                        break;
                    case 4:
                        tempFencer.listPosition = 4;
                        break;
                    case 5:
                        tempFencer.listPosition = 3;
                        break;
                    case 6:
                        tempFencer.listPosition = 5;
                        break;
                    case 7:
                        tempFencer.listPosition = 7;
                        break;
                    case 8:
                        tempFencer.listPosition = 2;
                        break;
                }
            }

            Intent intent = new Intent().setClass(this, TableofEight.class);
            intent.putExtra("fencers", fencerData);
            intent.putExtra("currentTableFencers", fencerData);
            intent.putExtra("fromNewTable", true);
            startActivity(intent);
        } else if (numOfFencers > 2 && numOfFencers <= 4) {
            //table of 4

            //1v4
            //3v2
            for (int i = 0; i < numOfFencers; i++) {
                Fencer tempFencer = fencerData.get(i);
                switch (tempFencer.seed) {
                    case 1:
                        tempFencer.listPosition = 1;
                        break;
                    case 2:
                        tempFencer.listPosition = 4;
                        break;
                    case 3:
                        tempFencer.listPosition = 3;
                        break;
                    case 4:
                        tempFencer.listPosition = 2;
                        break;
                }
            }
            Intent intent = new Intent().setClass(this, TableofFour.class);
            intent.putExtra("fencers", fencerData);
            intent.putExtra("fromNewTable", true);
            intent.putExtra("currentTableFencers", fencerData);
            startActivity(intent);
        } else {
            //table of 2
            for (int i = 0; i < numOfFencers; i++) {
                Fencer tempFencer = fencerData.get(i);
                switch (tempFencer.seed) {
                    case 1:
                        tempFencer.listPosition = 1;
                        break;
                    case 2:
                        tempFencer.listPosition = 2;
                        break;
                }
            }
            Intent intent = new Intent().setClass(this, TableofTwo.class);
            intent.putExtra("fencers", fencerData);
            intent.putExtra("fromNewTable", true);
            intent.putExtra("currentTableFencers", fencerData);
            startActivity(intent);
        }


    }

}
