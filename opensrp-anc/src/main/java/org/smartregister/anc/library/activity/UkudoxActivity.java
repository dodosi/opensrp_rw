package org.smartregister.anc.library.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import org.smartregister.anc.library.util.ConstantsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UkudoxActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    private RecyclerView contentLayout;
    Button go;
    private DatePickerDialog datePickerDialog;
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
    @Override
    public void onResume() {
        super.onResume();
        attachLayoutContentAdapter();
    }
    private void setUpViews() {
        mToolbar = findViewById(R.id.library_toolbar);
        contentLayout = findViewById(R.id.layout_attach_recycler_view);
        initDatePicker();
        initDatePicker2();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        dateButton2 = findViewById(R.id.datePickerButton2);
        dateButton2.setText(getTodaysDate());
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
    private  List<HomeItem> getHomeContent(){
        List<HomeItem> homeItems= new ArrayList<>();

        HomeItem dueContactToday=new HomeItem();
        dueContactToday.setTitle(getString(R.string.due_contact_today));
        dueContactToday.setNumber(23);
        dueContactToday.setBackground(R.drawable.profile_bg);

        homeItems.add(dueContactToday);

        HomeItem refered= new HomeItem();
        refered.setTitle(getString(R.string.women_referred));
        refered.setBackground(R.drawable.quick_check_bg);
        refered.setNumber(0);

        homeItems.add(refered);

        HomeItem  dangersigns= new HomeItem();
        dangersigns.setTitle(getString(R.string.mother_with_danger_Signs));
        dangersigns.setNumber(0);
        dangersigns.setBackground(R.drawable.quick_check_bg);

        homeItems.add(dangersigns);
        HomeItem processed =new HomeItem();
        processed.setTitle(getString(R.string.home_visits));
        processed.setNumber(6);
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
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {

        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = getMonthFormat(month) + " " + day + " " + year;
                dateButton.setText(date);
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
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = getMonthFormat(month) + " " + day + " " + year;
                dateButton2.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = sdf.parse(dateButton.getText().toString());
//        long millis = date.getTime();
//        datePickerDialog.getDatePicker().setMinDate(millis);
    }
    public void openDatePicker2(View view)
    {
        datePickerDialog.show();
    }
    private class HomeActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.card_layout_home) {
                Toast.makeText(UkudoxActivity.this, "High Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UkudoxActivity.this, UkudoxDetailsActivity.class);
                intent.putExtra(ConstantsUtils.IntentKeyUtils.LIBRARY_HEADER, "headerText");
                startActivity(intent);
            }
        }
    }
}