package com.servicenow.processmining.extensions.pm.demo;

import com.servicenow.processmining.extensions.pm.timeline.InstanceTimelineItem;
import com.servicenow.processmining.extensions.pm.timeline.TimelineItem;
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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoModelProcessMiningInstances
{
    private DemoModelTimeline timeline = null;
    private ServiceNowInstance instance = null;
    private HashMap<String, String> instToSysIdMap = null;
    private boolean batchMode = false;

    public DemoModelProcessMiningInstances(final DemoModelTimeline timeline, final ServiceNowInstance instance)
    {
        this.timeline = timeline;
        this.instance = instance;
        this.instToSysIdMap = new HashMap<String, String>();
    }

    public DemoModelTimeline getTimeline()
    {
        return this.timeline;
    }

    public DemoModel getModel()
    {
        return getTimeline().getModel();
    }

    private ServiceNowInstance getInstance()
    {
        return this.instance;
    }

    public boolean create()
    {
        if (!loadChoiceValues()) {
            return false;
        }

        for (int i=0; i < getTimeline().getTimeline().getSortedTimeline().size(); i++) {
            TimelineItem ti = getTimeline().getTimeline().getSortedTimeline().get(i);
            if (ti instanceof InstanceTimelineItem) {
                if (!processRecordEvent((InstanceTimelineItem) ti)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean loadChoiceValues()
    {
        for (DemoModelPath path : getModel().getPaths()) {
            String tableName = path.getTable();
            String attributeName = "state";
            if (getModel().getChoiceValues(tableName, attributeName).size() == 0) {
                ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
                String sysChoiceQueryUrl = "https://" + getInstance().getInstance() + "/api/now/table/sys_choice?sysparm_display_value=false&ysparm_fields=name,element,label&sysparm_query=name=" + tableName + URLEncoder.encode("^", StandardCharsets.UTF_8) + "element=" + attributeName + URLEncoder.encode("^", StandardCharsets.UTF_8) + "language=en" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "ORDERBYDESCsys_created_on";
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
        }

        return true;
    }

    private boolean processRecordEvent(InstanceTimelineItem ti)
    {
        if (ti.isStartEvent()) {
            // Create the record with initial values ...
            if (!createRecordEvent(ti)) {
                return false;
            }
        }
        else {
            // Perform the necessary subsequent actions to mimic the user behavior progressing the case to closure.
            if (!updateRecordEvent(ti)) {
                return false;
            }   
        }

        return true;
    }

    private boolean createRecordEvent(InstanceTimelineItem ti)
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		String url = "https://" + getInstance().getInstance() + "/api/now/table/" + ti.getTableName() + "?sysparm_display_value=false";
		String payload = createRecordEventPayload(ti.getTableName(), ti.getUpdateValues());
		String response = snrs.executePostRequest(url, payload);
		if (response == null || response != null && response.equals("")) {
			return false;
		}

        String caseSysId = getCaseCreationResponse(response);
        instToSysIdMap.put(ti.getId(), caseSysId);

        HashMap<String, String> creationUpdateValues = new HashMap<String, String>();
        String createdOnDT = dateToString(ti.getEventTime());
        creationUpdateValues.put("sys_created_on", createdOnDT);
        creationUpdateValues.put("opened_at", createdOnDT);
        creationUpdateValues.put("sys_created_by", "maint");

        url = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/" + ti.getTableName() + "/" + caseSysId + "?sysparm_display_value=false";
		payload = createRecordEventPayload(ti.getTableName(), creationUpdateValues);
		response = snrs.executePutRequest(url, payload);
		if (response == null || response != null && response.equals("")) {
			if (snrs.getErrorStatusCode() == 400) {
				String errorMessage = "ERROR: The '/api/snc/fixaudittrail/{table}/{id}" + caseSysId + "' REST Scripted endpoint does not exist and it must be created. Check the documentation to create it and try again.";
				logger.error(errorMessage);
				System.err.println(errorMessage);
			}
            else if (snrs.getErrorStatusCode() == 405) {
				String errorMessage = "ERROR: The '/api/snc/fixaudittrail/{table}/{id}" + caseSysId + "' REST Scripted endpoint exists, but you need to  make sure it is defined as a 'put' method. Update the Scripted REST API endpoint definition and try again.";
				logger.error(errorMessage);
				System.err.println(errorMessage);
            }

			return false;
		}

        if (batchMode) {
            return fixAuditTrailBatch(ti.getTableName(), ti.getId(), ti.getEventTime());
        }
        else {
            return fixAuditTrail(ti.getTableName(), ti.getId(), ti.getEventTime());
        }
    }

    private String createRecordEventPayload(final String tableName, final HashMap<String, String> updateValues)
    {
        boolean processedFirstEntry = false;
        String payload = "{";
        for (String key : updateValues.keySet()) {
            if (key.equals("task_script")) {
                continue;
            }
            if (processedFirstEntry) {
                payload += ",";
            }
            String value = getValue(updateValues.get(key));
            if (isKeyChoiceAttribute(key)) {
                if (!getModel().getChoiceValues(tableName, key).contains(value)) {
                    System.err.println("The choice value: (" + value + ") does not exist for attribute: (" + key + ") in table: (" + tableName + "). Valid [" + key + "] values: (" + getModel().getChoiceValues(tableName, key) + ").");
                    System.err.println("Values are case sensitive. Please fix this in the appropriate place in your input Excel spreadsheet.");
                    System.exit(-1);
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

    private boolean updateRecordEvent(InstanceTimelineItem ti)
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String sysId = instToSysIdMap.get(ti.getId());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/" + ti.getTableName() + "/" + sysId + "?sysparm_display_value=false";
        String payload = createRecordEventPayload(ti.getTableName(), ti.getUpdateValues());
        String response = snrs.executePutRequest(url, payload);
        if (response == null || response != null && response.equals("")) {
            System.err.println("ERROR: Could not update the (" + ti.getTableName() + ") record with timestamp: (" + ti.getEventTime() + ") and values: (" + ti.getUpdateValues() + ").");
            System.err.println("Make sure all needed attributes and values in your spreadsheet for the time (" + ti.getEventTime() + "), and values (" + ti.getUpdateValues() + ") provided are correct.");
            System.err.println("This way, the update transaction can be completed successfully.");
            return false;
        }

        if (batchMode) {
            return fixAuditTrailBatch(ti.getTableName(), ti.getId(), ti.getEventTime());
        }
        else {
            return fixAuditTrail(ti.getTableName(), ti.getId(), ti.getEventTime());
        }
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

    private boolean fixAuditTrail(final String tableName, final String instanceId, final DateTime createdOn)
    {
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        ArrayList<String> ids = new ArrayList<String>();
        String sysId = instToSysIdMap.get(instanceId);
        ids.add(sysId);
        SysAuditLog sysAuditLog = getEmptyReasonSysAuditLogForIds(tableName, wfDAO, ids);
        for (SysAuditEntry entry : sysAuditLog.getLog()) {
            if (entry.getReason().equals("")) {
                // Fix creation time ...
                entry.setSysCreatedOn(dateToString(createdOn));
                entry.setReason(getModel().getDataIdentifier() != null ? getModel().getDataIdentifier() : "pm_eval");
                String payload = getUpdatedSysAuditPayload(entry);
                SysAuditEntryPK pk = ((SysAuditEntryPK) entry.getPK());
                ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
                String url = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/sys_audit/" + pk.getSysId() + "?sysparm_display_value=false";
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

    private boolean fixAuditTrailBatch(final String tableName, final String instanceId, final DateTime createdOn)
    {
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<SysAuditEntry> workNotesOrCommentsEntries = new ArrayList<SysAuditEntry>();
        String sysId = instToSysIdMap.get(instanceId);
        ids.add(sysId);
        SysAuditLog sysAuditLog = getEmptyReasonSysAuditLogForIds(tableName, wfDAO, ids);
        String payload = "[ ";
        boolean firstEntryProcessed = false;
        for (SysAuditEntry entry : sysAuditLog.getLog()) {
            if (entry.getReason().equals("")) {
                if (firstEntryProcessed) {
                    payload += ",\n";
                }
                // Fix creation time ...
                entry.setSysCreatedOn(dateToString(createdOn));
                entry.setReason(getModel().getDataIdentifier() != null ? getModel().getDataIdentifier() : "pm_eval");
                payload += getUpdatedSysAuditPayloadBatch(entry);
                if (entry.getFieldName().equals("work_notes") || entry.getFieldName().equals("comments")) {
                    workNotesOrCommentsEntries.add(entry);
                }
                firstEntryProcessed = true;
            }
        }
        payload += "]";

        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrailbatch/sys_audit?sysparm_display_value=false";
        String response = snrs.executePutRequest(url, payload);
        if (response == null || response != null && response.equals("")) {
            return false;
        }

        // Fix Work Notes and Comments.
        payload = "[ ";
        payload += "]";
        for (SysAuditEntry entry : sysAuditLog.getLog()) {
            // If it is the work_notes or comments attribute, we also need to update the record in the sys_journal_field table
            if (!fixWorkNotesOrCommentsBatch(entry, createdOn)) {
                return false;
            }
        }

        return true;
    }

    private SysAuditLog getEmptyReasonSysAuditLogForIds(final String tableName, SysAuditLogDAOREST wfDAO, ArrayList<String> ids)
    {
        SysAuditLogPK pk = new SysAuditLogPK(tableName);
        SysAuditLog sysAuditLog = wfDAO.findByIds(pk, ids);
        SysAuditLog sysAuditLogWithEmptyReason = new SysAuditLog(pk);
        for (SysAuditEntry entry : sysAuditLog.getLog()) {
            String reason = entry.getReason();
            if (reason == null || (reason != null && reason.equals(""))) {
                sysAuditLogWithEmptyReason.getLog().add(entry);
            }
        }

        return sysAuditLog;
    }

    private boolean fixWorkNotesOrComments(final SysAuditEntry entry, final DateTime createdOn)
    {
        if (entry.getFieldName().equals("work_notes") || entry.getFieldName().equals("comments")) {
            ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
            String sysJournalFieldQueryUrl = "https://" + getInstance().getInstance() + "/api/now/table/sys_journal_field?sysparm_display_value=false&sysparm_query=table=incident" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "element=work_notes" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "element_id=" + entry.getDocumentKey() + URLEncoder.encode("^", StandardCharsets.UTF_8) + "ORDERBYDESCsys_created_on";
            String response = snrs.executeGetRequest(sysJournalFieldQueryUrl);
            if (response == null || response != null && response.equals("")) {
                return false;
            }

            String sysJournalFieldPayload = getUpdatedSysJournalFieldPayload(response, dateToString(createdOn));
            String sysJournalFieldKey = getSysJournalFieldKey(response);
            snrs = new ServiceNowRESTService(getInstance());
            String sysJournalFieldUrl = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/sys_journal_field/" + sysJournalFieldKey + "?sysparm_display_value=false";
            response = snrs.executePutRequest(sysJournalFieldUrl, sysJournalFieldPayload);
            if (response == null || response != null && response.equals("")) {
                return false;
            }
        }

        return true;
    }

    private boolean fixWorkNotesOrCommentsBatch(final SysAuditEntry entry, final DateTime createdOn)
    {
        if (entry.getFieldName().equals("work_notes") || entry.getFieldName().equals("comments")) {
            ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
            String sysJournalFieldQueryUrl = "https://" + getInstance().getInstance() + "/api/now/table/sys_journal_field?sysparm_display_value=false&sysparm_query=table=incident" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "element=work_notes" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "element_id=" + entry.getDocumentKey() + URLEncoder.encode("^", StandardCharsets.UTF_8) + "ORDERBYDESCsys_created_on";
            String response = snrs.executeGetRequest(sysJournalFieldQueryUrl);
            if (response == null || response != null && response.equals("")) {
                return false;
            }

            String sysJournalFieldPayload = getUpdatedSysJournalFieldPayload(response, dateToString(createdOn));
            String sysJournalFieldKey = getSysJournalFieldKey(response);
            snrs = new ServiceNowRESTService(getInstance());
            String sysJournalFieldUrl = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/sys_journal_field/" + sysJournalFieldKey + "?sysparm_display_value=false";
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
        if (entry.getFieldName().equals("opened_at") || entry.getFieldName().equals("resolved_at") || entry.getFieldName().equals("closed_at")) {
            sysAuditPayload += "\"newvalue\" : ";
            sysAuditPayload += "\"" + entry.getSysCreatedOn() + "\",";
        }
        sysAuditPayload += "\"reason\":";
        sysAuditPayload += "\"" + entry.getReason() + "\"";
        sysAuditPayload += "}";

        return sysAuditPayload;
    }

    private String getUpdatedSysAuditPayloadBatch(final SysAuditEntry entry)
    {
        SysAuditEntryPK pk = ((SysAuditEntryPK) entry.getPK());
        String auditLogRecordSysId = pk.getSysId();

        String sysAuditPayload = "{";
        sysAuditPayload += "\"sys_id\":";
        sysAuditPayload += "\"" + auditLogRecordSysId + "\",";
        sysAuditPayload += "\"sys_created_on\":";
        sysAuditPayload += "\"" + entry.getSysCreatedOn() + "\",";
        if (entry.getFieldName().equals("opened_at") || entry.getFieldName().equals("resolved_at") || entry.getFieldName().equals("closed_at")) {
            sysAuditPayload += "\"newvalue\" : ";
            sysAuditPayload += "\"" + entry.getSysCreatedOn() + "\",";
        }
        sysAuditPayload += "\"reason\":";
        sysAuditPayload += "\"" + entry.getReason() + "\"";
        sysAuditPayload += "}";

        return sysAuditPayload;
    }

    private String dateToString(final DateTime dt)
    {
        if (dt.isAfterNow()) {
            throw new RuntimeException("ERROR: It is not possible to create a Date (" + dt + ") in the future and after NOW (" + DateTime.now() + ")");
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = formatter.print(dt);

        return formattedDateTime;
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

    private static final Logger logger = LoggerFactory.getLogger(DemoModelProcessMiningInstances.class);
}