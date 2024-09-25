package com.servicenow.processmining.extensions.pm.model;

public class ProcessMiningModelFilterTransitionsNode
{
    private String entityId = null;
    private String field = null;
    private String predicate = null;
    private String occurrence = null;
    private String values = null;
    private String relation = null;

    public ProcessMiningModelFilterTransitionsNode()
    {
    }

    public void setEntityId(final String entityId)
    {
        this.entityId = entityId;
    }

    public String getEntityId()
    {
        return this.entityId;
    }

    public void setField(final String field)
    {
        this.field = field;
    }

    public String getField()
    {
        return this.field;
    }

    public void setPredicate(final String predicate)
    {
        this.predicate = predicate;
    }

    public String getPredicate()
    {
        return this.predicate;
    }

    public void setOcurrence(final String ocurrence)
    {
        this.occurrence = ocurrence;
    }

    public String getOcurrence()
    {
        return this.occurrence;
    }

    public void setValues(final String values)
    {
        this.values = values;
    }

    public String getValues()
    {
        return this.values;
    }

    public void setRelation(final String relation)
    {
        this.relation = relation;
    }

    public String getRelation()
    {
        return this.relation;
    }
}
