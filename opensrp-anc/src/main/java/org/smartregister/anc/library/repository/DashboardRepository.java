package org.smartregister.anc.library.repository;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.custom.CustomClient;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;


public class DashboardRepository extends BaseRepository {

    public static final String TABLE_PREVIOUS_CONTACT = "previous_contact";
    public static final String TABLE_EC_CLIENT = "ec_client";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    private static final String[] projection = getRegisterQueryProvider().mainColumns();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getDueContactDash(LocalDate datetoday) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

//            String query = "SELECT COUNT(*) FROM " + getRegisterQueryProvider().getDetailsTable() + " WHERE " + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE+ "= '"+datetoday+"'";
//        SQLiteStatement statement = db.compileStatement(query);
//        long count = statement.simpleQueryForLong();
//        return count;
        String query = "SELECT * FROM " + getRegisterQueryProvider().getDetailsTable();
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        while (cursor.moveToNext()) {
            String next_contact_date = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE));


            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
            LocalDate d1 = null;
            if (next_contact_date.charAt(0) == '-') {
                d1 = LocalDate.parse(next_contact_date.substring(1), df);
            } else {
                d1 = LocalDate.parse(next_contact_date, df);
            }

//            if(datetoday.isEqual(d1) || datetoday.isAfter(d1)) {
            if (datetoday.isEqual(d1)) {
                if (!isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                    count++;
                }
            } else {

            }


        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getWomanLateVisits(LocalDate datetoday) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

//            String query = "SELECT COUNT(*) FROM " + getRegisterQueryProvider().getDetailsTable() + " WHERE " + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE+ "= '"+datetoday+"'";
//        SQLiteStatement statement = db.compileStatement(query);
//        long count = statement.simpleQueryForLong();
//        return count;
        String query = "SELECT * FROM " + getRegisterQueryProvider().getDetailsTable();
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        while (cursor.moveToNext()) {
            try {
                String next_contact_date = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE));


                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
                LocalDate d1 = null;
                if (next_contact_date.charAt(0) == '-') {
                    d1 = LocalDate.parse(next_contact_date.substring(1), df);
                } else {
                    d1 = LocalDate.parse(next_contact_date, df);
                }

                if (datetoday.isAfter(d1)) {
//            if(datetoday.isEqual(d1)) {
                    if (!isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                        count++;
                    }
                } else {

                }
            } catch (DateTimeParseException e) {
                Timber.e(e);
            }


        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getExpectedDeliveries(String dateStart, String dateEnd) {
        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + getRegisterQueryProvider().getDetailsTable();
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            String edd = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.EDD));

            if (!"0".equals(edd) && edd != null) {
                LocalDate d = LocalDate.parse(edd);
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (!isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                        count++;
                    }
                }
            }


        }
        return count;
    }

    private static boolean isAnc_Closed(String base_entity_id) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_EC_CLIENT + " WHERE base_entity_id='" + base_entity_id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("date_removed")) != null) {
                return true;
            }

        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getWomanReferred(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isReferred(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getWomanVaccinated(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isVaccinatedToday(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    private static boolean isVaccinatedToday(String woman_id) {
        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT + " WHERE base_entity_id= '" + woman_id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            try {
                if (cursor.getString(3).equals("tt1_date")) {


                    JSONObject val = new JSONObject(cursor.getString(4));

                    if (val.get(VALUE).toString().equals("done_today")) {

                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getWomanAccompaniedWithPartner(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isAccompanied(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getWomanReceivedDewormingPills(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isDewormingPerfomed(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    private static boolean isDewormingPerfomed(String woman_id) {
        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT + " WHERE base_entity_id= '" + woman_id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            try {
                if (cursor.getString(3).equals("deworming_performed")) {


                    JSONObject val = new JSONObject(cursor.getString(4));

                    if (val.get(VALUE).toString().equals("yes")) {

                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private static boolean isAccompanied(String woman_id) {
        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT + " WHERE base_entity_id= '" + woman_id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            try {
                if (cursor.getString(3).equals("accompanied_by_partner")) {


                    JSONObject val = new JSONObject(cursor.getString(4));

                    if (val.get(VALUE).toString().equals("yes")) {

                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private static boolean isReferred(String woman_id) {
        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT + " WHERE base_entity_id= '" + woman_id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            try {
                if (cursor.getString(3).equals("referred_hosp")) {


                    JSONObject val = new JSONObject(cursor.getString(4));

                    if (val.get(VALUE).toString().equals("yes")) {

                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long getProcessedVisits(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

//        String query = "SELECT COUNT(*) FROM " + TABLE_PREVIOUS_CONTACT + " WHERE " + KEY + "= 'contact_date' AND " +VALUE+ "='"+datet+"'";
//        Cursor   cursor = db.rawQuery(query, null);
//        SQLiteStatement statement = db.compileStatement(query);
//        long count = statement.simpleQueryForLong();
        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (!isAnc_Closed(cursor.getString(2))) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getWomanWithDangerSing(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (hasDangerSign(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    public static boolean hasDangerSign(String woman_id) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + getRegisterQueryProvider().getDetailsTable() + " WHERE id= '" + woman_id + "'";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String r = cursor.getString(18);
            if (!"0".equals(r) && r != null) {
                return true;
            } else {
                return false;
            }

        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getWomanWithSyphilisPositive(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isSyphilisPositive(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    private static boolean isSyphilisPositive(String woman_id) {
        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT + " WHERE base_entity_id= '" + woman_id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {

            if (cursor.getString(3).equals("syphilis_positive") && cursor.getString(4).equals("1")) {
                return true;
            }


        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getWomanInParticularAge(String dateStart, String dateEnd, int age) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM ec_details";
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            Long dt = Long.parseLong(cursor.getString(cursor.getColumnIndex("event_date")));

            DateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
            Date result = new Date(dt);
            LocalDate d = LocalDate.parse(simple.format(result));

            if (d.isAfter(start) && d.isBefore(end)) {

                if (cursor.getString(1).equals("age_calculated")) {
                    int ag = getWomanAge(cursor.getString(cursor.getColumnIndex("base_entity_id")));
                    Log.i("TEST1", String.valueOf(ag) + "getWomanInParticularAge: " + cursor.getString(cursor.getColumnIndex("base_entity_id")));
                    if (age == 19) {
                        if (ag < 20 && !isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                            count++;
                        }
                    } else if (age == 30) {

                        if (ag >= 20 && ag < 30 && !isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                            count++;
                        }

                    } else {
                        if (ag >= 30 && !isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                            count++;
                        }
                    }
                }


            }

        }
        return count;
    }

    protected static Repository getMasterRepository() {
        return DrishtiApplication.getInstance().getRepository();
    }

    private static RegisterQueryProvider getRegisterQueryProvider() {
        return AncLibrary.getInstance().getRegisterQueryProvider();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getWomanProfileDetails(LocalDate datetoday) {
        Cursor cursor = null;
        List<CustomClient> clientList = new ArrayList<>();

        try {
            SQLiteDatabase db = getMasterRepository().getReadableDatabase();

            String query =
                    "SELECT " + StringUtils.join(projection, ",") + " FROM " + getRegisterQueryProvider().getDemographicTable() + " join " + getRegisterQueryProvider().getDetailsTable() +
                            " on " + getRegisterQueryProvider().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = " + getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID;
//                            + " WHERE " + getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE + " = ?";
//            cursor = db.rawQuery(query, new String[]{baseEntityId});??
            cursor = db.rawQuery(query, null);

            if (cursor != null) {

                while (cursor.moveToNext()) {
                    CustomClient client = new CustomClient();
                    String next_contact_date = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE));
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
                    LocalDate d1 = null;
                    if (next_contact_date.charAt(0) == '-') {
                        d1 = LocalDate.parse(next_contact_date.substring(1), df);
                    } else {
                        d1 = LocalDate.parse(next_contact_date, df);
                    }

//                    if(datetoday.isEqual(d1) || datetoday.isAfter(d1)) {
                    if (datetoday.isAfter(d1)) {
                        if (!isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {


                            client.setFirstName(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.FIRST_NAME)));
                            client.setLastName(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.LAST_NAME)));
                            client.setAge(String.valueOf(calculateAge(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.DOB)))));
                            client.setGa(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.EDD)));
                            client.setRegistrationId(cursor.getString(cursor.getColumnIndex("register_id")));
                            client.setNextContactDate(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE)));
                            String r = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT));
                            String red = r == null ? "0" : r;
                            String y = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT));
                            String yellow = y == null ? "0" : y;
                            client.setAttentionFlag(Integer.parseInt(red) + Integer.parseInt(yellow));
                            client.setBaseIntityId(cursor.getString(cursor.getColumnIndex("base_entity_id")));
                            clientList.add(client);
                        }
                    } else {
                    }

                }
            }

            return clientList;
        } catch (Exception e) {
            Timber.e(e, "%s ==> getWomanProfileDetails()", PatientRepository.class.getCanonicalName());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getProcessedVisitsDetails(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

//        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT + " WHERE " + KEY + "= 'contact_date' AND " +VALUE+ "='"+datet+"'";
//        Cursor   cursor = db.rawQuery(query, null);
        List<CustomClient> clientList = new ArrayList<>();
//
//        while (cursor.moveToNext()){
//           clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
//        }
        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (!isAnc_Closed(cursor.getString(2))) {
                        clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                    }
                }
            }

        }

        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getWomanReferredDetails(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        List<CustomClient> clientList = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isReferred(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                    }
                }
            }

        }
        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getWomanWithDangerSingDetails(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        List<CustomClient> clientList = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (hasDangerSign(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {

                        clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                    }
                }
            }

        }
        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getWomanAccompaniedWithPartnerDetails(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        List<CustomClient> clientList = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isAccompanied(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                    }
                }
            }

        }
        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getWomanReceivedDewormingPillsDetails(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        List<CustomClient> clientList = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isDewormingPerfomed(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                    }
                }
            }

        }
        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getWomanInParticularAgeDetails(String dateStart, String dateEnd, int age) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM ec_details";
        Cursor cursor = db.rawQuery(query, null);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);

        List<CustomClient> clientList = new ArrayList<>();
        LocalDate end = LocalDate.parse(dateEnd, df);
        while (cursor.moveToNext()) {
            Long dt = Long.parseLong(cursor.getString(cursor.getColumnIndex("event_date")));

            DateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
            Date result = new Date(dt);
            LocalDate d = LocalDate.parse(simple.format(result));
            if (d.isAfter(start) && d.isBefore(end)) {
                if (cursor.getString(1).equals("age_calculated")) {
                    int ag = getWomanAge(cursor.getString(cursor.getColumnIndex("base_entity_id")));
                    Log.i("TEST1", String.valueOf(ag) + "getWomanInParticularAge: " + cursor.getString(cursor.getColumnIndex("base_entity_id")));
                    if (age == 19) {
                        if (ag < 20 && !isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                            clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                        }
                    } else if (age == 30) {

                        if (ag >= 20 && ag < 30 && !isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                            clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                        }

                    } else {
                        if (ag >= 30 && !isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {
                            clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                        }
                    }
                }


            }

        }
        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getWomanVaccinatedDetails(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        List<CustomClient> clientList = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isVaccinatedToday(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                    }
                }
            }

        }
        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getWomanWithSyphilisPositiveDetails(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        List<CustomClient> clientList = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("contact_date")) {
                LocalDate d = LocalDate.parse(cursor.getString(4));
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (isSyphilisPositive(cursor.getString(2)) && !isAnc_Closed(cursor.getString(2))) {
                        clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                    }
                }
            }

        }
        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CustomClient> getExpectedDeliveriesDetails(String dateStart, String dateEnd) {
        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + getRegisterQueryProvider().getDetailsTable();
        Cursor cursor = db.rawQuery(query, null);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate start = LocalDate.parse(dateStart, df);
        LocalDate end = LocalDate.parse(dateEnd, df);
        Log.i("TESTing", "getExpectedDeliveriesDetails: " + start);
        Log.i("TESTing", "getExpectedDeliveriesDetails: " + end);
        List<CustomClient> clientList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String edd = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.EDD));

            if (!"0".equals(edd) && edd != null) {
                LocalDate d = LocalDate.parse(edd);
                if (d.isAfter(start) && d.isBefore(end)) {
                    if (!isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {

                        clientList.add(getDetailsList(cursor.getString(cursor.getColumnIndex("base_entity_id"))));
                    }
                }
            }


        }
        return clientList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int getWomanAge(String base_entity_id) {
        SQLiteDatabase db = getMasterRepository().getReadableDatabase();
        int age = 25;
        String query = "SELECT * FROM " + TABLE_EC_CLIENT + " WHERE base_entity_id='" + base_entity_id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            age = calculateAge(cursor.getString(cursor.getColumnIndex("dob")));

        }

        return age;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static CustomClient getDetailsList(String baseEntityId) {
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getMasterRepository().getReadableDatabase();

            String query =
                    "SELECT " + StringUtils.join(projection, ",") + " FROM " + getRegisterQueryProvider().getDemographicTable() + " join " + getRegisterQueryProvider().getDetailsTable() +
                            " on " + getRegisterQueryProvider().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = " + getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " WHERE " +
                            getRegisterQueryProvider().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{baseEntityId});
            CustomClient client = new CustomClient();
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    if (!isAnc_Closed(cursor.getString(cursor.getColumnIndex("base_entity_id")))) {

                        client.setFirstName(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.FIRST_NAME)));
                        client.setLastName(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.LAST_NAME)));
                        client.setAge(String.valueOf(calculateAge(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.DOB)))));
                        client.setGa(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.EDD)));
                        client.setRegistrationId(cursor.getString(cursor.getColumnIndex("register_id")));
                        client.setNextContactDate(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE)));
                        String r = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT));
                        String red = r == null ? "0" : r;
                        String y = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT));
                        String yellow = y == null ? "0" : y;
                        client.setAttentionFlag(Integer.parseInt(red) + Integer.parseInt(yellow));
                        client.setBaseIntityId(cursor.getString(cursor.getColumnIndex("base_entity_id")));


                    }

                }
            }

            return client;
        } catch (Exception e) {
            Timber.e(e, "%s ==> getWomanProfileDetails()", PatientRepository.class.getCanonicalName());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int calculateAge(String birthDate) {
        LocalDate currentDate = LocalDate.now();
        if ((birthDate != null) && (currentDate != null)) {
            return currentDate.getYear() - Integer.valueOf(birthDate.split("-", 0)[0]);
        } else {
            return 0;
        }
    }
}