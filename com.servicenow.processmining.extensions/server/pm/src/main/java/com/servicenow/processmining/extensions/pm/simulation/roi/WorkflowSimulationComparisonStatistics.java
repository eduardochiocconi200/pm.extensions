package com.servicenow.processmining.extensions.pm.simulation.roi;

import java.util.HashMap;

public class WorkflowSimulationComparisonStatistics
{
    private HashMap<String, StatisticMetric> metrics = null;

    public WorkflowSimulationComparisonStatistics()
    {
    }

    public HashMap<String, StatisticMetric> getMetrics()
    {
        if (this.metrics == null) {
            this.metrics = new HashMap<String, StatisticMetric>();
        }
        return this.metrics;
    }

    public static final String LEAD_TIME_METRIC = "Lead Time";
}
