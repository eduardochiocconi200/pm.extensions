package com.servicenow.processmining.extensions.sc.dao;

import com.servicenow.processmining.extensions.sc.entities.SysAuditEntry;
import com.servicenow.processmining.extensions.sc.entities.SysAuditEntryPK;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLog;
import com.servicenow.processmining.extensions.sc.entities.SysAuditLogPK;
import com.servicenow.processmining.extensions.sc.entities.WorkflowVersion;
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

public class SysAuditLogDAOREST
	extends GenericDAOREST<SysAuditLog, SysAuditLogPK>
{
	public SysAuditLogDAOREST(final ServiceNowInstance instance)
	{
		super(instance);
	}

	@Override
	public SysAuditLog findById(SysAuditLogPK id)
		throws ObjectNotFoundException
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		int baseOffset = 0;
		int batchSize = getBatchSize(id);
		int maxResultSet = getMaxResultSetSize(id);
		boolean continueRetrieving = true;
		boolean timeoutRetried = false;
		SysAuditLog sysAuditLog = new SysAuditLog(id);
		logger.debug("Retrieving Sys Audit Logs: ");

		do {
			long startTime = System.currentTimeMillis();
			String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_audit?";
			url += "sysparm_query=tablename=" + id.getTableName();
			if (id.getFieldName() != null) {
				url += URLEncoder.encode("^", StandardCharsets.UTF_8) + "fieldname=" + id.getFieldName() + "&";
			}
			else {
				url += "&";
			}
			url += "sysparm_limit=" + batchSize + "&";
			url += "sysparm_offset=" + baseOffset + "&";
			url += "sysparm_fields=sys_id,documentkey,tablename,fieldname,oldvalue,newvalue,reason,sys_created_on,sys_created_by";
			String response = snrs.executeGetRequest(url);
			long endTime = System.currentTimeMillis();
			logger.debug("Retrieved (" + batchSize + ") records [" + baseOffset + "-" + (baseOffset+batchSize) + "] from sys_audit in (" + (endTime-startTime) + ") milliseconds.");

			if (response == null || response != null && response.equals("")) {
				logger.error("The requested REST API operation (SysAuditLogDAOREST.findById) could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
				// If it is within the reasonable 60 second timeout error, we will give it one more shot. If not, we need to fail since it may be another problem.
				// Unfortunately, we need to follow this logic since the Table REST API raises a generic 500 error without indicating this was a timeout issue.
				long timeElapsed = endTime - startTime;
				if (timeElapsed > 56000 && timeElapsed < 64000) {
					logger.error("The requested REST API operation (SysAuditLogDAOREST.findById) took approx 1 min. So we will retry it one more time. Otherwise, we will show the message to extend the default 60 seconds timeout.");
					// The default REST Table API timeout is 60 seconds. If after a retry (which probably forced a table caching and the second time the query can run faster),
					// fails, then we fail the transaction and will need to show a message indicating that the default needs to be updated.
					if (timeoutRetried) {
						continueRetrieving = false;
					}
					else {
						timeoutRetried = true;
					}
				}
				else {
					return null;
				}
			}
			else {
				JSONObject sysAuditLogResponse = new JSONObject(response);
				if (sysAuditLogResponse.has("result")) {
					JSONArray sysAuditLogEntries = sysAuditLogResponse.getJSONArray("result");
					int processedEntries = 0;
					for (int i=0; i < sysAuditLogEntries.length(); i++, processedEntries++) {
						JSONObject resultObject = (JSONObject) sysAuditLogEntries.get(i);
						if (resultObject.has("sys_id")) {
							String sysId = resultObject.getString("sys_id");
							String documentkey = resultObject.getString("documentkey");
							String tablename = resultObject.getString("tablename");
							String fieldname = resultObject.getString("fieldname");
							String oldvalue = resultObject.getString("oldvalue");
							String newvalue = resultObject.getString("newvalue");
							String createdOn = resultObject.getString("sys_created_on");
							String createdBy = resultObject.getString("sys_created_by");
							String reason = resultObject.has("reason") ? resultObject.getString("reason") : "";

							SysAuditEntry entry = new SysAuditEntry(new SysAuditEntryPK(sysId));
							entry.setDocumentKey(documentkey);
							entry.setTableName(tablename);
							entry.setFieldName(fieldname);
							entry.setOldValue(oldvalue);
							entry.setNewValue(newvalue);
							entry.setSysCreatedOn(createdOn);
							entry.setSysCreatedBy(createdBy);
							entry.setReason(reason);

							sysAuditLog.getLog().add(entry);
						}
					}

					baseOffset += batchSize;
					if (processedEntries < batchSize || baseOffset >= maxResultSet) {
						continueRetrieving = false;
					}
				}
			}
		} while (continueRetrieving);

		logger.debug("SysAuditLogDAOREST.findById(): (" + sysAuditLog.getLog().size() + ")");
		return sysAuditLog;
	}

	private int getBatchSize(SysAuditLogPK id)
	{
		if (id.getResultSetSize() < BATCH_SIZE) {
			return id.getResultSetSize();
		}

		return BATCH_SIZE;
	}

	private int getMaxResultSetSize(SysAuditLogPK id)
	{
		if (id.getResultSetSize() < MAX_RESULT_SET) {
			return id.getResultSetSize();
		}

		return MAX_RESULT_SET;
	}

	public SysAuditLog findByIds(final SysAuditLogPK id, final ArrayList<String> ids)
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		int baseOffset = 0;
		boolean continueRetrieving = true;
		SysAuditLog sysAuditLog = new SysAuditLog(id);
		logger.debug("Retrieving Sys Audit Logs: ");

		do {
			long startTime = System.currentTimeMillis();
			String instancesInSysLog = getNextBatch(baseOffset, ids);
			String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_audit?";
			url += "sysparm_query=documentkeyIN" + instancesInSysLog + "&";
			url += "sysparm_fields=sys_id,documentkey,tablename,fieldname,oldvalue,newvalue,reason,sys_created_on,sys_created_by";
			String response = snrs.executeGetRequest(url);
			if (response == null || response != null && response.equals("")) {
				logger.error("The requested REST API operation (SysAuditLogDAOREST.findById) could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
				return null;
			}
			long endTime = System.currentTimeMillis();
			logger.debug("Retrieved (" + IDS_BATCH_SIZE + ") records [" + baseOffset + "-" + (baseOffset+IDS_BATCH_SIZE) + "] from sys_audit in (" + (endTime-startTime) + ") milliseconds.");

			JSONObject sysAuditLogResponse = new JSONObject(response);
			if (sysAuditLogResponse.has("result")) {
				JSONArray sysAuditLogEntries = sysAuditLogResponse.getJSONArray("result");
				int processedEntries = 0;
				for (int i=0; i < sysAuditLogEntries.length(); i++, processedEntries++) {
					JSONObject resultObject = (JSONObject) sysAuditLogEntries.get(i);
					if (resultObject.has("sys_id")) {
						String sysId = resultObject.getString("sys_id");
						String documentkey = resultObject.getString("documentkey");
						String tablename = resultObject.getString("tablename");
						String fieldname = resultObject.getString("fieldname");
						String oldvalue = resultObject.getString("oldvalue");
						String newvalue = resultObject.getString("newvalue");
						String createdOn = resultObject.getString("sys_created_on");
						String createdBy = resultObject.getString("sys_created_by");
						String reason = resultObject.has("reason") ? resultObject.getString("reason") : "";

						SysAuditEntry entry = new SysAuditEntry(new SysAuditEntryPK(sysId));
						entry.setDocumentKey(documentkey);
						entry.setTableName(tablename);
						entry.setFieldName(fieldname);
						entry.setOldValue(oldvalue);
						entry.setNewValue(newvalue);
						entry.setSysCreatedOn(createdOn);
						entry.setSysCreatedBy(createdBy);
						entry.setReason(reason);

						sysAuditLog.getLog().add(entry);
					}
				}

				baseOffset += IDS_BATCH_SIZE;
				if (processedEntries < IDS_BATCH_SIZE || baseOffset >= MAX_RESULT_SET) {
					continueRetrieving = false;
				}
			}
		} while (continueRetrieving);

		logger.debug("SysAuditLogDAOREST.findById(): (" + sysAuditLog.getLog().size() + ")");
		return sysAuditLog;
	}

	private String getNextBatch(final int baseOffset, final ArrayList<String> ids)
	{
		boolean processedFirstId = false;
		StringBuffer sb = new StringBuffer();
		for (int i=baseOffset; i < baseOffset+IDS_BATCH_SIZE && i < ids.size(); i++) {
			if (processedFirstId) {
				sb.append(",");
			}
			sb.append(ids.get(i));
			processedFirstId = true;
		}

		return sb.toString();
	}

	@Override
	public List<SysAuditLog> findAll()
		throws ObjectNotFoundException
	{
		ArrayList<SysAuditLog> sysLogFullHistory = new ArrayList<SysAuditLog>();
		WorkflowVersionDAOREST wfDAO = new WorkflowVersionDAOREST(getInstance());
        List<WorkflowVersion> wfItems = wfDAO.findAll();
		for (WorkflowVersion wv : wfItems) {
			SysAuditLogPK pk = new SysAuditLogPK(wv.getTable());
			SysAuditLog sysLogH = this.findById(pk);
			sysLogFullHistory.add(sysLogH);
		}

		return sysLogFullHistory;
	}

	@Override
	public boolean existEntity(SysAuditLogPK id)
		throws ObjectNotFoundException
	{
		return findById(id) != null;
	}

	public boolean insert(final SysAuditEntry auditLogEntry)
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());

		long startTime = System.currentTimeMillis();
		String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_audit";
		String payload = auditLogEntry.toJSON();
		logger.debug("Inserting SysAuditEntry: (" + payload + ")");
		String response = snrs.executePostRequest(url, payload);
		long endTime = System.currentTimeMillis();
		logger.debug("Completed inserting (" + auditLogEntry.getPK() + ") records in the sys_audit table in: (" + (endTime-startTime) + ") milliseconds.");

		if (response == null || response != null && response.equals("")) {
			logger.error("The requested REST API operation (SysAuditLogDAOREST.insert) could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
			return false;
		}

		return true;
	}

	public boolean update(final SysAuditEntry auditLogEntry)
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());

		long startTime = System.currentTimeMillis();
		String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_audit";
		String payload = auditLogEntry.toJSON();
		logger.debug("Updating SysAuditEntry: (" + payload + ")");
		String response = snrs.executePutRequest(url, payload);
		long endTime = System.currentTimeMillis();
		logger.debug("Completed updating (" + auditLogEntry.getPK() + ") records in the sys_audit table in: (" + (endTime-startTime) + ") milliseconds.");

		if (response == null || response != null && response.equals("")) {
			logger.error("The requested REST API operation (SysAuditLogDAOREST.update) could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
			return false;
		}

		return true;
	}

	public boolean delete(final SysAuditEntry auditLogEntry)
	{
		ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		String auditLogEntrySysId = ((SysAuditEntryPK) auditLogEntry.getPK()).getSysId();
		long startTime = System.currentTimeMillis();
		String url = "https://" + getInstance().getInstance() + "/api/now/table/sys_audit/" + auditLogEntrySysId;
		String response = snrs.executeDeleteRequest(url);
		long endTime = System.currentTimeMillis();
		logger.debug("Completed deleting record with key: (" + auditLogEntrySysId + ") from sys_audit table in: (" + (endTime-startTime) + ") milliseconds.");

		if (response == null || response != null && response.equals("")) {
			logger.error("The requested REST API operation (SysAuditLogDAOREST.delete) could not complete successfully on ServiceNow instance: (" + getInstance().getInstance() + ")!");
			logger.error("Tried to delete record via URL: (" + url + ")");
			return false;
		}

		return true;
	}

	private static final int BATCH_SIZE = 2000;
	private static final int IDS_BATCH_SIZE = 500;
	private static final int MAX_RESULT_SET = 10000;

	private static final Logger logger = LoggerFactory.getLogger(SysAuditLogDAOREST.class);
}
