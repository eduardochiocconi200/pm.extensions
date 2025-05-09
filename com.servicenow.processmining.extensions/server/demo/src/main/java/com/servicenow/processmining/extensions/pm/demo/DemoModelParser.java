package com.servicenow.processmining.extensions.pm.demo;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoModelParser
{
    private String modelFileLocation = null;
    private String dataIdentifier = null;
    private DemoModel model = null;

    public DemoModelParser(final String modelFileLocation, final String dataIdentifier)
    {
        this.modelFileLocation = modelFileLocation;
        this.dataIdentifier = dataIdentifier;
    }

    public String getModelFileLocation()
    {
        return this.modelFileLocation;
    }

    public String getDataIdentifier()
    {
        return this.dataIdentifier;
    }

    public DemoModel getModel()
    {
        if (model == null) {
            this.model = new DemoModel(dataIdentifier);
        }

        return this.model;
    }

    public boolean parse()
    {
        // Try block to check for exceptions
        try {
            // Reading file from local directory
            FileInputStream file = new FileInputStream(new File(getModelFileLocation()));
            // Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            // Get first/desired sheet from the workbook
            for (int i=0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                if (sheet.getSheetName().startsWith("Path")) {
                    getModel().addPath(parsePathSheet(sheet));
                }
            }

            // Closing file output streams
            file.close();
            workbook.close();
        }
        // Catch block to handle exceptions
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }

    private DemoModelPath parsePathSheet(final XSSFSheet sheet)
    {
        String sheetName = sheet.getSheetName();
        int count = Integer.valueOf(sheet.getRow(0).getCell(1).getRawValue()).intValue();
        double creationDelta = getCellValueAsDouble(sheet.getRow(1).getCell(1));
        String table = getCellValueAsString(sheet.getRow(2).getCell(1));
        DemoModelPath path = new DemoModelPath(sheetName, count, creationDelta, table);
        int i=5;
        boolean hasRows = true;
        do {
            if (sheet.getRow(i) == null || (sheet.getRow(i) != null && sheet.getRow(i).getCell(0) == null)) {
                hasRows = false;
            }
            else {
                double time = getCellValueAsDouble(sheet.getRow(i).getCell(0));
                String field = getCellValueAsString(sheet.getRow(i).getCell(1));
                String values = getCellValueAsString(sheet.getRow(i).getCell(2));
                if (field == null || values == null) {
                    hasRows = false;
                }
                else {
                    DemoModelPathEntry entry = new DemoModelPathEntry(time, field, values);
                    path.addEntry(entry);
                    i++;
                }
            }
        }
        while (hasRows);
        logger.debug("Sheet: (" + sheet.getSheetName() + "). Last Row: (" + i + ")");
        path.setTotalDuration(path.getEntries().get(path.getEntries().size()-1).getTime());

        return path;
    }

    private double getCellValueAsDouble(final XSSFCell cell)
    {
        return cell.getNumericCellValue();
    }

    private String getCellValueAsString(final XSSFCell cell)
    {
        String value = null;
        if (cell.getCellType() == CellType.STRING) {
            value = cell.getStringCellValue();
        }
        else {
            value = cell.getRawValue();
        }

        return value;
    }

    private static final Logger logger = LoggerFactory.getLogger(DemoModelParser.class);
}
