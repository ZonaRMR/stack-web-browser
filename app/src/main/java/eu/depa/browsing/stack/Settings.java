package eu.depa.browsing.stack;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.ViewConfiguration;
import android.widget.Toast;

@SuppressWarnings("deprecation")

public class Settings extends PreferenceActivity {

    SharedPreferences.OnSharedPreferenceChangeListener ChListener;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        setThemeFromPrefs();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ChListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("theme")) {
                    askIfRestart();
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(ChListener);

        super.onCreate(SavedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference showDots = this.findPreference("showDots");
        if (!ViewConfiguration.get(this).hasPermanentMenuKey()) showDots.setEnabled(false);

        Preference showGPL = findPreference("showGPL");
        showGPL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent gotogpl = new Intent(getApplicationContext(), GPL.class);
                startActivity(gotogpl);
                return false;
            }
        });
        Preference version = findPreference("vers");
        version.setSummary(BuildConfig.VERSION_NAME);
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            int clicks = 0;
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clicks++;
                if (clicks == 5) {
                    Toast.makeText(Settings.this,
                            getResources().getString(R.string.stopcklicking),
                            Toast.LENGTH_SHORT).show();
                    clicks = 0;
                }
                return false;
            }
        });
    }

    public void askIfRestart () {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.wanna_restart));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doRestart(getApplicationContext(), 100);
            }
        });
        builder.setNegativeButton(getString(R.string.plzNo), null);
        builder.create();
        builder.show();
    }

    public static void doRestart(Context context, int delay) {
        if (delay == 0)
            delay = 1;
        Intent restartIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName() );
        PendingIntent intent = PendingIntent.getActivity(
                context, 0,
                restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        System.exit(2);
    }

    public void setThemeFromPrefs () {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        switch(sharedPref.getString("theme", "def")) {
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