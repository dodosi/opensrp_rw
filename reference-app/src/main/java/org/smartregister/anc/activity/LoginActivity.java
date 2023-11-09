package org.smartregister.anc.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.SiteCharacteristicsEnterActivity;
import org.smartregister.anc.library.activity.UkudoxActivity;
import org.smartregister.anc.library.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.anc.presenter.LoginPresenter;
import org.smartregister.repository.dao.LocationDaoImpl;
import org.smartregister.security.SecurityHelper;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 21/06/2018.
 */
public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {
    EditText usernameField,passwordField;
    Button loginBtn;
    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();

    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(!getUsername().equals("")){
//            loginBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(validateCredentials(usernameField.getText().toString(),passwordField.getText().toString()) ){
//                        Log.d("CLICKED","MY BUTTON");
//                        gotToHomeRegister(false);
//
//                    }
//                }
//            });
//        }else{
//            Log.d("UKUDOX","NOT FROM LOCAL 11");
////        }
//    }

    private void setUpViews() {
        TextView formReleaseTextView = findViewById(R.id.manifest_text_view);
        usernameField= findViewById(R.id.login_user_name_edit_text);
        passwordField= findViewById(R.id.login_password_edit_text);
        loginBtn= findViewById(R.id.login_login_btn);
        if (StringUtils.isNotBlank(new Utils().getManifestVersion(this))) {
            formReleaseTextView.setText(new Utils().getManifestVersion(this));
        } else {
            formReleaseTextView.setVisibility(View.GONE);
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
       mLoginPresenter = new LoginPresenter(this);
     }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
//            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
            new SaveTeamLocationsTask().execute();
        }
       if (mLoginPresenter.isServerSettingsSet()) {
            Log.d("UKUDOX","FROM REMOTE");
            setCredentials(usernameField.getText().toString(),passwordField.getText().toString());
            gotToHomeRegister(remote);
        } else {
            goToSiteCharacteristics(remote);
        }

        finish();
    }

    private void gotToHomeRegister(boolean remote) {
       //Intent intent = new Intent(this, BaseHomeRegisterActivity.class);
        //ukudox stuff
        Intent intent = new Intent(this, UkudoxActivity.class);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }

    private void goToSiteCharacteristics(boolean remote) {
        Intent intent = new Intent(this, SiteCharacteristicsEnterActivity.class);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViews(ViewConfigurationSyncCompleteEvent syncCompleteEvent) {
        if (syncCompleteEvent != null) {
            Timber.d("Refreshing Login View...");
            mLoginPresenter.processViewCustomizations();

        }
    }
    public  void setCredentials(String username,String password){
        SharedPreferences sharedPreferences=getSharedPreferences("credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.commit();
    }
    public boolean validateCredentials(String newUsername,String newPassword){
        SharedPreferences sharedPreferences=getSharedPreferences("credentials",Context.MODE_PRIVATE);
        String username="";
        String password="";
        if(sharedPreferences.contains("username")&&(sharedPreferences.contains("password"))){
            username=sharedPreferences.getString("username","");
            password=sharedPreferences.getString("password","");
            Log.d("CRED",username+" "+password);
        }else{
            Log.d("CRED","Nothing saved "+username+" PASS "+password);
        }
        return newUsername.equals(username)&&newPassword.equals(password);
    }
    public String getUsername(){
        SharedPreferences sharedPreferences=getSharedPreferences("credentials",Context.MODE_PRIVATE);
        String username="";
        if(sharedPreferences.contains("username")){
            username=sharedPreferences.getString("username","");
        }else{
            Log.d("USERNAME","Nothing saved "+username);
        }
        return username;
    }
}