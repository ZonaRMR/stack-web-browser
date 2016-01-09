package eu.depa.browsing.stack;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.File;
import java.util.HashMap;

@SuppressWarnings("deprecation")

public class LCWV extends WebView {

    public LCWV(Context context) {
        super(context);
    }

    public LCWV(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
    EditText toptextbar = new EditText(getContext());
    ProgressBar pb = new ProgressBar(getContext());
    ImageView favicon = new ImageView(getContext());

    WebViewClient webViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("market://")) {
                Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(openIntent);
            }
            else {
                HashMap<String, String> extraHeaders = new HashMap<>();
                if (sharedPref.getBoolean("DNT", true)) extraHeaders.put("DNT", "1");
                else extraHeaders.put("DNT", "0");
                loadUrl(url, extraHeaders);
                if (getUrl().startsWith("<html>")) toptextbar.setText(getTitle());
                else toptextbar.setText(getUrl().split("//")[1]);
                pb.setVisibility(View.VISIBLE);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            ErrorPage(view, failingUrl);
        }

        private void ErrorPage(WebView view, String url) {
            String customErrorPageHtml = "<html>" +
                    "  <head>" +
                    "    <title>" + getString(R.string.pageNotLoaded) + "</title>" +
                    "  </head>" +
                    "  <body style=\"font-family: 'Open Sans'; text-align: center\">" +
                    "    <br>" +
                    "    <h4>" + getString(R.string.pageNotLoaded) + " :(</h4>" +
                    "    <hr>" +
                    "    <ul style=\"font-family: 'Open Sans'; text-align: left\">" +
                    "      <li>" + getString(R.string.checkTypos) +
                    "          <br>" +
                    "          <strong>ww</strong>.example.com " + getString(R.string.insteadof) +
                    "          <br>" +
                    "          <strong>www</strong>.example.com</li>" +
                    "      <li>" + getString(R.string.cantLoad) + " " +
                    getString(R.string.checkConn) + "</li>" +
                    "      <li>" + getString(R.string.web_isPermitted) + "</li>" +
                    "    </ul>" +
                    "    <hr>" +
                    "    <div style=\"text-align:center; font-size: 15px !important\">" +
                    "      <INPUT TYPE=\"button\" " +
                    "       onClick=\"parent.location='" + url + "'\"" +
                    "       VALUE=\"" + getString(R.string.reload).toUpperCase() + "\">" +
                    "    </div>" +
                    "  </body>" +
                    "</html>";
            view.loadData(customErrorPageHtml, "text/html", null);
            toptextbar.setText(url);
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            if (url.startsWith("<html>") || url.endsWith("</html>")) toptextbar.setText(webView.getTitle());
            else toptextbar.setText(url.split("//")[1]);

        }
    };
    WebChromeClient webChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView wv, int progress) {
            pb.setProgress(progress);
            if (getUrl().startsWith("<html>")) toptextbar.setText(getTitle());
            else toptextbar.setText(getUrl().split("//")[1]);
            if (progress == 100) pb.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            favicon.setImageBitmap(icon);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            if (sharedPref.getString("location", "ask").equals("doprovide")) {
                callback.invoke(origin, true, false);
                return;
            }
            else if (sharedPref.getString("location", "ask").equals("dontprovide")) {
                callback.invoke(origin, false, false);
                return;
            }
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

            builder.setTitle(getString(R.string.location));
            builder.setMessage(getString(R.string.askuselocation));

            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    callback.invoke(origin, true, false);
                }
            });
            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callback.invoke(origin, false, false);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    };

    protected void init(){
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setDisplayZoomControls(false);
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setAppCacheEnabled(true);
        this.getSettings().setDatabaseEnabled(true);
        this.getSettings().setDomStorageEnabled(true);
        toptextbar = (EditText) getRootView().findViewById(R.id.toptextbar);
        pb = (ProgressBar) getRootView().findViewById(R.id.pb);
        favicon = (ImageView) getRootView().findViewById(R.id.favicon);
        this.setWebViewClient(webViewClient);
        this.setWebChromeClient(webChromeClient);
    }

    final int SHARE_LINK_ID = 0,
            VIEW_IMAGE_ID = 1,
            SAVE_IMAGE_ID = 2,
            SHARE_IMAGE_ID = 3;

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);

        final HitTestResult result = getHitTestResult();

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case SHARE_LINK_ID: //share link
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
                        getContext().startActivity(sharingIntent);
                        break;
                    case VIEW_IMAGE_ID: //view image
                        loadUrl(result.getExtra());
                        break;
                    case SAVE_IMAGE_ID:
                        downloadFile(result.getExtra());
                        Toast.makeText(getContext(), getContext().getString(R.string.image_downloaded), Toast.LENGTH_SHORT).show();
                        break;
                    case SHARE_IMAGE_ID:
                        downloadFile(result.getExtra());
                        Intent sharePicIntent = new Intent(Intent.ACTION_SEND);
                        String FN = getFileName(result.getExtra());
                        File picFile = new File("/storage/emulated/0/Android/data/eu.depa.browsing.stack", FN);
                        sharePicIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picFile));
                        sharePicIntent.setType("image/*");
                        getContext().startActivity(sharePicIntent);
                }
                return true;
            }
        };

        switch (result.getType()) {
            case HitTestResult.IMAGE_TYPE:      //PICTURE
                menu.setHeaderTitle(result.getExtra());
                menu.add(0, SAVE_IMAGE_ID, 0, getContext().getString(R.string.save_image)).setOnMenuItemClickListener(handler);
                menu.add(0, VIEW_IMAGE_ID, 0, getContext().getString(R.string.view_image)).setOnMenuItemClickListener(handler);
                menu.add(0, SHARE_IMAGE_ID, 0, getContext().getString(R.string.share_image)).setOnMenuItemClickListener(handler);
                break;
            case HitTestResult.SRC_ANCHOR_TYPE: //LINK
                menu.setHeaderTitle(result.getExtra());
                menu.add(0, SHARE_LINK_ID, 0, getContext().getString(R.string.share_link)).setOnMenuItemClickListener(handler);
                break;
        }
    }

    public void downloadFile(String url) {
        File direct = new File("/storage/emulated/0/Android/data/eu.depa.browsing.stack");

        if (!direct.exists())
            direct.mkdirs();

        DownloadManager mgr = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String FN = getFileName(url);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(FN)
                .setDestinationUri(Uri.fromFile(direct));

        mgr.enqueue(request);
    }

    public String getFileName(String url) {
        url = url.split("%2F")[url.split("%2F").length - 1];
        url = url.split("/")  [url.split("/")  .length - 1];
        url = url.split("&")[0];
        url = url.split("$")[0];
        url = url.split("#")[0];
        return url;
    }

    public String getString(int res) {
        return getResources().getString(res);
    }
}