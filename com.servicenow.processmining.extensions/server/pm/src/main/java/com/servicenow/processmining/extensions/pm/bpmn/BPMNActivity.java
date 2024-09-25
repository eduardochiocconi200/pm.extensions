package com.servicenow.processmining.extensions.pm.bpmn;

import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;

public class BPMNActivity {
    private int position = -1;
    private int activityType = NO_TYPE;
    private String id = null;
    private String name = null;
    private BpmnModelElementInstance element = null;
    private double xCoordinate = -1;
    private double yCoordinate = -1;

    public BPMNActivity(final int position, final int activityType, final String id, final String name,
            final BpmnModelElementInstance element, final double xCoordinate, final double yCoordinate) {
        this.position = position;
        this.activityType = activityType;
        this.id = id;
        this.name = name;
        this.element = element;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public int getPosition() {
        return this.position;
    }

    public int getActivityType() {
        return this.activityType;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BpmnModelElementInstance getElement() {
        return this.element;
    }

    public double getXCoordinate() {
        return this.xCoordinate;
    }

    public double getYCoordinate() {
        return this.yCoordinate;
    }

    public final static int NO_TYPE = 0;
    public final static int START_TYPE = 1;
    public final static int END_TYPE = 2;
    public final static int USER_TYPE = 3;
    public final static int GW_TYPE = 4;
}
