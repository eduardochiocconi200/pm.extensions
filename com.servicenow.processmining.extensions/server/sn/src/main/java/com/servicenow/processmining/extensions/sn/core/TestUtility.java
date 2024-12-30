package com.servicenow.processmining.extensions.sn.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtility
{
    public String loadProcessMiningModel(final String filePath)
    {
        return loadFileAsResource(filePath);
    }

    public String loadProcessMiningAuditLogs(final String filePath)
    {
        return loadFileAsResource(filePath);
    }

    private String loadFileAsResource(final String filePath)
    {
        StringBuilder contents = new StringBuilder();

        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader input = new BufferedReader(new InputStreamReader(is, "UTF8"));
            try {
                String line = null; //not declared within while loop
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return contents.toString();
    }
}
