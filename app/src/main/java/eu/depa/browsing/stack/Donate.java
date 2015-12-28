package eu.depa.browsing.stack;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.Random;

public class Donate extends Activity {

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        setThemeFromPrefs();
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.donate);

        ImageButton bitcoin = (ImageButton) findViewById(R.id.bitcoin);
        ImageButton paypal  = (ImageButton) findViewById(R.id.paypal);

        bitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] addresses = {"1GYHc7Gsx1VkHtB26qoC643ubyxPT5g8SM",
                        "1KZRBZmZgpLRwUWtGekpzQ1Ehfa281chdE",
                        "12L1d8iJ41rJA9cw2uPgwYmL7Kot3xnSmk",
                        "1CiBnEBvJtxgDmAHsGveD3PXXsnPJv4KQy",
                        "13qnSxDytd8LQGzomp1bHrXZ2zUE7RaVYD",
                        "1C1SeJwRfqhHTjUzMcwqQEfg2AgxizCKew"};
                Random ran = new Random();
                String chosen = addresses[ran.nextInt(addresses.length)];
                String bitcoinURI = "bitcoin:" + chosen + "?amount=0.005";
                Intent bitcoinIntent = new Intent(Intent.ACTION_VIEW);
                bitcoinIntent.setData(Uri.parse(bitcoinURI));
                try {
                    startActivity(bitcoinIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.bitcoin_noclient), Toast.LENGTH_SHORT).show();
                }
            }
        });

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(16019);
                finish();
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
            case "gray":
                setTheme(R.style.Gray);
                return;
        }
    }
}
