/*This file is part of Stack.

Stack is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Stack is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Stack. In fact, it is available under app/res/GPL.txt
If not, see <http://www.gnu.org/licenses/>.*/

package eu.depa.browsing.stack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebIconDatabase;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

@SuppressWarnings({"deprecation"})
@SuppressLint("SetJavaScriptEnabled")

public class MainActivity extends AppCompatActivity implements OnKeyListener{

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setThemeFromPrefs();                        //set background color before anything is shown
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        reloadGraphicalTheme();                     //set background color of all markup items according to user prefs

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);    //set pref defs, only once

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        final LCWV  webview       = (LCWV)        findViewById(R.id.webView);
        final EditText toptextbar = (EditText)    findViewById(R.id.toptextbar);
        final ImageButton X       = (ImageButton) findViewById(R.id.X);

        reloadSettings();       //load up prefs, see comment below

        //MOST BROWSERY STUFF HAS MOVED TO THE LCWV SUBCLASS FOR CLEANLINESS PURPOSES
        registerForContextMenu(webview);
        webview.init();
        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());    //why is this deprecated you cuck give me an alternative
        //NO BROWSERY STUFF, MORE AUTOCONFIG

        toptextbar.setOnKeyListener(this);      //set that for l8r
        onNewIntent(getIntent());       //do this just in case you get called which will never happen
        X.setVisibility(View.GONE);     //hide the X in the beginning

        toptextbar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (toptextbar.hasFocus()) X.setVisibility(View.VISIBLE);   //when it's focused show the X
                else X.setVisibility(View.GONE);
            }
        });
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
    }       //when u get an intent

    public void loadAll(String url) {
        LCWV webView        = (LCWV)        findViewById(R.id.webView);
        EditText toptextbar = (EditText)    findViewById(R.id.toptextbar);
        ProgressBar pb      = (ProgressBar) findViewById(R.id.pb);

        pb.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
        if (url.startsWith("<html>")) toptextbar.setText(webView.getTitle());
        else {
            try {
                toptextbar.setText(url.split("//")[1]);
            }catch (Exception e) {
                toptextbar.setText(url);
            }
        }
    }//load everything u need to all nicely packed in 1 method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }   //wat do when options menu is triggered

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

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.dots));
        popup.getMenuInflater().inflate(R.menu.options, popup.getMenu());
        popup.show();
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
        LCWV webview = (LCWV) findViewById(R.id.webView);
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        if (webview.canGoBack()) {
            webview.goBack();
            toptextbar.setText(webview.getOriginalUrl());
        }
        else {
            super.onBackPressed();
        }
    }                 //override: go back instead

    @Override
    protected void onResume() {
        super.onResume();
        reloadSettings();
        reloadGraphicalTheme();
    }

    public void openInApp () {
        LCWV webView = (LCWV) findViewById(R.id.webView);
        Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webView.getUrl()));
        startActivity(openIntent);
    }       //make and execute intent to open webviewed page in other apps

    public void share () {
        LCWV webView = (LCWV) findViewById(R.id.webView);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());

        startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.sharewith)), 1);

        getFragmentManager().popBackStack();
    }           //share the current webviewed page

    public void addPagetoHome () {
        LCWV webView = (LCWV) findViewById(R.id.webView);

        Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
        shortcutIntent.putExtra("extra", webView.getUrl());
        shortcutIntent.setData(Uri.parse(webView.getUrl()));
        shortcutIntent.setAction(Intent.ACTION_VIEW);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, webView.getTitle());
        //if (webView.getFavicon() != null && webView.getFavicon().getHeight() >= 63)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, webView.getFavicon());
        /*else {
            Bitmap favicon = webView.getFavicon();
            int mdl = favicon.getPixel((int) Math.floor(favicon.getHeight() / 2), (int) Math.floor(favicon.getWidth()/2));
            Bitmap bitmap = BitmapFromSVG(webView.getTitle().toUpperCase().toCharArray()[0], mdl);
            if (bitmap != null)
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        }*/ // FIXME: 23/01/16 make it put standardised icon if favicon is too small


        addIntent.putExtra("duplicate", false);
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        getApplicationContext().sendBroadcast(addIntent);
        Toast.makeText(this, getString(R.string.added_to_home), Toast.LENGTH_SHORT).show();
    }   //duh NOTE: WORKS 3/1/16 HELL YEAH

    public static ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 4893;
    private final static int PAYPAL_RESULTCODE = 16019;
    public static final int INPUT_FILE_REQUEST_CODE = 8443;
    public ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == PAYPAL_RESULTCODE) {
            Intent launchPayPal = getPackageManager().getLaunchIntentForPackage("com.paypal.android.p2pmobile");
            if(launchPayPal == null) {
                PackageManager pm = getPackageManager();
                Intent newTabIntent = pm.getLaunchIntentForPackage("eu.depa.browsing.stack");
                newTabIntent.setAction(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    newTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                newTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newTabIntent.setData(Uri.parse("http://paypal.me/makeitrainonme/1/?locale.x=" + Locale.getDefault().toString()));
                startActivity(newTabIntent);
            }
            else {
                launchPayPal.setAction(Intent.ACTION_SENDTO);
                launchPayPal.setData(Uri.fromParts("mailto", "depasquale.a@tuta.io", null));
                startActivity(launchPayPal);
            }
        }

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }

        Uri[] results = null;
        if(resultCode == Activity.RESULT_OK) {
            if(intent == null) {
                if(mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = intent.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        LCWV WV = (LCWV) findViewById(R.id.webView);
        if (WV.getmFilePathCallback() != null) {
            WV.getmFilePathCallback().onReceiveValue(results);
            WV.setmFilePathCallback(null);      //the FilePathCallback is shared across activities this way,
            // in order to prevent NPE
        }

        reloadSettings();
    }   //wat do when u get a result from activity: PayPal, Uploader

    public void reloadSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ImageButton bang = (ImageButton) findViewById(R.id.bang);
        ImageButton dots = (ImageButton) findViewById(R.id.dots);
        if (!sharedPref.getString("searchEngine", "").equals("ddg")) bang.setVisibility(View.GONE);
        else bang.setVisibility(View.VISIBLE);
        if (ViewConfiguration.get(this).hasPermanentMenuKey()) dots.setVisibility(View.GONE);
        if (sharedPref.getBoolean("showDots", false)) dots.setVisibility(View.VISIBLE);
    }           //set button visibility et al according to prefs

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
    }       //set background color according to user prefs

    public void reloadGraphicalTheme () {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        RelativeLayout topelements = (RelativeLayout) findViewById(R.id.topelements);
        switch(sharedPref.getString("theme", "def")) {
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
        }
    }    //set background color of all markup items according to user prefs

    public void emptyBar(View v) {
        EditText toptextbar = (EditText) findViewById(R.id.toptextbar);
        toptextbar.setText("");
    }           //empty top text bar duh

    public void reloadPage (MenuItem item) {
        LCWV webView = (LCWV) findViewById(R.id.webView);
        webView.reload();
        loadAll(webView.getUrl());
    } //duh

    public void inflatePageMenu (MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.menu_page));
        builder.setItems(new CharSequence[]{getString(R.string.openInApp),
                        getString(R.string.share),
                        getString(R.string.addtohome),
                        getString(R.string.addtoBM)},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openInApp();
                                break;
                            case 1:
                                share();
                                break;
                            case 2:
                                addPagetoHome();
                                break;
                            case 3:
                                bookmarkCurrent();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void bookmarkCurrent() {
        LCWV webView = (LCWV) findViewById(R.id.webView);
        EditTextDialog ETD = new EditTextDialog(this, webView.getTitle(), webView.getUrl());
        ETD.setTitle(R.string.add_bm);
        ETD.show();

        //sharedPref.edit().putString("bMarks", sharedPref.getString("bMarks", "") + );
    }

    public void goToDonate (MenuItem item) {
        Intent gotodonate = new Intent(getApplicationContext(), Donate.class);
        startActivityForResult(gotodonate, 16019);
    }

    public void goToSettings (MenuItem item) {
        Intent gotosettings = new Intent(getApplicationContext(), Settings.class);
        startActivityForResult(gotosettings, 36165);
    }

    public void goToAbout (MenuItem item) {
        Intent gotoabout = new Intent(getApplicationContext(), About.class);
        startActivity(gotoabout);
    }

    public void goToHistory(MenuItem item) {
        Intent gotoHistory = new Intent(getApplicationContext(), History.class);
        Bundle historyData = new Bundle();

        LCWV webView = (LCWV) findViewById(R.id.webView);
        WebBackForwardList historyList = webView.copyBackForwardList();
        ArrayList<String> titles = new ArrayList<>(),
                          addrs  = new ArrayList<>();
        for (int i = historyList.getSize(); i > 0; i--) {
            if (historyList.getItemAtIndex(i) == null) continue;
            titles.add(historyList.getItemAtIndex(i).getTitle());
            addrs.add(historyList.getItemAtIndex(i).getUrl());
        }
        historyData.putStringArrayList("titles", titles);
        historyData.putStringArrayList("addrs", addrs);
        gotoHistory.putExtra("bundle", historyData);
        startActivity(gotoHistory);
    }

    public void newTab(MenuItem item) {
        PackageManager pm = getPackageManager();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String HP = sharedPref.getString("HP", "duckduckgo.com");
        Intent newTabIntent = pm.getLaunchIntentForPackage("eu.depa.browsing.stack");
        newTabIntent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            newTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        newTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newTabIntent.setData(Uri.fromParts("http", HP, null));
        startActivity(newTabIntent);
    }

    public void goToBookmarks(MenuItem item) {
        Intent gotoBookmarks = new Intent(getApplicationContext(), Bookmarks.class);
        Bundle bookmarkData = new Bundle();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String[] titles = sharedPref.getString("BMtitles", "").split(";;");
        String[] addrs = sharedPref.getString("BMurls", "").split(";;");
        bookmarkData.putStringArrayList("titles", new ArrayList<>(Arrays.asList(titles)));
        bookmarkData.putStringArrayList("addrs", new ArrayList<>(Arrays.asList(addrs)));
        gotoBookmarks.putExtra("bundle", bookmarkData);
        startActivity(gotoBookmarks);
    }

    public Bitmap BitmapFromSVG(char c, int mdl) {
        SVG svg = null;
        try {
            svg = SVG.getFromString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<!-- Created with Inkscape (http://www.inkscape.org/) -->\n" +
                    "\n" +
                    "<svg\n" +
                    "   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                    "   xmlns:cc=\"http://creativecommons.org/ns#\"\n" +
                    "   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                    "   xmlns:svg=\"http://www.w3.org/2000/svg\"\n" +
                    "   xmlns=\"http://www.w3.org/2000/svg\"\n" +
                    "   version=\"1.1\"\n" +
                    "   width=\"128\"\n" +
                    "   height=\"128\"\n" +
                    "   id=\"svg2\">\n" +
                    "  <metadata\n" +
                    "     id=\"metadata9\">\n" +
                    "    <rdf:RDF>\n" +
                    "      <cc:Work\n" +
                    "         rdf:about=\"\">\n" +
                    "        <dc:format>image/svg+xml</dc:format>\n" +
                    "        <dc:type\n" +
                    "           rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" />\n" +
                    "        <dc:title></dc:title>\n" +
                    "      </cc:Work>\n" +
                    "    </rdf:RDF>\n" +
                    "  </metadata>\n" +
                    "  <defs\n" +
                    "     id=\"defs4\" />\n" +
                    "  <rect\n" +
                    "     width=\"128\"\n" +
                    "     height=\"128\"\n" +
                    "     x=\"0\"\n" +
                    "     y=\"0\"\n" +
                    "     id=\"rect2985\"\n" +
                    "     style=\"fill:#004d40;fill-opacity:1;fill-rule:evenodd;stroke:#000000;stroke-width:5;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;stroke-dashoffset:0\" />\n" +
                    "  <text\n" +
                    "     x=\"0\"\n" +
                    "     y=\"0\"\n" +
                    "     id=\"text3755\"\n" +
                    "     xml:space=\"preserve\"\n" +
                    "     style=\"font-size:64px;font-style:normal;font-weight:normal;line-height:125%;letter-spacing:0px;word-spacing:0px;fill:#ffffff;fill-opacity:1;stroke:none;font-family:Monospace\"><tspan\n" +
                    "       x=\"44\"\n" +
                    "       y=\"88\"\n" +
                    "       id=\"tspan3814\">" + c +"</tspan></text>\n" +
                    "</svg>\n");
        } catch (SVGParseException e) {
            e.printStackTrace();
        }

        Picture picture = svg.renderToPicture();
        Bitmap bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(picture);
        return bitmap;
    }
}