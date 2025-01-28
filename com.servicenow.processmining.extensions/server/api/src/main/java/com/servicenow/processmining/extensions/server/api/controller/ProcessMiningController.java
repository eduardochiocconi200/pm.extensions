package com.servicenow.processmining.extensions.server.api.controller;

import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStream;
import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStreamPhase;
import com.servicenow.processmining.extensions.pm.bpmn.BPMNProcessGenerator;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelFilterDAOREST;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrieval;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrievalFactory;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelVersionDAOREST;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersionFilter;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersionFilterPK;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelValueStream;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelValueStreamPK;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersion;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelVersionPK;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVersionByNameAndDateComparator;
import com.servicenow.processmining.extensions.pm.report.ProcessMiningModelExcelReport;
import com.servicenow.processmining.extensions.pm.report.ProcessMiningModelFilterPowerpointReport;

import java.util.List;
import java.util.TreeSet;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessMiningController
	extends BaseController
{
	@GetMapping("/login/{instance}/{user}/{password}")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public String getLogin(@PathVariable String instance, @PathVariable String user, @PathVariable String password)
	{
		logger.info("Enter ProcessMiningController.GET(/login)");
		String loginStatus = "error";
		setSNInstance(instance);
		setSNUser(user);
		setSNPassword(password);
		if (getInstance() != null && getInstance().getSNVersion() != null && !getInstance().getSNVersion().equals("")) {
			loginStatus = "success";
		}
		else {
			invalidateInstance();
			setSNInstance(null);
			setSNUser(null);
			setSNPassword(null);
		}

		logger.info("Exit ProcessMiningController.GET(/login) = (" + loginStatus + ")");
		return loginStatus;
	}

	@GetMapping("/logout")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public String getLogout()
	{
		logger.info("Enter ProcessMiningController.GET(/logout)");
		String logoutStatus = "error";
		logoutStatus = "success";
		setSNInstance(null);
		setSNUser(null);
		setSNPassword(null);
		invalidateInstance();

		logger.info("Exit ProcessMiningController.GET(/logout) = (" + logoutStatus + ")");
		return logoutStatus;
	}

	@GetMapping("/models")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public List<ProcessMiningModelVersion> getModels()
	{
		logger.info("Enter ProcessMiningController.GET(/models)");
		ArrayList<ProcessMiningModelVersion> list = new ArrayList<ProcessMiningModelVersion>();
		ProcessMiningModelVersionDAOREST dao = new ProcessMiningModelVersionDAOREST(getInstance());
        for (ProcessMiningModelVersion version : dao.findAll()) {
			list.add(version);
		}

		TreeSet<ProcessMiningModelVersion> sortedModels = new TreeSet<ProcessMiningModelVersion>(new ProcessMiningModelVersionByNameAndDateComparator());
		for (ProcessMiningModelVersion version : list) {
			sortedModels.add(version);
		}

		list.clear();
		for (ProcessMiningModelVersion version : sortedModels) {
			list.add(version);
		}

		logger.info("Exit ProcessMiningController.GET(/models)");
		return list;
	}

	@GetMapping("/models/{modelId}")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ProcessMiningModelVersion processModelVersion(@PathVariable String modelId)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + ")");
		ProcessMiningModelVersionPK pk = new ProcessMiningModelVersionPK(modelId);
		ProcessMiningModelVersionDAOREST dao = new ProcessMiningModelVersionDAOREST(getInstance());
		ProcessMiningModelVersion model = dao.findById(pk);

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + ")");
		return model;
	}

	@GetMapping("/models/{modelId}/filters")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public List<ProcessMiningModelVersionFilter> processModelVersionFilters(@PathVariable String modelId)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/filters");
		List<ProcessMiningModelVersionFilter> filters = new ArrayList<ProcessMiningModelVersionFilter>();
		try {
			ProcessMiningModelFilterDAOREST pmmfDAO = new ProcessMiningModelFilterDAOREST(getInstance());
			filters = pmmfDAO.findAllByProcessModel(modelId, true);
		}
		catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/filters");
		return filters;
	}

	@GetMapping("/models/{modelId}/filters/{filterName}")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ProcessMiningModelVersionFilter processModelVersionFilter(@PathVariable String modelId, @PathVariable String filterName)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + ")");
		ProcessMiningModelVersionFilterPK pk = new ProcessMiningModelVersionFilterPK("1");
		ProcessMiningModelVersionFilter filter = new ProcessMiningModelVersionFilter(pk);
		filter.setName("Email Channel Filter");
		filter.setProjectId("abcdef123456");
		filter.setBreakdownFilterCondition("{\"condition\":\"ABC=1\"}");

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + ")");
		return filter;
	}

	@GetMapping("/models/{modelId}/filters/{filterName}/ppt")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ResponseEntity<Resource> processModelVersionFilterPowerPoint(@PathVariable String modelId, @PathVariable String filterName)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/ppt)");
		ResponseEntity<Resource> response = null;
		String fileName = null;
		try {
			ProcessMiningModelParser parser = null;
			ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelId);
			if (pmmr.runEmptyFilter()) {
				parser = new ProcessMiningModelParser(modelId);
				if (!parser.parse(pmmr.getProcessMiningModelJSONString())) {
					logger.error("Could not parse filter payload successfully!");
				}
				else {
					ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(parser.getProcessMiningModel());
					if (!report.createReport()) {
						logger.error("Could not create PPT report successfully!");
					}
					else {
						fileName = report.getReportFileName();
						FileSystemResource resource = new FileSystemResource(fileName);
						MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(mediaType);
						ContentDisposition disposition = ContentDisposition.attachment().filename(resource.getFilename()).build();
						headers.setContentDisposition(disposition);
						response = new ResponseEntity<>(resource, headers, HttpStatus.OK);
					}
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/ppt)");
		return response;
	}

	@GetMapping("/models/{modelId}/filters/{filterName}/xls")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ResponseEntity<Resource> processModelVersionFilterExcel(@PathVariable String modelId, @PathVariable String filterName)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/xls)");
		ResponseEntity<Resource> response = null;
		String fileName = null;
		try {
			ProcessMiningModelParser parser = null;
			ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelId);
			if (pmmr.runEmptyFilter()) {
				parser = new ProcessMiningModelParser(modelId);
				if (!parser.parse(pmmr.getProcessMiningModelJSONString())) {
					logger.error("Could not parse filter payload successfully!");
				}
				else {
					ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(parser.getProcessMiningModel());
					if (!report.createReport()) {
						logger.error("Could not create XLS report successfully!");
					}
					else {
						fileName = report.getReportFileName();
						FileSystemResource resource = new FileSystemResource(fileName);
						MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(mediaType);
						ContentDisposition disposition = ContentDisposition.attachment().filename(resource.getFilename()).build();
						headers.setContentDisposition(disposition);
						response = new ResponseEntity<>(resource, headers, HttpStatus.OK);
					}
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/xls)");
		return response;
	}

	@GetMapping("/models/{modelId}/filters/{filterName}/bpmn")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ResponseEntity<Resource> processModelModelFilterBPMN(@PathVariable String modelId, @PathVariable String filterName)
		throws IOException
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/bpmn)");
		ResponseEntity<Resource> response = null;
		String fileName = null;
		try {
			ProcessMiningModelParser parser = null;
			ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelId);
			if (pmmr.runEmptyFilter()) {
				parser = new ProcessMiningModelParser(modelId);
				if (!parser.parse(pmmr.getProcessMiningModelJSONString())) {
					logger.error("Could not parse filter payload successfully!");
				}
				else {
					if (parser.getProcessMiningModel().getAggregate().getCaseCount() > 0) {
						BPMNProcessGenerator generator = new BPMNProcessGenerator(parser.getProcessMiningModel());
						if (generator.createBPMNProcessFile()) {
							fileName = generator.getBPMNFileName();
							FileSystemResource resource = new FileSystemResource(fileName);
							MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
							HttpHeaders headers = new HttpHeaders();
							headers.setContentType(mediaType);
							ContentDisposition disposition = ContentDisposition.attachment().filename(resource.getFilename()).build();
							headers.setContentDisposition(disposition);
							response = new ResponseEntity<>(resource, headers, HttpStatus.OK);
						}
					}
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/bpmn)");
		return response;
	}

	@GetMapping("/models/{modelId}/valuestream")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ProcessMiningModelValueStream processModelVersionValueStream(@PathVariable String modelId)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/valuestream");
		ProcessMiningModelValueStream vStream = new ProcessMiningModelValueStream(new ProcessMiningModelValueStreamPK(modelId));
		try {
			// ProcessMiningModeValueStreamDAOREST pmmvsDAO = new ProcessMiningModeValueStreamDAOREST(getInstance());
			// vStream = pmmvsDAO.findAllByProcessModel(modelId, true).get(0);
			vStream = new ProcessMiningModelValueStream(new ProcessMiningModelValueStreamPK("VS1"));
			ValueStream valueStream = new ValueStream();
			ValueStreamPhase phase1 = new ValueStreamPhase("Phase 0");
			ValueStreamPhase phase2 = new ValueStreamPhase("Phase 1");
			valueStream.getPhases().add(phase1);
			valueStream.getPhases().add(phase2);
			vStream.setValueStream(valueStream);
		}
		catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/valuestream");
		return vStream;
	}

	@DeleteMapping("/models/{modelId}/valuestream")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ProcessMiningModelValueStream deleteModelVersionValueStream(@PathVariable String modelId)
	{
		logger.info("Enter ProcessMiningController.DELETE(/models/" + modelId + "/valuestream");
		ProcessMiningModelValueStream vStream = new ProcessMiningModelValueStream(new ProcessMiningModelValueStreamPK(modelId));
		try {
			vStream = new ProcessMiningModelValueStream(new ProcessMiningModelValueStreamPK("VS1"));
		}
		catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		logger.info("Exit ProcessMiningController.DELETE(/models/" + modelId + "/valuestream");
		return vStream;
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcessMiningController.class);
}
