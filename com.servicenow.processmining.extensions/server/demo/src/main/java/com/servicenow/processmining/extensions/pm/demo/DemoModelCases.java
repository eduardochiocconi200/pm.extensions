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
import org.joda.time.DateTimeZone;
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
            DateTime batchStartTime = DateTime.now();
            DateTime startCreationOfRecords = path.getCreationStartTime().withZone(DateTimeZone.UTC);
            DateTime pathFirstStartTime = startCreationOfRecords.minusSeconds((int)path.getTotalDuration());
            if (!loadChoiceValues(path)) {
                return false;
            }
            System.out.println("Creating [" + path.getCount() + "] [" + path.getTable() + "] records for path defined in Tab: [" + path.getPathName() + "] starting on (" + startCreationOfRecords + "). (A '.' will be printed for each created record. Be patient!)");
            for (int count=0; count < path.getCount(); count++) {
                if (!createCase(path, pathFirstStartTime)) {
                    return false;
                }
                System.out.print(".");
                pathFirstStartTime = pathFirstStartTime.minusSeconds((int)path.getCreationDelta());
            }
            double elapsedTime = DateTime.now().minus(batchStartTime.getMillis()).getMillis() / 1000.0 / 60.0;
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
        boolean batch = false;
        HashMap<String, String> values = path.getInitialValues();
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		String url = "https://" + getInstance().getInstance() + "/api/now/table/" + path.getTable() + "?sysparm_display_value=false";
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

        url = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/" + path.getTable() + "/" + this.caseSysId + "?sysparm_display_value=false";
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

        if (batch) {
            return fixAuditTrailBatch(path, createdOn);
        }
        else {
            return fixAuditTrail(path, createdOn);
        }
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
        DateTime previousRecordUpdateTS = null;
        DateTime recordUpdateTS = createdOn;
        Double previousUpdateTS = null;
        for (Double updateTime : updateBatches.keySet()) {
            logger.debug("Update Batch: (" + updateTime + ") = (" + updateBatches.get(updateTime) + ")");
            if (previousUpdateTS == null) {
                recordUpdateTS = recordUpdateTS.plusSeconds(updateTime.intValue());
            }
            else {
                recordUpdateTS = recordUpdateTS.plusSeconds(updateTime.intValue() - previousUpdateTS.intValue());
                recordUpdateTS = adjustRandomVariation(recordUpdateTS);
            }

            if (previousUpdateTS != null && updateBatches.get(previousUpdateTS).get(DemoModelPath.TASK_SCRIPT_FIELD_NAME) != null) {
                String taskName = updateBatches.get(previousUpdateTS).get(DemoModelPath.TASK_SCRIPT_FIELD_NAME);
                if (!processCaseTask(previousRecordUpdateTS, recordUpdateTS, taskName)) {
                    return false;
                }
            }

            if (!processUpdateRecordBatch(path, previousUpdateTS, updateTime, recordUpdateTS, updateBatches.get(updateTime))) {
                return false;
            }
            previousUpdateTS = updateTime;
            previousRecordUpdateTS = recordUpdateTS;
        }

        return true;
    }

    // Let's add a random deviation to the createdOn to avoid all data being equally sparsed.
    private DateTime adjustRandomVariation(final DateTime recordUpdateTS)
    {
        Random random = new Random();
        // We will create a randomness of 5 mins (600 seconds) + o - the next time.
        int seconds = (int) random.nextDouble(120);
        seconds = (seconds % 2 == 0) ? seconds : (seconds * -1);
        DateTime adjustedTime = recordUpdateTS.plusSeconds(seconds);
        if (adjustedTime.isAfterNow()) {
            adjustedTime = DateTime.now();
        }

        return adjustedTime;
    }

    private boolean processCaseTask(final DateTime previousUpdateTS, final DateTime recordUpdateTS, final String taskName)
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
	String url = "https://" + getInstance().getInstance() + "/api/sn_tm_core/taskmininginteraction?sysparm_display_value=false";
	// We need to make sure the random delta for step execution does not burn until the task breakdown.
        // DateTime taskStartTS = previousUpdateTS.getMillis() < recordUpdateTS.getMillis() ? recordUpdateTS : previousUpdateTS.plusMinutes(1);
	String tasksPayload = createTaskPayload(previousUpdateTS, recordUpdateTS, taskName);
	String response = snrs.executePostRequest(url, tasksPayload);
	if (response == null || response != null && response.equals("")) {
		logger.error("Failed invoking " + "https://" + getInstance().getInstance() + "/api/sn_tm_core/taskmininginteraction endpoint.");
		logger.error("Error Code: (" + snrs.getErrorStatusCode() + ") - (" + snrs.getErrorMessage() + ")");
		return false;
	}

        return true;
    }

    private String createTaskPayload(final DateTime previousUpdateTS, final DateTime recordUpdateTS, final String taskName)
    {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        String payload = "[\n";
        double totalTaskExecutionTimeInMillis = recordUpdateTS.getMillis() - previousUpdateTS.getMillis();

        boolean processedFirstEntry = false;
        Double taskDuration = 0.0;
        Double accumulatedTasksDuration = 0.0;
        for (DemoModelTaskEntry taskEntry : getModel().getTask(taskName).getEntries()) {
            if (processedFirstEntry) {
                payload += ",\n";
            }

            // Make Proper time + timezone adjustments.
            String formattedDateTime = adjustTaskDateTime(previousUpdateTS, formatter, accumulatedTasksDuration);
            taskDuration = ((taskEntry.getPercentageOfTotalTaskExecutionTime()*totalTaskExecutionTimeInMillis)/100.00);
            accumulatedTasksDuration += taskDuration;

            payload += "{\n";
            payload += "\"userId\": \"" + taskEntry.getUserId() + "\", \n";
            payload += "\"hostName\": \"" + taskEntry.getHostName() + "\", \n";
            payload += "\"applicationName\": \"" + taskEntry.getApplicationName() + "\", \n";
            payload += "\"screenName\": \"" + taskEntry.getScreenName() + "\", \n";
            payload += "\"url\": \"" + (taskEntry.getURL() == null ? "" : taskEntry.getURL()) + "\", \n";
            payload += "\"startDateTime\": \"" + formattedDateTime + "\", \n";
            payload += "\"duration\": " + (taskDuration/1000.00) + ", \n";
            payload += "\"mouseClickCount\": " + taskEntry.getMouseClickCount() + " \n";
            payload += "}";
            processedFirstEntry = true;
        }

        if (totalTaskExecutionTimeInMillis != accumulatedTasksDuration.doubleValue()) {
            throw new RuntimeException("Total Task: (" + totalTaskExecutionTimeInMillis + ") != (" + accumulatedTasksDuration.doubleValue() + ") : Accumulated Task.");
        }

        payload += "\n]";
    
        return payload;
    }

    private String adjustTaskDateTime(final DateTime recordUpdateTS, final DateTimeFormatter formatter, final Double duration)
    {
        DateTime taskDateTime = recordUpdateTS.plusMillis(duration.intValue());

        DateTimeZone localTimeZone = DateTimeZone.getDefault();
        DateTimeZone utcTimeZone = DateTimeZone.forID("UTC");

        // Get the offset in milliseconds for each time zone at the reference instant
        int localTZOffsetMillis = localTimeZone.getOffset(taskDateTime);
        int utcTZOffsetMillis = utcTimeZone.getOffset(taskDateTime);

        // Calculate the difference in offsets
        int offsetDifferenceMillis = utcTZOffsetMillis - localTZOffsetMillis;

        // Convert the difference to hours
        taskDateTime = taskDateTime.plusMillis(offsetDifferenceMillis);
       
        String formattedDateTime = formatter.print(taskDateTime);

        return formattedDateTime;
    }

    private boolean processUpdateRecordBatch(final DemoModelPath path, final Double previousUpdateTime, final Double updateTime, final DateTime createdOn, HashMap<String, String> updateValues)
    {
        boolean batch = false;
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/" + path.getTable() + "/" + this.caseSysId + "?sysparm_display_value=false";
        String payload = createPayload(path, updateValues);
        String response = snrs.executePutRequest(url, payload);
        if (response == null || response != null && response.equals("")) {
            System.err.println("ERROR: Could not update the (" + path.getTable() + ") record with timestamp: (" + updateTime + ") and values: (" + updateValues + ") referenced in Tab: (" + path.getPathName() + ").");
            System.err.println("Make sure all needed attributes and values in your spreadsheet for the time (" + updateTime + "), and values (" + updateValues + ") provided are correct.");
            System.err.println("This way, the update transaction can be completed successfully.");
            return false;
        }

        // Let's add a random deviation to the createdOn to avoid all data being equally sparsed.
/*
        Random random = new Random();
        // We will create a randomness of 5 mins (600 seconds) + o - the next time.
        int seconds = (int) random.nextDouble(600);
        seconds = (seconds % 2 == 0) ? seconds : (seconds * -1);
        DateTime adjustedTime = createdOn.plusSeconds(seconds);
        if (adjustedTime.isAfterNow()) {
            adjustedTime = DateTime.now();
        }
*/

        if (batch) {
            return fixAuditTrailBatch(path, createdOn);
        }
        else {
            return fixAuditTrail(path, createdOn);
        }
    }

    private boolean fixAuditTrail(final DemoModelPath path, final DateTime createdOn)
    {
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(caseSysId);
        SysAuditLog sysAuditLog = getEmptyReasonSysAuditLogForIds(path, wfDAO, ids);
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

    private boolean fixAuditTrailBatch(final DemoModelPath path, final DateTime createdOn)
    {
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<SysAuditEntry> workNotesOrCommentsEntries = new ArrayList<SysAuditEntry>();
        ids.add(caseSysId);
        SysAuditLog sysAuditLog = getEmptyReasonSysAuditLogForIds(path, wfDAO, ids);
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

    private SysAuditLog getEmptyReasonSysAuditLogForIds(final DemoModelPath path, SysAuditLogDAOREST wfDAO, ArrayList<String> ids)
    {
        SysAuditLogPK pk = new SysAuditLogPK(path.getTable());
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
