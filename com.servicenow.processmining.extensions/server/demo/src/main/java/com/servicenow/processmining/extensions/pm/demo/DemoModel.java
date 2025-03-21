package com.servicenow.processmining.extensions.pm.demo;

import java.util.ArrayList;
import java.util.HashMap;

public class DemoModel
{
    private ArrayList<DemoModelPath> paths = null;
    private HashMap<String, ArrayList<String>> choiceValues = new HashMap<String, ArrayList<String>>();

    public DemoModel()
    {
    }

    public boolean addPath(final DemoModelPath path)
    {
        return getPaths().add(path);
    }

    public ArrayList<DemoModelPath> getPaths()
    {
        if (paths == null) {
            paths = new ArrayList<DemoModelPath>();
        }

        return paths;
    }

    public boolean addChoiceValue(final String table, final String attribute, final String choiceValue)
    {
        String key = table + "-" + attribute;
        if (choiceValues.get(key) == null) {
            choiceValues.put(key, new ArrayList<String>());
        }

        return choiceValues.get(key).add(choiceValue);
    }

    public ArrayList<String> getChoiceValues(final String table, final String attribute)
    {
        String key = table + "-" + attribute;
        if (choiceValues.get(key) == null) {
            choiceValues.put(key, new ArrayList<String>());
        }

        return choiceValues.get(key);
    }
}