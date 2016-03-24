package eu.depa.browsing.stack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bookmarks extends BaseActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarks);
        setTitle(R.string.menu_bookmarks);

        final ListView LV = (ListView) findViewById(R.id.BMList);

        init();

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        LV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Bookmarks.this);

                builder.setTitle(getString(R.string.delete));

                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TwoLineListItem item = (TwoLineListItem) view;
                        String url = item.getText2().getText().toString();
                        String title = item.getText1().getText().toString();
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sharedPref.edit().putString("BMtitles", sharedPref.getString("BMtitles", "").replace(";;" + title, "")).apply();
                        sharedPref.edit().putString("BMurls", sharedPref.getString("BMurls", "").replace(";;" + url, "")).apply();
                        Toast.makeText(Bookmarks.this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                        init();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setCancelable(false);
                builder.show();
                return true;
            }
        });
    }

    public void init() {

        final ListView LV = (ListView) findViewById(R.id.BMList);

        List<Map<String, String>> data = new ArrayList<>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String[] titlesArr = sharedPref.getString("BMtitles", "").split(";;");
        String[] addrsArr = sharedPref.getString("BMurls", "").split(";;");

        ArrayList<String> titles = new ArrayList<>(Arrays.asList(titlesArr));
        ArrayList<String> addrs = new ArrayList<>(Arrays.asList(addrsArr));

        if (titles.isEmpty() || addrs.isEmpty()) {
            TextView empty = (TextView) findViewById(R.id.emptyBM);
            empty.setVisibility(View.VISIBLE);
            return;
        }

        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).equals("")) continue;
            try {
                Map<String, String> datum = new HashMap<>(2);
                datum.put("title", titles.get(i));
                datum.put("addr", addrs.get(i));
                data.add(datum);
            }catch (Exception e){e.printStackTrace();}
        }

        final SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"title", "addr"},
                new int[] {android.R.id.text1, android.R.id.text2 });

        if (adapter.isEmpty() || data.isEmpty()) {
            TextView empty = (TextView) findViewById(R.id.emptyBM);
            empty.setVisibility(View.VISIBLE);
            LV.setVisibility(View.GONE);
            return;
        }


        LV.setAdapter(adapter);
    }
}
