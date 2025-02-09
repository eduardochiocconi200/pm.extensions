package com.servicenow.processmining.extensions.server.api.controller;

import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStream;
import com.servicenow.processmining.extensions.pm.analysis.goldratt.ValueStreamAnalysis;
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
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVersionByNameAndDateComparator;
import com.servicenow.processmining.extensions.pm.report.ProcessMiningModelExcelReport;
import com.servicenow.processmining.extensions.pm.report.ProcessMiningModelFilterPowerpointReport;

import java.util.List;
import java.util.TreeSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessMiningController
	extends BaseController
{
	private HashMap<String, ProcessMiningModelValueStream> valueStreams = null;
	private HashMap<String, ProcessMiningModel> models = null;

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

	private HashMap<String, ProcessMiningModel> getProcessMiningModels()
	{
		if (this.models == null) {
			this.models = new HashMap<String, ProcessMiningModel>();
		}

		return this.models;
	}

	@GetMapping("/models/{modelId}")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ProcessMiningModelVersion getProcessModelVersion(@PathVariable String modelId)
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
	public List<ProcessMiningModelVersionFilter> getProcessModelVersionFilters(@PathVariable String modelId)
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
	public ProcessMiningModelVersionFilter getProcessModelVersionFilter(@PathVariable String modelId, @PathVariable String filterName)
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
	public ResponseEntity<Resource> getProcessModelVersionFilterPowerPoint(@PathVariable String modelId, @PathVariable String filterName)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/ppt)");
		ResponseEntity<Resource> response = null;
		String fileName = null;
		ProcessMiningModel model = getProcessMiningModels().get(modelId);
		if (model == null) {
			ProcessMiningModelParser parser = null;
			ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelId);
			if (pmmr.runEmptyFilter()) {
				parser = new ProcessMiningModelParser(modelId);
				if (!parser.parse(pmmr.getProcessMiningModelJSONString())) {
					logger.error("Could not parse filter payload successfully!");
				}
				model = parser.getProcessMiningModel();
			}
		}

		ProcessMiningModelFilterPowerpointReport report = new ProcessMiningModelFilterPowerpointReport(model);
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

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/ppt)");
		return response;
	}

	@GetMapping("/models/{modelId}/filters/{filterName}/xls")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ResponseEntity<Resource> getProcessModelVersionFilterExcel(@PathVariable String modelId, @PathVariable String filterName)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/xls)");
		ResponseEntity<Resource> response = null;
		String fileName = null;
		ProcessMiningModel model = getProcessMiningModels().get(modelId);
		if (model == null) {
			ProcessMiningModelParser parser = null;
			ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelId);
			if (pmmr.runEmptyFilter()) {
				parser = new ProcessMiningModelParser(modelId);
				if (!parser.parse(pmmr.getProcessMiningModelJSONString())) {
					logger.error("Could not parse filter payload successfully!");
				}
				model = parser.getProcessMiningModel();
			}
		}

		ProcessMiningModelExcelReport report = new ProcessMiningModelExcelReport(model);
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

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/xls)");
		return response;
	}

	@GetMapping("/models/{modelId}/filters/{filterName}/bpmn")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ResponseEntity<Resource> getProcessModelModelFilterBPMN(@PathVariable String modelId, @PathVariable String filterName)
		throws IOException
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/bpmn)");
		ResponseEntity<Resource> response = null;
		String fileName = null;
		ProcessMiningModel model = getProcessMiningModels().get(modelId);
		if (model == null) {
			ProcessMiningModelParser parser = null;
			ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelId);
			if (pmmr.runEmptyFilter()) {
				parser = new ProcessMiningModelParser(modelId);
				if (!parser.parse(pmmr.getProcessMiningModelJSONString())) {
					logger.error("Could not parse filter payload successfully!");
				}
				model = parser.getProcessMiningModel();
			}
		}

		if (model != null && model.getAggregate().getCaseCount() > 0) {
			BPMNProcessGenerator generator = new BPMNProcessGenerator(model);
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

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/filters/" + filterName + "/bpmn)");
		return response;
	}

	private HashMap<String, ProcessMiningModelValueStream> getValueStreams()
	{
		if (this.valueStreams == null) {
			this.valueStreams = new HashMap<String, ProcessMiningModelValueStream>();
		}

		return this.valueStreams;
	}

	@GetMapping("/models/{modelId}/valuestream")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ProcessMiningModelValueStream getProcessModelVersionValueStream(@PathVariable String modelId)
	{
		logger.info("Enter ProcessMiningController.GET(/models/" + modelId + "/valuestream");
		ProcessMiningModelValueStream vStream = getValueStreams().get(modelId);
		if (vStream == null) {
			ProcessMiningModel model = getProcessMiningModels().get(modelId);
			if (model == null) {
				ProcessMiningModelParser parser = null;
				ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelId);
				if (pmmr.runEmptyFilter()) {
					parser = new ProcessMiningModelParser(modelId);
					if (!parser.parse(pmmr.getProcessMiningModelJSONString())) {
						logger.error("Could not parse filter payload successfully!");
					}
					model = parser.getProcessMiningModel();
				}
			}

			if (model != null) {
				vStream = new ProcessMiningModelValueStream(new ProcessMiningModelValueStreamPK(modelId));
				ValueStream valueStream = new ValueStream();
				ArrayList<String> nodes = new ArrayList<String>();
				for (ProcessMiningModelNode node : model.getNodes().values()) {
					nodes.add(node.getName());
				}
				valueStream.setNodes(nodes);
				vStream.setValueStream(valueStream);
				getValueStreams().put(modelId, vStream);
			}
		}

		logger.info("Exit ProcessMiningController.GET(/models/" + modelId + "/valuestream");
		return vStream;
	}

	@PutMapping("/models/{modelId}/valuestream")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ResponseEntity<ProcessMiningModelValueStream> putModelVersionValueStream(@RequestBody ProcessMiningModelValueStream valueStream, @PathVariable String modelId)
	{
		logger.info("Enter ProcessMiningController.PUT(/models/" + modelId + "/valuestream");
		ProcessMiningModelValueStream vStream = getValueStreams().get(modelId);
		if (vStream != null) {
			getValueStreams().remove(modelId);
		}
		System.out.println("VALUE STREAM: (" + valueStream.getValueStream() + ")");
		getValueStreams().put(modelId, valueStream);

		logger.info("Exit ProcessMiningController.PUT(/models/" + modelId + "/valuestream");
		return ResponseEntity.ok(vStream);
	}

	@DeleteMapping("/models/{modelId}/valuestream")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ProcessMiningModelValueStream deleteModelVersionValueStream(@PathVariable String modelId)
	{
		logger.info("Enter ProcessMiningController.DELETE(/models/" + modelId + "/valuestream");
		ProcessMiningModelValueStream vStream = getValueStreams().get(modelId);
		if (vStream != null) {
			getValueStreams().remove(modelId);
		}

		logger.info("Exit ProcessMiningController.DELETE(/models/" + modelId + "/valuestream");
		return vStream;
	}

	@PutMapping("/models/{modelId}/valuestream/run")
	@CrossOrigin(origins = CROSS_ORIGIN_DOMAIN)
	public ResponseEntity<ProcessMiningModelValueStream> putModelVersionValueStreamRun(@RequestBody ProcessMiningModelValueStream valueStream, @PathVariable String modelId)
	{
		logger.info("Enter ProcessMiningController.PUT(/models/" + modelId + "/valuestream/run");
		ProcessMiningModelValueStream vStream = getValueStreams().get(modelId);
		if (vStream == null) {
			throw new RuntimeException("Did not find ValueStream. Please Create Value Stream First before running analysis.");
		}
		else {
			ProcessMiningModel model = getProcessMiningModels().get(modelId);
			if (model == null) {
				ProcessMiningModelParser parser = null;
				ProcessMiningModelRetrieval pmmr = ProcessMiningModelRetrievalFactory.getProcessMiningRetrieval(getInstance(), modelId);
				if (pmmr.runEmptyFilter()) {
					parser = new ProcessMiningModelParser(modelId);
					if (!parser.parse(pmmr.getProcessMiningModelJSONString())) {
						logger.error("Could not parse filter payload successfully!");
					}
					model = parser.getProcessMiningModel();
				}
			}

			if (model != null) {
				ValueStreamAnalysis vsa = new ValueStreamAnalysis(getInstance(), model, vStream.getValueStream());
				if (!vsa.run()) {
					logger.error("Could not run value stream analysis successfully!");
				}
				else {
					vStream = new ProcessMiningModelValueStream(new ProcessMiningModelValueStreamPK(modelId));
					vStream.setValueStream(vsa.getValueStream());
					getValueStreams().put(modelId, vStream);
				}
			}
		}

		getValueStreams().put(modelId, valueStream);

		logger.info("Exit ProcessMiningController.PUT(/models/" + modelId + "/valuestream/run");
		return ResponseEntity.ok(vStream);
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcessMiningController.class);
}
