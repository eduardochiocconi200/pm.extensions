package com.servicenow.processmining.extensions.pm.dao;

import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersion;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersionPK;
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

public class ProcessMiningModelVersionDAOREST
	extends GenericDAOREST<ProcessMiningModelVersion, ProcessMiningModelVersionPK>
{
	public ProcessMiningModelVersionDAOREST(final ServiceNowInstance instance)
	{
		super(instance);
	}

	@Override
	public ProcessMiningModelVersion findById(ProcessMiningModelVersionPK id)
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/promin_model_def_version?";
        url += "sysparm_query=sys_id=" + id.getSysId() + URLEncoder.encode("^", StandardCharsets.UTF_8) + "state=AVAILABLE" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "sys_created_by=" + getInstance().getUser() + URLEncoder.encode("^", StandardCharsets.UTF_8) + "release_nameIN" + PLATFORM_VERSIONS + "&";
		url += "sysparm_fields=sys_id,name,total_records,release_name,go_to_workbench,last_mined_time,project.sys_id";
		logger.debug("URL: (" + url + ")");

		String response = snrs.executeGetRequest(url);

		if (response == null || response != null && response.equals("")) {
			logger.error("Could not properly connect to the ServiceNow Instance");
			return null;
		}

		JSONObject modelVersionsResponse = new JSONObject(response);
		logger.debug("Retrieving Process Mining Model Versions: ");
		ProcessMiningModelVersion version = null;
		if (modelVersionsResponse.has("result")) {
			JSONArray mVersions = modelVersionsResponse.getJSONArray("result");
			if (mVersions.length() == 1) {
				JSONObject resultObject = (JSONObject) mVersions.get(0);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String name = resultObject.getString("name");
					int totalRecords = resultObject.getInt("total_records");
					String releaseName = resultObject.getString("release_name");
					String workbench = resultObject.getString("go_to_workbench");
					String lastMinedTime = resultObject.getString("last_mined_time");
					String projectId = resultObject.getString("project.sys_id");

					version = new ProcessMiningModelVersion(new ProcessMiningModelVersionPK(sysId));
					version.setName(name);
					version.setTotalRecords(totalRecords);
					version.setReleaseName(releaseName);
					version.setWorkbenchId(workbench);
					version.setLastMinedTime(lastMinedTime);
					version.setProjectId(projectId);
				}
			}
			else if (mVersions.length() > 1) {
				logger.error("Retrieved more than one entry with id: (" + id.getSysId() + ") when the result should have been JUST ONE OR ZERO.");
			}
		}

		return version;
	}

	@Override
	public List<ProcessMiningModelVersion> findAll()
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		List<ProcessMiningModelVersion> modelVersions = new ArrayList<ProcessMiningModelVersion>();

		// First we traverse the projects and then their project versions ... We do this to make sure we are respecting permissions.
        String url = "https://" + getInstance().getInstance() + "/api/now/table/promin_project?";
        url += "sysparm_query=state=AVAILABLE&";
		url += "sysparm_fields=sys_id";
		logger.debug("URL: (" + url + ")");

		String response = snrs.executeGetRequest(url);
		if (response == null || response != null && response.equals("")) {
			logger.error("Could not properly connect to the ServiceNow Instance");
			return null;
		}

		JSONObject modelsResponse = new JSONObject(response);
		ArrayList<String> projectIds = getProjectIds(modelsResponse);

		if (projectIds.size() > 0) {
			boolean continueProcessingVersions = true;
			int startIds = 0;
			do {
				url = "https://" + getInstance().getInstance() + "/api/now/table/promin_model_def_version?";
				url += "sysparm_query=state=AVAILABLE" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "sys_created_by=" + getInstance().getUser() + URLEncoder.encode("^", StandardCharsets.UTF_8) + "release_nameIN" + PLATFORM_VERSIONS + URLEncoder.encode("^", StandardCharsets.UTF_8) + "project.sys_idIN" + getProcessModelIds(projectIds, startIds) + "&";
				url += "sysparm_fields=sys_id,name,total_records,release_name,go_to_workbench,last_mined_time,project.sys_id";
				logger.debug("URL: (" + url + ")");

				response = snrs.executeGetRequest(url);

				if (response == null || response != null && response.equals("")) {
					logger.error("Could not properly connect to the ServiceNow Instance");
					return null;
				}

				JSONObject modelVersionsResponse = new JSONObject(response);
				logger.debug("Retrieving Process Mining Model Versions: ");
				ProcessMiningModelVersion version = null;

				if (modelVersionsResponse.has("result")) {
					JSONArray mVersions = modelVersionsResponse.getJSONArray("result");
					for (int i = 0; i < mVersions.length(); i++) {
						JSONObject resultObject = (JSONObject) mVersions.get(i);
						if (resultObject.has("sys_id")) {
							String sysId = resultObject.getString("sys_id");
							String name = resultObject.getString("name");
							int totalRecords = resultObject.getInt("total_records");
							String releaseName = resultObject.getString("release_name");
							String workbench = resultObject.getString("go_to_workbench");
							String lastMinedTime = resultObject.getString("last_mined_time");
							String projectId = resultObject.getString("project.sys_id");

							version = new ProcessMiningModelVersion(new ProcessMiningModelVersionPK(sysId));
							version.setName(name);
							version.setTotalRecords(totalRecords);
							version.setReleaseName(releaseName);
							version.setWorkbenchId(workbench);
							version.setLastMinedTime(lastMinedTime);
							version.setProjectId(projectId);

							modelVersions.add(version);
						}
					}
				}
				startIds += BATCH_SIZE;
				continueProcessingVersions = (startIds <= projectIds.size());
			} while (continueProcessingVersions);
		}

		return modelVersions;
	}

	private String getProcessModelIds(ArrayList<String> projectIds, int startIds)
	{
		String ids = "";
		boolean processedFirst = false;
		for (int i=startIds; i < (startIds+BATCH_SIZE) && i < projectIds.size(); i++) {
			if (processedFirst) {
				ids += ",";
			}
			ids += projectIds.get(i);
			processedFirst = true;
		}

		return ids;
	}

	private ArrayList<String> getProjectIds(JSONObject modelsResponse)
	{
		ArrayList<String> ids = new ArrayList<String>();

		if (modelsResponse.has("result")) {
			JSONArray ms = modelsResponse.getJSONArray("result");
			for (int i = 0; i < ms.length(); i++) {
				JSONObject resultObject = (JSONObject) ms.get(i);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					ids.add(sysId);
				}
			}
		}

		return ids;
	}

	public List<ProcessMiningModelVersion> findAllActive()
		throws ObjectNotFoundException
	{
		return findAll();
	}

	@Override
	public boolean existEntity(ProcessMiningModelVersionPK id)
		throws ObjectNotFoundException
	{
		return findById(id) != null;
	}

	private static final String PLATFORM_VERSIONS = "ZURICH,YOKOHAMA,WASHINGTON,VANCOUVER,UTAH";
	private static final int BATCH_SIZE = 10;

	private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelVersionDAOREST.class);
}