package com.servicenow.processmining.extensions.pm.demo;

import com.servicenow.processmining.extensions.sc.dao.SysAuditLogDAOREST;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntryPK;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
            DateTime pathFirstStartTime = DateTime.now().minusSeconds((int)path.getTotalDuration());
            DateTime start = DateTime.now();
            System.out.println("Creating [" + path.getCount() + "] [" + path.getTable() + "] records for path defined in Tab: [" + path.getPathName() + "]. (A '.' will be printed for each created record. Be patient!)");
            for (int count=0; count < path.getCount(); count++) {
                if (!createCase(path, pathFirstStartTime)) {
                    return false;
                }
                System.out.print(".");
                pathFirstStartTime.minusSeconds((int)path.getCreationDelta());
            }
            double elapsedTime = DateTime.now().minus(start.getMillis()).getMillis() / 1000.0 / 60.0;
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
		String payload = createPayload(values);
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
		payload = createPayload(creationUpdateValues);
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

    private String createPayload(final HashMap<String, String> values)
    {
        boolean processedFirstEntry = false;
        String payload = "{";
        for (String key : values.keySet()) {
            if (processedFirstEntry) {
                payload += ",";
            }
            String value = getValue(values.get(key));
            payload += "\"" + key + "\":\"" + value + "\"";
            processedFirstEntry = true;
        }
        payload += "}";

        return payload;
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
            if (!processUpdateRecordBatch(path, updateTime, recordUpdateTS, updateBatches.get(updateTime))) {
                return false;
            }
            previousUpdateTS = updateTime;
        }

        return true;
    }

    private boolean processUpdateRecordBatch(final DemoModelPath path, final Double updateTime, final DateTime createdOn, HashMap<String, String> updateValues)
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
	String url = "https://" + getInstance().getInstance() + "/api/now/table/" + path.getTable() + "/" + this.caseSysId;
	String payload = createPayload(updateValues);
	String response = snrs.executePutRequest(url, payload);
	if (response == null || response != null && response.equals("")) {
		System.err.println("ERROR: Could not update the (" + path.getTable() + ") record with timestamp: (" + updateTime + ") and values: (" + updateValues + ") referenced in Tab: (" + path.getPathName() + ").");
		System.err.println("Make sure all needed attributes and values in your spreadsheet for the time (" + updateTime + "), and values (" + updateValues + ") provided are correct.");
		System.err.println("This way, the update transaction can be completed successfully.");
		return false;
	}

        return fixAuditTrail(path, createdOn);
    }

    private boolean fixAuditTrail(final DemoModelPath path, final DateTime createdOn)
    {
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(caseSysId);
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK(path.getTable()), ids);
        for (SysAuditEntry entry : sysAuditLog.getLog()) {
            if (entry.getReason().equals("")) {
                // Fix creation time ...
                entry.setSysCreatedOn(dateToString(createdOn));
                entry.setReason("itsm_pm_eval");
                String payload = getUpdatedSysAuditPayload(entry);
                SysAuditEntryPK pk = ((SysAuditEntryPK) entry.getPK());
                ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
                String url = "https://" + getInstance().getInstance() + "/api/snc/fixaudittrail/sys_audit/" + pk.getSysId();
                String response = snrs.executePutRequest(url, payload);
                if (response == null || response != null && response.equals("")) {
                    return false;
                }
            }
        }

        return true;
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