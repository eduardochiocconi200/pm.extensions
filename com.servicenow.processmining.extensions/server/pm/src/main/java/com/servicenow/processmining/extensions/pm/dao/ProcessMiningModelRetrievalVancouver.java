package com.servicenow.processmining.extensions.pm.dao;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilter;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningModelRetrievalVancouver
    extends ProcessMiningModelRetrieval
{
    public ProcessMiningModelRetrievalVancouver(final ServiceNowInstance instance, final String modelVersionId)
    {
        super(instance, modelVersionId);
    }

    protected String getEmptyFilterPayload()
    {
        logger.debug("Enter ProcessMiningModelRetrievalVancouver.getEmptyFilterPayload()");
        String variables = "{\n" +
        "\t\t\"versionId\": \"" + getProcessModelVersionId() + "\",\n" +
        "\t\t\"filterSets\": {\n" +
        "\t\t\t\"dataFilter\": [],\n" +
        "\t\t\t\"breakdowns\": [],\n" +
        "\t\t\t\"variantFilter\": [],\n" +
        "\t\t\t\"repetitions\": [],\n" +
        "\t\t\t\"advancedTransition\": [],\n" +
        "\t\t\t\"findingFilter\": \"\",\n" +
        "\t\t\t\"adIntentFilter\": \"\"\n" +
        "\t\t}\n" +
        "\t}";

        String payload = "{\n";
        payload += "\t\"query\": \"" + query + "\",\n";
        payload += "\t\"operationName\": \"snProminWorkbenchConnected\",\n";
        payload += "\t\"variables\": " + variables + "\n";
        payload += "}";

        logger.debug("payload: (" + payload + ")");
        logger.debug("Exit ProcessMiningModelRetrievalVancouver.getEmptyFilterPayload()");
        return payload;
    }

    protected String getFilterPayload(final String entityId, final ProcessMiningModelFilter filter)
    {
        logger.debug("Enter ProcessMiningModelRetrievalVancouver.getBreakdownsFilterPayload()");
        String variables = "{\n" +
        "\t\t\"versionId\": \"" + getProcessModelVersionId() + "\",\n" +
        "\t\t\"filterSets\": {\n" +
        "\t\t\t\"dataFilter\": [],\n";
        if (filter.getBreakdownCondition() != null) {
            variables += "\t\t\t\"breakdowns\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"entityId\": \"" + entityId + "\",\n" +
            "\t\t\t\t\t\"breakdowns\": [" + filter.getBreakdownConditionJSON() + "]\n" +
            "\t\t\t\t}\n"+
            "\t\t\t],\n";
        }
        else {
            variables += "\t\t\t\"breakdowns\": [],\n";
        }

        variables += "\t\t\t\"variantFilter\": [],\n" +
        "\t\t\t\"repetitions\": [],\n";
        if (filter.getFilterTransitions() != null) {
            variables += "\t\t\t\"advancedTransition\": [],\n";
        }
        else {
            variables += "\t\t\t\"advancedTransition\": [" + filter.getFilterTransitionsJSON()+ "],\n";
        }
        variables += "\t\t\t\"findingFilter\": \"\",\n" +
        "\t\t\t\"adIntentFilter\": \"\"\n" +
        "\t\t}\n" +
        "\t}";

        String payload = "{\n";
        payload += "\t\"query\": \"" + query + "\",\n";
        payload += "\t\"operationName\": \"snProminWorkbenchConnected\",\n";
        payload += "\t\"variables\": " + variables + "\n";
        payload += "}";

        logger.debug("payload: (" + payload + ")");
        logger.debug("Exit ProcessMiningModelRetrievalVancouver.getBreakdownsFilterPayload()");
       return payload;
    }

    private static String query = "query snProminWorkbenchConnected($versionId:ID!$filterSets:GlidePromin_FilterInput!){GlidePromin_Query{scheduleModel(entityId: \\\"\\\" versionId:$versionId filterSets:$filterSets){...on GlidePromin_Model{project{name projectId paUuid domain}projectEntities{...FRAGMENT_PROJECT_ENTITIES}filterSets{id name filter{...FRAGMENT_FILTER}caseCount variantCount totalDuration maxDuration minDuration avgDuration medianDuration stdDeviation trimmedAverage}nodes{...FRAGMENT_NODES}edges{...FRAGMENT_EDGES}findings{...FRAGMENT_FINDINGS}aggregates{...FRAGMENT_AGGREGATE}breakdowns{...FRAGMENT_BREAKDOWNS}permissions{canMine canWrite canDelete canShare}variantResult(entityId:null offset:0 limit:50 orderBy:health_score orderByDesc:true){totalVariants entityId variants{...FRAGMENT_VARIANTS}}miningStats{totalRecords}version{automationDiscoveryReport{id}}}}}}fragment FRAGMENT_NODES on GlidePromin_Node{key label activityId entityId isStart isEnd absoluteFreq caseFreq maxReps fieldLabel field value repHistogram{...FRAGMENT_HISTOGRAM}}fragment FRAGMENT_HISTOGRAM on GlidePromin_Histogram{bins{leftRangeInclusive rightRangeExclusive value}}fragment FRAGMENT_EDGES on GlidePromin_Edge{from to absoluteFreq caseFreq maxReps totalDuration maxDuration minDuration avgDuration trimmedAverage stdDeviation medianDuration q1 q3 q4 nQ1 nQ3 nQ4 nBins durationHistogram{...FRAGMENT_HISTOGRAM}repHistogram{...FRAGMENT_HISTOGRAM}}fragment FRAGMENT_FINDINGS on GlidePromin_Finding{id message category categoryLabel totalDuration avgDuration frequency hasCaseIds stdDeviation}fragment FRAGMENT_AGGREGATE on GlidePromin_Aggregate{entityId model{...FRAGMENT_AGGREGATE_MODEL}node{...FRAGMENT_AGGREGATE_NODE}edge{...FRAGMENT_AGGREGATE_EDGE}variant{...FRAGMENT_AGGREGATE_VARIANT}}fragment FRAGMENT_AGGREGATE_MODEL on GlidePromin_RootAggregate{caseCount variantCount minCaseDuration maxCaseDuration avgCaseDuration trimmedAverage stdDeviation medianDuration uniqueParentRecords}fragment FRAGMENT_AGGREGATE_NODE on GlidePromin_NodeAggregate{absoluteFreq{min max}caseFreq{min max}maxReps{min max}}fragment FRAGMENT_AGGREGATE_EDGE on GlidePromin_EdgeAggregate{absoluteFreq{min max}caseFreq{min max}maxReps{min max}minDuration{min max}maxDuration{min max}avgDuration{min max}totalDuration{min max}trimmedAverage{min max}stdDeviation{min max}medianDuration{min max}}fragment FRAGMENT_AGGREGATE_VARIANT on GlidePromin_VariantAggregate{nodeCount{min max}caseFreq{min max}avgDuration{min max}healthScore{min max}trimmedAverage{min max}stdDeviation{min max}medianDuration{min max}}fragment FRAGMENT_FILTER on GlidePromin_Filter{dataFilter{entityId query}breakdowns{entityId breakdowns{field values}}advancedTransitions{...FRAGMENT_ADVANCED_TRANSITION}repetitions{...FRAGMENT_REPETITIONS}variantFilter{entityId variantIds}}fragment FRAGMENT_ADVANCED_TRANSITION on GlidePromin_AdvancedTransitionFilter{advancedTransitions{entityId field predicate occurrence values relation context{...FRAGMENT_ADVANCED_TRANSITION_CONTEXT}}transitionConstraints{fromIndex toIndex minDuration maxDuration fieldConstraint{type field}}}fragment FRAGMENT_ADVANCED_TRANSITION_CONTEXT on GlidePromin_TransitionContext{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}}}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}right{conditionType entityId field predicate values left{conditionType entityId field predicate values}right{conditionType entityId field predicate values}}}}}}fragment FRAGMENT_REPETITIONS on GlidePromin_Repetitions{minReps maxReps source{entity field value}sink{entity field value}}fragment FRAGMENT_BREAKDOWNS on GlidePromin_EntityBreakdownStats{entityId breakdownStats{field label displayName line{label value caseCount variantCount avgDuration trimmedAverage stdDeviation medianDuration}}}fragment FRAGMENT_PROJECT_ENTITIES on GlidePromin_ProjectEntity{entityId parentId name table{name label}activities{id field label displayName}activitiesOfInterest{id field label displayName}}fragment FRAGMENT_VARIANTS on GlidePromin_Variant{id statsId entityId nodes caseIds nodeCount frequency totalDuration maxDuration minDuration avgDuration trimmedAverage stdDeviation medianDuration healthScore model{nodes{...FRAGMENT_NODES}edges{...FRAGMENT_EDGES}aggregates{...FRAGMENT_AGGREGATE}breakdowns{...FRAGMENT_BREAKDOWNS}projectEntities{...FRAGMENT_PROJECT_ENTITIES}}}";

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelRetrievalVancouver.class);
}