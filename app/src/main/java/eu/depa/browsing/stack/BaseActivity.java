package eu.depa.browsing.stack;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setThemeFromPrefs(getSupportActionBar());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setThemeFromPrefs (ActionBar actionBar) {
        int color = 0xc0c0c0;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        switch(sharedPref.getString("theme", "")) {
            case "def":
                setTheme(R.style.Cyan);
                color = getColorM(R.color.colorPrimary);
                break;
            case "bg":
                setTheme(R.style.BlueGray);
                color = getColorM(R.color.BGD);
                break;
            case "rock":
                setTheme(R.style.Rock);
                color = getColorM(R.color.RD);
                break;
            case "green":
                setTheme(R.style.Green);
                color = getColorM(R.color.GD);
                break;
            case "blue":
                setTheme(R.style.Blue);
                color = getColorM(R.color.BD);
                break;
            case "gray":
                setTheme(R.style.Gray);
                color = getColorM(R.color.GD);
        }

        actionBar.setBackgroundDrawable(new ColorDrawable(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(color);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public int getColorM (int id) {
        //noinspection deprecation
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
            getColor(id) : getResources().getColor(id);
    }
}
