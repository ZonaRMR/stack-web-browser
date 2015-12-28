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
import android.preference.*;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

public class Settings extends PreferenceActivity {

    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        setThemeFromPrefs();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("theme")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

                    builder.setTitle(getString(R.string.wanna_restart));

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            doRestart(getApplicationContext());
                        }
                    });
                    builder.setNegativeButton(getString(R.string.plzNo), null);
                    try {
                        builder.show();
                    } catch (WindowManager.BadTokenException e) {
                        e.printStackTrace();
                    }
                }
                setResult(36165);
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);

        super.onCreate(SavedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public void setThemeFromPrefs () {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        switch(sharedPref.getString("theme", "")) {
            case "def":
                setTheme(R.style.Teal);
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
                return;
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
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent);

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
