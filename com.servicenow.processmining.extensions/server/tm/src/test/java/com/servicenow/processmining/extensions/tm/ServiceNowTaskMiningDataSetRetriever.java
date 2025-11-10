package com.servicenow.processmining.extensions.tm;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;

import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceNowTaskMiningDataSetRetriever
{
    private ServiceNowInstance instance = null;

    public ServiceNowTaskMiningDataSetRetriever()
    {
    }
    
    public boolean retrieve()
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/sn_tm_core_user_data?";
        url += "sysparm_display_value=true&";
        // url += "sysparm_query=user=" + URLEncoder.encode("eduardo.chiocconi", StandardCharsets.UTF_8) + "&";
		url += "sysparm_fields=sys_id,date,host_name,sys_created_by,start_date_time,start_date_time_utc,application_name,screen_name,url,mouse_click_count,step_duration,sys_created_on,user,start_date_time_original,sys_updated_on,sys_updated_by";
		logger.debug("URL: (" + url + ")");
        System.out.println("URL: (" + url + ")");

		String response = snrs.executeGetRequest(url);

		if (response == null || response != null && response.equals("")) {
			logger.error("Could not properly connect to the ServiceNow Instance");
			return false;
		}

		JSONObject taskMiningCoreUserDataResponse = new JSONObject(response);
		logger.debug("Retrieving Task Mining Core User Data: ");
        System.out.println("Retrieving Task Mining Core User Data: ");
		if (taskMiningCoreUserDataResponse.has("result")) {
			JSONArray coreUserDataCollection = taskMiningCoreUserDataResponse.getJSONArray("result");
            for (int i=0; i < coreUserDataCollection.length(); i++) {
                JSONObject coreUserDataEntity = coreUserDataCollection.getJSONObject(i);
                System.out.println("[" + i + "] = (" + coreUserDataEntity + "]");
            }
		}

		return true;
    }

    public ServiceNowInstance getInstance()
    {
        if (this.instance == null) {
            this.instance = new ServiceNowInstance(instanceid, user, password);
        }

        return this.instance;
    }

    private static final String instanceid = "taskminingdemo.service-now.com";
    private static final String user = "admin";
    private static final String password = "rifni1-nisrit-bixviB";

	private static final Logger logger = LoggerFactory.getLogger(ServiceNowTaskMiningDataSetRetriever.class);
}
