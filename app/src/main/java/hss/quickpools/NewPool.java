package hss.quickpools;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Kat on 4/23/2015.
 */
public class NewPool extends ActionBarActivity {

    private static final int REORDER_LIST = Menu.FIRST;
    private static final int BOUT_ID = Menu.FIRST + 1;
    private static final int DE_BOUT_ID = Menu.FIRST + 2;
    private static final int CHART_ID = Menu.FIRST + 3;
    private static final int ACTIVITY_CREATE = 0;


    //adapter to use on ListView
    public ArrayAdapter<String> adapter;
    public ArrayList<Fencer> poolList = new ArrayList<Fencer>();

    //list to show on Adapter
    public ArrayList<String> listPoolNames = new ArrayList<String>();

    //on Create/start
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //set UI/Layout onto activity
        setContentView(R.layout.newpool);

        //Grab list view
        final ListView lvNames = (ListView) findViewById(R.id.listViewNames);

        //set array adapter to list
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listPoolNames);
        listPoolNames.add(getString(R.string.empty_pool));

        //save adapter
        lvNames.setAdapter(adapter);

        lvNames.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {

                //remove from adapter
                listPoolNames.remove(index);
                poolList.remove(index);

                //notify adapter to UI thread
                adapter.notifyDataSetChanged();

                //return true/handled
                return true;
            }
        });

    }

    public void newPoolBout() {
        Intent i = new Intent(this, BoutSheet.class);
        i.putExtra("Source", "NewPool");
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    public void newDEBout() {
        Intent di = new Intent(this, BoutSheet.class);
        di.putExtra("Source", "NewPoolDE");
        startActivityForResult(di, ACTIVITY_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, REORDER_LIST, 0, R.string.reorder_list);
        menu.add(0, BOUT_ID, 0, R.string.quick_bout);
        menu.add(0, DE_BOUT_ID, 0, R.string.de_quick_bout);
        menu.add(0, CHART_ID, 0, R.string.event_chart);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case BOUT_ID:
                newPoolBout();
                break;
            case REORDER_LIST:
                if (listPoolNames.size() <= 1) {
                    Toast.makeText(getApplicationContext(), getString(R.string.too_few_fencers),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent rInt = new Intent(this, ReorderList.class);
                rInt.putExtra("OriginalList", poolList);
                startActivityForResult(rInt, ACTIVITY_CREATE);
                break;
            case DE_BOUT_ID:
                newDEBout();
                break;
            case CHART_ID:
                Intent it = new Intent(this, EventClassChart.class);
                startActivity(it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Start a new Pool
    public void onNewPool(View v) {

        //Check length of pool
        int numFencers = listPoolNames.size();

        // Too many fencers
        if (numFencers > 12) {
            Toast.makeText(getApplicationContext(), getString(R.string.max_fencers_allowed),
                    Toast.LENGTH_SHORT).show();
            //leave function
            return;
        }
        // Only 1 fencer or fencer string
        else if (numFencers == 1) {
            // If has empty pool string
            if (listPoolNames.contains(getString(R.string.empty_pool))) {
                Toast.makeText(getApplicationContext(), getString(R.string.zero_fencers_in_pool),
                        Toast.LENGTH_SHORT).show();

                //leave function
                return;

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.too_few_fencers),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //start new fencer form
        Intent intent = new Intent().setClass(this, PoolSheet.class);

        //update position
        //used later for reordering
        for (int i = 0; i < poolList.size(); i++) {
            Fencer temp = poolList.get(i);
            temp.setListPos(i + 1);
        }

        intent.putExtra("fencers", poolList);

        //Start Activity
        startActivityForResult(intent, 1);
    }

    //Show new fencer Form
    public void showNewFencerForm(View v) {

        //start new fencer form
        Intent intent = new Intent().setClass(this, NewFencer.class);
        intent.putExtra("Source", "NewPool");

        //Start Activity
        startActivityForResult(intent, 1);
    }

    //get result from Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            String source = data.getStringExtra("Source");

            //clear Empty data if there
            if (listPoolNames.contains(getString(R.string.empty_pool))) {
                listPoolNames.clear();
            }

            switch (source) {
                case "NewFencer":

                    Boolean addToPool = data.getBooleanExtra("AddToPool", true);
                    if (addToPool == false) {
                        //don't add to the pool
                        return;
                    }

                    Fencer newFencer = data.getParcelableExtra("newFencer");
                    newFencer.setListPos(poolList.size());
                    //add fencer to list
                    adapter.add(newFencer.firstName + " " + newFencer.lastName);
                    poolList.add(newFencer);

                    //update GUI
                    adapter.notifyDataSetChanged();
                    break;


                case "SavedFencers":

                    //get data
                    ArrayList<Fencer> chosenFencers = data.getParcelableArrayListExtra("chosenFencers");

                    //add to adapter
                    for (int i = 0; i < chosenFencers.size(); i++) {

                        Fencer tempFencer = chosenFencers.get(i);

                        if (!listPoolNames.contains(tempFencer.firstName + " " + tempFencer.lastName)) {

                            //show on adapter
                            listPoolNames.add(tempFencer.firstName + " " + tempFencer.lastName);

                            //add to pool list
                            int pSize = poolList.size();
                            tempFencer.listPosition = pSize;
                            poolList.add(tempFencer);

                        }
                    }

                    adapter.notifyDataSetChanged();
                    break;

                case "ReOrderList":
                    //get data
                    poolList = data.getParcelableArrayListExtra("fencerList");

                    //save to listview
                    listPoolNames.clear();

                    for (int i = 0; i < poolList.size(); i++) {
                        Fencer temp = poolList.get(i);
                        listPoolNames.add(temp.firstName + " " + temp.lastName);
                    }
                    adapter.notifyDataSetChanged();
                    break;

                default:
                    //do nothing
                    break;
            }
        }

    }

    //Select from saved Fencers
    public void showSavedFencers(View v) {
        Intent intent = new Intent().setClass(this, FencerCheckboxList.class);
        //Start Activity
        startActivityForResult(intent, 1);
    }


    //Clear List for new pool
    public void clearPoolList(View v) {

        //clear list
        listPoolNames.clear();
        poolList.clear();
        adapter.notifyDataSetChanged();

    }
}
