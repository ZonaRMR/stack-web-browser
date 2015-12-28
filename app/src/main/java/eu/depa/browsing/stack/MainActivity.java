package eu.depa.browsing.stack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnKeyListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setThemeFromPrefs();                        //set background color before anything is shown
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reloadGraphicalTheme();                     //set background color of all markup items according to user prefs

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);    //set defs only once
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        try {getSupportActionBar().hide();}         //parser is too dumb to understand I'm catching the exce it's calling me out for
        catch (NullPointerException e) {e.printStackTrace();}       //see this parser u dumb cuck

        final WebView  webview    = (WebView)     findViewById(R.id.webView);
        final EditText toptextbar = (EditText)    findViewById(R.id.toptextbar);
        final ImageView favicon   = (ImageView)   findViewById(R.id.favicon);
        final ImageButton X       = (ImageButton) findViewById(R.id.X);
        ImageButton dots = (ImageButton) findViewById(R.id.dots);

        reloadSettings();       //load up prefs, see comment below

        //BROWSERY STUFF
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setJavaScriptEnabled(true);   // Enable javascript
        webview.setWebChromeClient(new WebChromeClient());  // Set WebView client
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("market://")) {
                    Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openIntent);
                }
                else {
                    HashMap<String, String> extraHeaders = new HashMap<String, String>();
                    if (sharedPref.getBoolean("DNT", true)) extraHeaders.put("DNT", "1");
                    else extraHeaders.put("DNT", "0");
                    view.loadUrl(url, extraHeaders);
                    toptextbar.setText(url.split("//")[1]);
                    favicon.setImageBitmap(webview.getFavicon());
                }
                return true;
            }
        });
        //NO BROWSERY STUFF, MORE AUTOCONFIG
        if (ViewConfiguration.get(this).hasPermanentMenuKey())
            dots.setVisibility(View.GONE);
        toptextbar.setOnKeyListener(this);      //set that for l8r
        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());    //why is this deprecated you cuck give me an alternative

        onNewIntent(getIntent());       //do this just in case you get called which will never happen

        X.setVisibility(View.GONE);     //hide the X at the beginning

        toptextbar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (toptextbar.hasFocus()) X.setVisibility(View.VISIBLE);   //when it's focused show the X TODO make X nice + pretty
                else X.setVisibility(View.GONE);
            }
        });
        //ALL DONE
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) loadAll(data);
        else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String HP = sharedPref.getString("HP", "");
            loadAll("http://" + HP);
        }
    }       //when u get an intento

    public void loadAll(String url) {
        WebView webView     = (WebView) findViewById(R.id.webView);
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        ImageView favicon   = (ImageView) findViewById(R.id.favicon);

        webView.loadUrl(url);
        toptextbar.setText(url.split("//")[1]);
        favicon.setImageBitmap(webView.getFavicon());

        webView.loadUrl(url);
        toptextbar.setText(url.split("//")[1]);
        favicon.setImageBitmap(webView.getFavicon());
    }                 //load errything u need to all nicely packed in 1 method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }   //wat do when options menu is TRIGGERED

    boolean first = true;   //sub menu inflating goofs up so u need this

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_about:
                Intent gotoabout = new Intent(this, About.class);
                startActivity(gotoabout);
                return true;

            case R.id.menu_settings:
                Intent gotosettings = new Intent(this, Settings.class);
                startActivityForResult(gotosettings, 36165);
                return true;

            case R.id.menu_donate:
                Intent gotodonate = new Intent(this, Donate.class);
                startActivityForResult(gotodonate, 16019);
                return true;
            case R.id.menu_page:
                if (first)
                    getMenuInflater().inflate(R.menu.menu_page, item.getSubMenu());
                first = false;
                return true;
        }
        return true;
    }       //when u pick from te menu

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sergine = sharedPref.getString("searchEngine", "");
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            String text = String.valueOf(toptextbar.getText());
            if (text.contains("easter"))
                Toast.makeText(MainActivity.this, getString(R.string.easter_toast), Toast.LENGTH_SHORT).show();
            if (!(text.contains(" ")) && text.contains(".")) {
                if (text.split("//")[0].equals("http:") || text.split("//")[0].equals("https:"))
                    loadAll(text);
                else
                    loadAll("http://" + text);
            } else {
                String search = text.replace("+", "%2B").replace(" ", "+");
                switch (sergine) {
                    case "ddg":
                        loadAll("https://duckduckgo.com/?q=" + search);
                        return true;
                    case "g":
                        loadAll("https://google.com/#q=" + search);
                        return true;
                    case "b":
                        loadAll("https://www.bing.com/search?q=" + search);
                }
            }
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(toptextbar.getWindowToken(), 0);
        }
        return false;
    }   //when u press a key: enter

    public void showPopup(final View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_about:
                        Intent gotoabout = new Intent(getApplicationContext(), About.class);
                        startActivity(gotoabout);
                        return true;

                    case R.id.menu_settings:
                        Intent gotosettings = new Intent(getApplicationContext(), Settings.class);
                        startActivityForResult(gotosettings, 36165);
                        return true;

                    case R.id.menu_donate:
                        Intent gotodonate = new Intent(getApplicationContext(), Donate.class);
                        startActivityForResult(gotodonate, 16019);
                        return true;
                    case R.id.menu_page:
                        getMenuInflater().inflate(R.menu.menu_page, item.getSubMenu());
                        return true;
                    case R.id.menu_reload:
                        WebView webView = (WebView) findViewById(R.id.webView);
                        webView.reload();
                }
                return false;
            }
        });
    }         //when dots r clicked

    public void bang(View v) {
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        toptextbar.setText("!");
        toptextbar.setSelection(1);
        toptextbar.requestFocus();
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }                    //if u like it put a bang on it

    @Override
    public void onBackPressed() {
        WebView webview = (WebView) findViewById(R.id.webView);
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        ImageView favicon = (ImageView) findViewById(R.id.favicon);
        if (webview.canGoBack()) {
            webview.goBack();
            toptextbar.setText(webview.getUrl());
            favicon.setImageBitmap(webview.getFavicon());
        }
        else {
            finish();
        }
    }                 //override: go back instead

    public void openInApp (MenuItem item) {
        WebView webView = (WebView) findViewById(R.id.webView);
        Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webView.getUrl()));
        startActivity(openIntent);
    }       //make and execute intent to open webviewed page in other apps

    public void share (MenuItem item) {
        WebView webView = (WebView) findViewById(R.id.webView);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());

        startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.sharewith)), 1);

        getFragmentManager().popBackStack();
    }           //share the current webviewed page

    public void addPagetoHome (MenuItem item) {
        WebView webView = (WebView) findViewById(R.id.webView);
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);

        Intent gonnaLaunch = new Intent();
        gonnaLaunch.putExtra(Intent.ACTION_VIEW, Uri.parse(webView.getUrl()));
        gonnaLaunch.putExtra(Intent.EXTRA_TEXT, Uri.parse(webView.getUrl()));

        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, webView.getTitle());
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, webView.getFavicon());
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, gonnaLaunch);

        sendBroadcast(shortcutintent);
    }   //duh

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == 16019) loadAll("http://paypal.me/makeitrainonme/1");
        reloadSettings();
    }   //wat do when u get a result from activity: paypal

    public void reloadSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ImageButton bang = (ImageButton) findViewById(R.id.bang);
        if (!sharedPref.getString("searchEngine", "").equals("ddg")) bang.setVisibility(View.GONE);
        else bang.setVisibility(View.VISIBLE);
    }           //set button visibility et al according to prefs

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
    }       //set background color according to user prefs

    public void reloadGraphicalTheme () {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        RelativeLayout topelements = (RelativeLayout) findViewById(R.id.topelements);
        switch(sharedPref.getString("theme", "")) {
            case "def":
                toptextbar.setHighlightColor(Color.rgb(128, 222, 234)); //200
                topelements.setBackgroundColor(Color.rgb(0, 131, 143)); //800
                return;
            case "bg":
                toptextbar.setHighlightColor(Color.rgb(176, 190, 197)); //200
                topelements.setBackgroundColor(Color.rgb(55, 71, 79));  //800
                return;
            case "rock":
                toptextbar.setHighlightColor(Color.rgb(208, 190, 181)); //Arbitrary
                topelements.setBackgroundColor(Color.rgb(87, 71, 63));  //Arbitrary
                return;
            case "green":
                toptextbar.setHighlightColor(Color.rgb(165, 214, 167)); //200
                topelements.setBackgroundColor(Color.rgb(46, 125, 50)); //800
                return;
            case "blue":
                toptextbar.setHighlightColor(Color.rgb(129, 212, 250)); //200
                topelements.setBackgroundColor(Color.rgb(2, 119, 189)); //800
                return;
            case "gray":
                toptextbar.setHighlightColor(Color.rgb(238, 238, 238)); //200
                topelements.setBackgroundColor(Color.rgb(66, 66, 66));  //800
                return;
        }
    }    //set background color of all markup items according to user prefs

    public void emptyBar(View v) {
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        toptextbar.setText("");
    }           //empty top text bar duh

    public void reloadPage (MenuItem item) {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.reload();
    } //duh
}