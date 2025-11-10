package com.servicenow.processmining.extensions.tm;

import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.AlgoPrefixSpan;

import java.io.IOException;

public class PrefixSpanExample
{
    public static void main(String[] args)
    {
        PrefixSpanExample pse = new PrefixSpanExample();
        // pse.loadDataFromSNInstance();
        pse.runAppName();
        pse.runWindowName();
    }

    public PrefixSpanExample()
    {
    }

    public void loadDataFromSNInstance()
    {
        ServiceNowTaskMiningDataSetRetriever retriever = new ServiceNowTaskMiningDataSetRetriever();
        retriever.retrieve();
    }

    public void runAppName()
    {
        String filePrefix = "prefixSpan-AppNames";
        LoadTaskMiningDataSetFromExcel dataSet = new LoadTaskMiningDataSetFromExcel(taskAuditLogFile);
        if (!dataSet.loadAppName(2, 10.0)) {
            System.out.println("Cannot run algorithm since there are sequences with duplicated sequential items!");
            System.exit(0);
        }
        dataSet.saveWindowNameCodes("/Users/eduardo.chiocconi/Development/ServiceNow/Public/pm.extensions/com.servicenow.processmining.extensions/server/tm/src/test/resources/" + filePrefix + "-Codes.txt");
        String inputFilePath = "/Users/eduardo.chiocconi/Development/ServiceNow/Public/pm.extensions/com.servicenow.processmining.extensions/server/tm/src/test/resources/" + filePrefix + "-Input.txt"; 
        dataSet.saveSequences(inputFilePath);
        // The output file path (where frequent patterns will be saved)
        String outputFilePath = "/Users/eduardo.chiocconi/Development/ServiceNow/Public/pm.extensions/com.servicenow.processmining.extensions/server/tm/src/test/resources/" + filePrefix + "-Output.txt";

        run(inputFilePath, outputFilePath);
    }

    public void runWindowName()
    {
        String filePrefix = "prefixSpan-WindowNames";
        LoadTaskMiningDataSetFromExcel dataSet = new LoadTaskMiningDataSetFromExcel(taskAuditLogFile);
        if (!dataSet.loadWindowName(2, 10.0)) {
            System.out.println("Cannot run algorithm since there are sequences with duplicated sequential items!");
            System.exit(0);
        }
        dataSet.saveWindowNameCodes("/Users/eduardo.chiocconi/Development/ServiceNow/Public/pm.extensions/com.servicenow.processmining.extensions/server/tm/src/test/resources/" + filePrefix + "-Codes.txt");
        String inputFilePath = "/Users/eduardo.chiocconi/Development/ServiceNow/Public/pm.extensions/com.servicenow.processmining.extensions/server/tm/src/test/resources/" + filePrefix + "-Input.txt"; 
        dataSet.saveSequences(inputFilePath);
        // The output file path (where frequent patterns will be saved)
        String outputFilePath = "/Users/eduardo.chiocconi/Development/ServiceNow/Public/pm.extensions/com.servicenow.processmining.extensions/server/tm/src/test/resources/" + filePrefix + "-Output.txt";
        run(inputFilePath, outputFilePath);
    }

    private void run(final String inputFilePath, final String outputFilePath)
    {
        long startTime = System.currentTimeMillis();
        // We use a minimum support of X sequences.
        int minsup = 20;

		// Create an instance of the algorithm with minsup = 50 %
		AlgoPrefixSpan algo = new AlgoPrefixSpan();
		// execute the algorithm
		try {
            algo.runAlgorithm(inputFilePath, outputFilePath, minsup);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Ran Algorithm in (" + (((endTime - startTime)/1000)/60) + ") miunutes.");
		algo.printStatistics();
    }

    private final static String taskAuditLogFile = "/Users/eduardo.chiocconi/Downloads/sn_tm_core_user_data_ec.csv";
}