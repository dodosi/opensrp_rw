package org.smartregister.anc.library.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.contract.RegisterContract;
import org.smartregister.anc.library.interactor.ContactInteractor;
import org.smartregister.anc.library.interactor.ProfileInteractor;
import org.smartregister.anc.library.interactor.RegisterInteractor;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfilePresenter implements ProfileContract.Presenter, RegisterContract.InteractorCallBack {
    private WeakReference<ProfileContract.View> mProfileView;
    private ProfileContract.Interactor mProfileInteractor;
    private RegisterContract.Interactor mRegisterInteractor;
    private ContactInteractor contactInteractor;

    public ProfilePresenter(ProfileContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new ProfileInteractor(this);
        mRegisterInteractor = new RegisterInteractor();
        contactInteractor = new ContactInteractor();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        if (mRegisterInteractor != null) {
            mRegisterInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
            mRegisterInteractor = null;
            contactInteractor = null;
        }

    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        //Overriden
    }

    @Override
    public void onNoUniqueId() {
        getProfileView().displayToast(R.string.no_openmrs_id);
    }

    @Override
    public void setBaseEntityRegister(String baseEntityId) {
        /**
         * Overrriden because the implemented contract requires it.  It can be used to set the base enitty id for a user.
         */
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        this.refreshProfileView(getProfileView().getIntentString(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
        getProfileView().hideProgressDialog();
        getProfileView().displayToast(isEdit ? R.string.registration_info_updated : R.string.new_registration_saved);
    }

    @Override
    public ProfileContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Override
    public void fetchProfileData(String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId, true);
    }

    @Override
    public void refreshProfileView(String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId, false);
    }

    @Override
    public void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences) {
        try {
            String jsonString = data.getStringExtra(ConstantsUtils.IntentKeyUtils.JSON);
            Timber.d(jsonString);
            if (jsonString != null) {
                JSONObject form = new JSONObject(jsonString);
                Log.d("MY_TEST",form.toString());
                getProfileView().showProgressDialog(
                        form.getString(ANCJsonFormUtils.ENCOUNTER_TYPE).equals(ConstantsUtils.EventTypeUtils.CLOSE) ?
                                R.string.removing_dialog_title : R.string.saving_dialog_title);

                if (form.getString(ANCJsonFormUtils.ENCOUNTER_TYPE).equals(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION)) {

                    //--------------------------------Eric code start here() for location----------------------
                   try {


                       JSONObject reg_form = new JSONObject(jsonString);
                       JSONObject step1 = reg_form.getJSONObject("step1");
                       JSONArray feilds = step1.getJSONArray("fields");

                       String province = "";
                       String district = "";
                       String sector = "";
                       String cell = "";
                       for (int i = 0; i < feilds.length(); i++) {


                           if (feilds.getJSONObject(i).getString("key").equals("address_number")) {

                               String[] locationArrays = feilds.getJSONObject(i).getString("value").split(",");
                               province = locationArrays[0].substring(2, locationArrays[0].length() - 1);
                               ;
                               district = locationArrays[1].substring(1, locationArrays[1].length() - 1);
                               sector = locationArrays[2].substring(1, locationArrays[2].length() - 1);
                               cell = locationArrays[3].substring(1, locationArrays[3].length() - 2);
                           }
                           if (feilds.getJSONObject(i).getString("key").equals("province")) {
                               feilds.getJSONObject(i).put("value", province);
                           }

                           if (feilds.getJSONObject(i).getString("key").equals("district")) {
                               feilds.getJSONObject(i).put("value", district);
                           }
                           if (feilds.getJSONObject(i).getString("key").equals("sector")) {
                               feilds.getJSONObject(i).put("value", sector);
                           }
                           if (feilds.getJSONObject(i).getString("key").equals("cell")) {
                               feilds.getJSONObject(i).put("value", cell);
                           }


                       }
                       step1.put("fields", feilds);
                       reg_form.put("step1", step1);
                       jsonString = reg_form.toString();
                   }catch (JSONException ex){

                   }
//                         ---------------- Eric code ends here--------------------

                    Pair<Client, Event> values = ANCJsonFormUtils.processRegistrationForm(allSharedPreferences, jsonString);
                    mRegisterInteractor.saveRegistration(values, jsonString, true, this);
                } else if (form.getString(ANCJsonFormUtils.ENCOUNTER_TYPE).equals(ConstantsUtils.EventTypeUtils.CLOSE)) {
                    mRegisterInteractor.removeWomanFromANCRegister(jsonString, allSharedPreferences.fetchRegisteredANM());
                } else {
                    getProfileView().hideProgressDialog();
                }
            }
        } catch (Exception e) {
            Timber.e(e, " --> processFormDetailsSave");
        }
    }


    @Override
    public void refreshProfileTopSection(Map<String, String> client) {
        if (client != null) {
            getProfileView()
                    .setProfileName(client.get(DBConstantsUtils.KeyUtils.FIRST_NAME) + " " + client.get(DBConstantsUtils.KeyUtils.LAST_NAME));
            getProfileView().setProfileAge(String.valueOf(Utils.getAgeFromDate(client.get(DBConstantsUtils.KeyUtils.DOB))));
            try {
                getProfileView().setProfileGestationAge(
                        client.containsKey(DBConstantsUtils.KeyUtils.EDD) && client.get(DBConstantsUtils.KeyUtils.EDD) != null ?
                                String.valueOf(Utils.getGestationAgeFromEDDate(client.get(DBConstantsUtils.KeyUtils.EDD))) : null);
            } catch (Exception e) {
                getProfileView().setProfileGestationAge("0");
            }
            getProfileView().setProfileID(client.get(DBConstantsUtils.KeyUtils.ANC_ID));
            getProfileView().setProfileImage(client.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID));
            getProfileView().setPhoneNumber(client.get(DBConstantsUtils.KeyUtils.PHONE_NUMBER));
        }
    }

    @Override
    public HashMap<String, String> saveFinishForm(Map<String, String> client, Context context) {
        return contactInteractor.finalizeContactForm(client, context);
    }

    @Override
    public void getTaskCount(String baseEntityId) {
        getProfileView().setTaskCount(mProfileInteractor.getTaskCount(baseEntityId));
    }

    @Override
    public void createContactSummaryPdf() {
        getProfileView().createContactSummaryPdf();
    }
}