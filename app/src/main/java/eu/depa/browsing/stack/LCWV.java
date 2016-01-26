package eu.depa.browsing.stack;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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


    public ValueCallback<Uri[]> mFilePathCallback;
    public ValueCallback<Uri[]> getmFilePathCallback() {
        return this.mFilePathCallback;
    }

    public void setmFilePathCallback(ValueCallback<Uri[]> mFilePathCallback) {
        this.mFilePathCallback = mFilePathCallback;
    }

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
                if (getUrl().startsWith("<html>") || getUrl().endsWith("</html>"))
                    try{toptextbar.setText(getTitle());}
                    catch (Exception e) {
                        e.printStackTrace();
                        toptextbar.setText(getTitle());
                    }
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
            String customErrorPageHtml =
                    "<html>" +
                    "  <head>" +
                    "    <title>" + url + "</title>" +
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
                    "       VALUE=\"" + getString(R.string.reload) + "\">" +
                    "    </div>" +
                    "  </body>" +
                    "</html>";
            view.loadData(customErrorPageHtml, "text/html", null);
            toptextbar.setText(getTitle());
        }
    };
    WebChromeClient webChromeClient = new WebChromeClient(){

        @Override
        public void onProgressChanged(WebView wv, int progress) {
            pb.setProgress(progress);
            /*try {
                if (getUrl().startsWith("<html>") || getUrl().endsWith("</html>")) toptextbar.setText(getTitle());
                else toptextbar.setText(getUrl().split("//")[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                builder.setIcon(getResources().getDrawable(R.drawable.pin));
            builder.show();
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (getUrl().startsWith("<html>") || getUrl().endsWith("</html>")){
                toptextbar.setText(getTitle());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                            getString(R.string.pageNotLoadedNoHTML),
                            null,
                            Color.rgb(221, 221, 221));
                    ((MainActivity)getContext()).setTaskDescription(taskDescription);
                }
            }
            else{
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                            getTitle().split("-")[0] + " - Stack",
                            null,
                            Color.rgb(221, 221, 221));
                    ((MainActivity)getContext()).setTaskDescription(taskDescription);
                }
            }
        }


        private final static int FILECHOOSER_RESULTCODE = 4893;
        // For Android < 3.0
        public void openFileChooser(ValueCallback uploadMsg) {
            MainActivity.mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            ((MainActivity)getContext()).startActivityForResult(
                    Intent.createChooser(i, getString(R.string.chooseSauce)),
                    FILECHOOSER_RESULTCODE);
        }

        // For Android > 3.0
        public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
            MainActivity.mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            ((MainActivity)getContext()).startActivityForResult(
                    Intent.createChooser(i, getString(R.string.chooseSauce)),
                    FILECHOOSER_RESULTCODE);
        }

        public static final int INPUT_FILE_REQUEST_CODE = 8443;
        private String mCameraPhotoPath;

        // For Lollipop
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            if(mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");
            Intent[] intentArray;
            if(takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.chooseSauce));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            ((MainActivity)getContext()).startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }
    };
    DownloadListener DListener = new DownloadListener() {
        @Override
        public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle(getString(R.string.download));
            builder.setMessage(getString(R.string.askdownload));

            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    downloadFile(url);
                    Toast.makeText(getContext(), getString(R.string.file_downloading), Toast.LENGTH_SHORT).show();
                }
            });
            builder.setIcon(new BitmapDrawable(getResources(), getFavicon()));
            builder.setCancelable(true);
            builder.show();
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    protected void init(){
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setDisplayZoomControls(false);
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setAppCacheEnabled(true);
        this.getSettings().setDatabaseEnabled(true);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setSaveFormData(true);
        this.getSettings().setLoadWithOverviewMode(true);
        reloadSize();
        toptextbar = (EditText) getRootView().findViewById(R.id.toptextbar);
        pb = (ProgressBar) getRootView().findViewById(R.id.pb);
        favicon = (ImageView) getRootView().findViewById(R.id.favicon);
        this.setWebViewClient(webViewClient);
        this.setWebChromeClient(webChromeClient);
        this.setDownloadListener(DListener);
    }

    final int SHARE_LINK_ID = 0,
            VIEW_IMAGE_ID   = 1,
            SAVE_IMAGE_ID   = 2,
            SHARE_IMAGE_ID  = 3,
            COPY_ID         = 4,
            NEW_TAB_ID      = 5,
            SHARE_RAW_ID    = 6;

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
                        shareFromUrl(result.getExtra());
                        break;
                    case COPY_ID:
                        ClipboardManager clipMan = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("stack_clip", result.getExtra());
                        clipMan.setPrimaryClip(clipData);
                        Toast.makeText(getContext(), getResources().getString(R.string.copied), Toast.LENGTH_SHORT).show();
                        break;
                    case NEW_TAB_ID:
                        PackageManager pm = getContext().getPackageManager();
                        Intent newTabIntent = pm.getLaunchIntentForPackage("eu.depa.browsing.stack");
                        newTabIntent.setAction(Intent.ACTION_VIEW);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            newTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        newTabIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newTabIntent.setData(Uri.parse(result.getExtra()));
                        getContext().startActivity(newTabIntent);
                        break;
                    case SHARE_RAW_ID:
                        String encodedDataString = result.getExtra().replace("data:image/jpeg;base64,","");
                        encodedDataString = encodedDataString.replace("data:image/jpg;base64,","");
                        encodedDataString = encodedDataString.replace("data:image/png;base64,","");
                        byte[] imageAsBytes = Base64.decode(encodedDataString.getBytes(), 0);
                        Bitmap finalBM = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), finalBM, "", null);
                        Uri finalUri = Uri.parse(path);

                        intent.putExtra(Intent.EXTRA_STREAM, finalUri);
                        intent.setType("image/*");
                        getContext().startActivity(intent);
                }
                return true;
            }
        };

        switch (result.getType()) {
            case HitTestResult.IMAGE_TYPE:      //PICTURE
            case HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                menu.setHeaderTitle(result.getExtra());
                if (result.getExtra().startsWith("data:image")) {
                    menu.setHeaderTitle(getString(R.string.image));
                    menu.add(0, VIEW_IMAGE_ID, 0, getContext().getString(R.string.view_image)).setOnMenuItemClickListener(handler);
                    menu.add(0, SHARE_RAW_ID, 0, getString(R.string.share_image)).setOnMenuItemClickListener(handler);
                }
                else {
                    menu.add(0, SAVE_IMAGE_ID, 0, getContext().getString(R.string.save_image)).setOnMenuItemClickListener(handler);
                    menu.add(0, VIEW_IMAGE_ID, 0, getContext().getString(R.string.view_image)).setOnMenuItemClickListener(handler);
                    menu.add(0, SHARE_IMAGE_ID, 0, getContext().getString(R.string.share_image)).setOnMenuItemClickListener(handler);
                    menu.add(0, COPY_ID, 0, getResources().getString(R.string.copy_image_link)).setOnMenuItemClickListener(handler);
                }
                break;
            case HitTestResult.SRC_ANCHOR_TYPE: //LINK
                menu.setHeaderTitle(result.getExtra());
                menu.add(0, SHARE_LINK_ID, 0, getContext().getString(R.string.share_link)).setOnMenuItemClickListener(handler);
                menu.add(0, COPY_ID, 0, getResources().getString(R.string.copy_link)).setOnMenuItemClickListener(handler);
                menu.add(0, NEW_TAB_ID, 0, getString(R.string.openInNewTab)).setOnMenuItemClickListener(handler);
                break;
        }
    }

    public void shareFromUrl(final String extra)  {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    URL url  = null;
                    try {
                        url = new URL(extra);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Bitmap image = null;
                    try {
                        if (url != null) image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), image, "", null);
                    Uri finalUri = Uri.parse(path);

                    intent.putExtra(Intent.EXTRA_STREAM, finalUri);
                    intent.setType("image/*");
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void downloadFile(String url) {
        File direct = new File("/storage/emulated/0/Download");

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
        url = url.split("\\?")[0];
        return url;
    }

    public String getString(int res) {
        return getResources().getString(res);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    public void reloadSize() {
        int sizePref = Integer.parseInt(sharedPref.getString("size", "0"));
        this.getSettings().setMinimumFontSize(sizePref);
    }
}