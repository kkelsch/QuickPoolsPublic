package hss.quickpools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kat on 4/26/2015.
 */
public class FencerCheckboxList extends Activity {
    private ListView mainListView;
    private ArrayAdapter<FencerCheckBox> listAdapter;
    private FencersDbAdapter mDbHelper;
    private ArrayList<FencerCheckBox> fencerList;
    private ArrayList<String> dbRowIDs = new ArrayList<String>();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkboxlist);

        // Find the ListView resource.
        mainListView = (ListView) findViewById(R.id.listviewCB);

        //open database connection
        mDbHelper = new FencersDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //Setup connection to database
        Cursor fencerCursor = mDbHelper.fetchAllFencers();

        // and an array of the fields we want to bind those fields to
        fencerList = fromCursorToArrayListStringNames(fencerCursor);

        if (fencerList.size() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.zero_fencers_saved),
                    Toast.LENGTH_SHORT).show();
        }

        //save row IDs
        dbRowIDs = fromCursorToArrayListStringRowIDs(fencerCursor);

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new FencerCBArrayAdapter(this, fencerList);
        mainListView.setAdapter(listAdapter);

        // When item is tapped, toggle checked properties of CheckBox and Planet.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                FencerCheckBox fencerCheckBox = listAdapter.getItem(position);
                fencerCheckBox.toggleChecked();
                FencerViewHolder viewHolder = (FencerViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(fencerCheckBox.isChecked());


            }
        });

    }


    public void clearSelection(View V) {

        // loop through and uncheck
        for (int i = 0; i < listAdapter.getCount(); i++) {

            FencerCheckBox fencerCheckBox = listAdapter.getItem(i);
            if (fencerCheckBox.isChecked()) {
                fencerCheckBox.toggleChecked();
            }
        }

        uncheckAllChildrenCascade(mainListView);

    }

    private void uncheckAllChildrenCascade(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(false);
            } else if (v instanceof ViewGroup) {
                uncheckAllChildrenCascade((ViewGroup) v);
            }
        }
    }

    // Finish
    public void onDone(View v) {

        //declare arrays
        ArrayList<Fencer> chosenFencers = new ArrayList<Fencer>();

        ArrayList<String> selectedNames = new ArrayList<String>();
        ArrayList<String> selectedRowIDs = new ArrayList<String>();

        for (int j = 0; j < fencerList.size(); j++) {
            FencerCheckBox temp = fencerList.get(j);

            //if selected, add to list
            if (temp.isChecked() == true) {

                //add to list
                Fencer nFencer = new Fencer();
                nFencer.dbRowID = Long.valueOf(dbRowIDs.get(j));
                String[] fullName = temp.getName().split(" ");
                nFencer.firstName = fullName[0];
                nFencer.lastName = fullName[1];
                chosenFencers.add(nFencer);

            }

        }

        //if none are selected, alert user
        if (chosenFencers.size() > 12) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.too_many_fencers));
            alertDialogBuilder.setMessage(getString(R.string.max_fencers_allowed)).setCancelable(false);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);
            //leave function
            return;
        }

        //send back to first activity with info
        Intent intent = new Intent();
        intent.putExtra("Source", "SavedFencers");
        intent.putExtra("selectedNames", selectedNames);
        intent.putExtra("selectedRowIDs", selectedRowIDs);
        intent.putExtra("chosenFencers", chosenFencers);

        //finish activity
        setResult(RESULT_OK, intent);
        finish();

    }

    /**
     * Holds fencer name data.
     */
    private static class FencerCheckBox {
        private String name = "";
        private boolean checked = false;
        public FencerCheckBox(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return checked;
        }
        public void setChecked(boolean checked) {
            this.checked = checked;
        }
        public String toString() {
            return name;
        }
        public void toggleChecked() {
            checked = !checked;
        }
    }

    /**
     * Holds child views for one row.
     */
    private static class FencerViewHolder {
        private CheckBox checkBox;
        private TextView textView;


        public FencerViewHolder(TextView textView, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.textView = textView;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public TextView getTextView() {
            return textView;
        }

    }

    /**
     * Custom adapter for displaying an array of check box objects.
     */
    private static class FencerCBArrayAdapter extends ArrayAdapter<FencerCheckBox> {

        private LayoutInflater inflater;

        public FencerCBArrayAdapter(Context context, List<FencerCheckBox> fencerCheckBoxList) {
            super(context, R.layout.simplerow, R.id.rowTextView, fencerCheckBoxList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Planet to display
            FencerCheckBox fencerCheckBox = (FencerCheckBox) this.getItem(position);

            // The child views in each row.
            CheckBox checkBox;
            TextView textView;

            // Create a new row view
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById(R.id.rowTextView);
                checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag(new FencerViewHolder(textView, checkBox));

                // If CheckBox is toggled, update the item it is tagged with.
                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        FencerCheckBox fencerCheckBox = (FencerCheckBox) cb.getTag();
                        fencerCheckBox.setChecked(cb.isChecked());
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                FencerViewHolder viewHolder = (FencerViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox();
                textView = viewHolder.getTextView();
            }

            // Tag the CheckBox with the Planet it is displaying, so that we can
            // access the planet in onClick() when the CheckBox is toggled.
            checkBox.setTag(fencerCheckBox);

            // Display planet data
            checkBox.setChecked(fencerCheckBox.isChecked());
            textView.setText(fencerCheckBox.getName());

            return convertView;
        }

    }

    //Convert database cursor data to array list of string
    private ArrayList<FencerCheckBox> fromCursorToArrayListStringNames(Cursor c) {
        ArrayList<FencerCheckBox> result = new ArrayList<FencerCheckBox>();

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            String firstName = c.getString(c.getColumnIndex(FencersDbAdapter.KEY_FIRSTNAME));
            String lastName = c.getString(c.getColumnIndex(FencersDbAdapter.KEY_LASTNAME));

            String fullName = firstName + " " + lastName;
            FencerCheckBox temp = new FencerCheckBox(fullName);

            result.add(temp);
            c.moveToNext();
        }
        return result;
    }

    private ArrayList<String> fromCursorToArrayListStringRowIDs(Cursor c) {
        ArrayList<String> result = new ArrayList<String>();

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            result.add(c.getString(c.getColumnIndex(FencersDbAdapter.KEY_ROWID)));
            c.moveToNext();
        }
        return result;
    }


}
