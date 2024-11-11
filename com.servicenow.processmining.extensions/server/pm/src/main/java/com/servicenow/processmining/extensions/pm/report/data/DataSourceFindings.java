package com.servicenow.processmining.extensions.pm.report.data;

import java.util.ArrayList;

import com.servicenow.processmining.extensions.pm.report.analysis.DataSourceAnalysis;

public class DataSourceFindings
{
    private String filterName = null;
    private ArrayList<DataSourceAnalysis> analysis = null;
    
    public DataSourceFindings(final String filterName)
    {
        this.filterName = filterName;
    }

    public String getFilterName()
    {
        return this.filterName;
    }

    public boolean addAnalysis(final DataSourceAnalysis a)
    {
        return getAnalysis().add(a);
    }

    public ArrayList<DataSourceAnalysis> getAnalysis()
    {
        if (analysis == null) {
            analysis = new ArrayList<DataSourceAnalysis>();
        }

        return analysis;
    }
}
