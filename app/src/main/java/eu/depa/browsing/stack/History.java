package eu.depa.browsing.stack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setThemeFromPrefs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        getSupportActionBar().hide();
        final ListView LV = (ListView) findViewById(R.id.historyList);

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        ArrayList<String> titles = getIntent().getBundleExtra("bundle").getStringArrayList("titles");
        ArrayList<String> addrs  = getIntent().getBundleExtra("bundle").getStringArrayList("addrs");

        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).equals("") || addrs.get(i).startsWith("data:")) continue;
            Map<String, String> datum = new HashMap<>(2);
            datum.put("title", titles.get(i));
            datum.put("addr",  addrs.get(i));
            data.add(datum);
        }

        final SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "addr"},
                new int[] {android.R.id.text1, android.R.id.text2 });

        LV.setAdapter(adapter);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TwoLineListItem item = (TwoLineListItem) view;
                String url = item.getText2().getText().toString();
                Toast.makeText(History.this, url, Toast.LENGTH_SHORT).show();
                //TODO THIS
            }
        });
    }

    public void setThemeFromPrefs () {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
