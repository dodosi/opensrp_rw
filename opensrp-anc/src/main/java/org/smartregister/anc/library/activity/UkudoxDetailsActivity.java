package org.smartregister.anc.library.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.custom.CustomClient;
import org.smartregister.anc.library.custom.DashClientAdapter;
import org.smartregister.anc.library.repository.DashboardRepository;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UkudoxDetailsActivity extends AppCompatActivity {

    private RecyclerView contentLayout;
    private TextView textView_title;
    private String title;
    private int number;
    private  String type;
    private String startDate;
    private String endDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ukudox_details);
        Intent intent=getIntent();
        title=intent.getStringExtra("title");
        number=intent.getIntExtra("number",0);
        type=intent.getStringExtra("type");
        startDate=intent.getStringExtra("start");
        endDate=intent.getStringExtra("end");
        setUpViews();
 }
    private void setUpViews() {

        contentLayout = findViewById(R.id.details_recycler_view);
        textView_title=findViewById(R.id.text_title_content);
        textView_title.setText(title);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void attachLayoutContentAdapter() {
        List<CustomClient> clientList=new ArrayList<>();
        switch (type){
            case "1":
                clientList=DashboardRepository.getWomanProfileDetails(getDate());
                break;

            default:
                clientList.add(new CustomClient("Marie","Louise","00385805","31","20","22/04/2022"));
        }
        DashClientAdapter dashClientAdapter = new DashClientAdapter( clientList ,new UkudoxDetailsActivity.HomeActionHandler());
        dashClientAdapter.notifyDataSetChanged();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        contentLayout.setLayoutManager(mLayoutManager);
        contentLayout.setAdapter(dashClientAdapter);
    }
    public void onResume() {
        super.onResume();
        attachLayoutContentAdapter();
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
                Toast.makeText(UkudoxDetailsActivity.this, "Hi, you Clicked me", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(UkudoxDetailsActivity.this, UkudoxDetailsActivity.class);
//                intent.putExtra(ConstantsUtils.IntentKeyUtils.LIBRARY_HEADER, "headerText");
//                startActivity(intent);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate getDate(){
        LocalDate d= LocalDate.now();
        return d;
    }
}