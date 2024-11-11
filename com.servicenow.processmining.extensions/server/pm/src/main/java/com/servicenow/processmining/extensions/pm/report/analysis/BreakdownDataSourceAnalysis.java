package com.servicenow.processmining.extensions.pm.report.analysis;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelBreakdown;

public class BreakdownDataSourceAnalysis
    extends DataSourceAnalysis
{
    private ProcessMiningModelBreakdown breakdown = null;
    public BreakdownDataSourceAnalysis(final ProcessMiningModelBreakdown breakdown)
    {
        super();
        this.breakdown = breakdown;
    }

    public ProcessMiningModelBreakdown getBreakdown()
    {
        return this.breakdown;
    }
}
