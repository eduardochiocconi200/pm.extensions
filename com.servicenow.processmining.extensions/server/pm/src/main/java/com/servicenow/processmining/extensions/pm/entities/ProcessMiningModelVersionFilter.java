package com.servicenow.processmining.extensions.pm.entities;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilter;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilterBreakdown;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilterBreakdownCondition;
import com.servicenow.processmining.extensions.sn.entities.ServiceNowEntity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProcessMiningModelVersionFilter
    extends ServiceNowEntity
{
    private String name = null;
    private String breakdownFiltersCondition = null;
    private ArrayList<ProcessMiningModelFilter> breakdownFilters = null;
    private String transitionsFiltersCondition = null;
    private ArrayList<ProcessMiningModelFilter> transitionsFilters = null;
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

    public String toString()
    {
        return "[ProcessMiningModelFilter: (" + getPK().toString() + "), Name: '" + getName() + "'', Breakdown Filters: '" + getBreakdownFilters() + "', Transitions Filters: '" + getTransitionsFilters() + "', ProjectId: '" + getProjectId() + "', Records: '" + getCaseFrequency() + "', Routes: '" + getVariantCount() + "', Avg: '" + getAvgDuration() +"', Median: '" + getMedianDuration() + "', Std Dev: '" + getStdDeviation() + "']";
    }

    public void setBreakdownFiltersCondition(final String condition)
    {
        this.breakdownFiltersCondition = condition;
        breakdownFilters = parseBreakdownFiltersFromCondition();
    }

    public String getBreakdownFiltersCondition()
    {
        return this.breakdownFiltersCondition;
    }

    public void setBreakdownFilters(final HashMap<String, ProcessMiningModelFilter> filters)
    {
        if (filters != null) {
            for (ProcessMiningModelFilter filter : filters.values()) {
                if (filter.getBreakdownCondition() != null) {
                    getBreakdownFilters().add(filter);
                }
            }
        }
    }

    public ArrayList<ProcessMiningModelFilter> getBreakdownFilters()
    {
        if (breakdownFilters == null) {
            breakdownFilters = new ArrayList<ProcessMiningModelFilter>();
        }

        return breakdownFilters;
    }

    public void setTransitionFiltersCondition(final String condition)
    {
        this.transitionsFiltersCondition = condition;
        transitionsFilters = parseTransitionFilterFromCondition();
    }

    public String getTransitionFiltersCondition()
    {
        return this.transitionsFiltersCondition;
    }

    public void setTransitionFilters(final HashMap<String, ProcessMiningModelFilter> filters)
    {
        if (filters != null) {
            for (ProcessMiningModelFilter filter : filters.values()) {
                if (filter.getFilterTransitions() != null) {
                    getTransitionsFilters().add(filter);
                }
            }
        }
    }

    public ArrayList<ProcessMiningModelFilter> getTransitionsFilters()
    {
        if (transitionsFilters == null) {
            transitionsFilters = new ArrayList<ProcessMiningModelFilter>();
        }

        return transitionsFilters;
    }

    // {"breakdownConditions":{"783f434393a886506e79bb1e1dba1020":{"breakdowns":[{"field":"contact_type","values":["phone"]},{"field":"priority","values":["1"]}]}}}
    private ArrayList<ProcessMiningModelFilter> parseBreakdownFiltersFromCondition()
    {
        ArrayList<ProcessMiningModelFilter> filters = new ArrayList<ProcessMiningModelFilter>();
        ProcessMiningModelFilter filter = new ProcessMiningModelFilter();
        filters.add(filter);

        JSONObject breakdownFilterConditions = new JSONObject(getBreakdownFiltersCondition());
        for (String entry : breakdownFilterConditions.keySet()) {
            JSONObject breakdownsObj = breakdownFilterConditions.getJSONObject(entry);
            JSONArray breakdowns = breakdownsObj.getJSONArray("breakdowns");
            if (breakdowns != null && breakdowns.length() > 0) {
                ProcessMiningModelFilterBreakdown filterBreakdown = new ProcessMiningModelFilterBreakdown();
                filter.setBreakdownCondition(filterBreakdown);
                for (int k=0; k < breakdowns.length(); k++) {
                    String condition = breakdowns.get(k).toString();
                    ProcessMiningModelFilterBreakdownCondition bCondition = new ProcessMiningModelFilterBreakdownCondition();
                    bCondition.setCondition(condition);
                    filterBreakdown.addCondition(bCondition);
                }
            }
        }

        throw new RuntimeException("Need to validate the implementation of this method.");
    }

    // {"transitionCondition":{"queries":[[{"eventVar":"x0","entityId":"783f434393a886506e79bb1e1dba1020","field":"__case","value":["created"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x1","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["1"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x2","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["200"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x3","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["2"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x4","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["6"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x5","entityId":"783f434393a886506e79bb1e1dba1020","field":"state","value":["7"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"},{"eventVar":"x6","entityId":"783f434393a886506e79bb1e1dba1020","field":"__case","value":["completed"],"predicate":"EQ","relation":"FOLLOWED_BY","occurrence":"ALWAYS"}]]}}
    private ArrayList<ProcessMiningModelFilter> parseTransitionFilterFromCondition()
    {
        throw new RuntimeException("Need to validate the implementation of this method.");
    }
}