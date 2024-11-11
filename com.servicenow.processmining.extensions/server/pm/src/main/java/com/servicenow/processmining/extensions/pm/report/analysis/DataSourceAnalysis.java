package com.servicenow.processmining.extensions.pm.report.analysis;

import java.util.ArrayList;

import com.servicenow.processmining.extensions.pm.report.data.DataSourceFinding;

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
