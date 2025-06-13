package com.servicenow.processmining.extensions.pm.simulation.roi;

import com.servicenow.processmining.extensions.pm.simulation.workflow.WorkflowSimulator;

public class WorkflowSimulationComparison
{
    private WorkflowSimulator baseline = null;
    private WorkflowSimulator newScenario = null;
    private WorkflowSimulationComparisonStatistics comparisonStats = null;

    public WorkflowSimulationComparison(final WorkflowSimulator baseline, final WorkflowSimulator newScenario)
    {
        this.baseline = baseline;
        this.newScenario = newScenario;
    }

    public WorkflowSimulator getBaselineSimulation()
    {
        return this.baseline;
    }

    public WorkflowSimulator getNewScenarioSimulation()
    {
        return this.newScenario;
    }

    public WorkflowSimulationComparisonStatistics getComparisonStatistics()
    {
        return this.comparisonStats;
    }

    public boolean compare()
    {
        this.comparisonStats = new WorkflowSimulationComparisonStatistics();
        System.out.println("Baseline MTTR: (" + getBaselineSimulation().getStatistics().getTotalSimulationTime() + ")");
        System.out.println("New Scenario MTTR: (" + getNewScenarioSimulation().getStatistics().getTotalSimulationTime() + ")");
        LeadTimeStatisticMetric leadTime = new LeadTimeStatisticMetric(getBaselineSimulation().getStatistics().getTotalSimulationTime(), getNewScenarioSimulation().getStatistics().getTotalSimulationTime());
        getComparisonStatistics().getMetrics().put(WorkflowSimulationComparisonStatistics.LEAD_TIME_METRIC, leadTime);
        System.out.println("Delta MTTR: (" + (leadTime.getDelta() < 0 ? "Baseline is faster by: (" + (leadTime.getDelta()*-1) + ")" : "New Scenario is faster by: (" + leadTime.getDelta() + ")") + ") seconds.");

        return true;
    }
}