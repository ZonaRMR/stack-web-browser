package eu.depa.browsing.stack;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Vector;

public class SpinnerDialog extends Dialog{

    Vector<String> folders = new Vector<>();

    public SpinnerDialog(Context context, Vector<String> foldersArg){
        super(context);
        folders = foldersArg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner_dialog);
        Spinner spinner = (Spinner) findViewById(R.id.folderSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_expandable_list_item_1, folders);
        spinner.setAdapter(adapter);
    }
}
