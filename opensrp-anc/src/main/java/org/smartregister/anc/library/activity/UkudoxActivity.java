package org.smartregister.anc.library.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.LibraryContentAdapter;
import org.smartregister.anc.library.adapter.UkudoxAdapter;
import org.smartregister.anc.library.model.LibraryContent;
import org.smartregister.anc.library.model.UkudoxContent;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.util.ArrayList;
import java.util.List;

public class UkudoxActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    private RecyclerView contentLayout;
    Button go;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ukudox);
        setUpViews();
        go=(Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               gotToHomeRegister(true);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        attachLayoutContentAdapter();
    }
    private void setUpViews() {
        mToolbar = findViewById(R.id.library_toolbar);
        contentLayout = findViewById(R.id.layout_attach_recycler_view);
    }
    private void gotToHomeRegister(boolean remote) {
        Intent intent = new Intent(this, BaseHomeRegisterActivity.class);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }
    private void attachLayoutContentAdapter() {
        UkudoxAdapter libraryContentAdapter = new UkudoxAdapter(getLibraryContent(), this);
        libraryContentAdapter.notifyDataSetChanged();
        contentLayout.setLayoutManager(new LinearLayoutManager(this));
        contentLayout.setAdapter(libraryContentAdapter);
    }

    private List<UkudoxContent> getLibraryContent() {
        List<UkudoxContent> libraryContents = new ArrayList<>();
        if (this != null && getResources() != null) {
            //LibraryContent birthEmergencyPlan = new LibraryContent(getActivity().getResources().getString(R.string.birth_and_emergency_plan));

            UkudoxContent birthEmergencyPlan = new UkudoxContent(getResources().getString(R.string.due_contacts));
            UkudoxContent physicalActivity = new UkudoxContent(getResources().getString(R.string.mother_with_danger_signs));
            UkudoxContent balancedNutrition = new UkudoxContent(getResources().getString(R.string.reffered_mothers));
            //UkudoxContent reportTab = new UkudoxContent("Dismissed");

            libraryContents.add(birthEmergencyPlan);
            libraryContents.add(balancedNutrition);
            libraryContents.add(physicalActivity);
            //libraryContents.add(reportTab);
        }
        return libraryContents;
    }
}