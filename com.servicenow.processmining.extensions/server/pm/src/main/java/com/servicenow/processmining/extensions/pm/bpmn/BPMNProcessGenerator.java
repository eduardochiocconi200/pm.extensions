package com.servicenow.processmining.extensions.pm.bpmn;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.xml.ModelValidationException;

public class BPMNProcessGenerator
    extends BPMNBaseGenerator
{
    private ProcessMiningModel processModel = null;
    private String fileName = null;
    private BpmnDiagram diagram = null;
    private BpmnPlane plane = null;
    private Process process = null;
    private HashMap<String, BPMNActivity> processNodes = null;
    private ArrayList<BpmnModelElementInstance> processSequences = null;

    private int position = 1;
    private double xCoordinate = X_START;
    private double yCoordinate = Y_START;

    public BPMNProcessGenerator(final ProcessMiningModel processModel)
    {
        super(Bpmn.createEmptyModel());
        this.processModel = processModel;
    }

    public ProcessMiningModel getProcessModel()
    {
        return this.processModel;
    }

    public boolean createBPMNProcessFile()
    {
        createProcess();
        addNodes();
        addTransitions();
        saveProcess();

        return true;
    }

    public String getBPMNFileName()
    {
        if (fileName == null) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            fileName = TARGET_FOLDER_NAME + getProcessModel().getName() + "_" + getProcessModel().getProjectId() + " - " + dateFormat.format(date) + ".bpmn";
        }

        return fileName;
    }

    private void createProcess()
    {
        Definitions definitions = getModelInstance().newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        getModelInstance().setDefinitions(definitions);

        process = getModelInstance().newInstance(Process.class);
        process.setAttributeValue("id", filterId(getProcessModel().getName()), true);
        process.setAttributeValue("name", getProcessModel().getName(), false);
        definitions.addChildElement(process);

        diagram = getModelInstance().newInstance(BpmnDiagram.class);
        plane = getModelInstance().newInstance(BpmnPlane.class);
        plane.setBpmnElement(process);
        diagram.setBpmnPlane(plane);
        definitions.addChildElement(diagram);
    }

    private void addNodes()
    {
        processNodes = new HashMap<String, BPMNActivity>();

        // Add Starting Node(s)
        addStartingNodes();

        // Add all nodes in between start and end events
        addMiddleNodes();

        // Add End Node(s)
        addEndingNodes();
    }

    private void addStartingNodes()
    {
        // Add Starting Node(s)
        for (String startNode : getProcessModel().getStartingNodes()) {
            String name = getProcessModel().getNodes().get(startNode).getName();
            String id = filterId(name);
            BpmnModelElementInstance bpmnProcessNode = createElement(process, id, name,
                    StartEvent.class, plane, xCoordinate + (WIDTH / 4), yCoordinate + (HEIGHT / 4), HEIGHT / 2,
                    WIDTH / 2, true);
            BPMNActivity activity = new BPMNActivity(position++, BPMNActivity.START_TYPE, id, name, bpmnProcessNode,
                    xCoordinate, yCoordinate);
            processNodes.put(name, activity);
            xCoordinate += X_INCREMENT;
            yCoordinate += Y_INCREMENT;

            // After adding the newly added starting transition, we need to check if we need
            // to place a Gateway in front.
            // We should add a Gateway to make the process BPMN compliant, when there is
            // more than one
            // transition coming out of the activity we are trying to add. As the Gateway
            // will serve as the
            // indicator to route the work through more than one unconditional path.
            if (getProcessModel().hasMoreThanOneOutgoingTransition(startNode)) {
                String gwName = getProcessModel().getNodes().get(startNode).getName() + "-GW";
                String gwId = filterId(gwName);
                BpmnModelElementInstance gwBpmnProcessNode = createElement(process, gwId, gwName,
                        ExclusiveGateway.class, plane, xCoordinate + (WIDTH / 4), yCoordinate + (HEIGHT / 4),
                        HEIGHT / 2, WIDTH / 2, false);
                BPMNActivity gwActivity = new BPMNActivity(position++, BPMNActivity.GW_TYPE, gwId, gwName,
                        gwBpmnProcessNode, xCoordinate, yCoordinate);
                processNodes.put(gwName, gwActivity);
                xCoordinate += X_INCREMENT;
                yCoordinate += Y_INCREMENT;
            }
        }
    }

    private void addMiddleNodes()
    {
        for (String middleNode : getProcessModel().getMiddleNodes()) {
            String name = getProcessModel().getNodes().get(middleNode).getName();
            String id = filterId(name);
            BpmnModelElementInstance bpmnProcessNode = createElement(process, id, name,
                    UserTask.class, plane, xCoordinate, yCoordinate, HEIGHT, WIDTH, true);
            BPMNActivity activity = new BPMNActivity(position++, BPMNActivity.USER_TYPE, id, name, bpmnProcessNode,
                    xCoordinate, yCoordinate);
            processNodes.put(name, activity);
            xCoordinate += X_INCREMENT;
            yCoordinate += Y_INCREMENT;

            // After we add the new activity, we need to check if we need to place a Gateway
            // after it.
            // We should add a Gateway to make the process BPMN compliant, when there is
            // more than one
            // transition coming out of the activity we are trying to add. As the Gateway
            // will serve as the
            // indicator to route the work through more than one unconditional path.
            if (getProcessModel().hasMoreThanOneOutgoingTransition(middleNode)) {
                String gwName = getProcessModel().getNodes().get(middleNode).getName() + "-GW";
                String gwId = filterId(gwName);
                BpmnModelElementInstance gwBpmnProcessNode = createElement(process, gwId, gwName,
                        ExclusiveGateway.class, plane, xCoordinate + (WIDTH / 4), yCoordinate + (HEIGHT / 4),
                        HEIGHT / 2, WIDTH / 2, false);
                BPMNActivity gwActivity = new BPMNActivity(position++, BPMNActivity.GW_TYPE, gwId, gwName,
                        gwBpmnProcessNode, xCoordinate, yCoordinate);
                processNodes.put(gwName, gwActivity);
                xCoordinate += X_INCREMENT;
                yCoordinate += Y_INCREMENT;
            }
        }
    }

    private void addEndingNodes()
    {
        for (String endNode : getProcessModel().getEndingNodes()) {
            String name = getProcessModel().getNodes().get(endNode).getName();
            String id = filterId(name);
            BpmnModelElementInstance bpmnProcessNode = createElement(process, id, name,
                    EndEvent.class, plane, xCoordinate, yCoordinate + (HEIGHT / 4), HEIGHT / 2, WIDTH / 2, true);
            BPMNActivity activity = new BPMNActivity(position++, BPMNActivity.END_TYPE, id, name, bpmnProcessNode,
                    xCoordinate, yCoordinate);
            processNodes.put(name, activity);
            xCoordinate += X_INCREMENT;
        }
    }

    private void addTransitions()
    {
        processSequences = new ArrayList<BpmnModelElementInstance>();
        for (ProcessMiningModelTransition transition : getProcessModel().getTransitions().values()) {
            // We need to check if we need to split a transition to route via a Gateway. If
            // the source has more than
            // one outgoing transition, then we need to create one transition from source to
            // GW, and then multiple
            // transitions from the GW to the target destination nodes...
            String fromNodeId = transition.getFrom();
            boolean addedGateway = false;
            if (getProcessModel().hasMoreThanOneOutgoingTransition(fromNodeId)) {
                BPMNActivity fromActivity = processNodes.get(transition.getFromName());
                BPMNActivity toActivity = processNodes.get(transition.getFromName() + "-GW");
                if (!sequenceExists(fromActivity, toActivity)) {
                    FlowNode fromNode = (FlowNode) fromActivity.getElement();
                    FlowNode toNode = (FlowNode) toActivity.getElement();
                    double fromX = getFromXCoordinate(fromActivity, toActivity),
                            fromY = getFromYCoordinate(fromActivity, toActivity);
                    double toX = getToXCoordinate(fromActivity, toActivity),
                            toY = getToYCoordinate(fromActivity, toActivity);
                    double middleX = getMiddleXCoordinate(fromActivity, toActivity);
                    double middleY = getMiddleYCoordinate(fromActivity, toActivity);
                    SequenceFlow bpmnProcessSequence = createSequenceFlow(process, fromNode, toNode, plane, fromX,
                            fromY, middleX, middleY, toX, toY);
                    processSequences.add(bpmnProcessSequence);
                }
                addedGateway = true;
            }

            String fromTransitionNode = addedGateway ? transition.getFromName() + "-GW" : transition.getFromName();
            BPMNActivity fromActivity = processNodes.get(fromTransitionNode);
            BPMNActivity toActivity = processNodes.get(transition.getToName());
            FlowNode fromNode = (FlowNode) fromActivity.getElement();
            FlowNode toNode = (FlowNode) toActivity.getElement();
            double fromX = getFromXCoordinate(fromActivity, toActivity),
                    fromY = getFromYCoordinate(fromActivity, toActivity);
            double toX = getToXCoordinate(fromActivity, toActivity), toY = getToYCoordinate(fromActivity, toActivity);
            double middleX = getMiddleXCoordinate(fromActivity, toActivity);
            double middleY = getMiddleYCoordinate(fromActivity, toActivity);
            SequenceFlow bpmnProcessSequence = createSequenceFlow(process, fromNode, toNode, plane, fromX, fromY,
                    middleX, middleY, toX, toY);
            processSequences.add(bpmnProcessSequence);
        }
    }

    private boolean sequenceExists(BPMNActivity fromActivity, BPMNActivity toActivity)
    {
        String sequenceId = fromActivity.getId() + "-" + toActivity.getId();
        for (BpmnModelElementInstance sequence : processSequences) {
            if (sequence.getAttributeValue("id").equals(sequenceId)) {
                return true;
            }
        }

        return false;
    }

    private void saveProcess()
    {
        try {
            Bpmn.validateModel(getModelInstance());
            File file = new File(getBPMNFileName());
            Bpmn.writeModelToFile(file, getModelInstance());
        }
        catch (ModelValidationException mve) {
            mve.printStackTrace();
        }
    }

    private final static double X_START = 100;
    private final static double Y_START = 100;
    private final static int X_INCREMENT = 200;
    private final static int Y_INCREMENT = 100;
    private static final String TARGET_FOLDER_NAME = "/tmp/ProcessMiningBPMNGenerator";
}