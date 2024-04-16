package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.utils.NativeFormLangUtils;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ContactSummaryFinishAdapter extends RecyclerView.Adapter<ContactSummaryFinishAdapter.ViewHolder> {

    private List<YamlConfig> mData;
    private LayoutInflater mInflater;
    private Facts facts;

    // data is passed into the constructor
    public ContactSummaryFinishAdapter(Context context, List<YamlConfig> data, Facts facts) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.facts = facts;
//        Log.i("MY_TEST", "onBindViewHolder: "+ yamlConfigItem.toString());
//        Log.i("MY_TEST2", "onBindViewHolder: "+facts.toString());
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_contact_summary_finish_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sectionHeader.setText(processUnderscores(mData.get(position).getGroup()));

        List<YamlConfigItem> fields = mData.get(position).getFields();
        StringBuilder outputBuilder = new StringBuilder();
        for (YamlConfigItem yamlConfigItem : fields) {

            for (String key :facts.asMap().keySet()) {
//
//                String translated_text="";
               String value = Utils.returnTranslatedStringJoinedValue(facts.get(key).toString());
//                Log.i("VALUE", "onBindViewHolder: "+value);
                if (StringUtils.isNotBlank(value)) {
                    facts.put(key, value);
                } else {
                    facts.put(key, "");
                }

//
//                String value=facts.get(key).toString();
//                if (value.startsWith("[{")){
//                    try {
//                        JSONArray array = new JSONArray(value);
//                        String temp="";
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject object = array.getJSONObject(i);
//                            temp=temp +", "+ NativeFormLangUtils.translateDatabaseString(object.get("text").toString(), AncLibrary.getInstance().getContext().applicationContext());
////
//                        }
//                        translated_text=temp;
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else if (value.startsWith("{")){
//                    try {
//                        JSONObject object1= new JSONObject(value);
//                        if (!"".equals(object1.get("text").toString())) {
//
//                            translated_text= NativeFormLangUtils.translateDatabaseString(object1.get("text").toString(), AncLibrary.getInstance().getContext().applicationContext());
//                        }
//                        else{
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else if (!value.startsWith("{") && !value.startsWith("[{")){
//                    if (value.contains(".") && value.length() > 15 && !key.equals("helper") && !value.startsWith(",") ){
//                        String[] arr=value.split(",");
////                    Log.i("MY_DATA", "onBindViewHolder: "+Arrays.toString(arr) );
//                        String temp="";
//                        for (int i = 0; i < arr.length; i++) {
//                            temp=temp +", "+ NativeFormLangUtils.translateDatabaseString(arr[i].toString(), AncLibrary.getInstance().getContext().applicationContext());
////                            Log.i("TRANSLATED", "onBindViewHolder: "+temp);
//                        }
//                        translated_text=temp;
//                    }
//                }
//                else {
//                    translated_text=value;
//                }
//                Log.i("TRANSLATED", "onBindViewHolder: "+translated_text);
//                facts.put(key,translated_text);
            }
            if (yamlConfigItem.isMultiWidget() != null && yamlConfigItem.isMultiWidget()) {
                //Iterate and get keys from facts
                Log.i("INSIDE", "onBindViewHolder: ");

                //Pass them through translation
                // String key="";
                // NativeFormLangUtils.translateDatabaseString(key, AncLibrary.getInstance().getContext().applicationContext());

                prefillInjectableFacts(facts, yamlConfigItem.getTemplate());
            }
//            Log.i("FACT2", "onBindViewHolder: "+facts.toString());
            if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getRelevance())) {
                outputBuilder.append(Utils.fillTemplate(yamlConfigItem.getTemplate(), facts)).append("\n\n");
            }
        }
        String output = outputBuilder.toString();
//        Log.i("OUTPUT", "onBindViewHolder: "+output);

        holder.sectionDetails.setText(output);

        if (output.trim().isEmpty()) {
            holder.parent.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            holder.parent.setVisibility(View.GONE);
        } else {
            holder.parent.setLayoutParams(
                    new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.parent.setVisibility(View.VISIBLE);
        }


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public Facts getTrFacts() {

//        try {
//            Map<String, Object> factsAsMap = facts.asMap();

//                JSONObject jsonObject = new JSONObject(facts.asMap().toString());
//                if (jsonObject.length() > 0) {
//                    Iterator<String> keys = jsonObject.keys();
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        String valueObject = jsonObject.optString(key), value;
//                        value = Utils.returnTranslatedStringJoinedValue(valueObject);
//                        if (StringUtils.isNotBlank(value)) {
//                            facts.put(key, value);
//                        } else {
//                            facts.put(key, "");
//                        }
//                    }
//                }
//
//        } catch (JSONException e) {
//            Timber.e(e);
//        }

//        return facts;
        return  null;
    }


    private String processUnderscores(String string) {
        return string.replace("_", " ").toUpperCase();
    }

    private void prefillInjectableFacts(Facts facts, String template) {
        String[] relevanceToken = template.split(",");
        String key;
        for (String token : relevanceToken) {
            if (token.contains("{") && token.contains("}")) {
                key = token.substring(token.indexOf('{') + 1, token.indexOf('}'));
                if (facts.get(key) == null) {
                    facts.put(key, "");
                }
            }
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sectionHeader;
        public TextView sectionDetails;
        public View parent;

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.contact_summary_section_header);
            sectionDetails = itemView.findViewById(R.id.contact_summary_section_details);
            parent = itemView;
        }
    }

}
