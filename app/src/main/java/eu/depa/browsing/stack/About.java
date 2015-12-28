package eu.depa.browsing.stack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class About extends Activity {
    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        setThemeFromPrefs();
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.about);

        ImageButton email_button = (ImageButton) findViewById(R.id.email_button);
        email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TO = "depasquale.a@tuta.io";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", TO, null));

                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, "API: " + android.os.Build.VERSION.SDK_INT + "\n" + getString(R.string.email_text));

                try {
                    startActivity(emailIntent);
                }
                catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(About.this, getString(R.string.email_noclient), Toast.LENGTH_SHORT).show();}
            }
        });
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
        }
    }
}
