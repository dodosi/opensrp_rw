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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;


public class DashboardRepository extends BaseRepository {

    public static final String TABLE_PREVIOUS_CONTACT = "previous_contact";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    private static final String[] projection = getRegisterQueryProvider().mainColumns();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getDueContactDash(String datetoday) {

            SQLiteDatabase db = getMasterRepository().getReadableDatabase();

//            String query = "SELECT COUNT(*) FROM " + getRegisterQueryProvider().getDetailsTable() + " WHERE " + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE+ "= '"+datetoday+"'";
//        SQLiteStatement statement = db.compileStatement(query);
//        long count = statement.simpleQueryForLong();
//        return count;
        String query = "SELECT * FROM " + getRegisterQueryProvider().getDetailsTable();
        Cursor   cursor = db.rawQuery(query, null);
        int count=0;
        while (cursor.moveToNext()){
            String next_contact_date=cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE));


            DateTimeFormatter df = DateTimeFormatter.ofPattern("d-MMM-yyyy");
            LocalDate  d1 = LocalDate.parse(next_contact_date, df);
                Log.i("TEST", "getDueContactDash: "+d1.getMonth().toString());
                count++;


        }
        return count;
    }
    public static int getWomanReferred(String dateStart, String dateEnd) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PREVIOUS_CONTACT + " WHERE " + KEY + "= 'referred_hosp'";
      Cursor   cursor = db.rawQuery(query, null);
      int count=0;
      while (cursor.moveToNext()){
         try {
              JSONObject val = new JSONObject(cursor.getString(4));

              if (val.get("value").toString().equals("yes")) { count++;}
         } catch (JSONException e) {
              e.printStackTrace();
          }

      }
        return count;
    }
    public static long getProcessedVisits(String datet) {

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_PREVIOUS_CONTACT + " WHERE " + KEY + "= 'contact_date' AND " +VALUE+ "='"+datet+"'";
        Cursor   cursor = db.rawQuery(query, null);
        SQLiteStatement statement = db.compileStatement(query);
        long count = statement.simpleQueryForLong();
        return count;
    }
    public static long getWomanWithDangerSing(String dateStart, String dateEnd){

        SQLiteDatabase db = getMasterRepository().getReadableDatabase();

        String query = "SELECT * FROM " + getRegisterQueryProvider().getDetailsTable();
        Cursor   cursor = db.rawQuery(query, null);
        int count=0;
        while (cursor.moveToNext()){
              String r=cursor.getString(18);
             if (r!="0" && r!= null){
                 count++;
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
    public static List<CustomClient> getWomanProfileDetails(String baseEntityId) {
        Cursor cursor = null;
        List<CustomClient> clientList=new ArrayList<>();

        try {
            SQLiteDatabase db = getMasterRepository().getReadableDatabase();

            String query =
                    "SELECT " + StringUtils.join(projection, ",") + " FROM " + getRegisterQueryProvider().getDemographicTable() + " join " + getRegisterQueryProvider().getDetailsTable() +
                            " on " + getRegisterQueryProvider().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = " + getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " WHERE " +
                            getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE + " = ?";
            cursor = db.rawQuery(query, new String[]{baseEntityId});

            if (cursor != null ) {

                while (cursor.moveToNext()){
                    CustomClient client=new CustomClient();
                    client.setFirstName(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.FIRST_NAME)));
                    client.setLastName(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.LAST_NAME)));
                    client.setAge(String.valueOf(calculateAge(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.DOB)))));
                    client.setGa(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.EDD)));
                    client.setRegistrationId(cursor.getString(cursor.getColumnIndex("register_id")));
                    client.setNextContactDate(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE)));
                    String r=cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT));
                    String red= r == null?  "0":  r;
                    String y=cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT));
                    String yellow= y == null?  "0":  y;
                    client.setAttentionFlag(Integer.parseInt(red)+ Integer.parseInt(yellow));
                    clientList.add(client)  ;

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
    private static int calculateAge(String birthDate) {
        LocalDate currentDate =  LocalDate.now();
        if ((birthDate != null) && (currentDate != null)) {
            return  currentDate.getYear()-Integer.valueOf(birthDate.split("-", 0)[0]);
        } else {
            return 0;
        }
    }
}
