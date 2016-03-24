package eu.depa.browsing.stack;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        setTitle(R.string.menu_history);

        final ListView LV = (ListView) findViewById(R.id.historyList);

        List<Map<String, String>> data = new ArrayList<>();
        ArrayList<String> titles = getIntent().getBundleExtra("bundle").getStringArrayList("titles");
        ArrayList<String> addrs  = getIntent().getBundleExtra("bundle").getStringArrayList("addrs");

        if (titles == null || addrs == null) return;

        if (titles.isEmpty() || addrs.isEmpty()) {
            TextView empty = (TextView) findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);
            return;
        }

        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).equals("") || addrs.get(i).startsWith("data:")) continue;
            Map<String, String> datum = new HashMap<>(2);
            datum.put("title", titles.get(i));
            datum.put("addr",  addrs.get(i));
            data.add(datum);
        }

        final SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"title", "addr"},
                new int[] {android.R.id.text1, android.R.id.text2 });

        LV.setAdapter(adapter);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TwoLineListItem item = (TwoLineListItem) view;
                String url = item.getText2().getText().toString();
                PackageManager pm = getApplicationContext().getPackageManager();
                Intent newTabIntent = pm.getLaunchIntentForPackage("eu.depa.browsing.stack");
                newTabIntent.setAction(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    newTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                newTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newTabIntent.setData(Uri.parse(url));
                getApplicationContext().startActivity(newTabIntent);
            }
        });
    }
}
