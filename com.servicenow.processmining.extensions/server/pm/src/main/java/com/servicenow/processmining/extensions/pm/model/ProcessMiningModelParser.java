package com.servicenow.processmining.extensions.pm.model;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningModelParser
{
    protected String versionId = null;
    protected ProcessMiningModel pmm = null;
    protected JSONObject processModelJSON = null;
    protected String errors = null;

    public ProcessMiningModelParser(final String versionId)
    {
        this.versionId = versionId;
    }

    public String getVersionId()
    {
        return this.versionId;
    }
    
    public boolean parse(final String processModelJSONString)
    {
        boolean parseResult = true;
        createEmptyProcessMiningModel();

        createJSONObject(processModelJSONString);
        if (!modelHasErrors()) {
            // Parse general model information...
            parseHeader();

            // Parse model nodes ...
            ArrayList<ProcessMiningModelNode> nodes = parseNodes(getRootNodesArray());
            pmm.setNodes(nodes);

            // Parse model transitions ...
            ArrayList<ProcessMiningModelTransition> transitions = parseTransitions(getRootTransitionsArray());
            pmm.addTransitions(transitions);
            pmm.synchronizeTransitionNames();

            // Parse breakdowns ...
            ArrayList<ProcessMiningModelBreakdown> breakdowns = parseBreakdowns(getRootBreakdownsArray());
            pmm.addBreakdowns(breakdowns);

            // Parse Variations ...
            parseVariations();
            logger.info("Successfully completed parsing Process Mining Payload.");
            logger.debug(pmm.toString());
        }
        else {
            parseResult = false;
            logger.info("Successfully parsed response, but the model has errors: (" + errors + ")");
        }
                
        return parseResult;
    }

    protected boolean modelHasErrors()
    {
        if (processModelJSON.has("errors")) {
            JSONArray errorsObj = (JSONArray) processModelJSON.getJSONArray("errors");
            String errorMessage = errorsObj.getJSONObject(0).getString("message");
            String errorType = errorsObj.getJSONObject(0).getString("errorType");
            this.errors = errorType + " - " + errorMessage;
            return true;
        }

        return false;
    }

    protected void createEmptyProcessMiningModel()
    {
        this.pmm = new ProcessMiningModel(getVersionId());
    }

    protected void createJSONObject(final String processModelJSONString)
    {
        processModelJSON = new JSONObject(processModelJSONString);
    }

    private void parseHeader()
    {
        if (processModelJSON.has("data")) {
            JSONObject dataObj = (JSONObject) processModelJSON.getJSONObject("data");
            if (dataObj.has("GlidePromin_Query")) {
                JSONObject queryObj = (JSONObject) dataObj.getJSONObject("GlidePromin_Query");
                if (queryObj.has("scheduleModel")) {
                    JSONObject scheduleModelObj = (JSONObject) queryObj.getJSONObject("scheduleModel");

                    // Project Details ...
                    if (scheduleModelObj.has("project")) {
                        JSONObject projectObj = (JSONObject) scheduleModelObj.getJSONObject("project");
                        if (projectObj != null) {
                            String name = projectObj.getString("name");
                            String projectId = projectObj.getString("projectId");
                            pmm.setName(name);
                            pmm.setProjectId(projectId);
                        }
                    }

                    // Project Entities Details ...
                    if (scheduleModelObj.has("projectEntities")) {
                        JSONArray projectEntities = (JSONArray) scheduleModelObj.getJSONArray("projectEntities");
                        if (projectEntities != null) {
                            for (int i=0; i < projectEntities.length(); i++) {
                                JSONObject entityObj = (JSONObject) projectEntities.get(i);
                                String tableId = entityObj.getString("entityId");
                                JSONObject tableNameObj = (JSONObject) entityObj.getJSONObject("table");
                                String tableName = null;
                                if (tableNameObj != null) {
                                    tableName = tableNameObj.getString("name");
                                }

                                if (entityObj != null) {
                                    if (entityObj.has("activities")) {
                                        JSONArray activities = (JSONArray) entityObj.getJSONArray("activities");
                                        if (activities != null) {
                                            for (int j=0; j < activities.length(); j++) {
                                                JSONObject activityObj = (JSONObject) activities.get(j);
                                                String fieldId = activityObj.getString("id");
                                                String fieldName = activityObj.getString("field");
                                                ProcessMiningModelEntity entity = new ProcessMiningModelEntity(tableId, tableName, fieldId, fieldName);
                                                pmm.getEntities().add(entity);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Model Aggregates
                    if (scheduleModelObj.has("aggregates")) {
                        JSONArray aggregateArray = scheduleModelObj.getJSONArray("aggregates");
                        JSONObject aggregateStats = (JSONObject) aggregateArray.getJSONObject(0);
                        if (aggregateStats != null) {
                            if (aggregateStats.has("model")) {
                                JSONObject modelStats = (JSONObject) aggregateStats.getJSONObject("model");
                                ProcessMiningModelAggregate aggregate = new ProcessMiningModelAggregate();
                                pmm.setAggregate(aggregate);
                                aggregate.setCaseCount(modelStats.getInt("caseCount"));
                                aggregate.setVariantCount(modelStats.getInt("variantCount"));
                                aggregate.setMinCaseDuration(modelStats.getInt("minCaseDuration"));
                                aggregate.setMaxCaseDuration(modelStats.getInt("maxCaseDuration"));
                                aggregate.setAvgCaseDuration(modelStats.getInt("avgCaseDuration"));
                                aggregate.setStdDeviation(modelStats.getInt("stdDeviation"));
                                aggregate.setMedianDuration(modelStats.getInt("medianDuration"));
                            }
                        }
                    }

                    // Filters ...
                    if (scheduleModelObj.has("filterSets")) {
                        JSONArray filterSets = scheduleModelObj.getJSONArray("filterSets");
                        for (int i=0; i < filterSets.length(); i++) {
                            JSONObject filterDetails = filterSets.getJSONObject(i);
                            if (filterDetails != null) {
                                if (filterDetails.has("id")) {
                                    String id = filterDetails.getString("id");
                                    String name = filterDetails.getString("name");
                                    int caseCount = filterDetails.getInt("caseCount");
                                    int variantCount = filterDetails.getInt("variantCount");
                                    int totalDuration = filterDetails.getInt("totalDuration");
                                    int maxDuration = filterDetails.getInt("maxDuration");
                                    int minDuration = filterDetails.getInt("minDuration");
                                    int avgDuration = filterDetails.getInt("avgDuration");
                                    int medianDuration = filterDetails.getInt("medianDuration");
                                    int stdDeviation = filterDetails.getInt("stdDeviation");

                                    ProcessMiningModelFilter filter = new ProcessMiningModelFilter();
                                    filter.setId(id);
                                    filter.setName(name);
                                    filter.setCaseFrequency(caseCount);
                                    filter.setVariantCount(variantCount);
                                    filter.setTotalDuration(totalDuration);
                                    filter.setMaxDuration(maxDuration);
                                    filter.setMinDuration(minDuration);
                                    filter.setAvgDuration(avgDuration);
                                    filter.setMedianDuration(medianDuration);
                                    filter.setStdDeviation(stdDeviation);

                                    // Parse Filter Conditions ...
                                    JSONObject filterObj = filterDetails.getJSONObject("filter");
                                    if (filterObj.has("breakdowns")) {
                                        JSONArray breakdowns = filterObj.getJSONArray("breakdowns");
                                        if (breakdowns != null && breakdowns.length() > 0) {
                                            ProcessMiningModelFilterBreakdown filterBreakdown = new ProcessMiningModelFilterBreakdown();
                                            filter.setFilterBreakdown(filterBreakdown);
                                            for (int j=0; j < breakdowns.length(); j++) {
                                                JSONObject breakdownObj = (JSONObject) breakdowns.get(j);
                                                String filterEntityId = breakdownObj.getString("entityId");
                                                filterBreakdown.setEntityId(filterEntityId);
                                                JSONArray innerBreakdowns = breakdownObj.getJSONArray("breakdowns");
                                                for (int k=0; k < innerBreakdowns.length(); k++) {
                                                    String condition = innerBreakdowns.get(k).toString();
                                                    ProcessMiningModelFilterBreakdownCondition bCondition = new ProcessMiningModelFilterBreakdownCondition();
                                                    bCondition.setCondition(condition);
                                                    filterBreakdown.addCondition(bCondition);
                                                }
                                            }
                                        }
                                    }

                                    if (filterObj.has("advancedTransitions")) {
                                        JSONArray tNodes = filterObj.getJSONArray("advancedTransitions");
                                        if (tNodes.length() > 0) {
                                            JSONObject transitionNodes = (JSONObject) filterObj.getJSONArray("advancedTransitions").get(0);
                                            ProcessMiningModelFilterTransitions filterTransitions = new ProcessMiningModelFilterTransitions();
                                            filter.setFilterTransitions(filterTransitions);
                                            if (transitionNodes.get("advancedTransitions") != null) {
                                                JSONArray innerTransitionsNode = transitionNodes.getJSONArray("advancedTransitions");
                                                for (int k=0; k < innerTransitionsNode.length(); k++) {
                                                    String entityId = ((JSONObject)innerTransitionsNode.get(k)).getString("entityId");
                                                    String field = ((JSONObject)innerTransitionsNode.get(k)).getString("field");
                                                    String predicate = ((JSONObject)innerTransitionsNode.get(k)).getString("predicate");
                                                    String occurrence = ((JSONObject)innerTransitionsNode.get(k)).has("occurrence") ? ((JSONObject)innerTransitionsNode.get(k)).getString("occurrence") : "";
                                                    JSONArray valuesArr = ((JSONObject)innerTransitionsNode.get(k)).getJSONArray("values");
                                                    String values = valuesArr.getString(0);
                                                    String relation = ((JSONObject)innerTransitionsNode.get(k)).getString("relation");

                                                    ProcessMiningModelFilterTransitionsNode transitionNode = new ProcessMiningModelFilterTransitionsNode();
                                                    transitionNode.setEntityId(entityId);
                                                    transitionNode.setField(field);
                                                    transitionNode.setPredicate(predicate);
                                                    transitionNode.setOcurrence(occurrence);
                                                    transitionNode.setValues(values);
                                                    transitionNode.setRelation(relation);

                                                    filterTransitions.addTransitionsNode(transitionNode);
                                                }
                                            }
                                        }
                                    }

                                    pmm.addFilter(filter);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private JSONArray getRootNodesArray()
    {
        JSONArray nodesObj = null;
        if (processModelJSON.has("data")) {
            JSONObject dataObj = (JSONObject) processModelJSON.getJSONObject("data");
            if (dataObj.has("GlidePromin_Query")) {
                JSONObject queryObj = (JSONObject) dataObj.getJSONObject("GlidePromin_Query");
                if (queryObj.has("scheduleModel")) {
                    JSONObject scheduleModelObj = (JSONObject) queryObj.getJSONObject("scheduleModel");
                    if (scheduleModelObj.has("nodes")) {
                        nodesObj = (JSONArray) scheduleModelObj.getJSONArray("nodes");
                    }
                }
            }
        }

        return nodesObj;
    }

    private ArrayList<ProcessMiningModelNode> parseNodes(final JSONArray nodesObj)
    {
        ArrayList<ProcessMiningModelNode> nodes = new ArrayList<ProcessMiningModelNode>();
        if (nodesObj != null) {
            for (int i=0; i < nodesObj.length(); i++) {
                JSONObject nodeObj = (JSONObject) nodesObj.get(i);
                ProcessMiningModelNode pmmn = new ProcessMiningModelNode(nodeObj.getString("key"), nodeObj.getString("label"));
                pmmn.setActivityId(nodeObj.getString("activityId"));
                pmmn.setEntityId(nodeObj.getString("entityId"));
                pmmn.setIsStart(nodeObj.getBoolean("isStart"));
                pmmn.setIsEnd(nodeObj.getBoolean("isEnd"));
                pmmn.setAbsoluteFrequency(nodeObj.getInt("absoluteFreq"));
                pmmn.setCaseFrequency(nodeObj.getInt("caseFreq"));
                pmmn.setMaxReps(nodeObj.getInt("maxReps"));
                pmmn.setField(nodeObj.getString("field"));
                pmmn.setFieldLabel(nodeObj.getString("fieldLabel"));
                pmmn.setValue(nodeObj.getString("value"));
                nodes.add(pmmn);
            }
        }

        logger.debug("Parsed (" + pmm.getNodes().size() + ") nodes.");
        return nodes;
    }

    private JSONArray getRootTransitionsArray()
    {
        JSONArray edgesObj = null;
        if (processModelJSON.has("data")) {
            JSONObject dataObj = (JSONObject) processModelJSON.getJSONObject("data");
            if (dataObj.has("GlidePromin_Query")) {
                JSONObject queryObj = (JSONObject) dataObj.getJSONObject("GlidePromin_Query");
                if (queryObj.has("scheduleModel")) {
                    JSONObject scheduleModelObj = (JSONObject) queryObj.getJSONObject("scheduleModel");
                    if (scheduleModelObj.has("edges")) {
                        edgesObj = (JSONArray) scheduleModelObj.getJSONArray("edges");
                    }
                }
            }
        }

        return edgesObj;
    }    

    private ArrayList<ProcessMiningModelTransition> parseTransitions(final JSONArray edgesObj)
    {
        ArrayList<ProcessMiningModelTransition> transitions = new ArrayList<ProcessMiningModelTransition>();
        if (edgesObj != null) {
            for (int i=0; i < edgesObj.length(); i++) {
                JSONObject edgeObj = (JSONObject) edgesObj.get(i);
                ProcessMiningModelTransition pmmt = new ProcessMiningModelTransition(edgeObj.getString("from"), edgeObj.getString("to"));
                pmmt.setAbsoluteFrequency(edgeObj.getInt("absoluteFreq"));
                pmmt.setCaseFrequency(edgeObj.getInt("caseFreq"));
                pmmt.setMaxReps(edgeObj.getInt("maxReps"));
                pmmt.setTotalDuration(edgeObj.getInt("totalDuration"));
                pmmt.setMaxDuration(edgeObj.getInt("maxDuration"));
                pmmt.setMinDuration(edgeObj.getInt("minDuration"));
                pmmt.setAvgDuration(edgeObj.getInt("avgDuration"));
                pmmt.setStdDeviation(edgeObj.getInt("stdDeviation"));
                pmmt.setMedianDuration(edgeObj.getInt("medianDuration"));
                transitions.add(pmmt);
            }
        }

        logger.debug("Parsed (" + transitions.size() + ") transitions.");
        return transitions;
    }

    private ArrayList<ProcessMiningModelBreakdown> parseBreakdowns(JSONArray rootBreakdownsArray)
    {
        ArrayList<ProcessMiningModelBreakdown> breakdowns = new ArrayList<ProcessMiningModelBreakdown>();

        if (rootBreakdownsArray != null) {
            for (int i=0; i < rootBreakdownsArray.length(); i++) { 
                JSONObject breakdownEntity = rootBreakdownsArray.getJSONObject(i);
                if (breakdownEntity.has("entityId")) {
                    String entityId = breakdownEntity.getString("entityId");
                    JSONArray breakdownStats = breakdownEntity.getJSONArray("breakdownStats");
                    for (int j=0; j < breakdownStats.length(); j++) {
                        JSONObject breakdownEntry = breakdownStats.getJSONObject(j);
                        String field = breakdownEntry.getString("field");
                        String label = breakdownEntry.getString("label");
                        String displayName = breakdownEntry.isNull("displayName") ? label : breakdownEntry.getString("displayName");
                        ProcessMiningModelBreakdown breakdown = new ProcessMiningModelBreakdown();
                        breakdown.setEntityId(entityId);
                        breakdown.setField(field);
                        breakdown.setLabel(label);
                        breakdown.setDisplayName(displayName);

                        if (breakdownEntry.has("line")) {
                            JSONArray lineArray = breakdownEntry.getJSONArray("line");
                            for (int k=0; k < lineArray.length(); k++) {
                                JSONObject breakdownStat = lineArray.getJSONObject(k);
                                String bdLabel = breakdownStat.getString("label");
                                String bdValue = breakdownStat.isNull("value") ? "<empty>" : breakdownStat.getString("value");
                                int bdCaseCount = breakdownStat.getInt("caseCount");
                                int bdVariantCount = breakdownStat.getInt("variantCount");
                                int bdAvgDuration = breakdownStat.getInt("avgDuration");
                                int bdMedianDuration = breakdownStat.getInt("medianDuration");
                                int bdStdDeviation = breakdownStat.getInt("stdDeviation");
                                ProcessMiningModelBreakdownStat bStat = new ProcessMiningModelBreakdownStat();
                                bStat.setLabel(bdLabel);
                                bStat.setValue(bdValue);
                                bStat.setCaseCount(bdCaseCount);
                                bStat.setVariantCount(bdVariantCount);
                                bStat.setAvgDuration(bdAvgDuration);
                                bStat.setMedianDuration(bdMedianDuration);
                                bStat.setStdDuration(bdStdDeviation);
                                breakdown.addBreakdownStat(bStat);
                            }
                        }

                        breakdowns.add(breakdown);
                    }
                }
            }
        }

        return breakdowns;
    }

    private JSONArray getRootBreakdownsArray()
    {
        JSONArray breakdownsObj = null;
        if (processModelJSON.has("data")) {
            JSONObject dataObj = (JSONObject) processModelJSON.getJSONObject("data");
            if (dataObj.has("GlidePromin_Query")) {
                JSONObject queryObj = (JSONObject) dataObj.getJSONObject("GlidePromin_Query");
                if (queryObj.has("scheduleModel")) {
                    JSONObject scheduleModelObj = (JSONObject) queryObj.getJSONObject("scheduleModel");
                    if (scheduleModelObj.has("edges")) {
                        breakdownsObj = (JSONArray) scheduleModelObj.getJSONArray("breakdowns");
                    }
                }
            }
        }

        return breakdownsObj;
    }

    protected void parseVariations()
    {
        if (processModelJSON.has("data")) {
            JSONObject dataObj = (JSONObject) processModelJSON.getJSONObject("data");
            if (dataObj.has("GlidePromin_Query")) {
                JSONObject queryObj = (JSONObject) dataObj.getJSONObject("GlidePromin_Query");
                if (queryObj.has("scheduleModel")) {
                    JSONObject scheduleModelObj = (JSONObject) queryObj.getJSONObject("scheduleModel");
                    if (scheduleModelObj.has("variantResult")) {
                        JSONArray variantResultObj = (JSONArray) scheduleModelObj.getJSONArray("variantResult");
                        if (variantResultObj != null) {
                            for (int i=0; i < variantResultObj.length(); i++) {
                                JSONObject vObj = (JSONObject) variantResultObj.get(i);
                                int totalVariants = vObj.has("totalVariants") ? vObj.getInt("totalVariants") : 0;
                                pmm.setTotalVariants(totalVariants);
                                if (vObj != null) {
                                    JSONArray variantsObj = (JSONArray) vObj.getJSONArray("variants");
                                    for (int j=0; j < variantsObj.length(); j++) {
                                        JSONObject variantObj = (JSONObject) variantsObj.get(j);
                                        ProcessMiningModelVariant pmmv = new ProcessMiningModelVariant(variantObj.getString("id"));
                                        pmmv.setEntityId(variantObj.getString("entityId"));
                                        JSONArray nodesObj = (JSONArray) variantObj.getJSONArray("nodes");
                                        if (nodesObj != null) {
                                            for (int k=0; k < nodesObj.length(); k++) {
                                                pmmv.addNodeToPath(nodesObj.get(k).toString());
                                            }
                                        }
                                        pmmv.setNodeCount(variantObj.getInt("nodeCount"));
                                        pmmv.setFrequency(variantObj.getInt("frequency"));
                                        pmmv.setTotalDuration(variantObj.getInt("totalDuration"));
                                        pmmv.setMaxDuration(variantObj.getInt("maxDuration"));
                                        pmmv.setMinDuration(variantObj.getInt("minDuration"));
                                        pmmv.setAvgDuration(variantObj.getInt("avgDuration"));
                                        pmmv.setMedianDuration(variantObj.getInt("medianDuration"));
                                        pmmv.setStdDeviation(variantObj.getInt("stdDeviation"));
                                        pmm.addVariant(pmmv);

                                        if (variantObj.has("model") && !variantObj.isNull("model")) {
                                            JSONObject modelObj = variantObj.getJSONObject("model");
                                            if (modelObj != null) {
                                                // Load Variant Nodes.
                                                JSONArray nodesArray = modelObj.getJSONArray("nodes");
                                                ArrayList<ProcessMiningModelNode> nodes = parseNodes(nodesArray);
                                                pmmv.setNodesFromArray(nodes);

                                                // Load Variant Edges.
                                                JSONArray edgesArray = modelObj.getJSONArray("edges");
                                                ArrayList<ProcessMiningModelTransition> transitions = parseTransitions(edgesArray);
                                                pmmv.addTransitions(transitions);

                                                JSONArray caseIdsArray = variantObj.getJSONArray("caseIds");
                                                ArrayList<String> caseIds = parseCaseIdsFromVariantNodeSequence(caseIdsArray);
                                                pmmv.setCaseIds(caseIds);
                                            }
                                        }
                                        // If there is no model, the edges are described by the sequence of nodes...
                                        else {
                                            // Load Variant Nodes.
                                            JSONArray nodesArray = variantObj.getJSONArray("nodes");
                                            ArrayList<ProcessMiningModelNode> nodes = parseNodesFromVariantNodeSequence(nodesArray);
                                            pmmv.setNodesFromArray(nodes);

                                            JSONArray caseIdsArray = variantObj.getJSONArray("caseIds");
                                            ArrayList<String> caseIds = parseCaseIdsFromVariantNodeSequence(caseIdsArray);
                                            pmmv.setCaseIds(caseIds);

                                            // Load Variant Edges.
                                            ArrayList<ProcessMiningModelTransition> transitions = parseTransitionsFromVariantNodeSequence(nodesArray);
                                            pmmv.addTransitions(transitions);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        logger.debug("Parsed: (" + pmm.getVariants().size() + ") variants.");
        for (ProcessMiningModelVariant variant : pmm.getVariants().values()) { 
            logger.debug(variant.toString());
        }
    }

    private ArrayList<ProcessMiningModelNode> parseNodesFromVariantNodeSequence(JSONArray nodesArray)
    {
        ArrayList<ProcessMiningModelNode> nodes = new ArrayList<ProcessMiningModelNode>();
        HashSet<String> addedNodes = new HashSet<String>();
        if (nodesArray != null) {
            for (int i=0; i < nodesArray.length(); i++) {
                String nodeId = nodesArray.getString(i);
                if (!addedNodes.contains(nodeId)) {
                    // When parsing 'variants', we do not get a node map. So we need to recreate the node object.
                    ProcessMiningModelNode pmmn = this.getProcessMiningModel().getNodes().get(nodeId);
                    if  (pmmn == null) {
                        pmmn = new ProcessMiningModelNode(nodeId, nodeId);
                    }
                    nodes.add(pmmn);
                    addedNodes.add(nodeId);
                }
            }
        }
        addedNodes = null;

        logger.debug("Parsed (" + pmm.getNodes().size() + ") nodes.");
        return nodes;
    }

    private ArrayList<String> parseCaseIdsFromVariantNodeSequence(JSONArray caseIdsArray)
    {
        ArrayList<String> caseIds = new ArrayList<String>();
        HashSet<String> addedCaseIds = new HashSet<String>();
        if (caseIdsArray != null) {
            for (int i=0; i < caseIdsArray.length(); i++) {
                String caseId = caseIdsArray.getString(i);
                if (!addedCaseIds.contains(caseId)) {
                    caseIds.add(caseId);
                    addedCaseIds.add(caseId);
                }
            }
        }
        addedCaseIds = null;

        logger.debug("Parsed (" + pmm.getNodes().size() + ") case Ids.");
        return caseIds;
    }

    private ArrayList<ProcessMiningModelTransition> parseTransitionsFromVariantNodeSequence(JSONArray nodesArray)
    {
        ArrayList<ProcessMiningModelTransition> transitions = new ArrayList<ProcessMiningModelTransition>();
        HashSet<String> addedTransitions = new HashSet<String>();
        if (nodesArray != null) {
            boolean processedFirstNode = false;
            String previousNodeId = null;
            for (int i=0; i < nodesArray.length(); i++) {
                String nodeId = nodesArray.getString(i);
                if (processedFirstNode) {
                    String transitionId = previousNodeId + "-To-" + nodeId;
                    if (!addedTransitions.contains(transitionId)) {
                        ProcessMiningModelTransition pmmt = new ProcessMiningModelTransition(previousNodeId, nodeId);
                        pmmt.setAbsoluteFrequency(0);
                        pmmt.setCaseFrequency(0);
                        pmmt.setMaxReps(0);
                        pmmt.setTotalDuration(0);
                        pmmt.setMaxDuration(0);
                        pmmt.setMinDuration(0);
                        pmmt.setAvgDuration(0);
                        pmmt.setStdDeviation(0);
                        pmmt.setMedianDuration(0);
                        transitions.add(pmmt);
                        addedTransitions.add(transitionId);
                    }
                }
                processedFirstNode = true;
                previousNodeId = nodeId;
            }
        }

        logger.debug("Parsed (" + transitions.size() + ") transitions.");
        return transitions;
    }

    public ProcessMiningModel getProcessMiningModel()
    {
        return pmm;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelParser.class);
}
