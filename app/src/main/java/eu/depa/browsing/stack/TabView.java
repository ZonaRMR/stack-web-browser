package eu.depa.browsing.stack;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import java.util.Vector;

public class TabView extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setThemeFromPrefs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_view);

        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Vector<Tab> tabsVector = new Vector<>();
            String[] allTabs = sharedPref.getString("tabs", "").split("@@STACK-TAB-OVER@@");
            for (String tab : allTabs) {
                String[] tabVals = tab.split(";;NEW-TAB-VAL;;");
                Tab T = new Tab(tabVals[0], Integer.parseInt(tabVals[1]), Boolean.parseBoolean(tabVals[2]));
                tabsVector.add(T);
            }
            Tab[] tabsArray = (Tab[]) tabsVector.toArray();
        }catch (Exception x) {
            x.printStackTrace();
        }

        ListView tabsList = (ListView) findViewById(R.id.tabslist);
    }

    public void setThemeFromPrefs() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        switch(sharedPref.getString("theme", "")) {
            case "def":
                setTheme(R.style.Cyan);
                return;
            case "bg":
                setTheme(R.style.BlueGray);
                return;
            case "rock":
                setTheme(R.style.Rock);
                return;
            case "green":
                setTheme(R.style.Green);
                return;
            case "blue":
                setTheme(R.style.Blue);
                return;
            case "gray":
                setTheme(R.style.Gray);
        }
    }
}
