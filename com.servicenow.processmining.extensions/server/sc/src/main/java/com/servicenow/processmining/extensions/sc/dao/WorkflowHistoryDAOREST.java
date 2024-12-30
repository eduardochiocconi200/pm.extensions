package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.WorkflowHistory;
import com.servicenow.processmining.extensions.sc.entities.WorkflowHistoryEntry;
import com.servicenow.processmining.extensions.sc.entities.WorkflowHistoryEntryPK;
import com.servicenow.processmining.extensions.sc.entities.WorkflowHistoryPK;
import com.servicenow.processmining.extensions.sc.entities.WorkflowVersion;
import com.servicenow.processmining.extensions.sc.entities.WorkflowVersionPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;
import com.servicenow.processmining.extensions.sn.dao.GenericDAOREST;
import com.servicenow.processmining.extensions.sn.exceptions.ObjectNotFoundException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowHistoryDAOREST
	extends GenericDAOREST<WorkflowHistory, WorkflowHistoryPK>
{
	public WorkflowHistoryDAOREST(final ServiceNowInstance instance)
	{
		super(instance);
	}

	@Override
	public WorkflowHistory findById(WorkflowHistoryPK id)
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/wf_history?";
		url += "sysparm_query=workflow_version.sys_id=" + id.getWorkflowVersionId() + URLEncoder.encode("^", StandardCharsets.UTF_8) + "GROUPBYcontext.sys_id" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "ORDERBYactivity_index&";
		url += "sysparm_fields=sys_id,context.sys_id,activity_index,activity.name,sys_created_on,sys_created_by";
        String response = snrs.executeGetRequest(url);

        if (response == null || response != null && response.equals("")) {
            logger.error("The requested REST API operation could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
            return null;
        }

        JSONObject workflowHistoryResponse = new JSONObject(response);
        logger.debug("Retrieving Workflow History: ");
		WorkflowHistory workflowHistory = null;
        if (workflowHistoryResponse.has("result")) {
            JSONArray wfHistory = workflowHistoryResponse.getJSONArray("result");
			workflowHistory = new WorkflowHistory(id);
			for (int i=0; i < wfHistory.length(); i++) {
				JSONObject resultObject = (JSONObject) wfHistory.get(i);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String wfInstId = resultObject.getString("context.sys_id");
					String index = resultObject.getString("activity_index");
					String activityName = resultObject.getString("activity.name");
					String createdOn = resultObject.getString("sys_created_on");
					String createdBy = resultObject.getString("sys_created_by");

					WorkflowHistoryEntry entry = new WorkflowHistoryEntry(new WorkflowHistoryEntryPK(sysId));
					entry.setWorkflowInstanceId(wfInstId);
					entry.setIndex(index);
					entry.setActivityName(activityName);
					entry.setSysCreatedOn(createdOn);
					entry.setSysCreatedBy(createdBy);

					workflowHistory.getHistory().add(entry);
				}
            }
		}

		return workflowHistory;
	}

	@Override
	public List<WorkflowHistory> findAll()
		throws ObjectNotFoundException
	{
		ArrayList<WorkflowHistory> wfHistory = new ArrayList<WorkflowHistory>();
		WorkflowVersionDAOREST wfDAO = new WorkflowVersionDAOREST(getInstance());
        List<WorkflowVersion> wfItems = wfDAO.findAll();
		for (WorkflowVersion wv : wfItems) {
			WorkflowHistoryPK pk = new WorkflowHistoryPK(((WorkflowVersionPK)wv.getPK()).getSysId());
			WorkflowHistory wfH = this.findById(pk);
			wfHistory.add(wfH);
		}

		return wfHistory;
	}

	@Override
	public boolean existEntity(WorkflowHistoryPK id)
		throws ObjectNotFoundException
	{
		return findById(id) != null;
	}

	private static final Logger logger = LoggerFactory.getLogger(WorkflowHistoryDAOREST.class);
}