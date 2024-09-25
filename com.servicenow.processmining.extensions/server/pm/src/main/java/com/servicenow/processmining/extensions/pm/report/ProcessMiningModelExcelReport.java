package com.servicenow.processmining.extensions.pm.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;

public class ProcessMiningModelExcelReport
{
    private ProcessMiningModel model = null;
    private String excelReportFolderName = "/tmp/ProcessMiningROIReport";
    private String excelReportFileName = null;
    private ProcessMiningModelRoutingTableDataSource routingTableDataSource = null;
    private XSSFWorkbook templateWorkbook = null;
    private XSSFSheet routingTableSheet = null;
    private ArrayList<XSSFSheet> routesSheet = null;
    private XSSFCellStyle inputCellStyle = null;
    private XSSFCellStyle headerCellStyle = null;
    private XSSFCellStyle blackCellStyle = null;

    public ProcessMiningModelExcelReport(final ProcessMiningModel model)
    {
        this.model = model;
    }

    public ProcessMiningModel getModel()
    {
        return this.model;
    }

    private String getExcelReportFileName()
    {
        if (excelReportFileName == null) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            this.excelReportFileName = this.excelReportFolderName + " - " + getModel().getName() + " - " + dateFormat.format(date) + ".xlsx";
        }

        return this.excelReportFileName;
    }

    public String getReportFileName()
    {
        return this.excelReportFileName;
    }

    private Object[][] getRoutingTableData()
    {
        if (routingTableDataSource == null) {
            routingTableDataSource = new ProcessMiningModelRoutingTableDataSource(getModel());
        }

        return routingTableDataSource.getRoutingTableData();
    }

    private ArrayList<Object[][]> getRoutesData()
    {
        if (routingTableDataSource == null) {
            routingTableDataSource = new ProcessMiningModelRoutingTableDataSource(getModel());
        }

        return this.routingTableDataSource.getRoutesData();
    }

    public boolean createReport()
    {
        prepareData();
        createWorkbook();
        addRoutingTableDataToTemplate();
        addRoutesDataToTemplate();
        saveReportFile();
        return true;
    }

    private void prepareData()
    {
        getRoutingTableData();
        getRoutesData();
    }

    private boolean createWorkbook()
    {
        templateWorkbook = new XSSFWorkbook();
        templateWorkbook.setForceFormulaRecalculation(true);
        routingTableSheet = templateWorkbook.createSheet("Routing Table");
        routesSheet = new ArrayList<XSSFSheet>();

        inputCellStyle = templateWorkbook.createCellStyle();
        inputCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        inputCellStyle.setFillPattern(FillPatternType.FINE_DOTS);

        headerCellStyle = templateWorkbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);

        blackCellStyle = templateWorkbook.createCellStyle();
        blackCellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        blackCellStyle.setFillPattern(FillPatternType.FINE_DOTS);

        return routingTableSheet != null;
    }

    private boolean saveReportFile()
    {
        try {
            FileOutputStream outputStream = new FileOutputStream(getExcelReportFileName());
            templateWorkbook.write(outputStream);
            templateWorkbook.close();
            outputStream.close();
        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }

        return routingTableSheet != null;
    }

    private void addRoutingTableDataToTemplate()
    {
        int rowCount = routingTableSheet.getLastRowNum();

        for (Object[] rowContent : getRoutingTableData()) {
            Row row = routingTableSheet.createRow(++rowCount);
            int columnCount = 0;
            for (Object field : rowContent) {
                routingTableSheet.setColumnWidth(columnCount, 15 * 256);
                Cell cell = row.createCell(columnCount++);
                if (field instanceof String) {
                    String fieldValue = (String) field;
                    if (fieldValue.startsWith("FORMULA=")) {
                        cell.setCellFormula(fieldValue.substring(8));
                    } else if (fieldValue.startsWith("HIGHLIGHT=")) {
                        // Highlight the values that can be changed...
                        String cellColor = fieldValue.substring(10, fieldValue.lastIndexOf("="));
                        changeCellBackgroundColor(cell, cellColor);
                        fieldValue = fieldValue.substring(fieldValue.lastIndexOf("=") + 1);
                        if (isInteger(fieldValue)) {
                            cell.setCellValue(Integer.valueOf(fieldValue));
                        } else {
                            cell.setCellValue(fieldValue);
                        }
                    } else {
                        cell.setCellValue((String) field);
                    }
                } else if (field instanceof Integer) {
                    Integer fieldValue = (Integer) field;
                    cell.setCellValue(fieldValue);
                }
            }
        }
    }

    private void addRoutesDataToTemplate()
    {
        int routeId = 1;
        for (Object[][] route : getRoutesData()) {
            addRouteDataToTemplate(route, String.valueOf(routeId++));
        }
    }

    private void addRouteDataToTemplate(final Object[][] route, final String routeId)
    {
        XSSFSheet variantSheet = templateWorkbook.createSheet("Route " + routeId);
        routesSheet.add(variantSheet);
        int rowCount = -1;

        for (Object[] rowContent : route) {
            Row row = variantSheet.createRow(++rowCount);
            int columnCount = 0;
            for (Object field : rowContent) {
                if (columnCount == 0) {
                    variantSheet.setColumnWidth(columnCount, 30 * 256);
                } else {
                    variantSheet.setColumnWidth(columnCount, 15 * 256);
                }
                Cell cell = row.createCell(columnCount++);
                if (field instanceof String) {
                    String fieldValue = (String) field;
                    if (fieldValue.startsWith("FORMULA=")) {
                        cell.setCellFormula(fieldValue.substring(8));
                    } else if (fieldValue.startsWith("HIGHLIGHT=")) {
                        // Highlight the values that can be changed...
                        String cellColor = fieldValue.substring(10, fieldValue.lastIndexOf("="));
                        changeCellBackgroundColor(cell, cellColor);
                        fieldValue = fieldValue.substring(fieldValue.lastIndexOf("=") + 1);
                        if (isInteger(fieldValue)) {
                            cell.setCellValue(Integer.valueOf(fieldValue));
                        } else {
                            cell.setCellValue(fieldValue);
                        }
                    } else {
                        cell.setCellValue((String) field);
                    }
                } else if (field instanceof Integer) {
                    Integer fieldValue = (Integer) field;
                    cell.setCellValue(fieldValue);
                }
            }
        }
    }

    private void changeCellBackgroundColor(Cell cell, final String cellColor)
    {
        if (cellColor.equals("YELLOW")) {
            cell.setCellStyle(inputCellStyle);
        } else if (cellColor.equals("GREEN")) {
            cell.setCellStyle(headerCellStyle);
        }
    }

    private boolean isInteger(final String s)
    {
        int radix = 10;
        if (s.isEmpty())
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1)
                    return false;
                else
                    continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0)
                return false;
        }

        return true;
    }
}