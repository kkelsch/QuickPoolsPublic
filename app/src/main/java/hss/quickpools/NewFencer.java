package hss.quickpools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Kat on 4/24/2015.
 */
public class NewFencer extends Activity {

    //setup adapter for Fencer
    private FencersDbAdapter mDbHelper;

    //Save Fields
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mClub;
    private EditText mBirthYear;
    private Spinner mWeapon;
    private ToggleButton mHand;
    private ToggleButton mGender;
    private EditText mRating;
    private AutoCompleteTextView mCountry;


    //setup weapons String
    private String source;
    private Long mRowID;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //open database helper
        mDbHelper = new FencersDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Show weapons
        ArrayList<String> weapons_arr = new ArrayList<>();
        weapons_arr.add(getString(R.string.saber));
        weapons_arr.add(getString(R.string.foil));
        weapons_arr.add(getString(R.string.epee));

        //show newFencer Layout
        setContentView(R.layout.newfencer);

        //setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spWeapon);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                weapons_arr);

        //setup adapter to spinner
        spinner.setAdapter(adapter);

        //Assign to public names
        mFirstName = (EditText) findViewById(R.id.txtFirstName);
        mLastName = (EditText) findViewById(R.id.txtLastName);
        mClub = (EditText) findViewById(R.id.txtClub);
        mBirthYear = (EditText) findViewById(R.id.txtBirthYear);
        mWeapon = (Spinner) findViewById(R.id.spWeapon);
        mHand = (ToggleButton) findViewById(R.id.tbHand);
        mRating = (EditText) findViewById(R.id.txtRating);
        mGender = (ToggleButton) findViewById(R.id.tbGender);
        Button bSaveFencer = (Button) findViewById(R.id.btnAddFencer);

        //Auto Complete & country codes
        ArrayAdapter<String> acAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        mCountry = (AutoCompleteTextView) findViewById(R.id.txtCountry);
        mCountry.setAdapter(acAdapter);

        //See origin
        Bundle extras = getIntent().getExtras();

        //show/cover checkbox based on origin
        source = extras.getString("Source");

        switch (source) {
            case "NewPool":
                bSaveFencer.setText(getString(R.string.save_new_fencer));
                break;
            case "FencerList":
                CheckBox checkbox = (CheckBox) findViewById(R.id.cbAddtoPool);
                checkbox.setChecked(false);
                checkbox.setVisibility(View.INVISIBLE);
                bSaveFencer.setText(getString(R.string.save_new_fencer));
                break;
            case "EditFencer":
                mRowID = extras.getLong("mRowID");
                CheckBox checkbox2 = (CheckBox) findViewById(R.id.cbAddtoPool);
                checkbox2.setChecked(false);
                checkbox2.setVisibility(View.INVISIBLE);

                //change button text
                bSaveFencer.setText(getString(R.string.save_changes));

                //populate the fields
                populateFields();
                break;

        }

    }

    //show saved fencer data
    private void populateFields() {
        if (mRowID != null) {
            Cursor fencer = null;
            try {
                fencer = mDbHelper.fetchFencer(mRowID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            startManagingCursor(fencer);

            //display data on form
            mFirstName.setText(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_FIRSTNAME)));
            mLastName.setText(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_LASTNAME)));
            mClub.setText(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_CLUB)));
            mBirthYear.setText(fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_BIRTHYEAR)));

            // Weapon
            String s_Weapon = fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_WEAPON));
            switch (s_Weapon) {

                // saber
                case "0":
                    mWeapon.setSelection(0);
                    break;

                // foil
                case "1":
                    mWeapon.setSelection(1);
                    break;

                // epee
                case "2":
                    mWeapon.setSelection(2);
                    break;
            }

            String s_Gender = fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_GENDER));
            if (s_Gender.charAt(0) == '1') {
                // Female
                mGender.setChecked(true);
            }

            String s_Hand = fencer.getString(
                    fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_HAND));
            if (s_Hand.charAt(0) == '1') {
                // Left Handed
                mHand.setChecked(true);
            }


            // Show rating and country
            try {
                mRating.setText(fencer.getString(
                        fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_RATING)));
                mCountry.setText(fencer.getString(
                        fencer.getColumnIndexOrThrow(FencersDbAdapter.KEY_COUNTRY)));
            } catch (Exception e) {
            }

        }
    }

    //Save new fencer or update fencer and finish
    public void onUpdateFencer(View V) {

        String firstName = mFirstName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();

        //replace spaces & bad characters
        firstName = firstName.replaceAll("\\P{Alnum}", "");
        lastName = lastName.replaceAll("\\P{Alnum}", "");

        String club = mClub.getText().toString();
        String s_birthYear = mBirthYear.getText().toString();

        //empty fencer
        if (firstName.equals("") || lastName.equals("")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.empty_fencer));
            alertDialogBuilder.setMessage(getString(R.string.invalid_name)).setCancelable(false);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);

            //leave function
            return;
        }


        //if null, put blank
        if (s_birthYear.equals("")) s_birthYear = "0";
        if (club.equals("")) club = getString(R.string.none);

        //update fencer
        if (source.charAt(0) == 'E') {

            int birthYear = Integer.valueOf(s_birthYear);
            Fencer.Hand hand;

            if (mHand.isChecked()) {
                hand = Fencer.Hand.LEFT;
            } else {
                hand = Fencer.Hand.RIGHT;
            }

            // Weapon
            Fencer.Weapon weapon;
            String spText = mWeapon.getSelectedItem().toString();

            if (spText.equals(getString(R.string.saber))) {
                weapon = Fencer.Weapon.SABER;
            } else if (spText.equals(getString(R.string.foil))) {
                weapon = Fencer.Weapon.FOIL;
            } else {
                weapon = Fencer.Weapon.EPEE;
            }

            // Gender
            Fencer.Gender gender;
            if (mGender.isChecked()) {
                gender = Fencer.Gender.FEMALE;
            } else gender = Fencer.Gender.MALE;

            // Country Code
            String countryCode = mCountry.getText().toString();
            if (countryCode.equals("")) countryCode = getString(R.string.none);

            // New Rating
            String str_Rating = mRating.getText().toString();
            if (str_Rating.equals("")) str_Rating = getString(R.string.unrated);

            //update Fencer
            mDbHelper.updateFencer(mRowID, firstName, lastName, club, birthYear, weapon, gender,
                    hand, str_Rating, countryCode);
        }
        //create new fencer
        else {

            //check for duplicates
            if (hasDuplicates(firstName, lastName)) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getString(R.string.duplicate_fencer));
                alertDialogBuilder.setMessage(getString(R.string.fencer_exists)).setCancelable(false);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(true);

                //leave function
                return;
            }

            // Add new fencer to database
            createFencer();
        }


        //if from new pool, send back fencer Data
        if (source.charAt(0) == 'N') {

            //create new fencer
            Fencer newFencer = new Fencer();
            newFencer.dbRowID = newRowID;
            newFencer.firstName = firstName;
            newFencer.lastName = lastName;


            //send back info
            Intent intent = new Intent();
            intent.putExtra("newFencer", newFencer);
            intent.putExtra("Source", "NewFencer");
            //see if they want to add it to the pool


            CheckBox checkbox = (CheckBox) findViewById(R.id.cbAddtoPool);
            if (checkbox.isChecked() == true) {
                intent.putExtra("AddToPool", true);
            } else {
                intent.putExtra("AddToPool", false);
            }

            //finish
            setResult(RESULT_OK, intent);
            finish();
        } else {

            //set result to OK
            setResult(RESULT_OK);
            finish();
        }


    }

    // Check if database has fencer
    public boolean hasDuplicates(String firstName, String LastName) {

        try {
            Cursor mCursor = mDbHelper.fetchFencer(firstName, LastName);

            if (mCursor.getCount() > 0) {
                return true;
            } else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return false;
    }

    public long newRowID;

    // Brand new fencer
    public void createFencer() {
        //get form's data
        String firstName = mFirstName.getText().toString().trim().replace(" ", "");
        String lastName = mLastName.getText().toString().trim().replace(" ", "");

        firstName = firstName.replaceAll("\\P{Alnum}", "");
        lastName = lastName.replaceAll("\\P{Alnum}", "");

        String club = mClub.getText().toString();
        String s_birthYear = mBirthYear.getText().toString();

        //empty fencer
        if (firstName.equals("") || lastName.equals("")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.empty_fencer));
            alertDialogBuilder.setMessage(getString(R.string.invalid_name)).setCancelable(false);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);
            //leave function
            return;
        }

        //if null, put blank
        if (s_birthYear.equals("")) s_birthYear = "0";
        if (club.equals("")) club = getString(R.string.none);

        //get hand
        Fencer.Hand hand;
        if (mHand.isChecked()) {
            hand = Fencer.Hand.LEFT;
        } else {
            hand = Fencer.Hand.RIGHT;
        }

        //weapon and birthyear
        int birthYear = Integer.valueOf(s_birthYear);
        Fencer.Weapon weapon;
        String spText = mWeapon.getSelectedItem().toString();

        if (spText.equals(getString(R.string.saber))) {
            weapon = Fencer.Weapon.SABER;
        } else if (spText.equals(getString(R.string.foil))) {
            weapon = Fencer.Weapon.FOIL;
        } else {
            weapon = Fencer.Weapon.EPEE;
        }

        // Gender
        Fencer.Gender gender;
        if (mGender.isChecked()) {
            gender = Fencer.Gender.FEMALE;
        } else gender = Fencer.Gender.MALE;

        // Country Code
        String countryCode = mCountry.getText().toString();
        if (countryCode.equals("")) countryCode = getString(R.string.none);

        //New Rating
        String str_Rating = mRating.getText().toString();
        if (str_Rating.equals("")) str_Rating = getString(R.string.unrated);

        //create Fencer
        newRowID = mDbHelper.createFencer(firstName, lastName, club, birthYear, weapon,
                gender, hand, str_Rating, countryCode);

    }

    private static final String[] COUNTRIES = new String[]{
            "AFG", "ALB", "ALG", "ASA", "AND", "ANG", "ANT", "ARG", "ARM", "ARU", "AUS", "ANZ", "AUT", "AZE",
            "BAH", "BRN", "BAN", "BAR", "BLR", "BEL", "BIZ", "BER", "BEN", "BHU", "BOL", "BIH", "BOT", "BRA",
            "IVB", "BRU", "BUL", "BUR", "BDI", "CAM", "CMR", "CAN", "CPV", "CAY", "CAF", "CHA", "CHI", "CHN",
            "COL", "COM", "CGO", "COD", "COK", "CRC", "CIV", "CRO", "CUB", "CYP", "CZE", "DEN", "DJI", "DMA",
            "DOM", "TLS", "ECU", "EGY", "ESA", "GEQ", "ERI", "EST", "ETH", "FIJ", "FIN", "FRA", "GAB", "GAM",
            "GEO", "GER", "GHA", "GRE", "GRN", "GUM", "GUA", "GUI", "GBS", "GUY", "HAI", "HON", "HKG", "HUN",
            "ISL", "IND", "IOA", "INA", "IRI", "IRQ", "IRL", "ISR", "ITA", "JAM", "JPN", "JOR", "KAZ", "KEN",
            "PRK", "KOR", "KUW", "KGZ", "LAO", "LAT", "LIB", "LES", "LBR", "LBA", "LIE", "LTU", "LUX", "MKD",
            "MAD", "MAW", "MAL", "MAS", "MDV", "MLI", "MLT", "MTN", "MRI", "MEX", "FSM", "ZZX", "MDA", "MON",
            "MGL", "MAR", "MOZ", "MYA", "NAM", "NRU", "NEP", "NED", "AHO", "NZL", "NCA", "NIG", "NGR", "NOR",
            "OMA", "PAK", "PLW", "PLE", "PAN", "PNG", "PAR", "PER", "PHI", "POL", "POR", "PUR", "QAT", "ROU",
            "RUS", "RWA", "SKN", "LCA", "VIN", "SAM", "SMR", "STP", "KSA", "SEN", "SCG", "SEY", "SLE", "SIN",
            "SVK", "SLO", "SOL", "SOM", "RSA", "ESP", "SRI", "SUD", "SUR", "SWZ", "SWE", "SUI", "SYR", "TPE",
            "TJK", "TAN", "THA", "TOG", "TGA", "TRI", "TUN", "TUR", "TKM", "UGA", "UKR", "EUN", "EUA", "UAE",
            "RAU", "GBR", "USA", "URU", "UZB", "VAN", "VEN", "VIE", "ISV", "ANT", "YEM", "ZAM", "ZIM"
    };


}
