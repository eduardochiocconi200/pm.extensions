package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.sc.dao.SysAuditLogDAOREST;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorkflowAuditLogGeneration
{
    public static void main(String args[])
    {
        ServiceNowInstance instance = new ServiceNowInstance(ServiceNowTestCredentials.getInstanceName(), ServiceNowTestCredentials.getUserName(), ServiceNowTestCredentials.getPassword());
        ArrayList<String> incidentIds = getIncidentIds(instance);
        System.out.println("# of Active = False Incidents: (" + incidentIds.size() + ")");
        SysAuditLogDAOREST sysAuditLogDAO = new SysAuditLogDAOREST(instance);
        StringBuffer sb = new StringBuffer();
        sb.append("[\n");
        for (int i=0; i < incidentIds.size(); ) {            
            int endBatchSize = ((i+1)*BATCH_SIZE) > incidentIds.size() ? incidentIds.size() : ((i+1)*BATCH_SIZE);
            List<String> batch = incidentIds.subList(i*BATCH_SIZE, endBatchSize);
            SysAuditLog sysAuditLog = sysAuditLogDAO.findByIds(new SysAuditLogPK("incident"), batch);
            boolean firstEntry = true;
            for (SysAuditEntry sysEntry : sysAuditLog.getLog()) {
                if (!firstEntry) {
                    sb.append(",\n");
                }
                sb.append(sysEntry.toJSON());
                firstEntry = false;
            }
            i++;
            if (endBatchSize == incidentIds.size()) {
                break;
            }
            sb.append(",\n");            
        }
        sb.append("\n]");

        String fileName = "server/pm/src/test/resources/simulation/audit-log-3.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(sb.toString());
            System.out.println("Successfully created an audit trail file at: (" + fileName + ")");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static ArrayList<String> getIncidentIds(final ServiceNowInstance instance)
    {
        ArrayList<String> incidentIds = new ArrayList<String>();
		ServiceNowRESTService snrs = new ServiceNowRESTService(instance);
        String url = "https://" + instance.getInstance() + "/api/now/table/incident?sysparm_query=active=false" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "closed_at" + URLEncoder.encode(">", StandardCharsets.UTF_8) + "2024-06-01" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "closed_at" + URLEncoder.encode("<", StandardCharsets.UTF_8) + "2025-01-01&sysparm_fields=sys_id&sysparm_limit=500000&sysparm_offset=0";
        System.out.println("URL: (" + url + ")");
        String response = snrs.executeGetRequest(url);

        if (response != null && !response.equals("")) {
            JSONObject incidentsResponse = new JSONObject(response);
            if (incidentsResponse.has("result")) {
                JSONArray incidentEntries = incidentsResponse.getJSONArray("result");
                for (int i=0; i < incidentEntries.length(); i++) {
                    JSONObject resultObject = (JSONObject) incidentEntries.get(i);
                    if (resultObject.has("sys_id")) {
                        String sysId = resultObject.getString("sys_id");
                        incidentIds.add(sysId);
					}
				}
			}
		}

        return incidentIds;
    }

    private final static int BATCH_SIZE = 250;
}