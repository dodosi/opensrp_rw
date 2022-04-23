package org.smartregister.anc.library.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.views.CustomTextView;

import org.jetbrains.annotations.NotNull;
import org.smartregister.anc.library.R;

import java.util.List;

public class DashClientAdapter extends RecyclerView.Adapter<DashClientAdapter.DashClientViewHolder>{

    private List<CustomClient> clientList;

    private View.OnClickListener clickListener;

    public DashClientAdapter( List<CustomClient> clientList, View.OnClickListener clickListener) {

        this.clientList = clientList;
        this.clickListener = clickListener;
    }
    public void setContacts(List<CustomClient> homeItems) {
        this.clientList = clientList;
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    @Override
    public void onBindViewHolder(final DashClientAdapter.DashClientViewHolder holder, int position) {
        CustomClient client = clientList.get(position);

        holder.patientName.setText(client.getFirstName()+" "+ client.getLastName());
        holder.age.setText("AGE: "+client.getAge());
        holder.ancId.setText("ID: "+client.getRegistrationId());
        holder.ga.setText("GA: "+client.getAge()+ " wks");
        holder.risk.setText(String.valueOf(client.getAttentionFlag()));
        holder.dueButton.setText("CONTACT DEU: \n" +client.getNextContactDate());
        holder.dueButton.setVisibility(View.VISIBLE);
        holder.contactDoneTodayButton.setVisibility(View.GONE);
        holder.dueButton.setOnClickListener(clickListener);
    }
    @NotNull
    @Override
    public DashClientAdapter.DashClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.register_home_list_row, parent, false);
        return new DashClientAdapter.DashClientViewHolder(itemView);
    }
    public class DashClientViewHolder extends RecyclerView.ViewHolder {
        private TextView patientName;
        private TextView age;
        private TextView period;
        private TextView ga;
        private TextView ancId;
        private TextView risk;
        private Button dueButton;
        private Button sync;
        private View patientColumn;
        private CustomTextView contactDoneTodayButton;

        public DashClientViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            age = itemView.findViewById(R.id.age);
            ga = itemView.findViewById(R.id.ga);
            period = itemView.findViewById(R.id.period);
            ancId = itemView.findViewById(R.id.anc_id);
            risk = itemView.findViewById(R.id.risk);
            dueButton = itemView.findViewById(R.id.due_button);
            sync = itemView.findViewById(R.id.sync);
            patientColumn = itemView.findViewById(R.id.patient_column);
            contactDoneTodayButton = itemView.findViewById(R.id.contact_today_text);
        }
    }
}
