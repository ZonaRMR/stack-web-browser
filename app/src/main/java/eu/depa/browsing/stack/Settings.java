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
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.ViewConfiguration;
import android.widget.Toast;

@SuppressWarnings("deprecation")

public class Settings extends BaseActivity {

    @Override
    public void onCreate(Bundle SavedInstanceState) {

        super.onCreate(SavedInstanceState);

        setTitle(R.string.menu_settings);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            prefs.registerOnSharedPreferenceChangeListener(this);

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference showDots = this.findPreference("showDots");
            if (!ViewConfiguration.get(getActivity()).hasPermanentMenuKey()) showDots.setEnabled(false);

            Preference showGPL = findPreference("showGPL");
            showGPL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent gotogpl = new Intent(getActivity(), GPL.class);
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
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.stopcklicking),
                                Toast.LENGTH_SHORT).show();
                        clicks = 0;
                    }
                    return false;
                }
            });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("theme"))
                askIfRestart();
        }

        public void askIfRestart () {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.wanna_restart));

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doRestart(getActivity(), 100);
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
    }
}