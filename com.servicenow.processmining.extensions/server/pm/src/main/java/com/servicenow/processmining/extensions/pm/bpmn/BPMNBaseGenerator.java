package com.servicenow.processmining.extensions.pm.bpmn;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

public abstract class BPMNBaseGenerator {
    private BpmnModelInstance modelInstance = null;

    public BPMNBaseGenerator(BpmnModelInstance modelInstance)
    {
        this.modelInstance = modelInstance;
    }

    public BpmnModelInstance getModelInstance()
    {
        return this.modelInstance;
    }

    protected double getFromXCoordinate(final BPMNActivity fromActivity, final BPMNActivity toActivity)
    {
        if (fromActivity.getPosition() < toActivity.getPosition()) {
            if (fromActivity.getActivityType() == BPMNActivity.START_TYPE) {
                return (fromActivity.getXCoordinate() + (WIDTH / 2)); // X = X+(Width/2)-(Width/4)
            }
            else {
                return (fromActivity.getXCoordinate() + (WIDTH / 2)); // X = X+(Width/2);
            }
        }
        else {
            return (fromActivity.getXCoordinate() + (WIDTH / 2)); // X = X+(Width/2)
        }
    }

    protected double getFromYCoordinate(final BPMNActivity fromActivity, final BPMNActivity toActivity)
    {
        if (fromActivity.getPosition() < toActivity.getPosition()) {
            if (fromActivity.getActivityType() == BPMNActivity.START_TYPE) {
                return (fromActivity.getYCoordinate() + (HEIGHT / 2) + (HEIGHT / 4)); // Y = Y+Height/2;
            }
            else if (fromActivity.getActivityType() == BPMNActivity.GW_TYPE) {
                return (fromActivity.getYCoordinate() + (HEIGHT / 2) + (HEIGHT / 4)); // Y = Y+Height/2;
            }
            else {
                return (fromActivity.getYCoordinate() + HEIGHT); // Y = Y+Height;
            }
        }
        else {
            if (fromActivity.getActivityType() == BPMNActivity.GW_TYPE) {
                return (fromActivity.getYCoordinate() + (HEIGHT / 2) - (HEIGHT / 4)); // Y = Y+Height/2;
            }
            else {
                return fromActivity.getYCoordinate(); // Y = Y;
            }
        }
    }

    protected double getToXCoordinate(final BPMNActivity fromActivity, final BPMNActivity toActivity)
    {
        if (fromActivity.getPosition() < toActivity.getPosition()) {
            if (toActivity.getActivityType() == BPMNActivity.GW_TYPE) {
                return (toActivity.getXCoordinate() + (WIDTH / 2) - (HEIGHT / 4)); // X = X + Width
            }
            else {
                return toActivity.getXCoordinate(); // X = X
            }
        }
        else {
            return (toActivity.getXCoordinate() + WIDTH); // X = X + Width
        }
    }

    protected double getToYCoordinate(final BPMNActivity fromActivity, final BPMNActivity toActivity)
    {
        if (fromActivity.getPosition() < toActivity.getPosition()) {
            return (toActivity.getYCoordinate() + (HEIGHT / 2)); // Y = Y+(height/2);
        }
        else {
            return (toActivity.getYCoordinate() + (HEIGHT / 2)); // Y = Y+(height/2)
        }
    }

    protected double getMiddleXCoordinate(final BPMNActivity fromActivity, final BPMNActivity toActivity)
    {
        if (fromActivity.getPosition() < toActivity.getPosition()) {
            return (fromActivity.getXCoordinate() + (WIDTH / 2)); // X = X+(width/2);
        }
        else {
            return (fromActivity.getXCoordinate() + (WIDTH / 2)); // X = X+(width/2)
        }
    }

    protected double getMiddleYCoordinate(final BPMNActivity fromActivity, final BPMNActivity toActivity)
    {
        if (fromActivity.getPosition() < toActivity.getPosition()) {
            return (toActivity.getYCoordinate() + (HEIGHT / 2)); // Y = ToY+(width/2);
        }
        else {
            return (toActivity.getYCoordinate() + (HEIGHT / 2)); // Y = FromY+(width/2)
        }
    }

    protected <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String id,
            String name, Class<T> elementClass, BpmnPlane plane, double x, double y, double heigth, double width, boolean withLabel)
    {
        T element = modelInstance.newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        element.setAttributeValue("name", name, false);
        parentElement.addChildElement(element);

        BpmnShape bpmnShape = modelInstance.newInstance(BpmnShape.class);
        bpmnShape.setBpmnElement((BaseElement) element);

        Bounds bounds = modelInstance.newInstance(Bounds.class);
        bounds.setX(x);
        bounds.setY(y);
        bounds.setHeight(heigth);
        bounds.setWidth(width);
        bpmnShape.setBounds(bounds);

        if (withLabel) {
            BpmnLabel bpmnLabel = modelInstance.newInstance(BpmnLabel.class);
            Bounds labelBounds = modelInstance.newInstance(Bounds.class);
            labelBounds.setX(x);
            labelBounds.setY(y + heigth);
            labelBounds.setHeight(heigth);
            labelBounds.setWidth(width);
            bpmnLabel.addChildElement(labelBounds);
            bpmnShape.addChildElement(bpmnLabel);
        }
        plane.addChildElement(bpmnShape);

        return element;
    }

    protected SequenceFlow createSequenceFlow(Process process, FlowNode from, FlowNode to, BpmnPlane plane, double... waypoints)
    {
        String identifier = filterId(from.getId() + "-" + to.getId());
        SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
        sequenceFlow.setAttributeValue("id", identifier, true);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);

        BpmnEdge bpmnEdge = modelInstance.newInstance(BpmnEdge.class);
        bpmnEdge.setBpmnElement(sequenceFlow);
        for (int i = 0; i < waypoints.length / 2; i++) {
            double waypointX = waypoints[i * 2];
            double waypointY = waypoints[i * 2 + 1];
            Waypoint wp = modelInstance.newInstance(Waypoint.class);
            wp.setX(waypointX);
            wp.setY(waypointY);
            bpmnEdge.addChildElement(wp);
        }
        plane.addChildElement(bpmnEdge);

        return sequenceFlow;
    }

    protected String filterId(String id)
    {
        String newId = id.replaceAll("[^a-zA-Z0-9]", "");
        if (Character.isDigit(newId.charAt(0))) {
            newId = "ID" + newId;
        }

        return newId;
    }

    protected final static double HEIGHT = 80;
    protected final static double WIDTH = 100;
}
