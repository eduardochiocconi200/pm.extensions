package com.servicenow.processmining.extensions.pm.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningVariantParser
    extends ProcessMiningModelParserGeneric
{
    public ProcessMiningVariantParser(final String versionId)
    {
        super(versionId);
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
            // Parse Variations ...
            parseVariations();
            logger.info("Successfully completed parsing Process Mining Variants Payload.");
            logger.debug(pmm.toString());
        }
        else {
            parseResult = false;
            logger.info("Successfully parsed response, but the variants model has errors: (" + errors + ")");
        }
                
        return parseResult;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelParserGeneric.class);
}
