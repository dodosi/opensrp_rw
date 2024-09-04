package org.smartregister.anc.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.library.repository.ContactTasksRepository;
import org.smartregister.anc.library.repository.PartialContactRepository;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 09/04/2018.
 */

public class AncRepository extends Repository {
    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;

    public AncRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(),
                CoreLibrary.getInstance().context().commonFtsObject(), openSRPContext.sharedRepositoriesArray());
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        ConfigurableViewsRepository.createTable(database);
        EventClientRepository
                .createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository
                .createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());

        UniqueIdRepository.createTable(database);
        SettingsRepository.onUpgrade(database);
        PartialContactRepository.createTable(database);
        PreviousContactRepository.createTable(database);
        ContactTasksRepository.createTable(database);
        ClientFormRepository.createTable(database);
        ManifestRepository.createTable(database);

        LocationRepository.createTable(database);
        LocationTagRepository.createTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.tag(AncRepository.class.getName()).w("Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion2(db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;
                case 4:
                    upgradeToVersion4(db);
                    break;
                case 5:
                    upgradeToVersion5(db);
                    break;
                case 6:
                    upgradeToVersion6(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        if (!ManifestRepository.isVersionColumnExist(db)) {
            ManifestRepository.addVersionColumn(db);
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(DrishtiApplication.getInstance().getPassword());
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(DrishtiApplication.getInstance().getPassword());
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Timber.e(e, "Database Error");
            return null;
        }

    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

    private void upgradeToVersion3(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME + " ADD COLUMN " + ConstantsUtils.SpinnerKeyConstants.PROVINCE + " VARCHAR");
            db.execSQL("ALTER TABLE " + DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME + " ADD COLUMN " + ConstantsUtils.SpinnerKeyConstants.DISTRICT + " VARCHAR");
            db.execSQL("ALTER TABLE " + DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME + " ADD COLUMN " + ConstantsUtils.SpinnerKeyConstants.SUB_DISTRICT + " VARCHAR");
            db.execSQL("ALTER TABLE " + DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME + " ADD COLUMN " + ConstantsUtils.SpinnerKeyConstants.FACILITY + " VARCHAR");
            db.execSQL("ALTER TABLE " + DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME + " ADD COLUMN " + ConstantsUtils.SpinnerKeyConstants.VILLAGE + " VARCHAR");
        } catch (Exception e) {
            Timber.e("upgradeToVersion3 %s", e.getMessage());
        }
    }

    private void upgradeToVersion4(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME + " ADD COLUMN " + ConstantsUtils.DATA_MIGRATION_IS_DIRTY + " VARCHAR");
        } catch (Exception e) {
            Timber.e("upgradeToVersion4 %s", e.getMessage());
        }
    }
    private void upgradeToVersion5(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME + " ADD COLUMN " + ConstantsUtils.JsonFormKeyUtils.VISIT_DATE + " VARCHAR");
        } catch (Exception e) {
            Timber.e("upgradeToVersion5 %s", e.getMessage());
        }
    }

    private void upgradeToVersion6(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE INDEX ec_client_first_name_index ON ec_client(first_name COLLATE NOCASE)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.first_name %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX ec_client_last_name_index ON ec_client(last_name COLLATE NOCASE)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.last_name %s", e.getMessage());
        }
    }

    private void upgradeToVersion7(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE INDEX ec_client_relationalid_index ON ec_client(relationalid)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.relationalid %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX ec_client_is_closed_index ON ec_client(is_closed)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.is_closed %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX ec_client_register_id_index ON ec_client(register_id)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.register_id %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX ec_client_last_interacted_with_index ON ec_client(last_interacted_with)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.last_interacted_with %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX ec_client_dob_index ON ec_client(dob)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.dob %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX ec_client_home_address_index ON ec_client(home_address)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.home_address %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX ec_client_data_migration_is_dirty_index ON ec_client(data_migration_is_dirty)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_client.data_migration_is_dirty %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX previous_contact_value_index ON previous_contact(value)");
        } catch (Exception e) {
            Timber.e("on adding index to previous_contact.value %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX previous_contact_value_index ON previous_contact(value)");
        } catch (Exception e) {
            Timber.e("on adding index to previous_contact.value %s", e.getMessage());
        }
    }

    private void upgradeToVersion8(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE INDEX ec_details_base_entity_id_index ON ec_details(base_entity_id COLLATE NOCASE)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_details.base_entity_id %s", e.getMessage());
        }

        try {
            db.execSQL("CREATE INDEX ec_details_key_index ON ec_details(key COLLATE NOCASE)");
        } catch (Exception e) {
            Timber.e("on adding index to ec_details.key %s", e.getMessage());
        }
    }

}

