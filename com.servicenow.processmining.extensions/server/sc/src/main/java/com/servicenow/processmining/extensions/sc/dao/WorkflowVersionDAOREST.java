package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.WorkflowVersion;
import com.servicenow.processmining.extensions.sc.entities.WorkflowVersionPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;
import com.servicenow.processmining.extensions.sn.dao.GenericDAOREST;
import com.servicenow.processmining.extensions.sn.exceptions.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowVersionDAOREST
	extends GenericDAOREST<WorkflowVersion, WorkflowVersionPK>
{
	public WorkflowVersionDAOREST(final ServiceNowInstance instance)
	{
		super(instance);
	}

	@Override
	public WorkflowVersion findById(WorkflowVersionPK id)
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/wf_workflow?";
		url += "sysparm_query=sys_id=" + id.getSysId() + "&";
		url += "sysparm_fields=sys_id,name,description,table,workflow.sys_id,sys_created_on,sys_updated_on";
        String response = snrs.executeGetRequest(url);

        if (response == null || response != null && response.equals("")) {
            logger.error("Could not properly connect to the ServiceNow Instance");
            return null;
        }

        JSONObject workflowResponse = new JSONObject(response);
        logger.debug("Retrieving Workflow: ");
		WorkflowVersion workflow = null;
        if (workflowResponse.has("result")) {
            JSONArray wf = workflowResponse.getJSONArray("result");
            if (wf.length() > 1) {
				throw new RuntimeException("Tried to access one single element and obtained more!");
			}
			else if (wf.length() == 0) {
				return null;
			}
			else {
                JSONObject resultObject = (JSONObject) wf.get(0);
                if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String name = resultObject.getString("name");
					String description = resultObject.getString("description");
					String table = resultObject.getString("table");
					String workflowId = resultObject.getString("workflow.sys_id");
					String createdOn = resultObject.getString("sys_created_on");
					String updatedOn = resultObject.getString("sys_updated_on");

					workflow = new WorkflowVersion(new WorkflowVersionPK(sysId));
					workflow.setName(name);
					workflow.setDescription(description);
					workflow.setTable(table);
					workflow.setWorkflowId(workflowId);
					workflow.setCreatedOn(createdOn);
					workflow.setLastUpdatedOn(updatedOn);
                }
            }
		}

		return workflow;
	}

	@Override
	public List<WorkflowVersion> findAll()
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		String url = "https://" + getInstance().getInstance() + "/api/now/table/wf_workflow_version?";
		url += "sysparm_fields=sys_id,name,description,table,workflow.sys_id,sys_created_on,sys_updated_on";
		String response = snrs.executeGetRequest(url);

		if (response == null || response != null && response.equals("")) {
			logger.error("Could not properly connect to the ServiceNow Instance");
			return null;
		}

		JSONObject workflowsResponse = new JSONObject(response);
		logger.debug("Retrieving Workflows: ");
		List<WorkflowVersion> workflows = new ArrayList<WorkflowVersion>();
		if (workflowsResponse.has("result")) {
			JSONArray workflowItems = workflowsResponse.getJSONArray("result");
			for (int i = 0; i < workflowItems.length(); i++) {
				JSONObject resultObject = (JSONObject) workflowItems.get(i);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String name = resultObject.getString("name");
					String description = resultObject.getString("description");
					String table = resultObject.getString("table");
					String workflowId = resultObject.getString("workflow.sys_id");
					String createdOn = resultObject.getString("sys_created_on");
					String updatedOn = resultObject.getString("sys_updated_on");

					WorkflowVersion wf = new WorkflowVersion(new WorkflowVersionPK(sysId));
					wf.setName(name);
					wf.setDescription(description);
					wf.setTable(table);
					wf.setWorkflowId(workflowId);
					wf.setCreatedOn(createdOn);
					wf.setLastUpdatedOn(updatedOn);

					workflows.add(wf);
				}
			}
		}

		return workflows;
	}

	public List<WorkflowVersion> findAllActive()
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		String url = "https://" + getInstance().getInstance() + "/api/now/table/wf_workflow_version?";
		url += "sysparm_query=active=true&";
		url += "sysparm_fields=sys_id,name,description,table,workflow.sys_id,sys_created_on,sys_updated_on";
		String response = snrs.executeGetRequest(url);

		if (response == null || response != null && response.equals("")) {
            logger.error("The requested REST API operation could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
			return null;
		}

		JSONObject workflowsResponse = new JSONObject(response);
		logger.debug("Retrieving Workflows: ");
		List<WorkflowVersion> workflows = new ArrayList<WorkflowVersion>();
		if (workflowsResponse.has("result")) {
			JSONArray workflowItems = workflowsResponse.getJSONArray("result");
			for (int i = 0; i < workflowItems.length(); i++) {
				JSONObject resultObject = (JSONObject) workflowItems.get(i);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String name = resultObject.getString("name");
					String description = resultObject.getString("description");
					String table = resultObject.getString("table");
					String workflowId = resultObject.has("workflow.sys_id") ? resultObject.getString("workflow.sys_id") : "";
					String createdOn = resultObject.getString("sys_created_on");
					String updatedOn = resultObject.getString("sys_updated_on");

					WorkflowVersion wf = new WorkflowVersion(new WorkflowVersionPK(sysId));
					wf.setName(name);
					wf.setDescription(description);
					wf.setTable(table);
					wf.setWorkflowId(workflowId);
					wf.setCreatedOn(createdOn);
					wf.setLastUpdatedOn(updatedOn);

					workflows.add(wf);
				}
			}
		}

		return workflows;
	}

	@Override
	public boolean existEntity(WorkflowVersionPK id)
		throws ObjectNotFoundException
	{
		return findById(id) != null;
	}

	private static final Logger logger = LoggerFactory.getLogger(WorkflowVersionDAOREST.class);
}