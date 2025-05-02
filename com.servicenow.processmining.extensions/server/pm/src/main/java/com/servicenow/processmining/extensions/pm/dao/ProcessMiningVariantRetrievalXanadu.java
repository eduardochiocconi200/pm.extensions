package com.servicenow.processmining.extensions.pm.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilter;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

public class ProcessMiningVariantRetrievalXanadu
    extends ProcessMiningModelRetrieval
{
    private String entityId = null;
    private int numberOfVariants = 0;

    public ProcessMiningVariantRetrievalXanadu(final ServiceNowInstance instance, final String modelVersionId, final String entityId, final int numberOfVariants)
    {
        super(instance, modelVersionId);
        this.entityId = entityId;
        this.numberOfVariants = numberOfVariants;
    }

    public String getEntityId()
    {
        return this.entityId;
    }

    public int getNumberOfVariants()
    {
        return this.numberOfVariants;
    }

    protected String getEmptyFilterPayload()
    {
        logger.debug("Enter ProcessMiningVariantRetrievalXanadu.getEmptyFilterPayload()");

        String variables = "{\n" + 
                "\t\"versionId\": \"" + getProcessModelVersionId() + "\",\n" + 
                "\t\"entityId\": \"" + getEntityId() + "\",\n" + 
                "\t\"filterSets\": {\n" + 
                "\t\t\"dataFilter\": [],\n" + 
                "\t\t\"breakdowns\": [],\n" + 
                "\t\t\"findingFilter\": \"\",\n" + 
                "\t\t\"adIntentFilter\": \"\",\n" + 
                "\t\t\"orderedFilters\": []\n" + 
                "\t},\n" + 
                "\t\"limit\": " + getNumberOfVariants() + ",\n" + 
                "\t\"orderBy\": \"node_count\",\n" + 
                "\t\"orderByDesc\": false,\n" + 
                "\t\"query\": \"\"\n" + 
                "}";

        String payload = "{\n";
        payload += "\t\"query\": \"" + query + "\",\n";
        payload += "\t\"operationName\": \"snProminWorkbenchConnected\",\n";
        payload += "\t\"variables\": " + variables + "\n";
        payload += "}";

        logger.debug("payload: (" + payload + ")");
        logger.debug("Exit ProcessMiningVariantRetrievalXanadu.getEmptyFilterPayload()");
        return payload;
    }

    protected String getFilterPayload(final String entityId, final ProcessMiningModelFilter filter)
    {
        logger.debug("Enter ProcessMiningVariantRetrievalXanadu.getFilterPayload()");
        String variables = "{\n" + 
                "\t\"versionId\": \"" + getProcessModelVersionId() + "\",\n" + 
                "\t\"entityId\": \"" + getEntityId() + "\",\n" + 
                "\t\"filterSets\": {\n" + 
                "\t\t\"dataFilter\": [],\n" + 
                "\t\t\"breakdowns\": [],\n" + 
                "\t\t\"findingFilter\": \"\",\n" + 
                "\t\t\"adIntentFilter\": \"\",\n" + 
                "\t\t\"orderedFilters\": []\n" + 
                "\t},\n" + 
                "\t\"limit\": " + getNumberOfVariants() + ",\n" + 
                "\t\"orderBy\": \"node_count\",\n" + 
                "\t\"orderByDesc\": false,\n" + 
                "\t\"query\": \"\"\n" + 
                "}";

        String payload = "{\n";
        payload += "\t\"query\": \"" + query + "\",\n";
        payload += "\t\"operationName\": \"snProminWorkbenchConnected\",\n";
        payload += "\t\"variables\": " + variables + "\n";
        payload += "}";

        logger.debug("payload: (" + payload + ")");
        logger.debug("Exit ProcessMiningVariantRetrievalXanadu.getFilterPayload()");
        return payload;
    }

    private final static String query = "query snVariants($versionId:ID!$filterSets:GlidePromin_FilterInput$limit:Int$orderBy:GlidePromin_VariantSortField$orderByDesc:Boolean$query:String$entityId:ID){GlidePromin_Query{scheduleModel(versionId:$versionId filterSets:$filterSets){...on GlidePromin_Model{variantResult(entityId:$entityId offset:0 limit:$limit orderBy:$orderBy orderByDesc:$orderByDesc query:$query){totalVariants entityId variants{...FRAGMENT_VARIANTS}}}}}}fragment FRAGMENT_NODES on GlidePromin_Node{nodeStatsId:id key label activityId entityId isStart isEnd absoluteFreq caseFreq maxReps fieldLabel field value}fragment FRAGMENT_EDGES on GlidePromin_Edge{id from to absoluteFreq caseFreq maxReps totalDuration maxDuration minDuration avgDuration trimmedAverage stdDeviation medianDuration}fragment FRAGMENT_AGGREGATE on GlidePromin_Aggregate{entityId model{...FRAGMENT_AGGREGATE_MODEL}node{...FRAGMENT_AGGREGATE_NODE}edge{...FRAGMENT_AGGREGATE_EDGE}variant{...FRAGMENT_AGGREGATE_VARIANT}}fragment FRAGMENT_AGGREGATE_MODEL on GlidePromin_RootAggregate{caseCount variantCount minCaseDuration maxCaseDuration avgCaseDuration trimmedAverage stdDeviation medianDuration uniqueParentRecords}fragment FRAGMENT_AGGREGATE_NODE on GlidePromin_NodeAggregate{absoluteFreq{min max}caseFreq{min max}maxReps{min max}}fragment FRAGMENT_AGGREGATE_EDGE on GlidePromin_EdgeAggregate{absoluteFreq{min max}caseFreq{min max}maxReps{min max}minDuration{min max}maxDuration{min max}avgDuration{min max}totalDuration{min max}trimmedAverage{min max}stdDeviation{min max}medianDuration{min max}}fragment FRAGMENT_AGGREGATE_VARIANT on GlidePromin_VariantAggregate{nodeCount{min max}caseFreq{min max}avgDuration{min max}healthScore{min max}trimmedAverage{min max}stdDeviation{min max}medianDuration{min max}}fragment FRAGMENT_VARIANTS on GlidePromin_Variant{id statsId entityId nodes caseIds nodeCount frequency totalDuration maxDuration minDuration avgDuration trimmedAverage stdDeviation medianDuration healthScore model{nodes{...FRAGMENT_NODES}edges{...FRAGMENT_EDGES}aggregates{...FRAGMENT_AGGREGATE}breakdowns{...FRAGMENT_BREAKDOWNS}projectEntities{...FRAGMENT_PROJECT_ENTITIES}}}fragment FRAGMENT_BREAKDOWNS on GlidePromin_EntityBreakdownStats{entityId breakdownStats{field label displayName line{label value caseCount variantCount avgDuration trimmedAverage stdDeviation medianDuration}}}fragment FRAGMENT_PROJECT_ENTITIES on GlidePromin_ProjectEntity{entityId parentId name sourceType{value}table{name label}filter{encodedQuery:value}activities{id field label displayName isGrouped}activitiesOfInterest{id field label displayName isGrouped}}";

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningVariantRetrievalWashington.class);
}