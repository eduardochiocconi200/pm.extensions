package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.FlowVersion;
import com.servicenow.processmining.extensions.sc.entities.FlowVersionPK;
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

public class FlowVersionDAOREST
	extends GenericDAOREST<FlowVersion, FlowVersionPK>
{
	public FlowVersionDAOREST(final ServiceNowInstance instance)
	{
		super(instance);
	}

	@Override
	public FlowVersion findById(FlowVersionPK id)
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_hub_flow?";
		url += "sysparm_query=sys_id=" + id.getSysId() + URLEncoder.encode("^", StandardCharsets.UTF_8);
		url += "sysparm_fields=sys_id,name,description,type,copied_from,sys_created_on,sys_updated_on";
        String response = snrs.executeGetRequest(url);

        if (response == null || response != null && response.equals("")) {
            logger.error("Could not properly connect to the ServiceNow Instance");
            return null;
        }

        JSONObject workflowResponse = new JSONObject(response);
        logger.debug("Retrieving Flow: ");
		FlowVersion flow = null;
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
					String type = resultObject.getString("type");
					String parentFlowId = resultObject.getString("copied_from");
					String createdOn = resultObject.getString("sys_created_on");
					String updatedOn = resultObject.getString("sys_updated_on");

					flow = new FlowVersion(new FlowVersionPK(sysId));
					flow.setName(name);
					flow.setDescription(description);
					flow.setType(type);
					flow.setParentFlowId(parentFlowId);
					flow.setCreatedOn(createdOn);
					flow.setLastUpdatedOn(updatedOn);
                }
            }
		}

		return flow;
	}

	@Override
	public List<FlowVersion> findAll()
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_hub_flow?";
		url += "sysparm_fields=sys_id,name,description,type,copied_from,sys_created_on,sys_updated_on";
		String response = snrs.executeGetRequest(url);

		if (response == null || response != null && response.equals("")) {
			logger.error("Could not properly connect to the ServiceNow Instance");
			return null;
		}

		JSONObject flowsResponse = new JSONObject(response);
		logger.debug("Retrieving Flows: ");
		FlowVersion flow = null;
		List<FlowVersion> flows = new ArrayList<FlowVersion>();
		if (flowsResponse.has("result")) {
			JSONArray flowItems = flowsResponse.getJSONArray("result");
			for (int i = 0; i < flowItems.length(); i++) {
				JSONObject resultObject = (JSONObject) flowItems.get(i);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String name = resultObject.getString("name");
					String description = resultObject.getString("description");
					String type = resultObject.getString("type");
					String parentFlowId = resultObject.getString("copied_from");
					String createdOn = resultObject.getString("sys_created_on");
					String updatedOn = resultObject.getString("sys_updated_on");

					flow = new FlowVersion(new FlowVersionPK(sysId));
					flow.setName(name);
					flow.setDescription(description);
					flow.setType(type);
					flow.setParentFlowId(parentFlowId);
					flow.setCreatedOn(createdOn);
					flow.setLastUpdatedOn(updatedOn);

					flows.add(flow);
				}
			}
		}

		return flows;
	}

	public List<FlowVersion> findAllActive()
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_hub_flow?";
		url += "sysparm_query=active=true&";
		url += "sysparm_fields=sys_id,name,description,type,copied_from,sys_created_on,sys_updated_on";
		logger.debug("FlowVersionDAOREST.findAllActive: URL: (" + url + ")");
		String response = snrs.executeGetRequest(url);

		if (response == null || response != null && response.equals("")) {
			logger.error("Could not properly connect to the ServiceNow Instance");
			return null;
		}

		JSONObject flowsResponse = new JSONObject(response);
		logger.debug("Retrieving Flows: ");
		FlowVersion flow = null;
		List<FlowVersion> flows = new ArrayList<FlowVersion>();
		if (flowsResponse.has("result")) {
			JSONArray flowItems = flowsResponse.getJSONArray("result");
			for (int i = 0; i < flowItems.length(); i++) {
				JSONObject resultObject = (JSONObject) flowItems.get(i);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String name = resultObject.getString("name");
					String description = resultObject.getString("description");
					String type = resultObject.getString("type");
					String parentFlowId = resultObject.getString("copied_from");
					String createdOn = resultObject.getString("sys_created_on");
					String updatedOn = resultObject.getString("sys_updated_on");

					flow = new FlowVersion(new FlowVersionPK(sysId));
					flow.setName(name);
					flow.setDescription(description);
					flow.setType(type);
					flow.setParentFlowId(parentFlowId);
					flow.setCreatedOn(createdOn);
					flow.setLastUpdatedOn(updatedOn);

					flows.add(flow);
				}
			}
		}

		return flows;
	}

	@Override
	public boolean existEntity(FlowVersionPK id)
		throws ObjectNotFoundException
	{
		return findById(id) != null;
	}

	private static final Logger logger = LoggerFactory.getLogger(FlowVersionDAOREST.class);
}