package com.servicenow.processmining.extensions.pm.report.data;

import java.util.ArrayList;

public abstract class DataSourceFinding
{
    private ArrayList<DataSourceFindingContent> content = null;

    public DataSourceFinding()
    {
    }

    public boolean addContent(final DataSourceFindingContent content)
    {
        return getContent().add(content);
    }

    public ArrayList<DataSourceFindingContent> getContent()
    {
        if (this.content == null) {
            this.content = new ArrayList<DataSourceFindingContent>();
        }

        return this.content;
    }
}
