package com.servicenow.processmining.extensions.pm.entities;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilterBreakdown;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilterBreakdownCondition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilterTransitions;
import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProcessMiningModelVersionFilter
    extends ServiceNowEntity
{
    private String name = null;
    private String breakdownFilterCondition = null;
    private ProcessMiningModelFilterBreakdown breakdownFilter = null;
    private String transitionsFilterCondition = null;
    private ProcessMiningModelFilterTransitions transitionsFilter = null;
    private String projectId = null;
    private int caseFrequency = -1;
    private int variantCount = -1;
    private int totalDuration = -1;
    private int maxDuration = -1;
    private int minDuration = -1;
    private int avgDuration = -1;
    private int medianDuration = -1;
    private int stdDeviation = -1;

    public ProcessMiningModelVersionFilter(ProcessMiningModelVersionFilterPK pk)
    {
        super(pk);
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setProjectId(final String projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectId()
    {
        return this.projectId;
    }

    public void setCaseFrequency(final int freq)
    {
        this.caseFrequency = freq;
    }

    public int getCaseFrequency()
    {
        return this.caseFrequency;
    }

    public void setVariantCount(final int count)
    {
        this.variantCount = count;
    }

    public int getVariantCount()
    {
        return this.variantCount;
    }

    public void setTotalDuration(final int total)
    {
        this.totalDuration = total;
    }

    public int getTotalDuration()
    {
        return this.totalDuration;
    }

    public void setMaxDuration(final int maxDuration)
    {
        this.maxDuration = maxDuration;
    }

    public int getMaxDuration()
    {
        return this.maxDuration;
    }

    public void setMinDuration(final int minDuration)
    {
        this.minDuration = minDuration;
    }

    public int getMinDuration()
    {
        return this.minDuration;
    }

    public void setAvgDuration(final int avgDuration)
    {
        this.avgDuration = avgDuration;
    }

    public int getAvgDuration()
    {
        return this.avgDuration;
    }

    public void setMedianDuration(final int medianDuration)
    {
        this.medianDuration = medianDuration;
    }

    public int getMedianDuration()
    {
        return this.medianDuration;
    }

    public void setStdDeviation(final int stdDeviation)
    {
        this.stdDeviation = stdDeviation;
    }

    public int getStdDeviation()
    {
        return this.stdDeviation;
    }

    public void setBreakdownFilterCondition(final String condition)
    {
        this.breakdownFilterCondition = condition;
        if (condition != null && !condition.equals("")) {
            breakdownFilter = parseBreakdownFilterFromCondition();
        }
    }

    public String getBreakdownFilterCondition()
    {
        return this.breakdownFilterCondition;
    }

    public void setBreakdownFilter(final ProcessMiningModelFilterBreakdown breakdownFilter)
    {
        this.breakdownFilter = breakdownFilter;
    }

    public ProcessMiningModelFilterBreakdown getBreakdownFilter()
    {
        if (breakdownFilter == null) {
            if (getBreakdownFilterCondition() != null && !getBreakdownFilterCondition().equals("")) {
                breakdownFilter = parseBreakdownFilterFromCondition();
            }
        }

        return breakdownFilter;
    }

    public void setTransitionFilterCondition(final String condition)
    {
        this.transitionsFilterCondition = condition;
        if (condition != null && !condition.equals("")) {
            transitionsFilter = parseTransitionFilterFromCondition();
        }
    }

    public String getTransitionFilterCondition()
    {
        return this.transitionsFilterCondition;
    }

    public void setTransitionsFilter(final ProcessMiningModelFilterTransitions transitionsFilter)
    {
        this.transitionsFilter = transitionsFilter;
    }

    public ProcessMiningModelFilterTransitions getTransitionsFilters()
    {
        if (this.transitionsFilter == null) {
            if (getTransitionFilterCondition() != null && !getTransitionFilterCondition().equals("")) {
                this.transitionsFilter = parseTransitionFilterFromCondition();
            }
        }

        return this.transitionsFilter;
    }

    // {"breakdownConditions":{"783f434393a88x506e79bb1e1dba1020":{"breakdowns":[{"field":"contact_type","values":["phone"]},{"field":"priority","values":["1"]}]}}}
    private ProcessMiningModelFilterBreakdown parseBreakdownFilterFromCondition()
    {
        ProcessMiningModelFilterBreakdown filterBreakdown = new ProcessMiningModelFilterBreakdown();
        JSONObject breakdownFilterConditions = new JSONObject(getBreakdownFilterCondition());
        for (String entry : breakdownFilterConditions.keySet()) {
            JSONObject breakdownsObj = breakdownFilterConditions.getJSONObject(entry);
            JSONArray breakdowns = breakdownsObj.getJSONArray("breakdowns");
            if (breakdowns != null && breakdowns.length() > 0) {
                for (int k=0; k < breakdowns.length(); k++) {
                    String condition = breakdowns.get(k).toString();
                    ProcessMiningModelFilterBreakdownCondition bCondition = new ProcessMiningModelFilterBreakdownCondition();
                    bCondition.setCondition(condition);
                    filterBreakdown.addCondition(bCondition);
                }
            }
        }

        return filterBreakdown;
    }

    // {"transitionCondition":{"queries":[[{"eventVar":"x0","entityId":"783f434393a886506e79bb1e1dba1020","field":"__case","value":["created"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x1","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["1"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x2","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["200"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x3","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["2"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x4","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["6"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x5","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["7"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x6","entityId":"783f434393a886506e79bb1e1dba1020","field":"__case","value":["completed"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"}]]}}
    private ProcessMiningModelFilterTransitions parseTransitionFilterFromCondition()
    {
        throw new RuntimeException("Need to validate the implementation of this method.");
    }

    public String toString()
    {
        return "[ProcessMiningModelVersionFilter: (" + getPK().toString() + "), Name: '" + getName() + "', ProjectId: '" + getProjectId() + "', Records: '" + getCaseFrequency() + "', Routes: '" + getVariantCount() + "', Avg: '" + getAvgDuration() +"', Median: '" + getMedianDuration() + "', Std Dev: '" + getStdDeviation() + "',\nBreakdown Filters: '" + getBreakdownFilter() + "',\nTransitions Filters: '" + getTransitionsFilters() + "']";
    }
}