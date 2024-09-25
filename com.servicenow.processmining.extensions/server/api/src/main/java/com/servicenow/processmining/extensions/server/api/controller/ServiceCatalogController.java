package com.servicenow.processmining.extensions.server.api.controller;

import com.servicenow.processmining.extensions.sc.dao.FlowVersionDAOREST;
import com.servicenow.processmining.extensions.sc.dao.WorkflowVersionDAOREST;
import com.servicenow.processmining.extensions.sc.entities.FlowVersion;
import com.servicenow.processmining.extensions.sc.entities.FlowVersionPK;
import com.servicenow.processmining.extensions.sc.entities.WorkflowVersion;
import com.servicenow.processmining.extensions.sc.entities.WorkflowVersionPK;
import com.servicenow.processmining.extensions.server.api.model.ModelVersion;
import com.servicenow.processmining.extensions.server.api.model.ServiceCatalogItemActionMine;
import com.servicenow.processmining.extensions.server.api.model.ServiceCatalogItemActionSimulate;
import com.servicenow.processmining.extensions.server.jobs.ExtensionScheduler;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceCatalogController
	extends BaseController
{
	@GetMapping("/servicecatalog")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public List<ModelVersion> getServiceCatalog(@RequestHeader Map<String, String> headers)
	{
		logger.info("Enter ServiceCatalogController.getServiceCatalog()");
		ArrayList<ModelVersion> all = new ArrayList<ModelVersion>();

		WorkflowVersionDAOREST wfdao = new WorkflowVersionDAOREST(getInstance());
		List<WorkflowVersion> workflows = wfdao.findAllActive();
		for (WorkflowVersion wf : workflows) {
			WorkflowVersionPK pk = (WorkflowVersionPK)wf.getPK();
			ModelVersion mv = new ModelVersion(pk.getSysId(), ModelVersion.WORKFLOW_TYPE, wf.getName(), wf.getLastUpdatedOn());
			mv.setCreatedOn(wf.getCreatedOn());
			mv.setUpdatedOn(wf.getLastUpdatedOn());
			mv.setAverageExecutionTime(12345);
			mv.setSuccessfulInstances(15000);
			mv.setFailedInstances(200);
			all.add(mv);
		}

		FlowVersionDAOREST fdao = new FlowVersionDAOREST(getInstance());
		List<FlowVersion> flows = fdao.findAllActive();
		for (FlowVersion f : flows) {
			FlowVersionPK pk = (FlowVersionPK)f.getPK();
			ModelVersion mv = new ModelVersion(pk.getSysId(), f.getType(), f.getName(), f.getLastUpdatedOn());
			mv.setCreatedOn(f.getCreatedOn());
			mv.setUpdatedOn(f.getLastUpdatedOn());
			mv.setAverageExecutionTime(12345);
			mv.setSuccessfulInstances(15000);
			mv.setFailedInstances(200);
			all.add(mv);
		}
		
		Collections.sort(all);

		logger.info("Exit ServiceCatalogController.getServiceCatalog()");
		return all;
	}

	@GetMapping("/servicecatalog/{type}/{id}")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ModelVersion getServiceCatalogEntry(@PathVariable String type, @PathVariable String id)
	{
		logger.info("Enter ServiceCatalogController.getServiceCatalogEntry(" + type + ", " + id + ")");
		ModelVersion mv = null;

		if (type != null && type.equals(ModelVersion.WORKFLOW_TYPE)) {
			WorkflowVersionDAOREST dao = new WorkflowVersionDAOREST(getInstance());
			WorkflowVersionPK wfPK = new WorkflowVersionPK(id);
			WorkflowVersion wf = dao.findById(wfPK);
			if (wf != null) {
				mv = new ModelVersion(wf.getWorkflowId(), ModelVersion.WORKFLOW_TYPE, wf.getName(), wf.getLastUpdatedOn());
				mv.setAverageExecutionTime(12345);
				mv.setSuccessfulInstances(15000);
				mv.setFailedInstances(200);
			}
		}

		logger.info("Exit ServiceCatalogController.getServiceCatalogEntry(" + type + ", " + id + ")");
		return mv;
	}

	@PostMapping("/servicecatalog/{type}/{id}/sync")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ServiceCatalogItemActionMine syncProcessMiningOnCatalogItem(@PathVariable String type, @PathVariable String id)
	{
		logger.info("Enter ServiceCatalogController.syncProcessMiningOnCatalogItem(" + type + ", " + id + ")");
		ServiceCatalogItemActionMine mine = new ServiceCatalogItemActionMine(type, id);
		String jobId = ExtensionScheduler.getScheduler().scheduleProcessAuditLogsSyncJob(id);
		mine.setResponse(jobId);
		logger.info("Exit ServiceCatalogController.syncProcessMiningOnCatalogItem(" + type + ", " + id + ")");
		return mine;
	}

	@PostMapping("/servicecatalog/{type}/{id}/mine")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ServiceCatalogItemActionMine runProcessMiningOnCatalogItem(@PathVariable String type, @PathVariable String id)
	{
		logger.info("Enter ServiceCatalogController.runProcessMiningOnCatalogItem(" + type + ", " + id + ")");
		ServiceCatalogItemActionMine mine = new ServiceCatalogItemActionMine(type, id);
		String jobId = ExtensionScheduler.getScheduler().scheduleProcessMineJob(id);
		mine.setResponse(jobId);
		logger.info("Exit ServiceCatalogController.runProcessMiningOnCatalogItem(" + type + ", " + id + ")");
		return mine;
	}

	@PostMapping("/servicecatalog/{type}/{id}/simulate")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ServiceCatalogItemActionSimulate runSimulationOnCatalogItem(@PathVariable String type, @PathVariable String id)
	{
		logger.info("Enter ServiceCatalogController.runSimulationOnCatalogItem(" + type + ", " + id + ")");
		ServiceCatalogItemActionSimulate simulate = new ServiceCatalogItemActionSimulate(type, id);
		simulate.setResponse("1");
		logger.info("Exit ServiceCatalogController.runSimulationOnCatalogItem(" + type + ", " + id + ")");
		return simulate;
	}

	private final static Logger logger = LoggerFactory.getLogger(ServiceCatalogController.class);
}