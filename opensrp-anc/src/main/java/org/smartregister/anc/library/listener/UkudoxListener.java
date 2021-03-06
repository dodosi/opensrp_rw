package org.smartregister.anc.library.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.LibraryContentActivity;
import org.smartregister.anc.library.activity.UkudoxDetailsActivity;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class UkudoxListener implements View.OnClickListener {
    private Activity activity;

    public UkudoxListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            CustomFontTextView header = view.findViewById(R.id.library_text_header);
            String headerText = header.getText().toString();

            if (activity != null) {
                Intent intent = new Intent(activity, UkudoxDetailsActivity.class);
                intent.putExtra(ConstantsUtils.IntentKeyUtils.LIBRARY_HEADER, headerText);
                activity.startActivity(intent);
            }
        }
    }
}
