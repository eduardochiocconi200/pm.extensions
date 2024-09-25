package com.servicenow.processmining.extensions.pm.report.data;

import java.util.ArrayList;

public class DataSourceFindingContent
{
    private String item = null;
    private ArrayList<String> subItems = null;

    public DataSourceFindingContent()
    {
    }

    public void setItem(final String item)
    {
        this.item = item;
    }

    public String getItem()
    {
        return item;
    }

    public boolean addSubItem(final String subItem)
    {
        return getSubItems().add(subItem);
    }

    public ArrayList<String> getSubItems()
    {
        if (this.subItems == null) {
            this.subItems = new ArrayList<String>();
        }

        return this.subItems;
    }
}
