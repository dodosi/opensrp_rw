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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpViews();
        }
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
        HomeAdapter libraryContentAdapter = new HomeAdapter( UkudoxActivity.this,getHomeContent(),new HomeActionHandler());
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


//
//        HomeItem dueContactToday=new HomeItem();
//        dueContactToday.setTitle(getString(R.string.due_contact_today));
//        dueContactToday.setType("1");
//        dueContactToday.setNumber((int) DashboardRepository.getDueContactDash(getDate()));
//        dueContactToday.setBackground(getResources().getColor(R.color.vaccine_blue_bg_stk));
//
//        homeItems.add(dueContactToday);

        HomeItem expected_deliveries=new HomeItem();
        expected_deliveries.setTitle(getString(R.string.expected_deliveries));
        expected_deliveries.setType("0");

        expected_deliveries.setEndDate(dateButton2.getText().toString());
        expected_deliveries.setStartDate(dateButton.getText().toString());
        expected_deliveries.setNumber(0);
       // expected_deliveries.setNumber((int) DashboardRepository.getExpectedDeliveries(dateButton.getText().toString(),dateButton2.getText().toString()));
        expected_deliveries.setBackground(getResources().getColor(R.color.vaccine_blue_bg_stk));

        homeItems.add(expected_deliveries);

        HomeItem processed =new HomeItem();
        processed.setTitle(getString(R.string.home_visits));
        processed.setType("4");
        processed.setEndDate(dateButton2.getText().toString());
        processed.setStartDate(dateButton.getText().toString());
        processed.setNumber((int) DashboardRepository.getProcessedVisits(dateButton.getText().toString(),dateButton2.getText().toString()));
        processed.setBackground(getResources().getColor(R.color.vaccine_blue_bg_stk));
        homeItems.add(processed);

        HomeItem refered= new HomeItem();
        refered.setTitle(getString(R.string.women_referred));
        refered.setType("2");
        refered.setEndDate(dateButton2.getText().toString());
        refered.setStartDate(dateButton.getText().toString());
        refered.setBackground(getResources().getColor(R.color.refer_close_red));
        refered.setNumber( DashboardRepository.getWomanReferred(dateButton.getText().toString(),dateButton2.getText().toString()));

        homeItems.add(refered);
        HomeItem rprpositive  =new HomeItem();
        rprpositive.setTitle(getString(R.string.rprpositive));
        rprpositive.setType("11");
        rprpositive.setEndDate(dateButton2.getText().toString());
        rprpositive.setStartDate(dateButton.getText().toString());
        rprpositive.setBackground(getResources().getColor(R.color.refer_close_red));
        rprpositive.setNumber( DashboardRepository.getWomanWithSyphilisPositive(dateButton.getText().toString(),dateButton2.getText().toString()));
        homeItems.add(rprpositive);

        HomeItem  dangersigns= new HomeItem();
        dangersigns.setTitle(getString(R.string.mother_with_danger_Signs));
        dangersigns.setType("3");
        dangersigns.setEndDate(dateButton2.getText().toString());
        dangersigns.setStartDate(dateButton.getText().toString());
        dangersigns.setNumber((int) DashboardRepository.getWomanWithDangerSing(dateButton.getText().toString(),dateButton2.getText().toString()));
        dangersigns.setBackground(getResources().getColor(R.color.refer_close_red));

        homeItems.add(dangersigns);
        HomeItem late_visits=new HomeItem();
        late_visits.setTitle(getString(R.string.late_visits));
        late_visits.setType("12");
        late_visits.setEndDate(dateButton2.getText().toString());
        late_visits.setStartDate(dateButton.getText().toString());
        late_visits.setBackground(getResources().getColor(R.color.vaccine_yellow));
        late_visits.setNumber(DashboardRepository.getWomanLateVisits(getDate()));
        homeItems.add(late_visits);



        HomeItem received_vaccination=new HomeItem();
        received_vaccination.setTitle(getString(R.string.received_vaccination));
        received_vaccination.setType("6");
        received_vaccination.setEndDate(dateButton2.getText().toString());
        received_vaccination.setStartDate(dateButton.getText().toString());
        received_vaccination.setBackground(getResources().getColor(R.color.vaccine_blue_bg_stk));
        received_vaccination.setNumber( DashboardRepository.getWomanVaccinated(dateButton.getText().toString(),dateButton2.getText().toString()));
        homeItems.add(received_vaccination);

        HomeItem received_deworming_pills =new HomeItem();
        received_deworming_pills.setTitle(getString(R.string.received_deworming_pills));
        received_deworming_pills.setType("7");
        received_deworming_pills.setEndDate(dateButton2.getText().toString());
        received_deworming_pills.setStartDate(dateButton.getText().toString());
        received_deworming_pills.setBackground(getResources().getColor(R.color.vaccine_blue_bg_stk));
        received_deworming_pills.setNumber( DashboardRepository.getWomanReceivedDewormingPills(dateButton.getText().toString(),dateButton2.getText().toString()));
        homeItems.add(received_deworming_pills);

        HomeItem accompanied_by_p=new HomeItem();
        accompanied_by_p.setTitle(getString(R.string.accompanied_by_their_partners));
        accompanied_by_p.setType("5");
        accompanied_by_p.setEndDate(dateButton2.getText().toString());
        accompanied_by_p.setStartDate(dateButton.getText().toString());
        accompanied_by_p.setBackground(getResources().getColor(R.color.contact_counselling_navigation));
        accompanied_by_p.setNumber( DashboardRepository.getWomanAccompaniedWithPartner(dateButton.getText().toString(),dateButton2.getText().toString()));
        homeItems.add(accompanied_by_p);

        HomeItem teenage_pregnancies  =new HomeItem();
        teenage_pregnancies.setTitle(getString(R.string.teenage_pregnancies));
        teenage_pregnancies.setType("8");
        teenage_pregnancies.setEndDate(dateButton2.getText().toString());
        teenage_pregnancies.setStartDate(dateButton.getText().toString());
        teenage_pregnancies.setBackground(getResources().getColor(R.color.vaccine_yellow));
        teenage_pregnancies.setNumber( DashboardRepository.getWomanInParticularAge(dateButton.getText().toString(),dateButton2.getText().toString(), 19));
        homeItems.add(teenage_pregnancies);

        HomeItem young_age_pregnancy  =new HomeItem();
        young_age_pregnancy.setTitle(getString(R.string.young_age_pregnancy));
        young_age_pregnancy.setType("9");
        young_age_pregnancy.setEndDate(dateButton2.getText().toString());
        young_age_pregnancy.setStartDate(dateButton.getText().toString());
        young_age_pregnancy.setBackground(getResources().getColor(R.color.contact_counselling_navigation));
        young_age_pregnancy.setNumber( DashboardRepository.getWomanInParticularAge(dateButton.getText().toString(),dateButton2.getText().toString(),30));
        homeItems.add(young_age_pregnancy);

        HomeItem old_age_pregnancy  =new HomeItem();
        old_age_pregnancy.setTitle(getString(R.string.old_age_pregnancy));
        old_age_pregnancy.setType("10");
        old_age_pregnancy.setEndDate(dateButton2.getText().toString());
        old_age_pregnancy.setStartDate(dateButton.getText().toString());
        old_age_pregnancy.setBackground(getResources().getColor(R.color.vaccine_yellow));
        old_age_pregnancy.setNumber( DashboardRepository.getWomanInParticularAge(dateButton.getText().toString(),dateButton2.getText().toString(),49));
        homeItems.add(old_age_pregnancy);

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
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

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
//        datePickerDialog2.getDatePicker().setMaxDate(System.currentTimeMillis());

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