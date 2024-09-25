package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.simulation.core.Simulator;

public class PrintSimulationState
    implements MessageHandler
{
    public void handle(Message message)
    {
        WorkflowInstance.printSummary((Simulator) message.getSimulator());
    }
}