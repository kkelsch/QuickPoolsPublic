package hss.quickpools;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by Kat on 4/25/2015.
 */
public class FencerList extends ListActivity {

    //Declare Constants
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int DELETE_ALL_ID = Menu.FIRST + 2;
    private static final int DELETE_ALL_HISTORY_ID = Menu.FIRST + 3;
    private FencersDbAdapter mDbHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fencer_list);
        mDbHelper = new FencersDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
        Cursor fencerCursor = mDbHelper.fetchAllFencers();
        startManagingCursor(fencerCursor);

        // Create an array to specify the fields we want to display in the list (
        String[] from = new String[]{FencersDbAdapter.KEY_FIRSTNAME, FencersDbAdapter.KEY_LASTNAME};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{R.id.text1, R.id.text2};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, fencerCursor, from, to);
        setListAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.fencer_insert);
        menu.add(0, DELETE_ALL_ID, 0, R.string.fencer_delete_all);
        menu.add(0, DELETE_ALL_HISTORY_ID, 0, R.string.fencer_delete_all_history);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                Intent i = new Intent(this, NewFencer.class);
                i.putExtra("Source", "FencerList");
                startActivityForResult(i, ACTIVITY_CREATE);
                break;
            case DELETE_ALL_ID:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.fencer_delete_all_long))
                        .setMessage(getString(R.string.confirm_action))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete
                                eraseAllFencers();

                                // Display
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.fencer_db_cleared),
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

            case DELETE_ALL_HISTORY_ID:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.fencer_delete_all_history_long))
                        .setMessage(getString(R.string.confirm_action))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete History
                                eraseAllHistory();

                                // Display
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.history_db_cleared),
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

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.fencer_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteFencer(info.id);
                fillData();
                return true;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ViewFencerStats.class);

        //start intent
        i.putExtra(FencersDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    // Erase All Fencers
    public void eraseAllFencers() {

        //Erase Fencers
        mDbHelper.resetTable();

        //Erase Bout History

        //Setup Adapter
        BoutDbAdapter mBoutAdapter = new BoutDbAdapter(this);
        try {
            mBoutAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mBoutAdapter.resetTable();

        //refill data
        fillData();


    }

    public void eraseAllHistory() {
        //Setup Adapter
        BoutDbAdapter mBoutAdapter = new BoutDbAdapter(this);
        try {
            mBoutAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mBoutAdapter.resetTable();

    }

}
