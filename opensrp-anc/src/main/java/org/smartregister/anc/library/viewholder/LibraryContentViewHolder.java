package org.smartregister.anc.library.viewholder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.listener.LibraryContentClickListener;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class LibraryContentViewHolder extends RecyclerView.ViewHolder {
    public View parent;
    public CustomFontTextView contentHeader;
    private RelativeLayout contentLayout;
    private ImageView attachIcon;
    private ImageView openContentIcon;
    private LibraryContentClickListener libraryContentClickListener;

    public LibraryContentViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView);
        contentLayout = itemView.findViewById(R.id.library_item_layout);
        attachIcon = itemView.findViewById(R.id.library_attach_icon);
        openContentIcon = itemView.findViewById(R.id.library_arrow_icon);
        contentHeader = itemView.findViewById(R.id.library_text_header);
//        libraryContentClickListener = new LibraryContentClickListener();
        libraryContentClickListener = new LibraryContentClickListener(activity);


        attachClickListeners();
    }

    private void attachClickListeners() {
        contentLayout.setOnClickListener(libraryContentClickListener);
    }
}
