package com.servicenow.processmining.extensions.pm.simulation.generate;

import com.servicenow.processmining.extensions.sc.dao.SysAuditLogDAOREST;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

public class ServiceNowAuditExporter
{
    private ServiceNowInstance instance = null;
    private ArrayList<String> incidentSysIds = null;

    public static void main(String args[])
    {
        ServiceNowAuditExporter snae = new ServiceNowAuditExporter();
        snae.exportIncidentsAuditData();
    }

    public ServiceNowAuditExporter()
    {
    }

    private boolean exportIncidentsAuditData()
    {
        boolean useRecordsQuery = false;
        if (useRecordsQuery) {
            Assert.assertTrue(getIncidentRecordSysIds());
            Assert.assertTrue(getIncidentAuditRecords());
        }
        else {
            Assert.assertTrue(getIncidentAuditRecordsByRemarks("Simulation60"));
        }

        return true;
    }

    private boolean getIncidentRecordSysIds()
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());

        this.incidentSysIds = new ArrayList<String>();

        // Query: https://empechiocconi1.service-now.com/api/now/table/incident?sysparm_query=active=false^closed_at%3E2023-01-01^closed_at%3C2024-01-01&sysparm_fields=sys_id,closed_at
        String url = "https://" + getInstance().getInstance() + "/api/now/table/incident?sysparm_query=active=false" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "closed_at%3E2023-01-01" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "closed_at%3C2024-01-01&sysparm_fields=sys_id&sysparm_limit=100";

        String response = snrs.executeGetRequest(url);
        if (response == null || response != null && response.equals("")) {
            System.err.println("The requested REST API operation to retrieve incidents could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
            return false;
        }

        JSONObject sysAuditLogResponse = new JSONObject(response);
        if (sysAuditLogResponse.has("result")) {
            JSONArray sysAuditLogEntries = sysAuditLogResponse.getJSONArray("result");
            for (int i=0; i < sysAuditLogEntries.length(); i++) {
                JSONObject resultObject = (JSONObject) sysAuditLogEntries.get(i);
                if (resultObject.has("sys_id")) {
                    String id = resultObject.getString("sys_id");
                    incidentSysIds.add(id);
                }
            }
            System.out.println("Retrieved (" + incidentSysIds.size() + ") records");
        }

        return true;
    }

    private boolean getIncidentAuditRecords()
    {
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        SysAuditLog sysAuditLog = wfDAO.findByIds(new SysAuditLogPK("incident"), this.incidentSysIds, "");
        System.out.println("Audit Logs: (" + sysAuditLog.getLog().size() + ")");

        try (FileWriter writer = new FileWriter("server/pm/src/test/resources/simulation/newAuditLog.json")) {
            boolean processedFirstRecord = false;
            writer.write("[\n");
            for (SysAuditEntry entry : sysAuditLog.getLog()) {
                if (processedFirstRecord) {
                    writer.write(",\n");
                }
                writer.write(entry.toJSON());
                processedFirstRecord = true;
            }
            writer.write("\n]");

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        return true;
    }

    private boolean getIncidentAuditRecordsByRemarks(final String remarks)
    {
        SysAuditLogDAOREST wfDAO = new SysAuditLogDAOREST(getInstance());
        SysAuditLog sysAuditLog = wfDAO.findByCondition(new SysAuditLogPK("incident"), "reason=" + remarks);
        System.out.println("Audit Logs: (" + sysAuditLog.getLog().size() + ")");

        try (FileWriter writer = new FileWriter("server/pm/src/test/resources/simulation/newAuditLog.json")) {
            boolean processedFirstRecord = false;
            writer.write("[\n");
            for (SysAuditEntry entry : sysAuditLog.getLog()) {
                if (processedFirstRecord) {
                    writer.write(",\n");
                }
                writer.write(entry.toJSON());
                processedFirstRecord = true;
            }
            writer.write("\n]");

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        return true;
    }

    private ServiceNowInstance getInstance()
    {
        if (instance == null) {
            instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        }

        return instance;
    }

    protected static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    protected static final String snUser = ServiceNowTestCredentials.getUserName();
    protected static final String snPassword = ServiceNowTestCredentials.getPassword();
}