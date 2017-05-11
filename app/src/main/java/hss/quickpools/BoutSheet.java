package hss.quickpools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kat on 4/26/2015.
 */
public class BoutSheet extends ActionBarActivity {

    public int MAX_BOUT_SCORE = 5;

    //Store Bout Data
    public String[] boutData;
    public String score1 = "0";
    public String score2 = "0";
    public String source;
    public boolean isFlipped = false;
    TextView timerTextView;


    //menu items
    private Menu ex_menu;
    private static final int FLIP_ID = Menu.FIRST;
    private static final int SHOW_CARDS_ID = Menu.FIRST + 1;
    private static final int NOTES_ID = Menu.FIRST + 2;
    private static final int COIN_FLIP_ID = Menu.FIRST + 3;
    public boolean showCards = true;
    private String str_currentCardMenu = "";
    static public String SHOWCARD_ID = "Show_Bout_Card";


    //Timer Setup
    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    private long nCounter = 180000;
    long minutes = 3;
    long seconds = 0;
    Boolean running;

    // Timer Tasks
    public void doTimerTask() {

        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        if (nCounter <= 0) {
                            stopTask();

                            //alert timer is done
                            try {
                                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate
                                v1.vibrate(3000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        } else if (running) {
                            nCounter--;
                            seconds = (nCounter / 1000) % 60;
                            minutes = ((nCounter / (1000 * 60)) % 60);
                            timerTextView.setText(String.format("%2d:%02d", minutes, seconds));

                        }


                    }
                });
            }
        };

        // public void schedule (TimerTask task, long delay, long period)
        t.scheduleAtFixedRate(mTimerTask, 0, 1);

    }

    public void stopTask() {

        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        running = false;

        //reset if finished
        if (nCounter <= 0) {
            //reset to 3 minutes
            minutes = 3;
            seconds = 0;
            nCounter = 180000;
            timerTextView.setText(String.format("%2d:%02d", minutes, seconds));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bout_sheet);
        running = false;

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //set defaults:
        SharedPreferences prefs = this.getSharedPreferences(
                "hss.quickpools.app", Context.MODE_PRIVATE);
        showCards = prefs.getBoolean(SHOWCARD_ID, true);

        timerTextView = (TextView) findViewById(R.id.tvTimer);
        timerTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (running == false) {
                    try {
                        Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v1.vibrate(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    doTimerTask();
                    running = true;

                } else {
                    stopTask();
                    running = false;

                    try {
                        Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v1.vibrate(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        timerTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (running) return false;
                if (!source.contains("DE")) return false;

                //Reset timer
                new AlertDialog.Builder(BoutSheet.this)
                        .setTitle(getString(R.string.resetTimer))
                        .setMessage(getString(R.string.setTimerValue))
                        .setPositiveButton("1:00", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //reset to 1:00
                                minutes = 1;
                                seconds = 0;
                                nCounter = 60000;
                                timerTextView.setText(String.format("%2d:%02d", minutes, seconds));
                            }

                        })
                        .setNegativeButton("3:00", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //reset to 3 minutes
                                minutes = 3;
                                seconds = 0;
                                nCounter = 180000;
                                timerTextView.setText(String.format("%2d:%02d", minutes, seconds));
                            }
                        })
                        .show();

                return false;
            }
        });

        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.bout_sheet);
        rLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (running) {
                    timerTextView.performClick();
                }

            }

        });

        //setup fencer names
        Bundle extras = getIntent().getExtras();
        source = extras.getString("Source");

        TextView name1 = (TextView) findViewById(R.id.tvName1);
        TextView name2 = (TextView) findViewById(R.id.tvName2);

        switch (source) {
            case "NewPool":
                break;
            case "NewPoolDE":
                MAX_BOUT_SCORE = 15;
                break;
            case "DE":
                if (extras != null) {
                    boutData = extras.getStringArray("boutData");
                    name1.setText(boutData[1]);
                    name2.setText(boutData[3]);
                }

                MAX_BOUT_SCORE = 15;
                break;
            default:
                if (extras != null) {
                    boutData = extras.getStringArray("boutData");

                    //set names
                    try {
                        String[] fullName1 = boutData[1].split(" ");
                        String[] fullName2 = boutData[3].split(" ");

                        name1.setText(fullName1[1] + "," + fullName1[0].charAt(0) + ".");
                        name2.setText(fullName2[1] + "," + fullName2[0].charAt(0) + ".");

                    } catch (Exception name) {
                        name1.setText(boutData[1]);
                        name2.setText(boutData[2]);
                    }
                }
                break;

        } //end of switch

    }

    //Menu Items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //Add Menu Items
        menu.add(0, FLIP_ID, 0, getString(R.string.flipFencers));

        //Changing Menu
        if (showCards) str_currentCardMenu = getString(R.string.hideCards);
        else str_currentCardMenu = getString(R.string.showCards);

        menu.add(0, SHOW_CARDS_ID, 0, str_currentCardMenu);
        menu.add(0, COIN_FLIP_ID, 0, getString(R.string.flipCoin));
        menu.add(0, NOTES_ID, 0, getString(R.string.notes));
        ex_menu = menu;

        return true;
    }
    private void flipShowCardsMenu() {
        MenuItem menuItem = ex_menu.getItem(SHOW_CARDS_ID - 1);

        showCards = !showCards;

        // flip text
        if (showCards) {
            menuItem.setTitle(getString(R.string.hideCards));

        } else {
            menuItem.setTitle(getString(R.string.showCards));
        }

        //save to preferences
        SharedPreferences prefs = this.getSharedPreferences(
                "hss.quickpools.app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SHOWCARD_ID, showCards);
        editor.commit();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case FLIP_ID:

                //get textViews
                TextView tvScore1 = (TextView) findViewById(R.id.tvScore1);
                TextView tvScore2 = (TextView) findViewById(R.id.tvScore2);
                TextView tvName1 = (TextView) findViewById(R.id.tvName1);
                TextView tvName2 = (TextView) findViewById(R.id.tvName2);

                //flip fencers
                if (isFlipped) {

                    isFlipped = false;
                } else {
                    isFlipped = true;

                }

                //flip numbers
                String temp1 = tvScore1.getText().toString();
                String temp2 = tvScore2.getText().toString();

                tvScore1.setText(temp2);
                tvScore2.setText(temp1);

                score1 = temp2;
                score2 = temp1;

                //flip names
                String name1 = tvName1.getText().toString();
                String name2 = tvName2.getText().toString();

                tvName1.setText(name2);
                tvName2.setText(name1);


                //Flip Card colors
                Button button1 = (Button) findViewById(R.id.card1);
                Button button2 = (Button) findViewById(R.id.card2);

                int color1 = ((ColorDrawable) button1.getBackground()).getColor();
                int color2 = ((ColorDrawable) button2.getBackground()).getColor();

                button1.setBackgroundColor(color2);
                button2.setBackgroundColor(color1);

                Bout.Card cTemp1 = fencerACard;
                Bout.Card cTemp2 = fencerBCard;
                fencerACard = cTemp2;
                fencerBCard = cTemp1;
                break;

            case NOTES_ID:
                //open Notepad
                Intent i = new Intent(this, Notepad.class);
                startActivity(i);
                break;

            case SHOW_CARDS_ID:
                flipShowCardsMenu();
                break;
            case COIN_FLIP_ID:
                Intent intent = new Intent(this, CoinFlip.class);
                startActivity(intent);
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //declared this way to make editable
    public String ACard;
    public String BCard;

    public void showYellowCard() {
        if (showCards == false) return;
        Intent intent = new Intent(this, ShowCard.class);
        intent.putExtra("Color", "YELLOW");
        startActivity(intent);
    }
    public void showRedCard() {
        if (showCards == false) return;
        Intent intent2 = new Intent(this, ShowCard.class);
        intent2.putExtra("Color", "RED");
        startActivity(intent2);
    }

    //Finish up
    public void onFinish(View v) {
        if (running) {
            timerTextView.performClick();
            return;
        }

        //if quick bout, no need for data result
        if (source.equals("NewPool") || source.equals("NewPoolDE")) {
            Intent intent = new Intent();
            intent.putExtra("Source", "");
            setResult(RESULT_OK, intent);
            finish();
            return;
        }

        //Prepare Data
        ACard = fencerACard.toString();
        BCard = fencerBCard.toString();
        final String AName = boutData[1];
        final String BName = boutData[3];
        final String IDA = boutData[0];
        final String IDB = boutData[2];
        TextView tvTimer = (TextView) findViewById(R.id.tvTimer);
        final String timeRemaining = tvTimer.getText().toString();


        //for display
        String LeftGuy = BName;
        String RightGuy = AName;

        if (isFlipped) {
            //flip numbers
            TextView tvScore1 = (TextView) findViewById(R.id.tvScore1);
            TextView tvScore2 = (TextView) findViewById(R.id.tvScore2);

            String temp1 = tvScore1.getText().toString();
            String temp2 = tvScore2.getText().toString();

            score1 = temp2;
            score2 = temp1;

            //flip names
            RightGuy = BName;
            LeftGuy = AName;

            //flip colors
            BCard = fencerACard.toString();
            ACard = fencerBCard.toString();

        }


        if (score1 == score2) {

            //alert user
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.duplicateScore))
                    .setMessage(getString(R.string.whoWon))
                    .setPositiveButton(LeftGuy, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //fencer 1 won
                            if (isFlipped) score1 = "V" + score1;
                            else score2 = "V" + score2;

                            //setup return string[]
                            String[] boutResult = {IDA, AName, score1, ACard, IDB, BName, score2,
                                    BCard, timeRemaining};

                            //close out
                            Intent intent = new Intent();
                            intent.putExtra("boutResult", boutResult);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setNegativeButton(RightGuy, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (isFlipped) score2 = "V" + score2;
                            else score1 = "V" + score1;

                            //setup return string[]
                            String[] boutResult = {IDA, AName, score1, ACard, IDB, BName, score2,
                                    BCard, timeRemaining};

                            //close out
                            Intent intent = new Intent();
                            intent.putExtra("boutResult", boutResult);
                            setResult(RESULT_OK, intent);
                            finish();

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();
        } else {

            //Setup V indicator
            int i_score1 = Integer.valueOf(score1);
            int i_score2 = Integer.valueOf(score2);

            //only remove 5 if in pools
            if (!source.contains("DE")) {
                if (i_score1 > i_score2) score1 = "V" + score1.replace("5", "");
                else score2 = "V" + score2.replace("5", "");
            } else {
                if (i_score1 > i_score2) score1 = "V" + score1;
                else score2 = "V" + score2;
            }

            //setup return string[]
            String[] boutResult = {IDA, AName, score1, ACard, IDB, BName, score2,
                    BCard, timeRemaining};

            //end
            Intent intent = new Intent();
            intent.putExtra("boutResult", boutResult);
            setResult(RESULT_OK, intent);
            finish();
        }


    }

    //Save fencer Card Results
    public Bout.Card fencerACard = Bout.Card.NONE;
    public Bout.Card fencerBCard = Bout.Card.NONE;

    //If cards are clicked
    public void onCardOneClick(View v) {

        if (running) {
            timerTextView.performClick();
            return;
        }

        //get button
        Button card1 = (Button) findViewById(R.id.card1);

        //grab color
        ColorDrawable drawable = (ColorDrawable) card1.getBackground();
        int color = drawable.getColor();

        switch (color) {
            //if Yellow, loop to red
            case Color.YELLOW:
                card1.setBackgroundColor(Color.RED);
                fencerACard = Bout.Card.RED;
                showRedCard();
                break;

            //if Red, go to normal
            case Color.RED:
                card1.setBackgroundColor(0xffa9a9a9);
                fencerACard = Bout.Card.NONE;
                break;

            default:
                card1.setBackgroundColor(Color.YELLOW);
                fencerACard = Bout.Card.YELLOW;
                showYellowCard();
                break;
        }


    }
    public void onCardTwoClick(View v) {
        if (running) {
            timerTextView.performClick();
            return;
        }

        //get button
        Button card = (Button) findViewById(R.id.card2);

        //grab color
        ColorDrawable drawable = (ColorDrawable) card.getBackground();
        int color = drawable.getColor();

        switch (color) {
            //if Yellow, loop to red
            case Color.YELLOW:
                card.setBackgroundColor(Color.RED);
                fencerBCard = Bout.Card.RED;
                showRedCard();
                break;
            //if Red, go to normal
            case Color.RED:
                card.setBackgroundColor(0xffa9a9a9);
                fencerBCard = Bout.Card.NONE;
                break;
            //if normal, go to yellow

            default:
                card.setBackgroundColor(Color.YELLOW);
                fencerBCard = Bout.Card.YELLOW;
                showYellowCard();
                break;
        }

    }

    //Timer Functions
    public void onTimerReset(View v) {
        if (running) {
            timerTextView.performClick();
            return;
        }

        if (source.contains("DE")) {
            //Reset timer
            new AlertDialog.Builder(BoutSheet.this)
                    .setTitle(getString(R.string.resetTimer))
                    .setMessage(getString(R.string.setTimerValue))
                    .setPositiveButton("1:00", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //reset to 1:00
                            minutes = 1;
                            seconds = 0;
                            nCounter = 60000;
                            timerTextView.setText(String.format("%2d:%02d", minutes, seconds));
                        }

                    })
                    .setNegativeButton("3:00", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //reset to 3 minutes
                            minutes = 3;
                            seconds = 0;
                            nCounter = 180000;
                            timerTextView.setText(String.format("%2d:%02d", minutes, seconds));
                        }
                    })
                    .show();

        } else {
            nCounter = 180000;
            timerTextView.setText("3:00");
        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //click it
            timerTextView.performClick();

            // Get instance of Vibrator from current Context
            try {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(300);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (source.equals("NewPool") || source.equals("NewPoolDE")) {
                if (running) {
                    running = false;
                    stopTask();
                }
                finish();
                return true;
            }
            //warn of discarding
            new AlertDialog.Builder(this)
                    .setTitle("Discard")
                    .setMessage("Discard Bout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (running) {
                                running = false;
                                stopTask();
                            }
                            //save to database then exit
                            String[] boutResult = {boutData[0], boutData[2]};

                            //close out
                            Intent intent = new Intent();
                            intent.putExtra("boutResult", boutResult);
                            setResult(RESULT_CANCELED, intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else return super.dispatchKeyEvent(event);

        return false;
    }

    //Button Functions
    public void onBtn1PlusClick(View v) {

        if (running) {
            timerTextView.performClick();
            return;
        }

        //Increment person 1's score
        TextView tvScore1 = (TextView) findViewById(R.id.tvScore1);
        int foo = Integer.parseInt(tvScore1.getText().toString());

        //only go up to 5
        if (foo == MAX_BOUT_SCORE) {
            //keep it 5
        } else {
            //add 1
            tvScore1.setText(Integer.toString(foo + 1));
        }

        //save Score
        score1 = tvScore1.getText().toString();


    }

    public void onBtn2PlusClick(View v) {
        if (running) {
            timerTextView.performClick();
            return;
        }

        //Increment person 1's score
        TextView tvScore = (TextView) findViewById(R.id.tvScore2);
        int foo = Integer.parseInt(tvScore.getText().toString());

        //only go up to 5
        if (foo == MAX_BOUT_SCORE) {
            //keep it 5
        } else {
            //add 1
            tvScore.setText(Integer.toString(foo + 1));
        }
        score2 = tvScore.getText().toString();

    }

    public void onBtn1MinusClick(View v) {

        if (running) {
            timerTextView.performClick();
            return;
        }

        //Increment person 1's score
        TextView tvScore = (TextView) findViewById(R.id.tvScore1);
        int foo = Integer.parseInt(tvScore.getText().toString());

        //only go up to 5
        if (foo == 0) {
            //keep it 5
        } else {
            //add 1
            tvScore.setText(Integer.toString(foo - 1));
        }
        score1 = tvScore.getText().toString();

    }

    public void onBtn2MinusClick(View v) {
        if (running) {
            timerTextView.performClick();
            return;
        }

        //Increment person 1's score
        TextView tvScore = (TextView) findViewById(R.id.tvScore2);
        int foo = Integer.parseInt(tvScore.getText().toString());

        //only go up to 5
        if (foo == 0) {
            //keep it 5
        } else {
            //add 1
            tvScore.setText(Integer.toString(foo - 1));
        }
        score2 = tvScore.getText().toString();

    }


}
