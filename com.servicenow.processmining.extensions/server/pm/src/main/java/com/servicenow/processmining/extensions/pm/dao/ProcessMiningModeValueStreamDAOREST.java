package com.servicenow.processmining.extensions.pm.dao;

import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelValueStream;
import com.servicenow.processmining.extensions.pm.entities.ProcessMiningModelValueStreamPK;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.dao.GenericDAOREST;
import com.servicenow.processmining.extensions.sn.exceptions.ObjectNotFoundException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningModeValueStreamDAOREST
	extends GenericDAOREST<ProcessMiningModelValueStream, ProcessMiningModelValueStreamPK>
{
	public ProcessMiningModeValueStreamDAOREST(final ServiceNowInstance instance)
	{
		super(instance);
	}

	@Override
	public ProcessMiningModelValueStream findById(ProcessMiningModelValueStreamPK id)
		throws ObjectNotFoundException
	{
		throw new RuntimeException("NEED TO IMPLEMENT");
	}

	@Override
	public List<ProcessMiningModelValueStream> findAll()
		throws ObjectNotFoundException
	{
		throw new RuntimeException("NEED TO IMPLEMENT");
	}

	public List<ProcessMiningModelValueStream> findAllByProcessModel(final String modelVersionId, final boolean includeMainProcess)
		throws ObjectNotFoundException
	{
		throw new RuntimeException("NEED TO IMPLEMENT");
	}

	public List<ProcessMiningModelValueStream> findAllActive()
		throws ObjectNotFoundException
	{
		return findAll();
	}

	@Override
	public boolean existEntity(ProcessMiningModelValueStreamPK id)
		throws ObjectNotFoundException
	{
		return findById(id) != null;
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelFilterDAOREST.class);
}