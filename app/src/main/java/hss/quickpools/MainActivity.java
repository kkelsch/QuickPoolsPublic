package hss.quickpools;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;


public class MainActivity extends TabActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources resources = getResources();
        TabHost tabHost = getTabHost();

        // New Pool tab
        Intent newPoolTab = new Intent().setClass(this, NewPool.class);
        TabHost.TabSpec tabSpecPool = tabHost
                .newTabSpec("NewPool")
                .setIndicator("", resources.getDrawable(R.drawable.blackchart))
                .setContent(newPoolTab);

        // Track Fencers tab
        Intent trackFencersTab = new Intent().setClass(this, FencerList.class);
        TabHost.TabSpec tabSpecTrackFencers = tabHost
                .newTabSpec("Fencers")
                .setIndicator("", resources.getDrawable(R.drawable.blackgroup))
                .setContent(trackFencersTab);

        // NotePad tab
        Intent notepadTab = new Intent().setClass(this, Notepad.class);
        TabHost.TabSpec tabSpecNotepad = tabHost
                .newTabSpec("Notepad")
                .setIndicator("", resources.getDrawable(R.drawable.blackpen))
                .setContent(notepadTab);


        // add all tabs

        tabHost.addTab(tabSpecTrackFencers);
        tabHost.addTab(tabSpecPool);
        tabHost.addTab(tabSpecNotepad);


        tabHost.setCurrentTab(1);
    }

}
