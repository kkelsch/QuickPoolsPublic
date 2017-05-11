package hss.quickpools;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Kat on 4/25/2015.
 */
public class ViewFencerStats extends ActionBarActivity {
    private static final int EDIT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int ERASE_ID = Menu.FIRST + 2;

    //Create Adapter
    private FencersDbAdapter mDbHelper;
    private BoutDbAdapter mBoutAdapter;
    private fencerStatAdapter adapter;
    private String fencerName;
    private String firstName;
    private String lastName;
    private String makeSelector = "MAKESELECTOR";
    private String makeBorder = "MAKEBORDER";

    //Fencer info
    Long mRowId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fencer_view);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Setup Fencer Adapter
        mDbHelper = new FencersDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Setup Bout Adapter
        mBoutAdapter = new BoutDbAdapter(this);
        try {
            mBoutAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Grab the fencer based off the Row ID
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(FencersDbAdapter.KEY_ROWID)
                    : null;
        }


        //Populate the list view
        populateFields();
        final ListView lv = (ListView) findViewById(R.id.lvFencerData);

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = (adapter.listings.get(position).topInfo);

                if (selectedFromList.equals(getString(R.string.view_all_Pool_bouts))) {
                    Intent intent = new Intent().setClass(ViewFencerStats.this, PastBouts.class);
                    intent.putExtra("FencerFullName", fencerName);
                    intent.putExtra("Case", "PastPools");

                    //Start Activity
                    startActivity(intent);

                } else if (selectedFromList.equals(getString(R.string.view_all_de_bouts))) {
                    Intent intent = new Intent().setClass(ViewFencerStats.this, PastBouts.class);
                    intent.putExtra("FencerFullName", fencerName);
                    intent.putExtra("Case", "PastDEs");
                    startActivity(intent);
                } else if (selectedFromList.equals(getString(R.string.view_history_certain_opponent))) {
                    Intent intent = new Intent().setClass(ViewFencerStats.this, SelectCertainFencer.class);
                    intent.putExtra("FirstName", firstName);
                    intent.putExtra("LastName", lastName);
                    startActivity(intent);
                }

            }
        });

        lv.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                String selectedFromList = (lv.getItemAtPosition(index)).toString();
                if (index < 5) {
                    editFencer();
                }

                //return true/handled
                return true;
            }
        });

    }

    public void populateFields() {
        if (mRowId != null) {
            Cursor fencer = null;
            try {
                fencer = mDbHelper.fetchFencer(mRowId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            startManagingCursor(fencer);

            //create list for custom adapter
            ArrayList<fencerStat> statsList = new ArrayList<>();

            //add to list
            firstName = fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_FIRSTNAME));
            lastName = fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_LASTNAME));

            fencerName = firstName + " " + lastName;
            statsList.add(new fencerStat(firstName, lastName));

            statsList.add(new fencerStat(getString(R.string.club), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_CLUB))));

            String s_Weapon = fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_WEAPON));
            switch (s_Weapon) {
                case "0":
                    statsList.add(new fencerStat(getString(R.string.saber), getString(R.string.weapon)));
                    break;
                case "1":
                    statsList.add(new fencerStat(getString(R.string.foil), getString(R.string.weapon)));
                    break;
                case "2":
                    statsList.add(new fencerStat(getString(R.string.epee), getString(R.string.weapon)));
                    break;
            }


            String s_Gender = fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_GENDER));
            if (s_Gender.charAt(0) == '0') {
                statsList.add(new fencerStat(getString(R.string.boy), getString(R.string.gender)));
            } else
                statsList.add(new fencerStat(getString(R.string.girl), getString(R.string.gender)));

            statsList.add(new fencerStat(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_BIRTHYEAR)), getString(R.string.birth_year)));

            try {
                statsList.add(new fencerStat(fencer.getString(
                        fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_RATING)), getString(R.string.rating)));
                statsList.add(new fencerStat(fencer.getString(
                        fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_COUNTRY)), getString(R.string.country)));

            } catch (Exception e) {

            }

            String s_Hand = fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_HAND));
            if (s_Hand.charAt(0) == '0') {
                statsList.add(new fencerStat(getString(R.string.right), getString(R.string.hand)));
            } else
                statsList.add(new fencerStat(getString(R.string.left), getString(R.string.hand)));

            statsList.add(new fencerStat(getString(R.string.view_history_certain_opponent), makeSelector));

            // Pools
            statsList.add(new fencerStat(getString(R.string.pools), makeBorder));
            statsList.add(new fencerStat(getString(R.string.total_yellow_cards), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_NUMYCARDS))));
            statsList.add(new fencerStat(getString(R.string.total_red_cards), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_NUMRCARDS))));
            statsList.add(new fencerStat(getString(R.string.total_num_bouts), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_TOTALBOUTS))));

            statsList.add(new fencerStat(getString(R.string.total_num_victories), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_TOTALVICT))));

            double tempABS = Double.valueOf(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_AVGBOUTSCORE)));
            statsList.add(new fencerStat(getString(R.string.avg_bout_score),
                    new DecimalFormat("##.##").format(tempABS)));

            double tempD = Double.valueOf(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_PERVICT)));
            tempD = tempD * 100;
            statsList.add(new fencerStat(getString(R.string.percentage_Victory),
                    new DecimalFormat("##.##").format(tempD) +
                    "%"));
            statsList.add(new fencerStat(getString(R.string.view_all_Pool_bouts), makeSelector));

            // DEs
            statsList.add(new fencerStat(getString(R.string.DEs), makeBorder));
            statsList.add(new fencerStat(getString(R.string.total_yellow_cards), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_NUMYCARDS_DE))));
            statsList.add(new fencerStat(getString(R.string.total_red_cards), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_NUMRCARDS_DE))));
            statsList.add(new fencerStat(getString(R.string.total_num_bouts), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_TOTALBOUTS_DE))));
            statsList.add(new fencerStat(getString(R.string.total_num_victories), fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_TOTALVICT_DE))));

            tempABS = Double.valueOf(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_AVGBOUTSCORE_DE)));
            statsList.add(new fencerStat(getString(R.string.avg_bout_score), new DecimalFormat("##.##").format(tempABS)));

            tempD = Double.valueOf(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_PERVICT_DE)));
            tempD = tempD * 100;
            statsList.add(new fencerStat(getString(R.string.percentage_Victory), new DecimalFormat("##.##").format(tempD) +
                    "%"));

            statsList.add(new fencerStat(getString(R.string.view_all_de_bouts), makeSelector));

            //Get list view
            final ListView lv = (ListView) findViewById(R.id.lvFencerData);
            adapter = new fencerStatAdapter(this, statsList);
            lv.setAdapter(adapter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, EDIT_ID, 0, R.string.fencer_edit);
        menu.add(0, DELETE_ID, 0, R.string.fencer_delete);
        menu.add(0, ERASE_ID, 0, R.string.erase_fencer_history);
        return true;
    }

    public void editFencer() {
        Intent intent = new Intent().setClass(this, NewFencer.class);
        intent.putExtra("Source", "EditFencer");
        intent.putExtra("mRowID", mRowId);

        //Start Activity
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Edit Fencer's Info
            case EDIT_ID:
                editFencer();
                break;
            // Delete Fencer
            case DELETE_ID:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.fencer_delete))
                        .setMessage(getString(R.string.confirm_action))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mDbHelper.deleteFencer(mRowId);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;

            // Erase Fencer's History
            case ERASE_ID:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.erase_fencer_history))
                        .setMessage(getString(R.string.confirm_action))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete past bouts
                                EraseFencerHistory();

                                // Display
                                Toast.makeText(getApplicationContext(), getString(R.string.deleted),
                                        Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    public void EraseFencerHistory() {
        //erase fencer
        mBoutAdapter.deleteFencerHistory(fencerName);
    }

    //on return from editing fencer
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                //clear the list
                adapter.clear();

                //repopulate the fields
                populateFields();

            }
        }
    }

    private class fencerStatAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<fencerStat> listings;

        public fencerStatAdapter(Context context, ArrayList<fencerStat> persons) {
            this.context = context;
            this.listings = persons;
        }

        @Override
        public int getCount() {
            return listings.size();
        }

        public void clear() {
            this.listings.clear();
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

            if (listings.get(position).subInfo.equals(makeSelector)) {
                text1.setTextAppearance(ViewFencerStats.this, android.R.style.TextAppearance_DeviceDefault_Medium);
                text1.setTypeface(null, Typeface.ITALIC);
                text2.setText(getString(R.string.view_past));
                text1.setTextColor(Color.rgb(0, 128, 255));
            } else if (listings.get(position).subInfo.equals(makeBorder)) {
                text1.setTextAppearance(ViewFencerStats.this, android.R.style.TextAppearance_DeviceDefault_Large);
                text1.setTypeface(null, Typeface.BOLD_ITALIC);
                text2.setText("");
            } else {
                text1.setTextAppearance(ViewFencerStats.this, android.R.style.TextAppearance_DeviceDefault_Medium);
                text2.setText("" + listings.get(position).subInfo);
            }

            return twoLineListItem;
        }

    }

    // for visuals
    public class fencerStat {
        String topInfo;
        String subInfo;

        fencerStat(String top, String sub) {
            this.subInfo = sub;
            this.topInfo = top;
        }

    }
}
