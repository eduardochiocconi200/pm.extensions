package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.ServiceCatalogItem;
import com.servicenow.processmining.extensions.sc.entities.ServiceCatalogItemPK;
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

public class ServiceCatalogItemDAOREST
	extends GenericDAOREST<ServiceCatalogItem, ServiceCatalogItemPK>
{
	public ServiceCatalogItemDAOREST(final ServiceNowInstance instance)
	{
		super(instance);
	}

	@Override
	public ServiceCatalogItem findById(ServiceCatalogItemPK id)
		throws ObjectNotFoundException
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'findById'");
	}

	@Override
	public List<ServiceCatalogItem> findAll()
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/sc_cat_item?";
		url += "sysparm_fields=sys_name,name,workflow.sys_id,flow_designer_flow.sys_id,short_description,roles,sc_catalogs";
        String response = snrs.executeGetRequest(url);

        if (response == null || response != null && response.equals("")) {
            logger.error("The requested REST API operation could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
            return null;
        }

        JSONObject serviceCatalogItemsResponse = new JSONObject(response);
        logger.debug("Retrieving Service Catalog Items: ");
		List<ServiceCatalogItem> scItems = new ArrayList<ServiceCatalogItem>();
        if (serviceCatalogItemsResponse.has("result")) {
            JSONArray serviceCatalogItems = serviceCatalogItemsResponse.getJSONArray("result");
            for (int i = 0; i < serviceCatalogItems.length(); i++) {
                JSONObject resultObject = (JSONObject) serviceCatalogItems.get(i);
                if (resultObject.has("sys_name")) {
                    String sysName = resultObject.getString("sys_name");
					String name = resultObject.getString("name");
					String workflowId = resultObject.getString("workflow.sys_id");
					String flowDesignerFlowId = resultObject.getString("flow_designer_flow.sys_id");
					String shortDescription = resultObject.getString("short_description");
					String roles = resultObject.getString("roles");
					String scCatalogs = resultObject.getString("sc_catalogs");

					ServiceCatalogItem sci = new ServiceCatalogItem(new ServiceCatalogItemPK(sysName));
					sci.setName(name);
					sci.setWorkflowId(workflowId);
					sci.setFlowDesignerId(flowDesignerFlowId);
					sci.setShortDescription(shortDescription);
					sci.setRoles(roles);
					sci.setSCCatalogs(scCatalogs);

                    scItems.add(sci);
                }
            }
		}

		return scItems;
	}

	public List<ServiceCatalogItem> findAllActive()
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/sc_cat_item?";
		url += "sysparm_query=active=true&state=published&sysparm_fields=sys_name,name,workflow.sys_id,flow_designer_flow.sys_id,short_description,roles,sc_catalogs";
        String response = snrs.executeGetRequest(url);

        if (response == null || response != null && response.equals("")) {
            logger.error("Could not properly connect to the ServiceNow Instance");
            return null;
        }

        JSONObject serviceCatalogItemsResponse = new JSONObject(response);
        logger.debug("Retrieving Service Catalog Items: ");
		List<ServiceCatalogItem> scItems = new ArrayList<ServiceCatalogItem>();
        if (serviceCatalogItemsResponse.has("result")) {
            JSONArray serviceCatalogItems = serviceCatalogItemsResponse.getJSONArray("result");
            for (int i = 0; i < serviceCatalogItems.length(); i++) {
                JSONObject resultObject = (JSONObject) serviceCatalogItems.get(i);
                if (resultObject.has("sys_name")) {
                    String sysName = resultObject.getString("sys_name");
					String name = resultObject.getString("name");
					String workflowId = resultObject.getString("workflow.sys_id");
					String flowDesignerFlowId = resultObject.getString("flow_designer_flow.sys_id");
					String shortDescription = resultObject.getString("short_description");
					String roles = resultObject.getString("roles");
					String scCatalogs = resultObject.getString("sc_catalogs");

					ServiceCatalogItem sci = new ServiceCatalogItem(new ServiceCatalogItemPK(sysName));
					sci.setName(name);
					sci.setWorkflowId(workflowId);
					sci.setFlowDesignerId(flowDesignerFlowId);
					sci.setShortDescription(shortDescription);
					sci.setRoles(roles);
					sci.setSCCatalogs(scCatalogs);

                    scItems.add(sci);
                }
            }
		}

		return scItems;
	}

	@Override
	public boolean existEntity(ServiceCatalogItemPK id)
		throws ObjectNotFoundException
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'existEntity'");
	}

	    private static final Logger logger = LoggerFactory.getLogger(ServiceCatalogItemDAOREST.class);
}