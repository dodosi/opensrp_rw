package org.smartregister.anc.library.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.custom.CustomClient;
import org.smartregister.anc.library.custom.DashClientAdapter;
import org.smartregister.anc.library.repository.DashboardRepository;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class UkudoxDetailsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView contentLayout;
    private TextView textView_client_number,toolbar_text;
    private String title;
    private int number;
    private  String type;
    private String startDate;
    private String endDate;
    private SearchView editsearch;
    private DashClientAdapter dashClientAdapter;
    private List<CustomClient> clientList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ukudox_details);

        ImageView bck=(ImageView) findViewById(R.id.backwhite);
        bck.setVisibility(View.VISIBLE);
        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UkudoxDetailsActivity.this.finish();
            }
        });
        Intent intent=getIntent();
        title=intent.getStringExtra("title");
        number=intent.getIntExtra("number",0);
        type=intent.getStringExtra("type");
        startDate=intent.getStringExtra("start");
        endDate=intent.getStringExtra("end");
        setUpViews();
        editsearch = (SearchView) findViewById(R.id.search_client);
        editsearch.setQueryHint(Html.fromHtml("<font color = #dbdbdb>" +getResources().getString(org.smartregister.R.string.search_hint)+ "</font>"));
        editsearch.setOnQueryTextListener(this);
        int id = editsearch.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) editsearch.findViewById(id);
        textView.setTextColor(Color.WHITE);
 }
    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setUpViews() {

        contentLayout = findViewById(R.id.details_recycler_view);
        textView_client_number=findViewById(R.id.header_text_displayclient);
        textView_client_number.setText(title);
        toolbar_text=findViewById(R.id.library_toolbar_title);
        toolbar_text.setText(title);

    }
    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        dashClientAdapter.filter(text);
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void attachLayoutContentAdapter() {
        switch (type){
            case "1":
                clientList=DashboardRepository.getWomanProfileDetails(getDate());
                break;
            case "4":
                clientList=DashboardRepository.getProcessedVisitsDetails(startDate,endDate);
                break;
            case "2":
                clientList=DashboardRepository.getWomanReferredDetails(startDate,endDate);
                break;
            case "3":
                clientList=DashboardRepository.getWomanWithDangerSingDetails(startDate,endDate);
                break;
            case"5":
                clientList=DashboardRepository.getWomanAccompaniedWithPartnerDetails(startDate,endDate);
                break;
            case "7":
                clientList=DashboardRepository.getWomanReceivedDewormingPillsDetails(startDate,endDate);
                break;
            case "6":
                clientList=DashboardRepository.getWomanVaccinatedDetails(startDate,endDate);
                break;
            case "8":
                clientList=DashboardRepository.getWomanInParticularAgeDetails(startDate,endDate,19);
                break;
            case "9":
                clientList=DashboardRepository.getWomanInParticularAgeDetails(startDate,endDate,30);
                break;
            case "10":
                clientList=DashboardRepository.getWomanInParticularAgeDetails(startDate,endDate,49);
                break;
            case "11":
                clientList=DashboardRepository.getWomanWithSyphilisPositiveDetails(startDate,endDate);
                break;
            case "0":
                clientList=DashboardRepository.getExpectedDeliveriesDetails(startDate,endDate);
                break;

            default:
                clientList.add(new CustomClient("Marie","Louise","00385805","31","20","22/04/2022"));
        }
        dashClientAdapter = new DashClientAdapter( clientList ,new UkudoxDetailsActivity.HomeActionHandler());
        dashClientAdapter.notifyDataSetChanged();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        contentLayout.setLayoutManager(mLayoutManager);
        contentLayout.setAdapter(dashClientAdapter);
    }
    public void onResume() {
        super.onResume();
        attachLayoutContentAdapter();
        textView_client_number.setText(clientList.size()+ " Clients");
    }
    private List<CustomClient> getContent() {
        List<CustomClient> clientList=new ArrayList<>();
        clientList.add(new CustomClient("Marie","Louise","00385805","31","20","22/04/2022"));
        return  clientList;
    }

    private class HomeActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.due_button) {
                CustomClient client=(CustomClient) view.getTag();
                continueToContact(client.getBaseIntityId());
            }
            if (i == R.id.patient_column) {
                CustomClient client=(CustomClient) view.getTag();
                goToClientProfile(client.getBaseIntityId());
            }
        }
    }
    private void continueToContact(String id) {
       HashMap<String, String> detailMap=getWomanProfileDetails(id);
            String baseEntityId = detailMap.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID);

            if (StringUtils.isNotBlank(baseEntityId)) {
                Utils.proceedToContact(baseEntityId, detailMap, UkudoxDetailsActivity.this);
            }

    }
    public void goToClientProfile(String bid) {

        HashMap<String, String> womanProfileDetails = getWomanProfileDetails(bid);
        if (womanProfileDetails != null) {
            Utils.navigateToProfile(this, womanProfileDetails);
        } else {
            Timber.e("Make sure the person object was fetched successfully");
        }
    }
            public HashMap<String, String> getWomanProfileDetails( String id) {
        return (HashMap<String, String>) PatientRepository.getWomanProfileDetails(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate getDate(){
        LocalDate d= LocalDate.now();
        return d;
    }
}