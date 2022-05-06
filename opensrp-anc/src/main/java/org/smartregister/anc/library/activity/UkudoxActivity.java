package org.smartregister.anc.library.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.HomeAdapter;
import org.smartregister.anc.library.adapter.LibraryContentAdapter;
import org.smartregister.anc.library.adapter.UkudoxAdapter;
import org.smartregister.anc.library.domain.HomeItem;
import org.smartregister.anc.library.model.LibraryContent;
import org.smartregister.anc.library.model.UkudoxContent;
import org.smartregister.anc.library.repository.DashboardRepository;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UkudoxActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    private RecyclerView contentLayout;
    Button go;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog datePickerDialog2;
    private Button dateButton;
    private Button dateButton2;
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        attachLayoutContentAdapter();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpViews() {
        mToolbar = findViewById(R.id.library_toolbar);
        contentLayout = findViewById(R.id.layout_attach_recycler_view);
        initDatePicker();
        initDatePicker2();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getDate().minusWeeks(1).toString());
        dateButton2 = findViewById(R.id.datePickerButton2);
        dateButton2.setText(getDate().toString());
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });
        dateButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker2(view);
            }
        });
    }
    private void gotToHomeRegister(boolean remote) {
        Intent intent = new Intent(this, BaseHomeRegisterActivity.class);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void attachLayoutContentAdapter() {
        HomeAdapter libraryContentAdapter = new HomeAdapter( getHomeContent(),new HomeActionHandler());
        libraryContentAdapter.notifyDataSetChanged();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(UkudoxActivity.this, 2);
        contentLayout.setLayoutManager(mLayoutManager);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private  List<HomeItem> getHomeContent(){
        List<HomeItem> homeItems= new ArrayList<>();

        String d1=dateButton.getText().toString().equals("") ? getDate().minusWeeks(1).toString():dateButton.getText().toString();
        String d2=dateButton2.getText().toString().equals("") ? getDate().toString():dateButton2.getText().toString();

        HomeItem dueContactToday=new HomeItem();
        dueContactToday.setTitle(getString(R.string.due_contact_today));
        dueContactToday.setType("1");
        dueContactToday.setNumber((int) DashboardRepository.getDueContactDash(getDate()));
        dueContactToday.setBackground(R.drawable.profile_bg);

        homeItems.add(dueContactToday);

        HomeItem refered= new HomeItem();
        refered.setTitle(getString(R.string.women_referred));
        refered.setType("2");
        refered.setEndDate(dateButton2.getText().toString());
        refered.setStartDate(dateButton.getText().toString());
        refered.setBackground(R.drawable.quick_check_bg);
        refered.setNumber( DashboardRepository.getWomanReferred(dateButton.getText().toString(),dateButton2.getText().toString()));

        homeItems.add(refered);

        HomeItem  dangersigns= new HomeItem();
        dangersigns.setTitle(getString(R.string.mother_with_danger_Signs));
        dangersigns.setType("3");
        dangersigns.setEndDate(dateButton2.getText().toString());
        dangersigns.setStartDate(dateButton.getText().toString());
        dangersigns.setNumber((int) DashboardRepository.getWomanWithDangerSing(dateButton.getText().toString(),dateButton2.getText().toString()));
        dangersigns.setBackground(R.drawable.quick_check_bg);

        homeItems.add(dangersigns);
        HomeItem processed =new HomeItem();
        processed.setTitle(getString(R.string.home_visits));
        processed.setType("4");
        processed.setNumber((int) DashboardRepository.getProcessedVisits(getDate()));
        processed.setBackground(R.drawable.profile_bg);
        homeItems.add(processed);

        return homeItems;

    }
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return   year +"-"+getMonthFormat(month) + "-" + getMonthFormat(day);
    }

    private String getMonthFormat(int month) {

        if(month < 10)
            return "0"+month;
        else
            return String.valueOf(month);
    }
    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date =  year+"-"+getMonthFormat(month)+"-"+getMonthFormat(day);
                dateButton.setText(date);
                attachLayoutContentAdapter();
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
    private void initDatePicker2() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = year+"-"+getMonthFormat(month) + "-" + getMonthFormat(day) ;
                dateButton2.setText(date);
                attachLayoutContentAdapter();
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog2 = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog2.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
    public void openDatePicker2(View view)
    {
        datePickerDialog2.show();
    }
    private class HomeActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.card_layout_home) {
                HomeItem homeItem=(HomeItem) view.getTag();
                Intent intent = new Intent(UkudoxActivity.this, UkudoxDetailsActivity.class);
                intent.putExtra("title",homeItem.getTitle());
                intent.putExtra("number",homeItem.getNumber());
                intent.putExtra("type",homeItem.getType());
                intent.putExtra("start",homeItem.getStartDate());
                intent.putExtra("end",homeItem.getEndDate());
                startActivity(intent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate getDate(){
        LocalDate d= LocalDate.now();
        return d;
    }
}