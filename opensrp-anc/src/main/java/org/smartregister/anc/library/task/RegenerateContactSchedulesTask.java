package org.smartregister.anc.library.task;

import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.rule.ContactRule;
import org.smartregister.anc.library.sync.BaseAncClientProcessorForJava;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.domain.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class RegenerateContactSchedulesTask extends AsyncTask<Void, Void, String> {

    Activity activity;

    String baseEntityId = "";

    public RegenerateContactSchedulesTask(Activity context, String baseEntityId) {
        this.activity = context;
        this.baseEntityId = baseEntityId;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HashMap<String, String> clientDetails = (HashMap<String, String>) PatientRepository.getWomanProfileDetails(baseEntityId);

        try {
            int contactNo = 1;
            if (clientDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT) != null) {
                contactNo = Integer.parseInt(Objects.requireNonNull(clientDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT))) - 1;
            }

            boolean isFirst = contactNo == 1;
            int gestationAge = Utils.getLastContactGA(clientDetails.get(DBConstantsUtils.KeyUtils.EDD), clientDetails.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE));
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

            LocalDate localDate = new LocalDate(clientDetails.get(DBConstantsUtils.KeyUtils.EDD));
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

            String contactKey = "Contact " + clientDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT);

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

                    BaseAncClientProcessorForJava baseAncClientProcessorForJava = BaseAncClientProcessorForJava.getInstance(activity);
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
            Toast.makeText(activity, R.string.successfully_updated_contact_schedule, Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(activity, R.string.an_error_occurred_while_regenerating_contact_schedule, Toast.LENGTH_LONG).show();
        if(activity instanceof ProfileActivity){
           activity.finish();
        } else activity.recreate();
    }


}