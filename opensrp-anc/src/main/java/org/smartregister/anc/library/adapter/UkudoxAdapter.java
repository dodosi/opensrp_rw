package org.smartregister.anc.library.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.model.LibraryContent;
import org.smartregister.anc.library.model.UkudoxContent;
import org.smartregister.anc.library.viewholder.LibraryContentViewHolder;
import org.smartregister.anc.library.viewholder.UkudoxViewHolder;

import java.util.List;

public class UkudoxAdapter extends RecyclerView.Adapter<UkudoxViewHolder> {
    private List<UkudoxContent> libraryContentList;
    private LayoutInflater inflater;
    private Activity activity;

    public UkudoxAdapter(List<UkudoxContent> libraryContentList, Context context) {
        this.libraryContentList = libraryContentList;
        this.inflater = LayoutInflater.from(context);
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public UkudoxViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.library_items_row, viewGroup, false);
        return new UkudoxViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull UkudoxViewHolder libraryContentViewHolder, int position) {
        if (libraryContentList != null && libraryContentList.size() > 0) {
            UkudoxContent libraryContent = libraryContentList.get(position);
            if (libraryContent != null) {
                String contentHeader = libraryContent.getContentHeader();
                libraryContentViewHolder.contentHeader.setText(contentHeader);
            }
        }
    }

    @Override
    public int getItemCount() {
        return libraryContentList.size();
    }
}
