package com.servicenow.processmining.extensions.sn.dao;

import java.util.List;

import com.servicenow.processmining.extensions.sn.entities.PrimaryKey;
import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;
import com.servicenow.processmining.extensions.sn.exceptions.ObjectNotFoundException;

public interface GenericDAO<SNEC extends ServiceNowEntity, ID extends PrimaryKey>
{
	public SNEC findById(ID id) throws ObjectNotFoundException;
	public List<SNEC> findAll() throws ObjectNotFoundException;	
	public boolean existEntity(ID id) throws ObjectNotFoundException;
}