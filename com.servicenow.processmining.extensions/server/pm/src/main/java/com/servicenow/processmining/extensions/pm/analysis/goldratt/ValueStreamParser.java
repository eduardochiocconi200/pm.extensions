package com.servicenow.processmining.extensions.pm.analysis.goldratt;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ValueStreamParser
{
    private ValueStream valueStream = null;

    public ValueStreamParser()
    {
    }

    public ValueStream getValueStream()
    {
        if (this.valueStream == null) {
            this.valueStream = new ValueStream();
        }

        return this.valueStream;
    }

    public boolean parse(final String valueStreamJSON)
    {
        JSONObject root = new JSONObject(valueStreamJSON);
        this.valueStream = new ValueStream();
    
        return parsePhases(root);
    }

    private boolean parsePhases(final JSONObject phases)
    {
        if (phases.has("phases")) {
            JSONArray phasesJSON = phases.getJSONArray("phases");
            for (int i=0; i < phasesJSON.length(); i++) {
                JSONObject phaseJSON = phasesJSON.getJSONObject(i);
                if (!parsePhase(phaseJSON)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean parsePhase(final JSONObject phase)
    {
        String name = phase.getString("name");
        ArrayList<String> nodes = new ArrayList<String>();
        JSONArray nodesJSON = phase.getJSONArray("nodes");
        for (int i=0; i < nodesJSON.length(); i++) {
            JSONObject nodeJSON = nodesJSON.getJSONObject(i);
            String node = nodeJSON.getString("name");
            nodes.add(node);
        }

        ValueStreamPhase vsp = new ValueStreamPhase(name);
        vsp.getNodes().addAll(nodes);

        valueStream.getPhases().add(vsp);

        return true;
    }
}