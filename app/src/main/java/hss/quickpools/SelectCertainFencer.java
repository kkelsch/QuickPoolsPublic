package hss.quickpools;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Kat on 6/6/2015.
 */
public class SelectCertainFencer extends Activity {
    private FencersDbAdapter mDbHelper;
    private String firstName;
    private String lastName;
    private ArrayList<String> fencers = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    public String selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcertainfencer);
        mDbHelper = new FencersDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        firstName = getIntent().getStringExtra("FirstName");
        lastName = getIntent().getStringExtra("LastName");


        Cursor fencerCursor = mDbHelper.fetchAllFencers();
        startManagingCursor(fencerCursor);

        fencerCursor.moveToFirst();
        while (fencerCursor.isAfterLast() == false) {

            if (lastName.equals(
                    fencerCursor.getString(fencerCursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_LASTNAME))) &&
                    firstName.equals(fencerCursor.getString(
                            fencerCursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_FIRSTNAME)))) {
                fencerCursor.moveToNext();

            } else {
                fencers.add(fencerCursor.getString(fencerCursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_FIRSTNAME)) +
                        " " + fencerCursor.getString(fencerCursor.getColumnIndexOrThrow(FencersDbAdapter.KEY_LASTNAME)));
                fencerCursor.moveToNext();
            }
        }

        //show on listview
        ListView lv = (ListView) findViewById(R.id.lvSelectCertainFencer);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, fencers);
        lv.setAdapter(adapter);

        selection = adapter.getItem(0);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selection = adapter.getItem(position);

            }
        });

        // change display
        TextView label = (TextView) findViewById(R.id.lblSelectFencer);
        label.setText(getString(R.string.all_bouts_with) + " " + firstName + " vs ");

    }


    // Open up Past History
    public void onCertainFencerNext(View v) {

        //DEs or Pools?
        boolean isDE = false;

        RadioButton rb = (RadioButton) findViewById(R.id.btnViewDEonly);
        if (rb.isChecked()) {
            isDE = true;
        }


        //send info to intent
        Intent intent = new Intent().setClass(this, PastBouts.class);
        intent.putExtra("Case", "FencerHistory");
        intent.putExtra("DEsOnly", isDE);
        intent.putExtra("FencerFullName", firstName + " " + lastName);
        intent.putExtra("FencerA", firstName + " " + lastName);
        intent.putExtra("FencerB", selection);
        startActivity(intent);

    }

    public void onDoneSelection(View v) {
        finish();
    }
}
