package org.smartregister.anc.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.smartregister.Context;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.HomeItem;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<HomeItem> homeItems;

    private View.OnClickListener clickListener;

    public HomeAdapter( List<HomeItem> homeItems, View.OnClickListener clickListener) {

        this.homeItems = homeItems;
        this.clickListener = clickListener;
    }

    public void setContacts(List<HomeItem> homeItems) {
        this.homeItems = homeItems;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HomeItem homeItem = homeItems.get(position);

        holder.cardLayout.setBackgroundResource(homeItem.getBackground());

        holder.cardLayout.setTag(homeItem);
        holder.name.setText(homeItem.getTitle());
        holder.number.setText(String.valueOf(homeItem.getNumber()));
        holder.cardLayout.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return homeItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View cardLayout;
        public TextView name;
        public TextView number;




        public ViewHolder(View view) {
            super(view);
            cardLayout = view.findViewById(R.id.card_layout_home);
            name = view.findViewById(R.id.container_name_home);
            number = view.findViewById(R.id.number);
        }
    }
}

