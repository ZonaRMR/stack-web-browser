package eu.depa.browsing.stack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.Toast;

@SuppressWarnings("deprecation")

public class Settings extends PreferenceActivity{

    SharedPreferences.OnSharedPreferenceChangeListener ChListener;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        setThemeFromPrefs();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ChListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("theme")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

                    builder.setTitle(getString(R.string.wanna_restart));

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            doRestart(getApplicationContext());
                        }
                    });
                    builder.setNegativeButton(getString(R.string.plzNo), null);
                    builder.show();
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
                if (clicks >= 10) {
                    Toast.makeText(Settings.this,
                            getResources().getString(R.string.stopcklicking),
                            Toast.LENGTH_SHORT).show();
                    clicks = 0;
                }
                return false;
            }
        });
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

    public void doRestart(Context c) {
        try {
            if (c != null) {
                PackageManager pm = c.getPackageManager();
                if (pm != null) {
                    Intent mStartActivity = pm.getLaunchIntentForPackage(c.getPackageName());
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent.getActivity
                                (c, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1, mPendingIntent);

                        ProgressDialog pd = new ProgressDialog(Settings.this);
                        pd.setCancelable(false);
                        pd.setMessage(getString(R.string.restarting));
                        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        pd.show();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("settings.doRestart", "Was not able to restart application");
        }
    }
}