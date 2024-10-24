package org.smartregister.anc.library.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

public class RegisterQueryProvider {

    public String getObjectIdsQuery(String mainCondition, String filters) {

        String strMainCondition = getMainCondition(mainCondition);

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" WHERE (" + getDemographicTable() +
                    ".first_name LIKE '%%%s%%' OR "+
                    getDemographicTable()+ ".last_name LIKE '%%%s%%' OR "+ getDemographicTable()+
                    ".register_id LIKE '%%%s%%')", filters, filters, filters);
        }

        return "select " + getDemographicTable() + ".id"  + " from " + getDemographicTable() + " " + getDemographicTable() + "  " +
                "join " + getDetailsTable() + " on " + getDemographicTable() + ".id" + " =  " + getDetailsTable() + "." + "id " + strMainCondition + strFilters;
    }

    private String getMainCondition(String mainCondition) {
        if (StringUtils.isNotBlank(mainCondition)) {
            return " where " + mainCondition;
        }
        return "";
    }

    private String getFilter(String filters) {

        if (StringUtils.isNotBlank(filters)) {
            return String.format(" AND (" + getDemographicTable() + ".first_name LIKE '%%%s%%' OR "+
                    getDemographicTable()+ ".last_name LIKE '%%%s%%' OR "+getDemographicTable()+
                    ".register_id LIKE '%%%s%%')", filters, filters, filters);
        }
        return "";
    }

    public String getDemographicTable() {
        return DBConstantsUtils.RegisterTable.DEMOGRAPHIC;
    }

    public String getDetailsTable() {
        return DBConstantsUtils.RegisterTable.DETAILS;
    }


    public String getCountExecuteQuery(String mainCondition, String filters) {

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(filters) && StringUtils.isBlank(mainCondition)) {
            strFilters = String.format(" WHERE (" + getDemographicTable() + ".first_name LIKE '%%%s%%' " +
                    "OR "+getDemographicTable()+ ".last_name LIKE '%%%s%%' OR "+getDemographicTable()+
                    ".register_id LIKE '%%%s%%')", filters, filters, filters);
        }

        String strMainCondition = getMainCondition(mainCondition);

        return "select count(" + getDemographicTable() + ".id) from " + getDemographicTable() + " " + getDemographicTable() + "  " +
                "join " + getDetailsTable() + " on " + getDemographicTable() + ".id =  " + getDetailsTable() + "." + "id " + strMainCondition + strFilters;
    }

    public String mainRegisterQuery() {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(getDemographicTable(), mainColumns());
        queryBuilder.customJoin(" join " + getDetailsTable()
                + " on " + getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + "= " + getDetailsTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " ");
        return queryBuilder.getSelectquery();
    }

    public String[] mainColumns() {
        return new String[]{DBConstantsUtils.KeyUtils.FIRST_NAME, DBConstantsUtils.KeyUtils.LAST_NAME, DBConstantsUtils.KeyUtils.DOB,
                DBConstantsUtils.KeyUtils.DOB_UNKNOWN, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PHONE_NUMBER, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_NAME,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " as " + DBConstantsUtils.KeyUtils.ID_LOWER_CASE, DBConstantsUtils.KeyUtils.ANC_ID,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.REMINDERS, DBConstantsUtils.KeyUtils.HOME_ADDRESS, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.EDD,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.CONTACT_STATUS, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.VISIT_START_DATE, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.COHABITANTS, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.RELATIONAL_ID,
                getDetailsTable() + "." + ConstantsUtils.SpinnerKeyConstants.PROVINCE, getDetailsTable() + "." + ConstantsUtils.SpinnerKeyConstants.DISTRICT,
                getDetailsTable() + "." + ConstantsUtils.SpinnerKeyConstants.SUB_DISTRICT, getDetailsTable() + "." + ConstantsUtils.SpinnerKeyConstants.FACILITY,
                getDetailsTable() + "." + ConstantsUtils.SpinnerKeyConstants.VILLAGE ,getDetailsTable()+ "." + DBConstantsUtils.KeyUtils.INSURANCE,
                getDetailsTable()+ "." + DBConstantsUtils.KeyUtils.VILLAGE,getDetailsTable()+ "." + DBConstantsUtils.KeyUtils.UBUDEHE_CATEGORY,
                getDetailsTable()+ "." + DBConstantsUtils.KeyUtils.NATIONAL_ID,getDetailsTable()+ "." + DBConstantsUtils.KeyUtils.DOCUMENT_TYPE,
                getDetailsTable() + "." + ConstantsUtils.JsonFormKeyUtils.VISIT_DATE,
                getDemographicTable()+ "." + ConstantsUtils.DATA_MIGRATION_IS_DIRTY,
                getDetailsTable()+ "." + DBConstantsUtils.KeyUtils.PASSPORT_ID,getDetailsTable()+ "." + DBConstantsUtils.KeyUtils.OTHER_ID };

    }
}