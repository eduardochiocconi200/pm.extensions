package com.servicenow.processmining.extensions.pm.model;

import java.io.Serializable;

public class ProcessMiningModelFilterTransitionsNode
    implements Serializable
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

    public String getTransitionNodeJSON()
    {
        String json = "{";
        json += "\"entityId\" : \"" + entityId + "\", ";
        json += "\"field\" : \"" + field + "\", ";
        json += "\"predicate\" : \"" + predicate + "\", ";
        json += "\"occurrence\" : \"" + occurrence + "\", ";
        json += "\"values\" : \"" + values + "\", ";
        json += "\"relation\" : \"" + relation + "\", ";
        json += "}";

        return json;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("[ EntityId: (" + entityId + "), ");
        sb.append(" Field: (" + field + "), ");
        sb.append(" Predicate: (" + predicate + "), ");
        sb.append(" Ocurrence: (" + occurrence + "), ");
        sb.append(" Values: (" + values + "), ");
        sb.append(" Relation: (" + relation + ")");

        return sb.toString();
    }
}
