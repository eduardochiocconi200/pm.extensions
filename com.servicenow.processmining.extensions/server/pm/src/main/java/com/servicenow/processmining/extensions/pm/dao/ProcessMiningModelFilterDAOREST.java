package com.servicenow.processmining.extensions.pm.dao;

import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersionFilter;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersionFilterPK;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilter;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
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

public class ProcessMiningModelFilterDAOREST
	extends GenericDAOREST<ProcessMiningModelVersionFilter, ProcessMiningModelVersionFilterPK>
{
	public ProcessMiningModelFilterDAOREST(final ServiceNowInstance instance)
	{
		super(instance);
	}

	@Override
	public ProcessMiningModelVersionFilter findById(ProcessMiningModelVersionFilterPK id)
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/promin_filter_set?";
        url += "sysparm_query=sys_id=" + id.getSysId() + URLEncoder.encode("^", StandardCharsets.UTF_8) + "state=AVAILABLE" + URLEncoder.encode("^", StandardCharsets.UTF_8) + "release_nameINVANCOUVER&";
		url += "sysparm_fields=sys_id,name,total_records,release_name,go_to_workbench,last_mined_time,project.sys_id";
		logger.debug("URL: (" + url + ")");

		String response = snrs.executeGetRequest(url);

		if (response == null || response != null && response.equals("")) {
			logger.error("Could not properly connect to the ServiceNow Instance");
			return null;
		}

		JSONObject modelFiltersResponse = new JSONObject(response);
		logger.debug("Retrieving Process Mining Model Filters: ");
		ProcessMiningModelVersionFilter filter = null;
		if (modelFiltersResponse.has("result")) {
			JSONArray mFilters = modelFiltersResponse.getJSONArray("result");
			if (mFilters.length() == 1) {
				JSONObject resultObject = (JSONObject) mFilters.get(0);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String name = resultObject.getString("name");
					String rawCondition = resultObject.getString("conditions.md_conditions");
					String projectId = resultObject.getString("entity.project.sys_id");

					JSONObject rawConditionJsonObject = new JSONObject(rawCondition);
					filter = new ProcessMiningModelVersionFilter(new ProcessMiningModelVersionFilterPK(sysId));
					filter.setName(name);
					filter.setProjectId(projectId);
					if (rawConditionJsonObject.has("breakdownConditions")) {
						String condition = rawConditionJsonObject.getJSONObject("breakdownConditions").toString();
						filter.setBreakdownFilterCondition(condition);
					}
					else if (rawConditionJsonObject.has("transitionCondition")) {
						String condition = rawConditionJsonObject.getJSONObject("transitionCondition").toString();
						filter.setTransitionFilterCondition(condition);
					}
				}
			}
			else if (mFilters.length() > 1) {
				logger.error("Retrieved more than one entry with id: (" + id.getSysId() + ") when the result should have been JUST ONE OR ZERO.");
			}
		}

		return filter;
	}

	@Override
	public List<ProcessMiningModelVersionFilter> findAll()
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
        String url = "https://" + getInstance().getInstance() + "/api/now/table/promin_filter_set?";
		url += "sysparm_fields=sys_id,name,conditions.md_conditions,entity.project.sys_id";
		logger.debug("URL: (" + url + ")");

		String response = snrs.executeGetRequest(url);

		if (response == null || response != null && response.equals("")) {
			logger.error("Could not properly connect to the ServiceNow Instance");
			return null;
		}

		JSONObject modelFiltersResponse = new JSONObject(response);
		logger.debug("Retrieving Process Mining Model Filters: ");
		ProcessMiningModelVersionFilter filter = null;
		List<ProcessMiningModelVersionFilter> modelFilters = new ArrayList<ProcessMiningModelVersionFilter>();
		if (modelFiltersResponse.has("result")) {
			JSONArray mFilters = modelFiltersResponse.getJSONArray("result");
			for (int i = 0; i < mFilters.length(); i++) {
				JSONObject resultObject = (JSONObject) mFilters.get(i);
				if (resultObject.has("sys_id")) {
					String sysId = resultObject.getString("sys_id");
					String name = resultObject.getString("name");
					String rawCondition = resultObject.getString("conditions.md_conditions");
					String projectId = resultObject.getString("entity.project.sys_id");
					JSONObject rawConditionJsonObject = new JSONObject(rawCondition);
					filter = new ProcessMiningModelVersionFilter(new ProcessMiningModelVersionFilterPK(sysId));
					filter.setName(name);
					filter.setProjectId(projectId);		
					if (rawConditionJsonObject.has("breakdownConditions")) {
						String condition = rawConditionJsonObject.getJSONObject("breakdownConditions").toString();
						filter.setBreakdownFilterCondition(condition);
					}
					else if (rawConditionJsonObject.has("transitionCondition")) {
						String condition = rawConditionJsonObject.getJSONObject("transitionCondition").toString();
						filter.setTransitionFilterCondition(condition);
					}
					modelFilters.add(filter);
				}
			}
		}

		return modelFilters;
	}

	public List<ProcessMiningModelVersionFilter> findAllByProcessModel(final String modelVersionId, final boolean includeMainProcess)
		throws ObjectNotFoundException
	{
		List<ProcessMiningModelVersionFilter> modelFilters = new ArrayList<ProcessMiningModelVersionFilter>();
		ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelVersionId);
		if (pmmr.runEmptyFilter()) {
			ProcessMiningModelParser pmmp = new ProcessMiningModelParser(modelVersionId);
			if (!pmmp.parse(pmmr.getProcessMiningModelJSONString())) {
				logger.error("Could not parse the Process Mining model: (" + modelVersionId + ").");
				return modelFilters;
			}
			else {
				if (includeMainProcess) { 
					ProcessMiningModelVersionFilterPK pk = new ProcessMiningModelVersionFilterPK("0");
					ProcessMiningModelVersionFilter f = new ProcessMiningModelVersionFilter(pk);
					f.setName("Main Process - No Filter");
					f.setCaseFrequency(pmmp.getProcessMiningModel().getAggregate().getCaseCount());
					f.setBreakdownFilterCondition(null);
					f.setTransitionFilterCondition(null);
					f.setAvgDuration(pmmp.getProcessMiningModel().getAggregate().getAvgCaseDuration());
					f.setMaxDuration(pmmp.getProcessMiningModel().getAggregate().getMaxCaseDuration());
					f.setMinDuration(pmmp.getProcessMiningModel().getAggregate().getMinCaseDuration());
					f.setMedianDuration(pmmp.getProcessMiningModel().getAggregate().getMedianDuration());
					f.setStdDeviation(pmmp.getProcessMiningModel().getAggregate().getStdDeviation());
					f.setTotalDuration(pmmp.getProcessMiningModel().getAggregate().getAvgCaseDuration() * pmmp.getProcessMiningModel().getAggregate().getCaseCount());
					f.setVariantCount(pmmp.getProcessMiningModel().getAggregate().getVariantCount());
					modelFilters.add(f);
				}

				for (ProcessMiningModelFilter filter : pmmp.getProcessMiningModel().getFilters().values()) {
					ProcessMiningModelVersionFilterPK pk = new ProcessMiningModelVersionFilterPK(filter.getId());
					ProcessMiningModelVersionFilter f = new ProcessMiningModelVersionFilter(pk);
					f.setName(filter.getName());
					f.setBreakdownFilter(filter.getBreakdownCondition());
					f.setTransitionsFilter(filter.getFilterTransitions());
					f.setCaseFrequency(filter.getCaseFrequency());
					f.setAvgDuration(filter.getAvgDuration());
					f.setMaxDuration(filter.getMaxDuration());
					f.setMinDuration(filter.getMinDuration());
					f.setMedianDuration(filter.getMedianDuration());
					f.setStdDeviation(filter.getStdDeviation());
					f.setTotalDuration(filter.getTotalDuration());
					f.setVariantCount(filter.getVariantCount());
					modelFilters.add(f);
				}
			}
		}
		else {
			logger.error("Could not retrieve Process Model via GraphQL query. Error (" + pmmr.getErrorMessage() + ").");
			return modelFilters;
		}

		return modelFilters;
	}

	public List<ProcessMiningModelVersionFilter> findAllActive()
		throws ObjectNotFoundException
	{
		return findAll();
	}

	@Override
	public boolean existEntity(ProcessMiningModelVersionFilterPK id)
		throws ObjectNotFoundException
	{
		return findById(id) != null;
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelFilterDAOREST.class);
}