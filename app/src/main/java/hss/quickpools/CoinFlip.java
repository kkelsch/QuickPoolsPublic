package hss.quickpools;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Kat on 5/21/2015.
 */
public class CoinFlip extends ActionBarActivity {
    private boolean hasFlipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_flip);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }

    public void flipCoin(View v) {
        TextView coin = (TextView) findViewById(R.id.tvCoinFlip);

        if (hasFlipped == false) {
            hasFlipped = true;
        } else {//hasflipped = true
            hasFlipped = false;
            coin.setText(getString(R.string.flip));
            coin.setTextColor(Color.BLACK);
            return;
        }

        Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v1.vibrate(250);

        //wait for visual effect
        boolean coinValue = Math.random() < 0.5;

        if (coinValue) {
            coin.setText(getString(R.string.left_upper));
            coin.setTextColor(Color.RED);
        } else {
            coin.setText(getString(R.string.right_upper));
            coin.setTextColor(Color.GREEN);
        }
    }

    public void finishCoinFlip(View v) {
        finish();
    }


}
