package org.smartregister.anc.library.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.jeasy.rules.api.Facts;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.PreviousContactsDetailsActivity;
import org.smartregister.anc.library.activity.PreviousContactsTestsActivity;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.adapter.LastContactAdapter;
import org.smartregister.anc.library.contract.ProfileFragmentContract;
import org.smartregister.anc.library.domain.ButtonAlertStatus;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.presenter.ProfileFragmentPresenter;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.rule.ContactRule;
import org.smartregister.anc.library.sync.BaseAncClientProcessorForJava;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.FilePathUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.domain.Event;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileContactsFragment extends BaseProfileFragment implements ProfileFragmentContract.View {
    private List<YamlConfigWrapper> lastContactDetails;
    private List<YamlConfigWrapper> lastContactTests;
    private TextView testsHeader;
    private LinearLayout lastContactLayout;
    private LinearLayout testLayout;
    private LinearLayout testsDisplayLayout;
    private final ProfileContactsActionHandler profileContactsActionHandler = new ProfileContactsActionHandler();
    private final ANCJsonFormUtils formUtils = new ANCJsonFormUtils();
    private ProfileFragmentContract.Presenter presenter;
    private String baseEntityId;
    private String contactNo;
    private Button dueButton;
    private ButtonAlertStatus buttonAlertStatus;
    private HashMap<String, String> clientDetails;
    private View noHealthRecordLayout;
    private ScrollView profileContactsLayout;
    private final Utils utils = new Utils();

    public static ProfileContactsFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ProfileContactsFragment fragment = new ProfileContactsFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
    }

    protected void initializePresenter() {
        if (getActivity() == null || getActivity().getIntent() == null) {
            return;
        }
        presenter = new ProfileFragmentPresenter(this);
    }

    @Override
    protected void onCreation() {
        lastContactDetails = new ArrayList<>();
        lastContactTests = new ArrayList<>();
        if (testsDisplayLayout != null) {
            testsDisplayLayout.removeAllViews();
        }
        if (getActivity() != null) {
            if (getActivity().getIntent() != null) {
                clientDetails =
                        (HashMap<String, String>) getActivity().getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
            }

            buttonAlertStatus = Utils.getButtonAlertStatus(clientDetails, getActivity(), true);
            if (clientDetails.get(ConstantsUtils.DATA_MIGRATION_IS_DIRTY) != null && clientDetails.get(ConstantsUtils.DATA_MIGRATION_IS_DIRTY).equals("1")) {
                buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.REGENERATE;
            }

        }
    }

    @Override
    protected void onResumption() {
        lastContactDetails = new ArrayList<>();
        lastContactTests = new ArrayList<>();
        if (testsDisplayLayout != null) {
            testsDisplayLayout.removeAllViews();
        }
        if (getActivity() != null && getActivity().getIntent() != null) {
            baseEntityId = getActivity().getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        }
        setUpAlertStatusButton();
        contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
        populatePreviousContactMissingEssentials(clientDetails);
        initializeLastContactDetails(clientDetails);

        if (lastContactDetails.isEmpty() && lastContactTests.isEmpty()) {
            noHealthRecordLayout.setVisibility(View.VISIBLE);
            profileContactsLayout.setVisibility(View.GONE);
        } else {
            noHealthRecordLayout.setVisibility(View.GONE);
            profileContactsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void populatePreviousContactMissingEssentials(HashMap<String, String> clientDetails) {
        if (clientDetails != null) {
            try {
                List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();
                List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList = new ArrayList<>();

                Facts facts = presenter.getImmediatePreviousContact(clientDetails, baseEntityId, contactNo);
                addOtherRuleObjects(facts);
                addAttentionFlagsRuleObjects(facts);
                contactNo = (String) facts.asMap().get(ConstantsUtils.CONTACT_NO);

                addTestsRuleObjects(facts);

                String contactDate = (String) facts.asMap().get(ConstantsUtils.CONTACT_DATE);
                String displayContactDate = "";
                if (!TextUtils.isEmpty(contactDate)) {
                    Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(contactDate);
                    displayContactDate = new SimpleDateFormat("dd MMM " + "yyyy", Locale.getDefault())
                            .format(lastContactDate);
                }


                if (lastContactDetails.isEmpty()) {
                    lastContactLayout.setVisibility(View.GONE);
                } else {
                    lastContactDetailsWrapperList
                            .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactDetails, facts));
                    setUpContactDetailsRecycler(lastContactDetailsWrapperList);
                }

                if (lastContactTests.isEmpty()) {
                    testLayout.setVisibility(View.GONE);
                } else {
                    lastContactDetailsTestsWrapperList
                            .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactTests, facts));
                    testsHeader.setText(
                            String.format(getActivity().getResources().getString(R.string.recent_test), displayContactDate));
                    setUpContactTestsDetails(lastContactDetailsTestsWrapperList);
                }

            } catch (Exception e) {
                Timber.e(e, " --> initializeLastContactDetails");
            }
        }
    }

    private void setUpAlertStatusButton() {
        Utils.processButtonAlertStatus(getActivity(), dueButton, buttonAlertStatus);
        if (buttonAlertStatus.buttonAlertStatus.equals(ConstantsUtils.AlertStatusUtils.REGENERATE)) {
            dueButton.setOnClickListener(view -> {
                Toast.makeText(getActivity(), "Regenerating Contact Schedule", Toast.LENGTH_SHORT).show();
                new RegenerateContactSchedulesTask(clientDetails, getActivity()).execute();
            });
        }
//        buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.REGENERATE;
    }

    private void initializeLastContactDetails(HashMap<String, String> clientDetails) {
        if (clientDetails != null) {

            List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();
            List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList = new ArrayList<>();
            Facts facts = presenter.getImmediatePreviousContact(clientDetails, baseEntityId, contactNo);
            try {
                addOtherRuleObjects(facts);
                addAttentionFlagsRuleObjects(facts);
                contactNo = (String) facts.asMap().get(ConstantsUtils.CONTACT_NO);

                addTestsRuleObjects(facts);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String displayContactDate = "";

            // If no visit date, try to get contact date
            String contactDate = (String) facts.asMap().get(ConstantsUtils.CONTACT_DATE);
            if (!TextUtils.isEmpty(contactDate)) {
                // If contact date exists, parse and format for display
                try {
                    Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(contactDate);
                    displayContactDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(lastContactDate);
                } catch (ParseException e) {
                    // Handle parsing exceptions
                    throw new RuntimeException(e);
                }

            }

            if (lastContactDetails.isEmpty()) {
                lastContactLayout.setVisibility(View.GONE);
            } else {
                lastContactDetailsWrapperList
                        .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactDetails, facts));
                setUpContactDetailsRecycler(lastContactDetailsWrapperList);
            }

            if (lastContactTests.isEmpty()) {
                testLayout.setVisibility(View.GONE);
            } else {
                lastContactDetailsTestsWrapperList
                        .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactTests, facts));
                testsHeader.setText(
                        String.format(getActivity().getResources().getString(R.string.recent_test), displayContactDate));
                setUpContactTestsDetails(lastContactDetailsTestsWrapperList);
            }
        }
    }

    private void addOtherRuleObjects(Facts facts) throws IOException {
        Iterable<Object> ruleObjects = utils.loadRulesFiles(FilePathUtils.FileUtils.PROFILE_LAST_CONTACT);

        for (Object ruleObject : ruleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;
            YamlConfig yamlConfig = (YamlConfig) ruleObject;

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            for (YamlConfigItem configItem : configItems) {
                if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, configItem.getRelevance())) {
                    yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                    valueCount += 1;
                }
            }

            if (valueCount > 0) {
                lastContactDetails.addAll(yamlConfigList);
            }
        }
    }

    private void addAttentionFlagsRuleObjects(Facts facts) throws IOException {
        Iterable<Object> attentionFlagsRuleObjects = AncLibrary.getInstance().readYaml(FilePathUtils.FileUtils.ATTENTION_FLAGS);

        for (Object ruleObject : attentionFlagsRuleObjects) {
            YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;
            for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {

                if (AncLibrary.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {
                    lastContactDetails.add(new YamlConfigWrapper(null, null, yamlConfigItem));

                }

            }
        }
    }

    private void addTestsRuleObjects(Facts facts) throws IOException {
        Iterable<Object> testsRuleObjects = AncLibrary.getInstance()
                .readYaml(FilePathUtils.FileUtils.PROFILE_TAB_PREVIOUS_CONTACT_TEST);

        for (Object ruleObject : testsRuleObjects) {
            YamlConfig testsConfig = (YamlConfig) ruleObject;
            for (YamlConfigItem yamlConfigItem : testsConfig.getFields()) {

                if (AncLibrary.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {
                    lastContactTests.add(new YamlConfigWrapper(null, null, yamlConfigItem));

                }

            }
        }
    }

    private void setUpContactDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsWrappers) {
        LastContactAdapter adapter = new LastContactAdapter(lastContactDetailsWrappers, getActivity());
        adapter.notifyDataSetChanged();
        RecyclerView recyclerView = lastContactLayout.findViewById(R.id.last_contact_information);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void setUpContactTestsDetails(List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList) {
        List<YamlConfigWrapper> data = new ArrayList<>();
        Facts facts = new Facts();
        if (lastContactDetailsTestsWrapperList.size() > 0) {
            for (int i = 0; i < lastContactDetailsTestsWrapperList.size(); i++) {
                LastContactDetailsWrapper lastContactDetailsTest = lastContactDetailsTestsWrapperList.get(i);
                data = lastContactDetailsTest.getExtraInformation();
                facts = lastContactDetailsTest.getFacts();
            }
        }

        populateTestDetails(data, facts);
    }

    private void populateTestDetails(List<YamlConfigWrapper> data, Facts facts) {
        if (data != null && data.size() > 0) {
            for (int position = 0; position < data.size(); position++) {
                if (data.get(position).getYamlConfigItem() != null) {
                    ConstraintLayout constraintLayout = formUtils.createListViewItems(data, facts, position, getActivity());
                    testsDisplayLayout.addView(constraintLayout);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile_contacts, container, false);
        lastContactLayout = fragmentView.findViewById(R.id.last_contact_layout);
        TextView lastContactBottom = lastContactLayout.findViewById(R.id.last_contact_bottom);
        lastContactBottom.setOnClickListener(profileContactsActionHandler);

        testLayout = fragmentView.findViewById(R.id.test_layout);
        testsHeader = testLayout.findViewById(R.id.tests_header);
        TextView testsBottom = testLayout.findViewById(R.id.tests_bottom);
        testsBottom.setOnClickListener(profileContactsActionHandler);

        testsDisplayLayout = testLayout.findViewById(R.id.test_display_layout);

        noHealthRecordLayout = fragmentView.findViewById(R.id.no_health_data_recorded_layout);
        profileContactsLayout = fragmentView.findViewById(R.id.profile_contacts_layout);

        dueButton = ((ProfileActivity) getActivity()).getDueButton();
//        if (!ConstantsUtils.AlertStatusUtils.TODAY.equals(buttonAlertStatus.buttonAlertStatus)) {
        dueButton.setOnClickListener((ProfileActivity) getActivity());
//        } else {
        dueButton.setEnabled(true);
//        }

        return fragmentView;
    }

    private void goToPreviousContacts() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), PreviousContactsDetailsActivity.class);
            String baseEntityId = getActivity().getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
            intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, baseEntityId);
            intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP,
                    getActivity().getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP));

            this.startActivity(intent);
        }
    }

    private void goToPreviousContactsTests() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), PreviousContactsTestsActivity.class);
            String baseEntityId = getActivity().getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
            intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, baseEntityId);
            intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP,
                    getActivity().getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP));

            this.startActivity(intent);
        }
    }

    @Override
    public void setContactTasks(List<Task> contactTasks) {
        // Implement here
    }

    @Override
    public void updateTask(Task task) {
        // Implement here
    }

    @Override
    public void refreshTasksList(boolean refresh) {
        // Implement here
    }

    /**
     * Handles the Click actions on any of the section in the page.
     */
    private class ProfileContactsActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.last_contact_bottom && !lastContactDetails.isEmpty()) {
                goToPreviousContacts();
            } else if (view.getId() == R.id.tests_bottom && !lastContactTests.isEmpty()) {
                goToPreviousContactsTests();
            }
        }

    }

    public class RegenerateContactSchedulesTask extends AsyncTask<Void, Void, String> {
        HashMap<String, String> details;
        Activity activity;

        RegenerateContactSchedulesTask(HashMap<String, String> details, Activity context) {
            this.details = details;
            this.activity = context;
        }

        @Override
        protected String doInBackground(Void... voids) {


            try {
                int contactNo = 1;
                if (details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT) != null) {
                    contactNo = Integer.parseInt(Objects.requireNonNull(details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT))) - 1;
                }

                boolean isFirst = contactNo == 1;
                int gestationAge = Utils.getLastContactGA(details.get(DBConstantsUtils.KeyUtils.EDD), details.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE));
                ContactRule contactRule = new ContactRule(gestationAge, isFirst, baseEntityId);


                List<Integer> integerList = AncLibrary.getInstance().getAncRulesEngineHelper()
                        .getContactVisitSchedule(contactRule, ConstantsUtils.RulesFileUtils.CONTACT_RULES);

                int nextContactVisitWeeks = integerList.get(0);

                JSONObject contactScheduleObject = new JSONObject();
                contactScheduleObject.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, integerList);
                AncLibrary.getInstance().getDetailsRepository().add(baseEntityId, ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, contactScheduleObject.toString(),
                        Calendar.getInstance().getTimeInMillis());

                ContentValues ecClientsCv = new ContentValues();
                ContentValues ecMotherDetailsCv = new ContentValues();
                ecClientsCv.put(ConstantsUtils.DATA_MIGRATION_IS_DIRTY, "0");
                PatientRepository.updatePatient(baseEntityId, ecClientsCv, DBConstantsUtils.RegisterTable.DEMOGRAPHIC);

                LocalDate localDate = new LocalDate(details.get(DBConstantsUtils.KeyUtils.EDD));
                String nextContactVisitDate =
                        localDate.minusWeeks(ConstantsUtils.DELIVERY_DATE_WEEKS).plusWeeks(nextContactVisitWeeks).toString();
                ecMotherDetailsCv.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, nextContactVisitDate);

                PatientRepository.updatePatient(baseEntityId, ecMotherDetailsCv, DBConstantsUtils.RegisterTable.DETAILS);
                JSONObject eventsForBaseEntityId = AncLibrary.getInstance().getEventClientRepository().getEventsByBaseEntityId(baseEntityId);


                JSONArray jsonArray = eventsForBaseEntityId.getJSONArray("events");
                int bound = eventsForBaseEntityId.getJSONArray("events").length();
                List<JSONObject> visits = new ArrayList<>();
                for (int i = 0; i < bound; i++) {
                    JSONObject eventObject = jsonArray.getJSONObject(i);
                    if ("Contact Visit".equals(eventObject.optString("eventType"))) {
                        visits.add(eventObject);
                    }
                }

                String contactKey = "Contact " + details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT);

                for (int j = 0; j < visits.size(); j++) {
                    JSONObject details = visits.get(j).getJSONObject("details");
                    if (details.get("Contact").equals(contactKey)) {
                        JSONObject visitToUpdate = visits.get(j);
                        // remove details since it is causing errors during deserialization
                        visitToUpdate.remove("details");
                        ObjectMapper objectMapper = new ObjectMapper();
                        // Register JodaModule
                        objectMapper.registerModule(new JodaModule());
                        Event visitEvent = objectMapper.readValue(visitToUpdate.toString(), Event.class);
                        JSONObject previousContact = new JSONObject(details.get("previous_contacts").toString());
                        previousContact.put("contact_schedule", contactScheduleObject.toString());
                        details.put("previous_contacts", previousContact.toString());

                        Map<String, String> eventDetails = new HashMap<>();
                        Iterator<?> keys = details.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            eventDetails.put(key, (String) details.get(key));
                        }
                        visitEvent.setDetails(eventDetails);

                        BaseAncClientProcessorForJava baseAncClientProcessorForJava = BaseAncClientProcessorForJava.getInstance(getActivity());
                        Method processVisitMethod = baseAncClientProcessorForJava.getClass().getDeclaredMethod("processVisit", Event.class);
                        processVisitMethod.setAccessible(true);

                        processVisitMethod.invoke(baseAncClientProcessorForJava, visitEvent);
                        // mark event as UnSynced and Invalid so that it can be uploaded in the next sync cycle
                        AncLibrary.getInstance().getEventClientRepository().markEventValidationStatus(visitEvent.getFormSubmissionId(), false);
                        break;
                    }
                }

                return "success";
            } catch (JSONException | NoSuchMethodException | JsonProcessingException |
                     InvocationTargetException | IllegalAccessException e) {
                Timber.e(e);
                return "failure";
            }


        }

        @Override
        protected void onPostExecute(String message) {
            if (message.equals("success")) {
                    Toast.makeText(activity, R.string.successfully_updated_contact_schedule,    Toast.LENGTH_LONG ).show();
            } else Toast.makeText(activity, R.string.an_error_occurred_while_regenerating_contact_schedule, Toast.LENGTH_LONG ).show();
            activity.finish();
        }


    }
}
