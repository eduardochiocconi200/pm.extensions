package com.servicenow.processmining.extensions.pm.analysis.goldratt;

import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrieval;
import com.servicenow.processmining.extensions.pm.dao.ProcessMiningModelRetrievalFactory;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParser;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelParserFactory;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueStreamAnalysis
{
    private ServiceNowInstance snInstance = null;
    private ProcessMiningModel model = null;
    private ValueStream valueStream = null;

    public ValueStreamAnalysis(final ServiceNowInstance snInstance, final ProcessMiningModel model, final ValueStream valueStream)
    {
        this.snInstance = snInstance;
        this.model = model;
        this.valueStream = valueStream;
        this.valueStream.setModel(this.model);
    }

    public ServiceNowInstance getInstance()
    {
        return this.snInstance;
    }

    public ProcessMiningModel getModel()
    {
        return this.model;
    }

    public ValueStream getValueStream()
    {
        return this.valueStream;
    }

    public boolean run()
    {
        if (retrieveAllVariants()) {
            for (ProcessMiningModelVariant variant : getModel().getVariants().values()) {
                int currentPhase = 1;
                logger.debug("\nVariant: (" + variant.getTranslatedRouteNodes("->") + ")");
                for (int i=1; i < variant.getPath().size(); i++) {
                    int lastCurrentPhaseIndex = -1;
                    do {
                        lastCurrentPhaseIndex = getValueStream().getVariantLastPhaseIndex(variant, currentPhase) + 1;
                        // If we did not find any node in the current phase, then we need to skip it and try to
                        // find nodes for the next one.
                        if (lastCurrentPhaseIndex == 0) {
                            currentPhase++;
                        }
                    }
                    while (lastCurrentPhaseIndex == 0);

                    // Now that we know the index in the path of the last ocurrence of the next phase index, we can calculate 
                    // the elapsed time to complete this phase.
                    long phaseAvgTime = 0;
                    int touchPoints = 0;
                    String phasePath = "";
                    for (int j=i; j <= lastCurrentPhaseIndex && j < variant.getPath().size(); j++) {
                        String from = variant.getPath().get(j-1);
                        String to = variant.getPath().get(j);
                        if (phasePath.equals("")) {
                            phasePath = getModel().getNodes().get(from).getName() + "->" + getModel().getNodes().get(to).getName();
                        }
                        else {
                            phasePath += "->" + getModel().getNodes().get(to).getName();
                        }
                        ProcessMiningModelTransition transition = getModel().getTransition(from, to);
                        phaseAvgTime += transition.getAvgDuration();
                        touchPoints++;
                        logger.debug("Phase (" + currentPhase + ") => (" + getModel().getNodes().get(from).getName() + ")->(" + getModel().getNodes().get(to).getName()+ "). Avg: (" + transition.getAvgDuration() + "), Mean: (" + transition.getMedianDeviation() + "), Min: (" + transition.getMinDuration() + "), Max: (" + transition.getMaxDuration() + ")");
                    }
                    ValueStreamPhaseMeasure measure = new ValueStreamPhaseMeasure(variant.getId(), variant.getFrequency(), phaseAvgTime, 0, 0, 0);
                    measure.setTouchpoints(touchPoints);
                    measure.addTouchpointsPath(phasePath);
                    getValueStream().getPhases().get(currentPhase-1).getStatistics().getMeasures().add(measure);
                    i = lastCurrentPhaseIndex;
                    currentPhase++;
                }
            }

            // Print ValueStream stats...
            logger.debug("\nValue Stream Summary:");
            for (ValueStreamPhase p : getValueStream().getPhases()) {
                p.getStatistics().computeSummary();
                ValueStreamPhaseMeasure m = p.getStatistics().getSummary();
                logger.debug("*) Phase (" + p.getName() + ") Summary: " + m.toString());
            }
            logger.debug("\n");
        }
        else {
            logger.error("Could not retrieve all variants.");
            return false;
        }

        return true;
    }

    private boolean retrieveAllVariants()
    {
        String entityId = getModel().getEntities().get(0).getTableId();
        String modelVersionId = getModel().getVersionId();
        int numberOfVariants = getModel().getTotalVariants();
        ProcessMiningModelRetrieval vModel = ProcessMiningModelRetrievalFactory.getProcessMiningVariantRetrieval(getInstance(), modelVersionId, entityId, numberOfVariants);
        if (vModel.runEmptyFilter()) {
            ProcessMiningModelParser vModelParser = ProcessMiningModelParserFactory.getParser(snInstance, modelVersionId);
            if (vModelParser.parse(vModel.getProcessMiningModelJSONString())) {
                // Update the Variants attribute of the model with ALL retrieved variants (and not just the top ones) ...
                getModel().getVariants().clear();
                for (ProcessMiningModelVariant v : vModelParser.getProcessMiningModel().getVariants().values()) {
                    v.setNodes(getModel().getNodes());
                    v.setTransitions(getModel().getTransitions());
                    getModel().addVariant(v);
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }

        return true;
    }

    private static final Logger logger = LoggerFactory.getLogger(ValueStreamAnalysis.class);
}