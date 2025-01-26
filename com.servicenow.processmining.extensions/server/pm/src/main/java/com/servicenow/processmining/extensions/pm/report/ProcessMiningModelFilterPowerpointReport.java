package com.servicenow.processmining.extensions.pm.report;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.report.analysis.BreakdownDataSourceAnalysis;
import com.servicenow.processmining.extensions.pm.report.analysis.DataSourceAnalysis;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceFindingContent;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceVariantFindingComplianceReferencePath;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.awt.Rectangle;

import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.PeriodFormat;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMiningModelFilterPowerpointReport
{
    private ProcessMiningModel model = null;
    private String fileName = null;
    private ProcessMiningModelFilterDataSource filterDataSource = null;
    private XMLSlideShow ppt = null;
    private XSLFSlide coverSlide = null;
    private XSLFSlide scopeSlide = null;
    private ArrayList<XSLFSlide> contextSlides = null;
    private XSLFSlide filterAnalysisTitleSlide = null;
    private ArrayList<XSLFSlide> filterAnalysisDetailSlide = null;
    private boolean complianceReferencePathAnalysisPrinted = false;
    private boolean breakdownIntroSlidePrinted = false;

    public ProcessMiningModelFilterPowerpointReport(final ProcessMiningModel model)
    {
        logger.debug("Creating ProcessMiningModelFilterPowerpointReport");
        this.model = model;
    }

    public ProcessMiningModel getModel()
    {
        return this.model;
    }

    public ProcessMiningModelFilterDataSource getFilterDataSource()
    {
        if (this.filterDataSource == null) {
            this.filterDataSource = new ProcessMiningModelFilterDataSource(getModel());
        }

        return this.filterDataSource;
    }

    private String getTemplatePPTFileName()
    {
        return "Process Mining POV Template.pptx";
    }

    private String getNewPresentationFileName()
    {
        if (fileName == null) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            fileName = "/tmp/ProcessMiningPPTGenerator" + "-" + getModel().getName() + "-" + dateFormat.format(date) + ".pptx";
        }

        return fileName;
    }

    public String getReportFileName()
    {
        return getNewPresentationFileName();
    }

    public boolean createReport()
    {
        runFilterAnalysis();
        createNewPresentationFile();
        addContentToPresentation();
        saveNewPresentationFile();
        return true;
    }

    private void runFilterAnalysis()
    {
        getFilterDataSource().runAllAnalysis();
    }

    private void createNewPresentationFile()
    {
        try {
            String fileName = getTemplatePPTFileName();
            InputStream inputstream = this.getClass().getClassLoader().getResourceAsStream(fileName);
            ppt = new XMLSlideShow(inputstream);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addContentToPresentation()
    {
        getCoverSlide();
        getScopeSlide();
        getContextSlides();
        getFilterAnalysisTitleSlide();
        getFilterAnalysisDetailsSlides();
    }

    private XSLFSlide getCoverSlide()
    {
        if (coverSlide == null) {
            XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
            XSLFSlideLayout titleLayout = slideMaster.getLayout("1_Cover_1");
            coverSlide = ppt.createSlide(titleLayout);
            XSLFTextShape title1 = coverSlide.getPlaceholder(0);
            title1.setText("ServiceNow Process Mining");
            XSLFTextShape title2 = coverSlide.getPlaceholder(1);
            title2.setText("[Customer] Art of the possible & insights");

            XSLFTextShape dateLabel = coverSlide.getPlaceholder(2);
            dateLabel.setText("Date");
            XSLFTextShape dateValue = coverSlide.getPlaceholder(3);
            Locale loc = new Locale("en", "US");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
            dateValue.setText(dateFormat.format(new Date()));

            XSLFTextShape name1 = coverSlide.getPlaceholder(4);
            name1.setText("[Presenter 1]");
            XSLFTextShape nameTitle1 = coverSlide.getPlaceholder(5);
            nameTitle1.setText("[Title Presenter 1]");

            XSLFTextShape name2 = coverSlide.getPlaceholder(6);
            name2.setText("[Presenter 2]");
            XSLFTextShape nameTitle2 = coverSlide.getPlaceholder(7);
            nameTitle2.setText("[Title Presenter 2]");
        }

        return coverSlide;
    }

    private XSLFSlide getScopeSlide()
    {
        if (scopeSlide == null) {
            XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
            XSLFSlideLayout titleLayout = slideMaster.getLayout("Title and Content");
            XSLFSlide slide = ppt.createSlide(titleLayout);

            XSLFTextShape title1 = slide.getPlaceholder(0);
            title1.setText("Scope of work");

            XSLFTextShape contentSlide = slide.getPlaceholder(1);
            contentSlide.clearText();
            XSLFTextParagraph bulletP = contentSlide.addNewTextParagraph();
            bulletP.setIndentLevel(0);
            XSLFTextRun bulletRun1 = bulletP.addNewTextRun();
            String content = "Instance: ";
            bulletRun1.setText(content);
            bulletRun1.setBold(true);
            bulletRun1.setFontSize(14d);
            XSLFTextRun bulletRun2 = bulletP.addNewTextRun();
            content = "[Specify Instance Name]";
            bulletRun2.setText(content);
            bulletRun2.setFontSize(14d);

            bulletP = contentSlide.addNewTextParagraph();
            bulletP.setIndentLevel(0);
            bulletRun1 = bulletP.addNewTextRun();
            content = "Process/Table: ";
            bulletRun1.setText(content);
            bulletRun1.setBold(true);
            bulletRun1.setFontSize(14d);
            bulletRun2 = bulletP.addNewTextRun();
            content = getFilterDataSource().getModel().getEntities().get(0).getTableName();
            bulletRun2.setText(content);
            bulletRun2.setFontSize(14d);

            bulletP = contentSlide.addNewTextParagraph();
            bulletP.setIndentLevel(0);
            bulletRun1 = bulletP.addNewTextRun();
            content = "Details:";
            bulletRun1.setText(content);
            bulletRun1.setBold(true);
            bulletRun1.setFontSize(14d);

            bulletP = contentSlide.addNewTextParagraph();
            bulletP.setIndentLevel(1);
            bulletRun1 = bulletP.addNewTextRun();
            content = "The analysis is capped at 3600 records. ";
            bulletRun1.setText(content);
            bulletRun1.setBold(true);
            bulletRun1.setFontSize(14d);
            bulletRun2 = bulletP.addNewTextRun();
            content = "It includes the oldest records first - based on the specified Data Source filtering conditions.";
            bulletRun2.setText(content);
            bulletRun2.setFontSize(14d);

            bulletP = contentSlide.addNewTextParagraph();
            bulletP.setIndentLevel(1);
            bulletRun1 = bulletP.addNewTextRun();
            content = "This is not a consulting engagement. ";
            bulletRun1.setText(content);
            bulletRun1.setBold(true);
            bulletRun1.setFontSize(14d);
            bulletRun2 = bulletP.addNewTextRun();
            content = "It is an exercise on your ServiceNow instance with your own data to show the art of the possible.";
            bulletRun2.setText(content);
            bulletRun2.setFontSize(14d);

            bulletP = contentSlide.addNewTextParagraph();
            bulletP.setIndentLevel(1);
            bulletRun1 = bulletP.addNewTextRun();
            content = "This exercise represents a subset of ALL your process data. ";
            bulletRun1.setText(content);
            bulletRun1.setBold(true);
            bulletRun1.setFontSize(14d);
            bulletRun2 = bulletP.addNewTextRun();
            content = "Remining the same project with all records will yield the more accurate results.";
            bulletRun2.setText(content);
            bulletRun2.setFontSize(14d);

        }

        return scopeSlide;
    }

    private void getContextSlides()
    {
        if (contextSlides == null) {
            contextSlides = new ArrayList<XSLFSlide>();
            XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
            XSLFSlideLayout contentLayout = slideMaster.getLayout("Two Content");

            XSLFSlide slide = ppt.createSlide(contentLayout);
            contextSlides.add(slide);
            XSLFTextShape title1 = slide.getPlaceholder(0);
            title1.clearText();
            title1.setText("Your full and complete AS-IS process ...");
            XSLFTextShape filterName = slide.getPlaceholder(3);
            filterName.clearText();
            filterName.setText("This is how your business process is currently being executed across all its routes/variants.");
            slide.removeShape(slide.getPlaceholder(1));
            slide.removeShape(slide.getPlaceholder(2));

            slide = ppt.createSlide(contentLayout);
            contextSlides.add(slide);
            title1 = slide.getPlaceholder(0);
            title1.clearText();
            title1.setText("Your (more manageable) AS-IS process ...");
            filterName = slide.getPlaceholder(3);
            filterName.clearText();
            filterName.setText("This is your (more manageable) business process traversed paths and where you should look to optimize.");
            slide.removeShape(slide.getPlaceholder(1));
            slide.removeShape(slide.getPlaceholder(2));
        }
    }

    private XSLFSlide getFilterAnalysisTitleSlide()
    {
        if (filterAnalysisTitleSlide == null) {
            XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
            XSLFSlideLayout titleLayout = slideMaster.getLayout("1_Cover_1");
            XSLFSlide slide = ppt.createSlide(titleLayout);
            XSLFTextShape title1 = slide.getPlaceholder(0);
            title1.clearText();
            title1.setText("Filter Analysis");
            XSLFTextShape filterName = slide.getPlaceholder(1);
            filterName.clearText();
            filterName.setText("Filter: " + getFilterDataSource().getFilterName());

            slide.removeShape(slide.getPlaceholder(2));
            slide.removeShape(slide.getPlaceholder(3));
            slide.removeShape(slide.getPlaceholder(4));
            slide.removeShape(slide.getPlaceholder(5));
            slide.removeShape(slide.getPlaceholder(6));
            slide.removeShape(slide.getPlaceholder(7));
        }

        return filterAnalysisTitleSlide;
    }
    
    private ArrayList<XSLFSlide> getFilterAnalysisDetailsSlides()
    {
        if (filterAnalysisDetailSlide == null) {
            filterAnalysisDetailSlide = new ArrayList<XSLFSlide>();
        }

        createComplianceDetailedVariantReport();

        return filterAnalysisDetailSlide;
    }

    private void createComplianceDetailedVariantReport()
    {
        for (DataSourceAnalysis analysis : getFilterDataSource().getFindings().getAnalysis()) {
            XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
            XSLFSlideLayout titleLayout = slideMaster.getLayout("Two Content");
            XSLFSlide slide = null;
            addIntroSlide(analysis);
            for (int i=0; i < analysis.getFindings().size(); i++) {
                slide = ppt.createSlide(titleLayout);

                XSLFTextShape title1 = slide.getPlaceholder(0);
                title1.setText("Filter: " + getFilterDataSource().getFilterName());
                XSLFTextShape breakdownTitle = slide.getPlaceholder(3);
                breakdownTitle.clearText();
                breakdownTitle.setText(analysis.getDescription());

                XSLFTextParagraph bParagraph = breakdownTitle.getTextParagraphs().get(0);
                XSLFTextRun bRun1 = bParagraph.getTextRuns().get(0);
                bRun1.setFontSize(16d);

                addChart(analysis, slide);

                XSLFTextShape filterFindings = slide.getPlaceholder(2);
                filterFindings.clearText();
                for (DataSourceFindingContent findingContent : analysis.getFindings().get(i).getContent()) {
                    XSLFTextParagraph paragraph = filterFindings.addNewTextParagraph();
                    XSLFTextRun run1 = paragraph.addNewTextRun();
                    run1.setText(findingContent.getItem());
                    run1.setFontSize(12d);
                    for (String subItem : findingContent.getSubItems()) {
                        XSLFTextParagraph bulletP = filterFindings.addNewTextParagraph();
                        bulletP.setIndentLevel(1);
                        XSLFTextRun bulletRun1 = bulletP.addNewTextRun();
                        bulletRun1.setText(subItem);
                        bulletRun1.setFontSize(11d);
                    }
                }
            }
        }
    }

    private void addChart(DataSourceAnalysis analysis, XSLFSlide slide)
    {
        if (analysis.getClass().equals(BreakdownDataSourceAnalysis.class)) {
            slide.removeShape(slide.getPlaceholder(1));
            addBreakdownAnalysisChart(slide, (BreakdownDataSourceAnalysis) analysis);
        }
        else {
            XSLFTextShape imageContent = slide.getPlaceholder(1);
            imageContent.setText("Copy Filter Process Map Screenshot");
        }
    }

    private void addBreakdownAnalysisChart(final XSLFSlide slide, final BreakdownDataSourceAnalysis analysis)
    {
        // create chart
        XSLFChart chart = ppt.createChart();
        chart.setTitleText("'" + analysis.getBreakdown().getDisplayName() + "' breakdown avg cycle times");
        chart.setTitleOverlay(false);
        chart.getTitle().getBody().getParagraphs().get(0).getTextRuns().get(0).setFontSize(12d);
        chart.getTitle().getBody().getParagraphs().get(0).getTextRuns().get(0).setFontColor(XDDFColor.from(PresetColor.WHITE));

        // set axis
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        // define chart data for bar chart
        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        data.setVaryColors(false);

        // add chart categories (x-axis data)
        String[] breakdownLabels = new String[analysis.getBreakdown().getValue().size()];
        for (int i=0; i < analysis.getBreakdown().getValue().size(); i++) {
            breakdownLabels[i] = analysis.getBreakdown().getValue().get(i).getLabel();
        }
        String breakdownLabelsDataRange = chart.formatRange(new org.apache.poi.ss.util.CellRangeAddress(1, breakdownLabels.length, 0, 0));
        XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromArray(breakdownLabels, breakdownLabelsDataRange, 0);

        // add chart values (y-axis data)
        Double[] breakdownValues = new Double[analysis.getBreakdown().getValue().size()];
        for (int i=0; i < analysis.getBreakdown().getValue().size(); i++) {
            // Report in Days metric.
            breakdownValues[i] = ((double) analysis.getBreakdown().getValue().get(i).getAvgDuration())/3600/24;
        }
        String breakdownValuesDataRange = chart.formatRange(new org.apache.poi.ss.util.CellRangeAddress(1, breakdownValues.length, 1, 1));
        XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromArray(breakdownValues, breakdownValuesDataRange, 1);
        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarDirection(BarDirection.BAR);

        // add series
        XDDFChartData.Series series = data.addSeries(categoryData, valueData);
        series.setTitle(analysis.getBreakdown().getDisplayName(), chart.setSheetTitle(analysis.getBreakdown().getDisplayName(), 1));

        CTValAx ctValAx = chart.getCTChart().getPlotArea().getValAxArray(0);
        ctValAx.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[]{(byte)255,(byte)255,(byte)255});
        CTTextBody ctTextBody = ctValAx.addNewTxPr();
        ctTextBody.addNewBodyPr();
        CTTextCharacterProperties ctTextCharacterProperties = ctTextBody.addNewP().addNewPPr().addNewDefRPr();
        ctTextCharacterProperties.setSz(10*100);
        ctTextCharacterProperties.addNewSolidFill().addNewSrgbClr().setVal(new byte[]{(byte)255,(byte)255,(byte)255});

        CTCatAx ctCatAx = chart.getCTChart().getPlotArea().getCatAxArray(0);
        ctCatAx.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[]{(byte)255,(byte)255,(byte)255});
        ctTextBody = ctCatAx.addNewTxPr();
        ctTextBody.addNewBodyPr();
        ctTextCharacterProperties = ctTextBody.addNewP().addNewPPr().addNewDefRPr();
        ctTextCharacterProperties.setSz(10*100);
        ctTextCharacterProperties.addNewSolidFill().addNewSrgbClr().setVal(new byte[]{(byte)255,(byte)255,(byte)255});

        // plot chart
        chart.plot(data);

        // set chart dimensions
        Rectangle chartDimensions = new Rectangle(50*Units.EMU_PER_POINT, 150*Units.EMU_PER_POINT, 400*Units.EMU_PER_POINT, analysis.getBreakdown().getValue().size()*40*Units.EMU_PER_POINT);

        // add chart to slide
        slide.addChart(chart, chartDimensions);
    }

    private void addIntroSlide(DataSourceAnalysis analysis)
    {
        String analysisClassName = analysis.getClass().getName();
        if (analysisClassName.indexOf("DataSourceComplianceReferencePathAnalysis") > 0) {
            if (!complianceReferencePathAnalysisPrinted) {
                createAnalysisIntroSlide("Compliance Analysis against Reference Path(s)");
                createComplianceReferencePathSummaryTable(analysis);
                complianceReferencePathAnalysisPrinted = true;
            }
        }
        else if (analysisClassName.indexOf("BreakdownDataSourceAnalysis") > 0) {
            if (!breakdownIntroSlidePrinted) {
                createAnalysisIntroSlide("Breakdown Analysis");
                breakdownIntroSlidePrinted = true;
            }
        }
        else if (analysisClassName.indexOf("TopVariantComparisonDataSourceAnalysis") > 0) {
            createAnalysisIntroSlide("Top Variant Comparison Analysis");
        }
    }

    private void createAnalysisIntroSlide(final String analysis)
    {
        XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout titleLayout = slideMaster.getLayout("1_Cover_1");
        XSLFSlide slide = ppt.createSlide(titleLayout);
        XSLFTextShape title1 = slide.getPlaceholder(0);
        title1.clearText();
        title1.setText(analysis);
        XSLFTextShape filterName = slide.getPlaceholder(1);
        filterName.clearText();
        filterName.setText("Filter: " + getFilterDataSource().getFilterName());

        slide.removeShape(slide.getPlaceholder(2));
        slide.removeShape(slide.getPlaceholder(3));
        slide.removeShape(slide.getPlaceholder(4));
        slide.removeShape(slide.getPlaceholder(5));
        slide.removeShape(slide.getPlaceholder(6));
        slide.removeShape(slide.getPlaceholder(7));
    }

    private void createComplianceReferencePathSummaryTable(final DataSourceAnalysis analysis)
    {
        XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout titleLayout = slideMaster.getLayout("Two Content");
        XSLFSlide tableSlide = null;
        tableSlide = ppt.createSlide(titleLayout);

        tableSlide.removeShape(tableSlide.getPlaceholder(1));
        tableSlide.removeShape(tableSlide.getPlaceholder(2));

        XSLFTextShape title1 = tableSlide.getPlaceholder(0);
        title1.setText("Filter: " + getFilterDataSource().getFilterName());
        XSLFTextShape breakdownTitle = tableSlide.getPlaceholder(3);
        breakdownTitle.clearText();
        breakdownTitle.setText("This table summarizes variant compliance against a specific reference path(s). A detailed analysis for each variant against reference path(s) follows.");
        XSLFTextParagraph bParagraph = breakdownTitle.getTextParagraphs().get(0);
        XSLFTextRun bRun1 = bParagraph.getTextRuns().get(0);
        bRun1.setFontSize(16d);

        XSLFTable tbl = tableSlide.createTable();
        Rectangle2D shape = new Rectangle2D.Double();
        shape.setFrame(50, 125, 650, 450);
        tbl.setAnchor(shape);
        int numColumns = 5;
        XSLFTableRow headerRow = tbl.addRow();
        headerRow.setHeight(25);

        for (int i = 0; i < numColumns; i++) {
            XSLFTableCell th = headerRow.addCell();
            XSLFTextParagraph p = th.addNewTextParagraph();
            p.setTextAlign(TextParagraph.TextAlign.CENTER);
            XSLFTextRun r = p.addNewTextRun();
            r.setText(getColumnHeaderLabel(i));
            r.setFontSize(12d);
            r.setFontColor(Color.WHITE);
            tbl.setColumnWidth(i, getColumnWith(i));
        }

        int numRows = analysis.getFindings().size();
        for (int rownum = 0; rownum < numRows; rownum++) {
            XSLFTableRow tr = tbl.addRow();
            tr.setHeight(25);
            final DataSourceVariantFindingComplianceReferencePath findingContent = (DataSourceVariantFindingComplianceReferencePath) analysis.getFindings().get(rownum);

            // Variation
            XSLFTableCell variationCell = tr.addCell();
            XSLFTextParagraph p = variationCell.addNewTextParagraph();
            XSLFTextRun r = p.addNewTextRun();
            r.setFontSize(12d);
            r.setFontColor(Color.WHITE);
            r.setText("Variation # " + (rownum+1));

            // Is Compliant?
            XSLFTableCell isCompliantCell = tr.addCell();
            p = isCompliantCell.addNewTextParagraph();
            p.setTextAlign(TextAlign.CENTER);
            r = p.addNewTextRun();
            r.setFontSize(12d);
            r.setFontColor(Color.WHITE);
            if (findingContent.getVariantIsCompliant()) {
                if (!findingContent.getReferencePathVariantId().equals("")) {
                    r.setText("Yes (" + findingContent.getReferencePathVariantId() + ")");
                }
                else {
                    r.setText("Yes");
                }
            }
            else {
                r.setText("No");
            }

            // Total Cases
            XSLFTableCell totalCasesCell = tr.addCell();
            p = totalCasesCell.addNewTextParagraph();
            p.setTextAlign(TextAlign.CENTER);
            r = p.addNewTextRun();
            r.setFontSize(12d);
            r.setFontColor(Color.WHITE);
            r.setText(findingContent.getVariationTotalCases() + "");

            // Average
            XSLFTableCell avgCell = tr.addCell();
            p = avgCell.addNewTextParagraph();
            r = p.addNewTextRun();
            r.setFontSize(12d);
            r.setFontColor(Color.WHITE);
            Period avgPeriod = new Period(Seconds.seconds(findingContent.getVariationPathAverage())).normalizedStandard();
            r.setText(PeriodFormat.getDefault().print(avgPeriod));

            // Total Time
            XSLFTableCell totalTimeCell = tr.addCell();
            p = totalTimeCell.addNewTextParagraph();
            r = p.addNewTextRun();
            r.setFontSize(12d);
            r.setFontColor(Color.WHITE);
            Period totalPeriod = new Period(Seconds.seconds(findingContent.getVariationTotalCases() * findingContent.getVariationPathAverage())).normalizedStandard();
            r.setText(PeriodFormat.getDefault().print(totalPeriod));
        }

        int referencePathYCoordinate = 400;
        for (ProcessMiningModelVariant referencePathVariant : getModel().getReferencePathVariants().values()) {
            XSLFTextShape refPath = tableSlide.createTextBox();
            Rectangle2D shapeRefPath = new Rectangle2D.Double();
            shapeRefPath.setFrame(50, referencePathYCoordinate, 800, 50);
            refPath.setAnchor(shapeRefPath);

            refPath.clearText();
            XSLFTextParagraph p = refPath.addNewTextParagraph();
            XSLFTextRun r = p.addNewTextRun();

            r.setFontSize(12d);
            r.setFontColor(Color.WHITE);
            Period referencePathAvgPeriod = new Period(Seconds.seconds(referencePathVariant.getAvgDuration())).normalizedStandard();
            r.setText("Reference Path[" + referencePathVariant.getId() + "]: [" + referencePathVariant.getTranslatedRouteNodes().replaceAll(",", " -> ") + "].\nAvg. Cycle Time: " + PeriodFormat.getDefault().print(referencePathAvgPeriod));
            referencePathYCoordinate += 40;
        }
    }

    private String getColumnHeaderLabel(int i)
    {
        switch (i) {
            case 0:
                return "Variation";
            case 1:
                return "Is Compliant?";
            case 2:
                return "Total Cases";
            case 3:
                return "Average";
            case 4:
                return "Total Time";
            default:
                return "N/A";
        }
    }

    private double getColumnWith(int i)
    {
        switch (i) {
            case 0:
                return 100;
            case 1:
                return 100;
            case 2:
                return 80;
            case 3:
                return 300;
            case 4:
                return 300;
            default:
                return 10;
        }
    }

    private void saveNewPresentationFile()
    {
        try {
            fileName = getNewPresentationFileName();
            FileOutputStream out = new FileOutputStream(fileName);
            ppt.write(out);
            out.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningModelFilterPowerpointReport.class);
}
