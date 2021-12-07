package org.smartregister.anc.library.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.LibraryContentActivity;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class LibraryContentClickListener implements View.OnClickListener {
    private Activity activity;

    public LibraryContentClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            CustomFontTextView header = view.findViewById(R.id.library_text_header);
            try {
            String headerText = header.getText().toString();

            if (activity != null) {
                ((BaseHomeRegisterActivity) activity).setLibrary(true);

                Intent intent = new Intent(activity, LibraryContentActivity.class);
                intent.putExtra(ConstantsUtils.IntentKeyUtils.LIBRARY_HEADER, headerText);
                activity.startActivity(intent);
            }
        }catch (NullPointerException e){
                //Toast.makeText(activity, "Click on header to view", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
