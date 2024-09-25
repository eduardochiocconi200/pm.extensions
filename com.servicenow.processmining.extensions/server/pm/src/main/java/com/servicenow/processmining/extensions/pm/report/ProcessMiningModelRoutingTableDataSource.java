package com.servicenow.processmining.extensions.pm.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelNode;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelTransition;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;

public class ProcessMiningModelRoutingTableDataSource
{
    private Object[][] routingTable = null;
    private HashMap<String, ArrayList<String>> routingTimesTracker = null;
    private HashMap<String, Integer> nodesOrder = null;
    private int routingTableNumberOfColumns = 0;
    private int routingTableNumberOfRows = 0;
    private int routingTableCurrentRow = 0;
    private int baseAsIsTableRow = 0;
    private int baseToBeTableRow = 0;

    private ArrayList<Object[][]> variants = null;
    private int routesTableNumberOfColumns = 0;
    private int routesTableNumberOfRows = 0;
    private int routesCurrentRow = 0;

    private ProcessMiningModel model = null;

    public ProcessMiningModelRoutingTableDataSource(final ProcessMiningModel model)
    {
        this.model = model;
    }

    public ProcessMiningModel getProcessMiningModel()
    {
        return this.model;
    }

    public Collection<ProcessMiningModelNode> getNodes()
    {
        return getProcessMiningModel().getNodes().values();
    }

    public Collection<ProcessMiningModelTransition> getTransitions()
    {
        return getProcessMiningModel().getTransitions().values();
    }

    public Collection<ProcessMiningModelVariant> getRoutes()
    {
        return getProcessMiningModel().getVariants().values();
    }

    public Object[][] getRoutingTableData()
    {
        if (routingTable == null) {
            createAsIsRoutingTable();
            createToBeRoutingTable();
            createDeltaImprovementsRoutingTable();
            createProductivitySavingsRoutingTable();
        }

        return routingTable;
    }

    private void createAsIsRoutingTable()
    {
        routingTableNumberOfRows = ((getNodes().size() + 4) * 4) + 6;    // + Routing Table Name +
                                                                         // Activity/Activity
                                                                         // label column
                                                                         // Totals x 6
        routingTableNumberOfColumns = (getNodes().size() * 2) + 1;       // + Activity/Activity
                                                                         // label column
        routingTable = new Object[routingTableNumberOfRows][routingTableNumberOfColumns];

        // Routing Table Identifier and name
        routingTableCurrentRow = 0;
        baseAsIsTableRow = 1;
        String modelSummary = "Process Model: Name: " + getProcessMiningModel().getName() + ", Table: " + getProcessMiningModel().getTableLabel() + ", Records: " + getProcessMiningModel().getAggregate().getCaseCount() + ", Variants: " + getProcessMiningModel().getVariants().size() + ".";
        routingTable[routingTableCurrentRow][0] = modelSummary;
        routingTableCurrentRow++;
        routingTable[routingTableCurrentRow][0] = "AS-IS Routing Matrix";

        // Routing Table Header
        routingTableCurrentRow++;
        routingTable[routingTableCurrentRow][0] = "HIGHLIGHT=GREEN=" + "Activity / Activity";
        routingTable[routingTableCurrentRow + 1][0] = "HIGHLIGHT=GREEN=" + "Metric";
        int currentColumn = 1;
        for (ProcessMiningModelNode node : getNodes()) {
            routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=GREEN=" + node.getName();
            routingTable[routingTableCurrentRow + 1][currentColumn++] = "HIGHLIGHT=GREEN=Avg (mins)";
            routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=GREEN=" + node.getName();
            routingTable[routingTableCurrentRow + 1][currentColumn++] = "HIGHLIGHT=GREEN=Executed Times";
        }

        // Routing Table content information
        routingTableCurrentRow++;
        routingTableCurrentRow++;

        // Now, include all transition times ...
        int topRow = routingTableCurrentRow;
        for (ProcessMiningModelNode node : getNodes()) {
            currentColumn = 0;
            routingTable[routingTableCurrentRow][currentColumn++] = "HIGHLIGHT=GREEN=" + node.getName();
            for (ProcessMiningModelNode node2 : getNodes()) {
                String fromNodeId = node.getId();
                String toNodeId = node2.getId();
                ProcessMiningModelTransition transition = getProcessMiningModel().getTransition(fromNodeId, toNodeId);
                // If the transition exists ...
                if (transition != null) {
                    routingTable[routingTableCurrentRow][currentColumn] = adjustTime(Long.valueOf(transition.getAvgDuration()).intValue());
                    routingTable[routingTableCurrentRow][currentColumn + 1] = Long.valueOf(transition.getAbsoluteFrequency()).intValue();
                }
                else {
                    routingTable[routingTableCurrentRow][currentColumn] = 0;
                    routingTable[routingTableCurrentRow][currentColumn + 1] = 0;
                }
                currentColumn++;
                currentColumn++;
            }
            routingTableCurrentRow++;
        }

        routingTable[routingTableCurrentRow][0] = "HIGHLIGHT=GREEN=Totals";
        int totalColumn = 2;
        for (int i=0; i < getNodes().size(); i++) {
            routingTable[routingTableCurrentRow][totalColumn] = "FORMULA=SUM(" + getExcelColumnLetter(totalColumn+1) + (topRow + 1) + ":" + getExcelColumnLetter(totalColumn+1) + (topRow + getNodes().size()) + ")";
            totalColumn += 2;
        }

        // Leave a row space.
        routingTableCurrentRow++;
        routingTableCurrentRow++;
    }

    private void createToBeRoutingTable()
    {
        baseToBeTableRow = routingTableCurrentRow;
        // Routing Table Identifier and name
        routingTable[routingTableCurrentRow][0] = "TO-BE Routing Matrix (Adjust the values in each cell to represent the new adjusted improvement time to route the work)";

        // Routing Table Header
        routingTableCurrentRow++;
        routingTable[routingTableCurrentRow][0] = "HIGHLIGHT=GREEN=" + "Activity / Activity";
        routingTable[routingTableCurrentRow + 1][0] = "HIGHLIGHT=GREEN=" + "Metric";
        int currentColumn = 1;
        for (ProcessMiningModelNode node : getNodes()) {
            routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=GREEN=" + node.getName();
            routingTable[routingTableCurrentRow + 1][currentColumn++] = "HIGHLIGHT=GREEN=Avg (mins)";
            routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=GREEN=" + node.getName();
            routingTable[routingTableCurrentRow + 1][currentColumn++] = "HIGHLIGHT=GREEN=Executed Times";
        }

        // Routing Table content information (initially, it should hold the same data as
        // the AS-IS). End users can modify.
        routingTableCurrentRow++;
        routingTableCurrentRow++;

        // Now, include all transition times ...
        int topRow = routingTableCurrentRow;        
        for (ProcessMiningModelNode node : getNodes()) {
            currentColumn = 0;
            routingTable[routingTableCurrentRow][currentColumn++] = "HIGHLIGHT=GREEN=" + node.getName();
            for (ProcessMiningModelNode node2 : getNodes()) {
                String fromNodeId = node.getId();
                String toNodeId = node2.getId();
                ProcessMiningModelTransition transition = getProcessMiningModel().getTransition(fromNodeId, toNodeId);
                // If the transition exists ...
                if (transition != null) {
                    int transitionExecutionTimes = Long.valueOf(transition.getAbsoluteFrequency()).intValue();
                    if (transitionExecutionTimes > 0) {
                        routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=YELLOW=" + adjustTime(Long.valueOf(transition.getAvgDuration()).intValue());
                        routingTable[routingTableCurrentRow][currentColumn + 1] = transitionExecutionTimes;
                    }
                    else {
                        routingTable[routingTableCurrentRow][currentColumn] = adjustTime(Long.valueOf(transition.getAvgDuration()).intValue());
                        routingTable[routingTableCurrentRow][currentColumn + 1] = transitionExecutionTimes;
                    }
                }
                else {
                    routingTable[routingTableCurrentRow][currentColumn] = Long.valueOf(0).intValue();
                    routingTable[routingTableCurrentRow][currentColumn + 1] = Long.valueOf(0).intValue();
                }
                currentColumn++;
                currentColumn++;
            }
            routingTableCurrentRow++;
        }

        routingTable[routingTableCurrentRow][0] = "HIGHLIGHT=GREEN=Totals";
        int totalColumn = 2;
        for (int i=0; i < getNodes().size(); i++) {
            routingTable[routingTableCurrentRow][totalColumn] = "FORMULA=SUM(" + getExcelColumnLetter(totalColumn+1) + (topRow + 1) + ":" + getExcelColumnLetter(totalColumn+1) + (topRow + getNodes().size()) + ")";
            totalColumn += 2;
        }

        // Leave a row space.
        routingTableCurrentRow++;
        routingTableCurrentRow++;
    }

    private void createDeltaImprovementsRoutingTable()
    {
        // Routing Table Identifier and name
        routingTable[routingTableCurrentRow][0] = "Delta Improvements Routing Matrix";

        // Routing Table Header
        routingTableCurrentRow++;
        routingTable[routingTableCurrentRow][0] = "HIGHLIGHT=GREEN=Activity / Activity";
        routingTable[routingTableCurrentRow + 1][0] = "HIGHLIGHT=GREEN=Metric";
        int currentColumn = 1;
        for (ProcessMiningModelNode node : getNodes()) {
            routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=GREEN=" + node.getName();
            routingTable[routingTableCurrentRow + 1][currentColumn++] = "HIGHLIGHT=GREEN=Avg Delta (Absolute)";
            routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=GREEN=" + node.getName();
            routingTable[routingTableCurrentRow + 1][currentColumn++] = "HIGHLIGHT=GREEN=Executions Delta (Absolute)";
        }

        // Routing Table content information
        routingTableCurrentRow++;
        routingTableCurrentRow++;

        // Now, include all transition times ...
        int adjustmentToTableTop = 4;
        for (ProcessMiningModelNode node : getNodes()) {
            currentColumn = 0;
            routingTable[routingTableCurrentRow][currentColumn++] = "HIGHLIGHT=GREEN=" + node.getName();
            for (@SuppressWarnings("unused") ProcessMiningModelNode node2 : getNodes()) {
                String avgAsIsCell = getExcelColumnLetter(currentColumn + 1) + (baseAsIsTableRow + adjustmentToTableTop);
                String avgToBeCell = getExcelColumnLetter(currentColumn + 1) + (baseToBeTableRow + adjustmentToTableTop);
                String execTimesAsIsCell = getExcelColumnLetter(currentColumn + 2) + (baseAsIsTableRow + adjustmentToTableTop);
                String execTimesToBeCell = getExcelColumnLetter(currentColumn + 2) + (baseToBeTableRow + adjustmentToTableTop);
                // String deltaCell = getExcelColumnLetter(currentColumn + 1) + String.valueOf(routingTableCurrentRow + 1);

                routingTable[routingTableCurrentRow][currentColumn] = "FORMULA=" + avgAsIsCell + "-" + avgToBeCell;
                routingTable[routingTableCurrentRow][currentColumn + 1] = "FORMULA=" + execTimesAsIsCell + "-" + execTimesToBeCell;
                // routingTable[routingTableCurrentRow][currentColumn + 1] = "FORMULA=IF(" + deltaCell + "=0, 0, ((" + avgAsIsCell + "-" + avgToBeCell + ")/" + avgToBeCell + ")*100*-1)";
                currentColumn++;
                currentColumn++;
            }
            routingTableCurrentRow++;
            adjustmentToTableTop++;
        }

        // Leave a row space.
        routingTableCurrentRow++;
    }

    private void createProductivitySavingsRoutingTable()
    {
        // Routing Table Identifier and name
        routingTable[routingTableCurrentRow][0] = "Productivity Savings Routing Matrix";

        // Routing Table Header
        routingTableCurrentRow++;
        routingTable[routingTableCurrentRow][0] = "HIGHLIGHT=GREEN=Activity / Activity";
        routingTable[routingTableCurrentRow + 1][0] = "HIGHLIGHT=GREEN=Metric";
        int currentColumn = 1;
        for (ProcessMiningModelNode node : getNodes()) {
            routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=GREEN=" + node.getName();
            routingTable[routingTableCurrentRow + 1][currentColumn++] = "HIGHLIGHT=GREEN=Single Productivity (mins)";
            routingTable[routingTableCurrentRow][currentColumn] = "HIGHLIGHT=GREEN=" + node.getName();
            routingTable[routingTableCurrentRow + 1][currentColumn++] = "HIGHLIGHT=GREEN=Total Productivity (mins)";
        }

        // Routing Table content information
        routingTableCurrentRow++;
        routingTableCurrentRow++;

        // Now, include all transition times ...
        int adjustmentToTableTop = 4;
        int topRow = routingTableCurrentRow;
        for (ProcessMiningModelNode node : getNodes()) {
            currentColumn = 0;
            routingTable[routingTableCurrentRow][currentColumn++] = "HIGHLIGHT=GREEN=" + node.getName();
            for (@SuppressWarnings("unused") ProcessMiningModelNode node2 : getNodes()) {
                String avgToBeCell = getExcelColumnLetter(currentColumn + 1) + (baseToBeTableRow + adjustmentToTableTop);
                String avgAsIsCell = getExcelColumnLetter(currentColumn + 1) + (baseAsIsTableRow + adjustmentToTableTop);
                String asIsActivityCountCell = getExcelColumnLetter(currentColumn + 2) + (baseAsIsTableRow + adjustmentToTableTop);
                String toBeActivityCountCell = getExcelColumnLetter(currentColumn + 2) + (baseToBeTableRow + adjustmentToTableTop);

                routingTable[routingTableCurrentRow][currentColumn] = "FORMULA=" + avgAsIsCell + "-" + avgToBeCell;
                routingTable[routingTableCurrentRow][currentColumn + 1] = "FORMULA=((" + avgAsIsCell + "-" + avgToBeCell + ")*" + toBeActivityCountCell + ")+(" + avgAsIsCell +"*(" + asIsActivityCountCell + "-" + toBeActivityCountCell + "))";
                currentColumn++;
                currentColumn++;
            }
            routingTableCurrentRow++;
            adjustmentToTableTop++;
        }

        // Add totals...
        routingTable[routingTableCurrentRow][0] = "HIGHLIGHT=GREEN=Activity Totals (mins)";
        routingTable[routingTableCurrentRow+1][0] = "HIGHLIGHT=GREEN=Activity Totals (hours)";

        int totalColumn = 2;
        for (int i=0; i < getNodes().size(); i++) {
            routingTable[routingTableCurrentRow][totalColumn] = "FORMULA=SUM(" + getExcelColumnLetter(totalColumn+1) + (topRow + 1) + ":" + getExcelColumnLetter(totalColumn+1) + (topRow + getNodes().size()) + ")";
            routingTable[routingTableCurrentRow+1][totalColumn] = "FORMULA=ROUNDUP((SUM(" + getExcelColumnLetter(totalColumn+1) + (topRow + 1) + ":" + getExcelColumnLetter(totalColumn+1) + (topRow + getNodes().size()) + ")/60), 0)";
            totalColumn += 2;
        }

        routingTable[routingTableCurrentRow+2][0] = "HIGHLIGHT=GREEN=Grand Totals (mins)";
        routingTable[routingTableCurrentRow+3][0] = "HIGHLIGHT=GREEN=Grand Totals (hours)";

        routingTable[routingTableCurrentRow+2][1] = "FORMULA=SUM(" + getExcelColumnLetter(2) + (routingTableCurrentRow + 1) + ":" + getExcelColumnLetter(totalColumn-1) + (routingTableCurrentRow + 1) + ")";
        routingTable[routingTableCurrentRow+3][1] = "FORMULA=SUM(" + getExcelColumnLetter(2) + (routingTableCurrentRow + 2) + ":" + getExcelColumnLetter(totalColumn-1) + (routingTableCurrentRow + 2) + ")";

        // Leave a row space.
        routingTableCurrentRow++;
    }

    public ArrayList<Object[][]> getRoutesData()
    {
        if (variants == null) {
            createVariantsRoutes();
        }

        return variants;
    }

    private void createVariantsRoutes()
    {
        int numberOfVariantsToPrint = 100;
        variants = new ArrayList<Object[][]>();
        routingTimesTracker = new HashMap<String, ArrayList<String>>();

        int nRoute = 1;
        for (ProcessMiningModelVariant variant : getRoutes()) {
            if (variant.getFrequency() != 0) {
                variants.add(getVariant(variant, nRoute++));
            }
            if (numberOfVariantsToPrint-- <= 0) {
                break;
            }
        }

        // Update the "Routing Table"'s TO-BE Executed Times based on variants execution times.
        for (String transition : routingTimesTracker.keySet()) {
            String fromNode = transition.substring(0, transition.indexOf(" -> "));
            String toNode = transition.substring(transition.indexOf(" -> ") + " -> ".length());
            int row = getToBeTableExecutionTimesRow(fromNode);
            int column = getToBeTableExecutionTimesColumn(toNode);
            if (row == -1 || column == -1) {
                throw new RuntimeException("This should not have happened");
            }
            String totalExecutions = "FORMULA=";
            boolean processedFirstCell = false;
            for (String cell : routingTimesTracker.get(transition)) {
                if (processedFirstCell) {
                    totalExecutions += "+";
                }
                totalExecutions += cell;
                processedFirstCell = true;
            }
            logger.debug("Setting 'Routing Table'[" + row + "][" + column + "] = (" + totalExecutions + ")");
            routingTable[row][column] = totalExecutions;
        }
    }

    private int getToBeTableExecutionTimesRow(String fromNode)
    {
        int base = baseToBeTableRow + 3;
        for (int i=0; i < getNodes().size(); i++) {
            String cellValue = String.valueOf(routingTable[base+i][0]);
            if (cellValue != null) {
                cellValue = cellValue.substring(cellValue.lastIndexOf("=")+1);
                if (cellValue.equals(fromNode)) {
                    return base+i;
                }
            }
        }

        return -1;
    }

    private int getToBeTableExecutionTimesColumn(String toNode)
    {
        int base = baseToBeTableRow + 1;
        for (int i=1; i < getNodes().size()*2; i++) {
            String cellValue = String.valueOf(routingTable[base][i]);
            if (cellValue != null) {
                cellValue = cellValue.substring(cellValue.lastIndexOf("=")+1);
                if (cellValue.equals(toNode)) {
                    return i+1;
                }
            }
        }

        return -1;
    }

    private Object[][] getVariant(final ProcessMiningModelVariant variant, final int nRoute)
    {
        routesTableNumberOfRows = 1000;
        routesTableNumberOfColumns = 25;
        Object[][] routesTable = new Object[routesTableNumberOfRows][routesTableNumberOfColumns];

        // Routing Table Identifier and name
        routesCurrentRow = 0;
        int baseOfRoutingTable = 0;
        // Row: 0 - Variant Name
        routesTable[routesCurrentRow][0] = "HIGHLIGHT=GREEN=" + "Variant Name:";
        routesTable[routesCurrentRow++][1] = "Route " + nRoute;
        // Row: 1 - Total Cases
        routesTable[routesCurrentRow][0] = "HIGHLIGHT=GREEN=" + "Total Cases:";
        Integer variantRecords = Integer.valueOf(variant.getFrequency());
        Integer totalRecords = Integer.valueOf(getProcessMiningModel().getAggregate().getCaseCount());
        Float percentageOfRecords = Float.valueOf(variantRecords.floatValue() * 100 / totalRecords.floatValue());
        routesTable[routesCurrentRow][1] = variantRecords;
        routesTable[routesCurrentRow++][2] = "(" + String.format("%.2f", percentageOfRecords) + " % of total records)";

        // Row: 2 - AS-IS Total Time
        routesTable[routesCurrentRow][0] = "HIGHLIGHT=GREEN=" + "AS-IS Average (mins):";
        routesTable[routesCurrentRow++][1] = Integer.valueOf(variant.getAvgDuration());
        // Row: 3 - TO-BE Total Time
        routesTable[routesCurrentRow][0] = "HIGHLIGHT=GREEN=" + "TO-BE Average (mins):";
        routesTable[routesCurrentRow++][1] = Integer.valueOf(variant.getTotalDuration());
        // Row: 4 - Route Improvements
        routesTable[routesCurrentRow][0] = "HIGHLIGHT=GREEN=" + "Route % Improvement:";
        routesTable[routesCurrentRow++][1] = "";
        routesCurrentRow++;

        // Row: 5 - Route Steps
        routesTable[routesCurrentRow][0] = "HIGHLIGHT=GREEN=" + "Route Steps:";
        routesTable[routesCurrentRow++][1] = variant.getNodeCount();
        // Row: 6 - Route Details + Header Columns for routing table details
        routesTable[routesCurrentRow][0] = "HIGHLIGHT=GREEN=" + "Route Details:";
        routesTable[routesCurrentRow][1] = "HIGHLIGHT=GREEN=" + "Routing Times (AS-IS)";
        routesTable[routesCurrentRow][2] = "HIGHLIGHT=GREEN=" + "Routing Times (TO-BE)";
        routesTable[routesCurrentRow][3] = "HIGHLIGHT=GREEN=" + "Min (mins)";
        routesTable[routesCurrentRow][4] = "HIGHLIGHT=GREEN=" + "Max (mins)";
        routesTable[routesCurrentRow][5] = "HIGHLIGHT=GREEN=" + "AS-IS Avg (mins)";
        routesTable[routesCurrentRow][6] = "HIGHLIGHT=GREEN=" + "TO-BE Avg (mins))";
        routesTable[routesCurrentRow][7] = "HIGHLIGHT=GREEN=" + "AS-IS Total (mins)";
        routesTable[routesCurrentRow][8] = "HIGHLIGHT=GREEN=" + "TO-BE Total (mins))";

        // Routing Table content information
        // Row: 7 - [From -> To] + Details
        routesCurrentRow++;
        baseOfRoutingTable = routesCurrentRow;
        String nodeList = variant.getPath().toString().replace("[", "").replace("]", "").replaceAll(" ", "");
        StringTokenizer nodesSt = new StringTokenizer(nodeList, ",");
        int totalNodes = nodesSt.countTokens();
        String fromNode = nodesSt.nextToken();
        String toNode = "";
        int currentColumn = 0;
        int startTransitionsIndex = routesCurrentRow;

        for (int i = 0; i < totalNodes - 1; i++) {
            currentColumn = 0;
            toNode = nodesSt.nextToken();
            ProcessMiningModelTransition transition = getProcessMiningModel().getTransition(fromNode, toNode);
            if (transition == null) {
                throw new RuntimeException("Could not find transition from: (" + fromNode + ") to: (" + toNode + ")");
            }
            logger.debug("Row: (" + routesCurrentRow + ", " + currentColumn + ") = (" + fromNode + " -> " + toNode + "). Case Count: (" + transition.getCaseFrequency() + ")");
            String fromLabel = getProcessMiningModel().getNodes().get(fromNode).getName();
            String toLabel = getProcessMiningModel().getNodes().get(toNode).getName();
            int rowExists = doesTransitionExist(routesTable, startTransitionsIndex, routesCurrentRow, fromLabel, toLabel);
            if (rowExists < 0) {
                routesTable[routesCurrentRow][currentColumn++] = fromLabel + " -> " + toLabel;
                routesTable[routesCurrentRow][currentColumn++] = Integer.valueOf(variant.getFrequency());
                routesTable[routesCurrentRow][currentColumn++] = "HIGHLIGHT=YELLOW=" + Integer.valueOf(variant.getFrequency());
                updateRoutingTimesTracker(nRoute, fromLabel, toLabel, routesCurrentRow+1);
                routesTable[routesCurrentRow][currentColumn++] = adjustTime(Integer.valueOf(transition.getMinDuration()).intValue());
                routesTable[routesCurrentRow][currentColumn++] = adjustTime(Integer.valueOf(transition.getMaxDuration()).intValue());
                routesTable[routesCurrentRow][currentColumn++] = adjustTime(Integer.valueOf(transition.getAvgDuration()).intValue());
                int fromNodeOrder = getNodePosition(fromNode);
                int toNodeOrder = getNodePosition(toNode);
                int transitionRow = baseToBeTableRow + 2 + fromNodeOrder;
                int transitionColumn = toNodeOrder;
                routesTable[routesCurrentRow][currentColumn++] = "FORMULA='Routing Table'!" + getExcelColumnLetter(transitionColumn * 2) + (transitionRow + 1);
                routesTable[routesCurrentRow][currentColumn++] = "FORMULA=" + getExcelColumnLetter(2) + (routesCurrentRow+1) + "*" + getExcelColumnLetter(6) + (routesCurrentRow+1);
                routesTable[routesCurrentRow][currentColumn++] = "FORMULA=" + getExcelColumnLetter(3) + (routesCurrentRow+1) + "*" + getExcelColumnLetter(7) + (routesCurrentRow+1);
                routesCurrentRow++;
            }
            else {
                routesTable[rowExists][1] = (Integer)(routesTable[rowExists][1]) + Integer.valueOf(variant.getFrequency());
                routesTable[rowExists][2] = "HIGHLIGHT=YELLOW=" + (Integer)(routesTable[rowExists][1]);
                updateRoutingTimesTracker(nRoute, fromLabel, toLabel, rowExists+1);
            }
            fromNode = toNode;
        }
        routesTable[routesCurrentRow][0] = "HIGHLIGHT=GREEN=" + "Totals";
        routesTable[routesCurrentRow][1] = "FORMULA=SUM(" + getExcelColumnLetter(2) + (baseOfRoutingTable + 1) + ":" + getExcelColumnLetter(2) + (routesCurrentRow) + ")";
        routesTable[routesCurrentRow][2] = "FORMULA=SUM(" + getExcelColumnLetter(3) + (baseOfRoutingTable + 1) + ":" + getExcelColumnLetter(3) + (routesCurrentRow) + ")";
        routesTable[routesCurrentRow][5] = "FORMULA=SUM(" + getExcelColumnLetter(6) + (baseOfRoutingTable + 1) + ":" + getExcelColumnLetter(6) + (routesCurrentRow) + ")";
        routesTable[routesCurrentRow][6] = "FORMULA=SUM(" + getExcelColumnLetter(7) + (baseOfRoutingTable + 1) + ":" + getExcelColumnLetter(7) + (routesCurrentRow) + ")";
        routesTable[routesCurrentRow][7] = "FORMULA=SUM(" + getExcelColumnLetter(8) + (baseOfRoutingTable + 1) + ":" + getExcelColumnLetter(8) + (routesCurrentRow) + ")";
        routesTable[routesCurrentRow][8] = "FORMULA=SUM(" + getExcelColumnLetter(9) + (baseOfRoutingTable + 1) + ":" + getExcelColumnLetter(9) + (routesCurrentRow) + ")";
        String asIsPathAvg = getExcelColumnLetter(8) + (routesCurrentRow + 1);
        String toBePathAvg = getExcelColumnLetter(9) + (routesCurrentRow + 1);
        routesTable[2][1] = "FORMULA=" + asIsPathAvg;
        routesTable[3][1] = "FORMULA=" + toBePathAvg;
        routesTable[4][1] = "FORMULA=IF(" + toBePathAvg + "=0, 0, ((" + toBePathAvg + "-" + asIsPathAvg + ")/" + asIsPathAvg + ")*100*-1)";

        return routesTable;
    }

    private void updateRoutingTimesTracker(final int nRoute, final String fromNode, final String toNode, final int currentRow)
    {
        String transitionId = fromNode + " -> " + toNode;
        if (routingTimesTracker.get(transitionId) == null) {
            routingTimesTracker.put(transitionId, new ArrayList<String>());
        }

        String cell = "'Route " + nRoute + "'!" + getExcelColumnLetter(3) + (currentRow);
        if (!routingTimesTracker.get(transitionId).contains(cell)) {
            routingTimesTracker.get(transitionId).add(cell);
        }
    }

    private int doesTransitionExist(final Object[][] routesTable, final int startRow, final int lastRow, final String fromLabel, final String toLabel)
    {
        for (int i=startRow; i < lastRow; i++) {
            if (routesTable[i][0] != null && routesTable[i][0].equals(fromLabel + " -> " + toLabel)) {
                return i;
            }
        }

        return -1;
    }

    private int getNodePosition(final String nodeId)
    {
        if (nodesOrder == null) {
            nodesOrder = new HashMap<String, Integer>();
            int currentRow = 1;
            for (ProcessMiningModelNode node : getProcessMiningModel().getNodes().values()) {
                nodesOrder.put(node.getId(), currentRow++);
            }
        }

        return nodesOrder.get(nodeId);
    }

    private String getExcelColumnLetter(int columnNumber)
    {
        // To store result (Excel column name)
        StringBuilder columnName = new StringBuilder();

        while (columnNumber > 0) {
            // Find remainder
            int rem = columnNumber % 26;

            // If remainder is 0, then a
            // 'Z' must be there in output
            if (rem == 0) {
                columnName.append("Z");
                columnNumber = (columnNumber / 26) - 1;
            }
            else { // If remainder is non-zero
                columnName.append((char) ((rem - 1) + 'A'));
                columnNumber = columnNumber / 26;
            }
        }

        // Reverse the string and print result
        return columnName.reverse().toString();
    }

    private int adjustTime(final int seconds)
    {
        return seconds / 60;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelRoutingTableDataSource.class);
}