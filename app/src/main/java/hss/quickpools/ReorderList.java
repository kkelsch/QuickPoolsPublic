package hss.quickpools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Kat on 5/1/2015.
 */
public class ReorderList extends Activity {

    //get data
    private ArrayList<Fencer> fencerList = new ArrayList<Fencer>();
    private ArrayList<String> ViewingList = new ArrayList<>();
    ListView origLV;
    private int ListPos;


    //Setup Adapters
    public ArrayAdapter<String> origAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reorderlist);

        //get old list of fencers
        Bundle extras = getIntent().getExtras();
        fencerList = extras.getParcelableArrayList("OriginalList");

        //add to list
        for (int i = 0; i < fencerList.size(); i++) {
            Fencer temp = fencerList.get(i);
            ViewingList.add(temp.firstName + " " + temp.lastName);
        }

        //setup Adapters
        origAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, ViewingList);
        origLV = (ListView) findViewById(R.id.originalPoolList);


        origLV.setAdapter(origAdapter);

        //setup selected item
        origLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListPos = position;
            }
        });
    }


    public void onMoveListDown(View v) {

        if (ListPos >= 0) {

            //get new position
            //don't move if at end
            if (ListPos + 1 < ViewingList.size()) {
                //move down one
                String item = ViewingList.get(ListPos);
                ViewingList.remove(item);
                ViewingList.add(ListPos + 1, item);
                origAdapter.notifyDataSetChanged();

                //reorder fencing List
                Fencer tempFencer = fencerList.get(ListPos);
                tempFencer.listPosition = ListPos + 1;
                fencerList.remove(ListPos);
                fencerList.add(ListPos + 1, tempFencer);

                //set selected item
                ListPos = ListPos + 1;
                origLV.setItemChecked(ListPos, true);

            }
        }
    }

    public void onMoveListUp(View v) {

        if (ListPos >= 0) {

            //get new position
            //don't move if at end
            if (ListPos - 1 >= 0) {
                //move down one
                String item = ViewingList.get(ListPos);
                ViewingList.remove(item);
                ViewingList.add(ListPos - 1, item);
                origAdapter.notifyDataSetChanged();

                Fencer tempFencer = fencerList.get(ListPos);
                tempFencer.listPosition = ListPos - 1;
                fencerList.remove(ListPos);
                fencerList.add(ListPos - 1, tempFencer);

                //set selected item
                ListPos = ListPos - 1;
                origLV.setItemChecked(ListPos, true);

            }

        }

    }

    public void doneReordering(View v) {

        //reorder fencer's list's position

        for (int i = 0; i < fencerList.size(); i++) {
            fencerList.get(i).setListPos(i);

        }

        Intent intent = new Intent();
        intent.putExtra("fencerList", fencerList);
        intent.putExtra("Source", "ReOrderList");
        setResult(RESULT_OK, intent);
        finish();


    }


}
