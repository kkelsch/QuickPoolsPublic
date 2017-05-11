package hss.quickpools;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Kat on 4/30/2015.
 */
public class ShowCard extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);

        //get extras
        Bundle extras = getIntent().getExtras();
        String color = extras.getString("Color");
        LinearLayout ly = (LinearLayout) findViewById(R.id.cardLayout);

        switch (color) {
            case "YELLOW":
                break;
            case "RED":
                ly.setBackgroundColor(Color.RED);
                break;

        }

        ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
