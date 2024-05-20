package org.smartregister.anc.service;

import android.content.Intent;
import android.util.Log;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.sync.intent.BaseSyncIntentService;

import timber.log.Timber;

public class ManualEncounterService extends BaseSyncIntentService implements SyncStatusBroadcastReceiver.SyncStatusListener {
   private static String TAG = "ManualEncounterService";
    public ManualEncounterService() {
        super("ManualEncounterService");

    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.e(TAG,"onHandleIntent");
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
    }

    @Override
    public void onSyncStart() {
        Log.e(TAG,"onSyncStart");
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        Log.e(TAG,"onSyncComplete");
    }
}
