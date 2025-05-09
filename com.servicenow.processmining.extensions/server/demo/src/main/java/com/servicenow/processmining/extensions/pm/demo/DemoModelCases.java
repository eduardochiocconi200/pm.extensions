package com.servicenow.processmining.extensions.pm.demo;

import com.servicenow.processmining.extensions.sc.dao.SysAuditLogDAOREST;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntryPK;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoModelCases
{
    private DemoModel model = null;
    private ServiceNowInstance instance = null;
    private String caseSysId = null;

    public DemoModelCases(final DemoModel model, final ServiceNowInstance instance)
    {
        this.model = model;
        this.instance = instance;
    }

    public DemoModel getModel()
    {
        return this.model;
    }

    public boolean create()
    {
        for (DemoModelPath path : getModel().getPaths()) {
            DateTime startCreationOfRecords = DateTime.now();
            DateTime pathFirstStartTime = DateTime.now().minusSeconds((int)path.getTotalDuration());
            if (!loadChoiceValues(path)) {
                return false;
            }
            System.out.println("Creating [" + path.getCount() + "] [" + path.getTable() + "] records for path defined in Tab: [" + path.getPathName() + "]. (A '.' will be printed for each created record. Be patient!)");
            for (int count=0; count < path.getCount(); count++) {
                if (!createCase(path, pathFirstStartTime)) {
                    return false;
                }
                System.out.print(".");
                pathFirstStartTime = pathFirstStartTime.minusSeconds((int)path.getCreationDelta());
            }
            double elapsedTime = DateTime.now().minus(startCreationOfRecords.getMillis()).getMillis() / 1000.0 / 60.0;
            System.out.println("\nCreated (" + path.getCount() + ") " + path.getTable() + " records along with its audit log records in (" + elapsedTime + ") mins.");
        }

        return true;
    }

    private String dateToString(final DateTime dt)
    {
        if (dt.isAfterNow()) {
            throw new RuntimeException("ERROR: It is not possible to create a Date (" + dt + ") in the future and after NOW (" + DateTime.now() + ")");
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String createdOn = formatter.print(dt);

        return createdOn;
    }

    private boolean createCase(final DemoModelPath path, final DateTime createdOn)
    {
        // Create the record with initial values ...
        if (!createRecord(path, createdOn)) {
            return false;
        }

        // Perform the necessary subsequent actions to mimic the user behavior progressing the case to closure.
        if (!updateRecord(path, createdOn)) {
            return false;
        }

        return true;
    }

    private boolean createRecord(final DemoModelPath path, final DateTime createdOn)
    {
        HashMap<String, String> values = path.getInitialValues();
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		String url = "https://" + getInstance().getInstance() + "/api/now/table/" + path.getTable();
		String payload = createPayload(path, values);
		String response = snrs.executePostRequest(url, payload);
		if (response == null || response != null && response.equals("")) {
			return false;
		}

        this.caseSysId = getCaseCreationResponse(response);

        HashMap<String, String> creationUpdateValues = new HashMap<String, String>();
        String createdOnDT = dateToString(createdOn);
        creationUpdateValues.put("sys_created_on", createdOnDT);
        creationUpdateValues.put("opened_at", createdOnDT);
        creationUpdateValues.put("sys_created_by", "maint");

        url = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/" + path.getTable() + "/" + this.caseSysId;
		payload = createPayload(path, creationUpdateValues);
		response = snrs.executePutRequest(url, payload);
		if (response == null || response != null && response.equals("")) {
			if (snrs.getErrorStatusCode() == 400) {
				String errorMessage = "ERROR: The '/api/snc/fixaudittrail/{table}/{id}" + this.caseSysId + "' REST Scripted endpoint does not exist and it must be created. Check the documentation to create it and try again.";
				logger.error(errorMessage);
				System.err.println(errorMessage);
			}
            else if (snrs.getErrorStatusCode() == 405) {
				String errorMessage = "ERROR: The '/api/snc/fixaudittrail/{table}/{id}" + this.caseSysId + "' REST Scripted endpoint exists, but you need to  make sure it is defined as a 'put' method. Update the Scripted REST API endpoint definition and try again.";
				logger.error(errorMessage);
				System.err.println(errorMessage);
            }

			return false;
		}

        return fixAuditTrail(path, createdOn);
    }

    private String getCaseCreationResponse(final String response)
    {
        JSONObject responseJSON = new JSONObject(response);
        if (responseJSON.has("result")) {
            JSONObject resultJSON = responseJSON.getJSONObject("result");
            if (resultJSON.has("sys_id")) {
                return resultJSON.getString("sys_id");
            }    
        }

        return null;
    }

    private String createPayload(final DemoModelPath path, final HashMap<String, String> values)
    {
        boolean processedFirstEntry = false;
        String payload = "{";
        for (String key : values.keySet()) {
            if (processedFirstEntry) {
                payload += ",";
            }
            String value = getValue(values.get(key));
            if (isKeyChoiceAttribute(key)) {
                if (!getModel().getChoiceValues(path.getTable(), key).contains(value)) {
                    System.err.println("The choice value: (" + value + ") does not exist for attribute: (" + key + ") in table: (" + path.getTable() + "). Valid [" + key + "] values: (" + getModel().getChoiceValues(path.getTable(), key) + ").");
                    System.err.println("Values are case sensitive. Please fix this in the appropriate place in Tab: (" + path.getPathName() + ") in your input Excel spreadsheet.");
                    System.exit(-1);
                    // This code used to automatically create choice values if the values in the XLS did not match any existing choice value.
                    // We disabled this as it is better to report an error and the data be fixed manually in the XLS.
                    // createChoiceValue(path.getTable(), key, value);
                    // getModel().getChoiceValues(path.getTable(), key).add(value);
                }
            }
            // We need to check if we need to inject the data identifier if it exists
            if (getModel().getDataIdentifier() != null) {
                value = addDataIdentifier(key, value);
            }
            payload += "\"" + key + "\":\"" + value + "\"";
            processedFirstEntry = true;
        }
        payload += "}";

        return payload;
    }

    private String addDataIdentifier(final String key, final String value)
    {
        if (key != null && (key.equals("description"))) {
            return value + " - " + getModel().getDataIdentifier();
        }

        return value;
    }

    private boolean isKeyChoiceAttribute(String key)
    {
        if (key != null && key.equals("state")) {
            return true;
        }

        return false;
    }

    private boolean loadChoiceValues(final DemoModelPath model)
    {
        String tableName = model.getTable();
        String attributeName = "state";
        if (getModel().getChoiceValues(tableName, attributeName).size() == 0) {
            ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
            String sysChoiceQueryUrl = "https://" + getInstance().getInstance() + "/api/now/table/sys_choice?sysparm_fields=name,element,label&sysparm_query=name=" + tableName + URLEncoder.encode("^", StandardCharsets.UTF_8) + "element=" + attributeName + URLEncoder.encode("^", StandardCharsets.UTF_8) + "language=en" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "ORDERBYDESCsys_created_on";
            String response = snrs.executeGetRequest(sysChoiceQueryUrl);
            if (response == null || response != null && response.equals("")) {
                System.err.println("Could not load Choice values. Error: (" + snrs.getErrorMessage() + ") - Status Code: (" + snrs.getErrorStatusCode() + ")");
                return false;
            }

            JSONObject choiceValuesResult = new JSONObject(response);
            JSONArray choiceValues = choiceValuesResult.optJSONArray("result");
            for (int i=0; i < choiceValues.length(); i++) {
                JSONObject choiceElement = choiceValues.getJSONObject(i);
                String label = choiceElement.getString("label");
                getModel().getChoiceValues(tableName, attributeName).add(label);
            }
        }

        return true;
    }

    @SuppressWarnings("unused")
    private boolean createChoiceValue(final String table, final String attribute, final String value)
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String sysChoicePostUrl = "https://" + getInstance().getInstance() + "/api/now/table/sys_choice";
        String payload = "{ \"name\" : \"" + table + "\", \"element\" : \"" + attribute + "\", \"language\" : \"en\", \"value\" : \"" + value + "\", \"label\" : \"" + value + "\" }";
        String response = snrs.executePostRequest(sysChoicePostUrl, payload);
        if (response == null || response != null && response.equals("")) {
            return false;
        }

        return true;
    }

    private String getValue(final String value)
    {
        if (value.indexOf(",") > 0) {
            StringTokenizer st = new StringTokenizer(value, ",");
            ArrayList<String> values = new ArrayList<String>();
            while (st.hasMoreElements()) {
                String element = (String) st.nextElement();
                values.add(element.trim());
            }
            Random rand = new Random();
            int randomIndex = rand.nextInt(values.size());
            return values.get(randomIndex);
        }

        return value;
    }

    private boolean updateRecord(final DemoModelPath path, final DateTime createdOn)
    {
        TreeMap<Double, HashMap<String, String>> updateBatches = path.getPostInitialValues();
        DateTime recordUpdateTS = createdOn;
        Double previousUpdateTS = null;
        for (Double updateTime : updateBatches.keySet()) {
            logger.debug("Update Batch: (" + updateTime + ") = (" + updateBatches.get(updateTime) + ")");
            if (previousUpdateTS == null) {
                recordUpdateTS = recordUpdateTS.plusSeconds(updateTime.intValue());
            }
            else {
                recordUpdateTS = recordUpdateTS.plusSeconds(updateTime.intValue() - previousUpdateTS.intValue());
            }
            if (!processUpdateRecordBatch(path, previousUpdateTS, updateTime, recordUpdateTS, updateBatches.get(updateTime))) {
                return false;
            }
            previousUpdateTS = updateTime;
        }

        return true;
    }

    private boolean processUpdateRecordBatch(final DemoModelPath path, final Double previousUpdateTime, final Double updateTime, final DateTime createdOn, HashMap<String, String> updateValues)
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/" + path.getTable() + "/" + this.caseSysId;
        String payload = createPayload(path, updateValues);
        String response = snrs.executePutRequest(url, payload);
        if (response == null || response != null && response.equals("")) {
            System.err.println("ERROR: Could not update the (" + path.getTable() + ") record with timestamp: (" + updateTime + ") and values: (" + updateValues + ") referenced in Tab: (" + path.getPathName() + ").");
            System.err.println("Make sure all needed attributes and values in your spreadsheet for the time (" + updateTime + "), and values (" + updateValues + ") provided are correct.");
            System.err.println("This way, the update transaction can be completed successfully.");
            return false;
        }

        // Let's add a random deviation to the createdOn to avoid all data being equally sparsed.
        Random random = new Random();
        // We will create a randomness of 5 mins (600 seconds) + o - the next time.
        int seconds = (int) random.nextDouble(600);
        seconds = (seconds % 2 == 0) ? seconds : (seconds * -1);
        DateTime adjustedTime = createdOn.plusSeconds(seconds);
        if (adjustedTime.isAfterNow()) {
            adjustedTime = DateTime.now();
        }

        return fixAuditTrail(path, adjustedTime);
    }

    private boolean fixAuditTrail(final DemoModelPath path, final DateTime createdOn)
    {
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(caseSysId);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK(path.getTable()), ids, "reasonISEMPTY");
        for (SysAuditEntry entry : sysAuditLog.getLog()) {
            if (entry.getReason().equals("")) {
                // Fix creation time ...
                entry.setSysCreatedOn(dateToString(createdOn));
                entry.setReason(getModel().getDataIdentifier() != null ? getModel().getDataIdentifier() : "pm_eval");
                String payload = getUpdatedSysAuditPayload(entry);
                SysAuditEntryPK pk = ((SysAuditEntryPK) entry.getPK());
                ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
                String url = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/sys_audit/" + pk.getSysId();
                String response = snrs.executePutRequest(url, payload);
                if (response == null || response != null && response.equals("")) {
                    return false;
                }

                // If it is the work_notes or comments attribute, we also need to update the record in the sys_journal_field table
                if (!fixWorkNotesOrComments(entry, createdOn)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean fixWorkNotesOrComments(final SysAuditEntry entry, final DateTime createdOn)
    {
        if (entry.getFieldName().equals("work_notes") || entry.getFieldName().equals("comments")) {
            ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
            String sysJournalFieldQueryUrl = "https://" + getInstance().getInstance() + "/api/now/table/sys_journal_field?sysparm_query=table=incident" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "element=work_notes" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "element_id=" + entry.getDocumentKey() + URLEncoder.encode("^", StandardCharsets.UTF_8) + "ORDERBYDESCsys_created_on";
            String response = snrs.executeGetRequest(sysJournalFieldQueryUrl);
            if (response == null || response != null && response.equals("")) {
                return false;
            }

            String sysJournalFieldPayload = getUpdatedSysJournalFieldPayload(response, dateToString(createdOn));
            String sysJournalFieldKey = getSysJournalFieldKey(response);
            snrs = new ServiceNowRESTService(getInstance());
            String sysJournalFieldUrl = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/sys_journal_field/" + sysJournalFieldKey;
            response = snrs.executePutRequest(sysJournalFieldUrl, sysJournalFieldPayload);
            if (response == null || response != null && response.equals("")) {
                return false;
            }
        }

        return true;
    }

    private String getSysJournalFieldKey(final String response)
    {
        String updatePayload = "";
        JSONObject responseJSON = new JSONObject(response);
        if (responseJSON.has("result")) {
            JSONObject resultJSON = responseJSON.getJSONArray("result").getJSONObject(0);
            if (resultJSON.has("sys_id")) {
                return resultJSON.getString("sys_id");
            }
        }

        return updatePayload;
    }

    private String getUpdatedSysJournalFieldPayload(final String response, final String createdOn)
    {
        String payload = null;
        JSONObject responseJSON = new JSONObject(response);
        if (responseJSON.has("result")) {
            JSONObject resultJSON = responseJSON.getJSONArray("result").getJSONObject(0);
            if (resultJSON.has("sys_id")) {
                String editedWorkNote = resultJSON.getString("value") + " Note for instance: " + resultJSON.getString("element_id");
                payload = "{ ";
                payload += "\"value\" : \"" + editedWorkNote + "\", ";
                payload += "\"sys_created_on\" : \"" + createdOn + "\"";
                payload += " }";
            }
        }

        return payload;
    }

    private String getUpdatedSysAuditPayload(final SysAuditEntry entry)
    {
        String sysAuditPayload = "{";
        sysAuditPayload += "\"sys_created_on\":";
        sysAuditPayload += "\"" + entry.getSysCreatedOn() + "\",";
        sysAuditPayload += "\"reason\":";
        sysAuditPayload += "\"" + entry.getReason() + "\"";
        sysAuditPayload += "}";

        return sysAuditPayload;
    }

    private ServiceNowInstance getInstance()
    {
        return this.instance;
    }

    private static final Logger logger = LoggerFactory.getLogger(DemoModelCases.class);
}
