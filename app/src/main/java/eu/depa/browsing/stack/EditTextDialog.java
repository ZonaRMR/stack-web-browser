package eu.depa.browsing.stack;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditTextDialog extends Dialog implements View.OnClickListener {

    Context context = null;
    String title = null,
            url = null;

    public EditTextDialog(Context contextArg, String titleArg, String urlArg){
        super(contextArg);
        context = contextArg;
        title = titleArg;
        url = urlArg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittext_dialog);
        ImageButton plus = (ImageButton) findViewById(R.id.saveBM);
        plus.setOnClickListener(this);
        EditText ET = (EditText) findViewById(R.id.editBM);
        ET.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveBM:
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                sharedPref.edit().putString("BMtitles", sharedPref.getString("BMtitles", "") + ";;" + title).commit();
                sharedPref.edit().putString("BMurls", sharedPref.getString("BMurls", "") + ";;" + url).commit();
                Toast.makeText(context, getContext().getString(R.string.BM_added), Toast.LENGTH_SHORT).show();
                dismiss();
        }
    }
}
