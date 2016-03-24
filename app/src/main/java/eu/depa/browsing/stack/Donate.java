package eu.depa.browsing.stack;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Random;

public class Donate extends BaseActivity {

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.donate);
        setTitle(R.string.menu_donate);

        ImageButton bitcoin = (ImageButton) findViewById(R.id.fab_btc),
                paypal  = (ImageButton) findViewById(R.id.fab_paypal);

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
}
