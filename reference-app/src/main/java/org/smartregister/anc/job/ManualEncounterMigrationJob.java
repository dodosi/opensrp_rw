package org.smartregister.anc.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.anc.service.ManualEncounterService;
import org.smartregister.job.BaseJob;

public class ManualEncounterMigrationJob extends BaseJob {

    public static final String TAG = "ManualEncounterMigrationJob";
    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), ManualEncounterService.class);
        startIntentService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
