package com.servicenow.processmining.extensions.pm.demo;

import java.util.ArrayList;

public class DemoModel
{
    private ArrayList<DemoModelPath> paths = null;

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
}