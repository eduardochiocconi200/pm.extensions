package com.servicenow.processmining.extensions.pm.simulation.roi;

public class LeadTimeStatisticMetric
    extends StatisticMetric
{
    private double baseline = 0.0;
    private double newScenario = 0.0;
    private double delta = 0.0;

    public LeadTimeStatisticMetric(final double baseline, final double newScenario)
    {
        super(WorkflowSimulationComparisonStatistics.LEAD_TIME_METRIC);
        this.baseline = baseline;
        this.newScenario = newScenario;
        this.delta = this.baseline - this.newScenario;
    }

    public double getBaselineMetric()
    {
        return this.baseline;
    }

    public double getNewScenarioMetric()
    {
        return this.newScenario;
    }

    public double getDelta()
    {
        return this.delta;
    }
}
