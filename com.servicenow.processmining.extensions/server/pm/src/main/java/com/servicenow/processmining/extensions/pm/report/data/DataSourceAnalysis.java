package com.servicenow.processmining.extensions.pm.report.data;

import java.util.ArrayList;

public abstract class DataSourceAnalysis
{
    private String description = null;
    private ArrayList<DataSourceFinding> findings = null;
    
    public DataSourceAnalysis()
    {
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return this.description;
    }

    public boolean addFindings(final ArrayList<DataSourceFinding> fs)
    {
        return getFindings().addAll(fs);
    }

    public boolean addFinding(final DataSourceFinding finding)
    {
        return getFindings().add(finding);
    }

    public ArrayList<DataSourceFinding> getFindings()
    {
        if (findings == null) {
            findings = new ArrayList<DataSourceFinding>();
        }

        return findings;
    }
}
