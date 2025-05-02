package com.servicenow.processmining.extensions.pm.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelFilter;
import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;

public class ProcessMiningVariantRetrievalYokohama
    extends ProcessMiningModelRetrieval
{
    private String entityId = null;
    private int numberOfVariants = 0;

    public ProcessMiningVariantRetrievalYokohama(final ServiceNowInstance instance, final String modelVersionId, final String entityId, final int numberOfVariants)
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
        logger.debug("Enter ProcessMiningVariantRetrievalYokohama.getEmptyFilterPayload()");

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
                "\t\"variantsLimit\": " + (getNumberOfVariants() == -1 ? MAX_VARIANT_THRESHOLD : getNumberOfVariants()) + ",\n" + 
                "\t\"variantsOrderBy\": \"node_count\",\n" + 
                "\t\"variantsOrderByDesc\": true,\n" + 
                "\t\"variantsQuery\": \"\"\n" + 
                "}";

        String payload = "{\n";
        payload += "\t\"query\": \"" + query + "\",\n";
        payload += "\t\"operationName\": \"snProminWorkbenchConnected\",\n";
        payload += "\t\"variables\": " + variables + "\n";
        payload += "}";

        logger.debug("payload: (" + payload + ")");
        logger.debug("Exit ProcessMiningVariantRetrievalYokohama.getEmptyFilterPayload()");
        return payload;
    }

    protected String getFilterPayload(final String entityId, final ProcessMiningModelFilter filter)
    {
        logger.debug("Enter ProcessMiningVariantRetrievalYokohama.getFilterPayload()");
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
                "\t\"variantsLimit\": " + (getNumberOfVariants() == -1 ? MAX_VARIANT_THRESHOLD : getNumberOfVariants()) + ",\n" + 
                "\t\"variantsOrderBy\": \"node_count\",\n" + 
                "\t\"variantsOrderByDesc\": true,\n" + 
                "\t\"variantsQuery\": \"\"\n" + 
                "}";

        String payload = "{\n";
        payload += "\t\"query\": \"" + query + "\",\n";
        payload += "\t\"operationName\": \"snProminWorkbenchConnected\",\n";
        payload += "\t\"variables\": " + variables + "\n";
        payload += "}";

        logger.debug("payload: (" + payload + ")");
        logger.debug("Exit ProcessMiningVariantRetrievalYokohama.getFilterPayload()");
        return payload;
    }

    private final static String query = "query snVariants($versionId:ID!$filterSets:GlidePromin_FilterInput$variantsLimit:Int$variantsOrderBy:GlidePromin_VariantSortField$variantsOrderByDesc:Boolean$variantsQuery:String$entityId:ID){GlidePromin_Query{variantResult(versionId:$versionId filterSets:$filterSets entityId:$entityId offset:0 limit:$variantsLimit orderBy:$variantsOrderBy orderByDesc:$variantsOrderByDesc query:$variantsQuery){totalVariants entityId variants{...FRAGMENT_VARIANTS}}}}fragment FRAGMENT_VARIANTS on GlidePromin_Variant{id statsId entityId nodes caseIds nodeCount frequency totalDuration maxDuration minDuration avgDuration trimmedAverage stdDeviation medianDuration healthScore}";
    private final static int MAX_VARIANT_THRESHOLD = 1000;
    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningVariantRetrievalWashington.class);
}