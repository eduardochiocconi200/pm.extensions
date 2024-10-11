package com.servicenow.processmining.extensions.pm.report;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.servicenow.processmining.extensions.pm.model.ProcessMiningModel;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelBreakdown;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariant;
import com.servicenow.processmining.extensions.pm.model.ProcessMiningModelVariantDelta;
import com.servicenow.processmining.extensions.pm.model.detectors.ComplianceDetector;
import com.servicenow.processmining.extensions.pm.model.detectors.ReworkLoopDetector;
import com.servicenow.processmining.extensions.pm.report.data.BreakdownDataSourceAnalysis;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceAnalysis;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceComplianceReferencePathAnalysis;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceVariantFindingComplianceReferencePath;
import com.servicenow.processmining.extensions.pm.report.data.TopVariantComparisonDataSourceAnalysis;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceFindingContent;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceFindings;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceVariantFinding;

public class ProcessMiningModelFilterDataSource
{
    private String filterName = null;
    private ProcessMiningModel model = null;
    private DataSourceFindings findings = null;
    private DecimalFormat df = null;

    public ProcessMiningModelFilterDataSource(final ProcessMiningModel model)
    {
        this("All Records", model);
    }

    public ProcessMiningModelFilterDataSource(final String filterName, final ProcessMiningModel model)
    {
        this.filterName = filterName;
        this.model = model;
        findings = new DataSourceFindings(filterName);
        df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
    }

    public String getFilterName()
    {
        return this.filterName;
    }

    public ProcessMiningModel getModel()
    {
        return this.model;
    }

    public DataSourceFindings getFindings()
    {
        return this.findings;
    }

    public boolean runAllAnalysis()
    {
        if (!runVariantToCaseCountAnalysis()) {
            return false;
        }
        if (!runVariantAnalysis()) {
            return false;
        }
        if (!runComplianceAnalysis()) {
            return false;
        }

        return true;
    }

    private boolean runVariantToCaseCountAnalysis()
    {
        for (ProcessMiningModelBreakdown breakdown : getModel().getBreakdowns().values()) {
            DataSourceAnalysis analysis = new BreakdownDataSourceAnalysis();
            String breakdownId = breakdown.getDisplayName() != null && !breakdown.getDisplayName().equals("") ? breakdown.getDisplayName() : breakdown.getField();
            analysis.setDescription("This analysis reviews the '" + breakdownId + "' breakdown values relevance. Breakdown values with high volume and high average execution categorization will offer the best place to start looking for inefficiences as they offer the better potential return for their fix.");
            breakdown.getVariantToCaseCountRatioAnalysis();
            analysis.getFindings().addAll(breakdown.getFindings());
            findings.addAnalysis(analysis);
        }

        return true;
    }

    private boolean runVariantAnalysis()
    {
        int eightyPctVolume = (int) (getModel().getAggregate().getCaseCount() * 0.8);
        int accumulatedCaseVolume = 0;
        ArrayList<ProcessMiningModelVariant> topVariants = new ArrayList<ProcessMiningModelVariant>();
        ProcessMiningModelVariant topVariant = null;
        for (ProcessMiningModelVariant variant : getModel().getVariantsSortedByFrequency()) {
            if (topVariant == null) {
                topVariant = variant;
            }
            else {
                topVariants.add(variant);
            }
            accumulatedCaseVolume += variant.getFrequency();
            if (accumulatedCaseVolume > eightyPctVolume) {
                break;
            }
        }

        DataSourceAnalysis analysis = new TopVariantComparisonDataSourceAnalysis();
        analysis.setDescription("This variant/route execution volume-based analysis, focused on the top 80% executions for the selected filter. We recommend to focus efforts in optimizing the 'Top Variant' or any of the variants that follow.");
        DataSourceVariantFinding variantFinding = new DataSourceVariantFinding();
        analysis.getFindings().add(variantFinding);
        DataSourceFindingContent variantFindingContent = new DataSourceFindingContent();
        variantFinding.addContent(variantFindingContent);
        variantFindingContent.setItem("The 'Top Variant' variant/route with '" + topVariant.getFrequency() + "' executions represent '" + df.format(((topVariant.getFrequency() * 100)/getModel().getAggregate().getCaseCount())) + "'% of the total traffic for the selected filter and averages '" + topVariant.getAvgDuration() + "' hours per execution.");
        variantFindingContent.addSubItem(runVariantReworkAnalysis(1, topVariant));
        int count = 2;
        for (ProcessMiningModelVariant variant : topVariants) {
            ProcessMiningModelVariantDelta topVariantToVariant = new ProcessMiningModelVariantDelta(topVariant, variant);
            variantFindingContent = new DataSourceFindingContent();
            variantFinding.addContent(variantFindingContent);
            // End to End Time Cycle Analysis relative to Top Route.
            variantFindingContent.setItem(variantCycleAnalysis(count, topVariantToVariant));
            // Number of Steps Analysis relative to Top Route.
            variantFindingContent.addSubItem(variantStepAnalysis(count, topVariantToVariant));
            // Rework Analysis ...
            variantFindingContent.addSubItem(runVariantReworkAnalysis(count, variant));

            if (count % 2 == 0) {
                variantFinding = new DataSourceVariantFinding();
                analysis.getFindings().add(variantFinding);
            }
            count++;
        }

        // Let's make sure we clean up the last added empty element.
        if (variantFinding.getContent().size() == 0) {
            analysis.getFindings().remove(analysis.getFindings().get(analysis.getFindings().size()-1));
        }

        findings.addAnalysis(analysis);

        return true;
    }

    private String variantCycleAnalysis(final int count, final ProcessMiningModelVariantDelta topVariantToVariant)
    {
        ProcessMiningModelVariant topVariant = topVariantToVariant.getVariant1();
        ProcessMiningModelVariant variant = topVariantToVariant.getVariant2();
    
        if (topVariantToVariant.isSlower()) {
            float slower = (((float)variant.getAvgDuration()-(float)topVariant.getAvgDuration())/(float)topVariant.getAvgDuration())*100;
            return "The #" + count + " variant/route executed '" + variant.getFrequency() + "' times through '" + variant.getNodeCount() + "' steps and it takes '" + ((variant.getAvgDuration()-topVariant.getAvgDuration())/3600) + "' hours more on average to complete than the 'Top Variant' (or " + df.format(slower) + "% slower).";
        }
        else {
            float faster = (((float)topVariant.getAvgDuration()-(float)variant.getAvgDuration())/(float)variant.getAvgDuration())*100;
            return "The #" + count + " variant/route executed '" + variant.getFrequency() + "' times through '" + variant.getNodeCount() + "' steps and it takes '" + ((topVariant.getAvgDuration()-variant.getAvgDuration())/3600) + "' hours less on average to complete than the 'Top Variant' (or " + df.format(faster) + "% faster).";
        }
    }

    private String variantStepAnalysis(final int count, final ProcessMiningModelVariantDelta topVariantToVariant)
    {
        ProcessMiningModelVariant topVariant = topVariantToVariant.getVariant1();
        ProcessMiningModelVariant variant = topVariantToVariant.getVariant2();
        String stepAnalysis = "";
    
        if (topVariantToVariant.hasSameNumberOfSteps()) {
            stepAnalysis = "It executes the same number of steps as the 'Top Variant'.";
        }
        else if (topVariantToVariant.hasLessSteps()) {
            stepAnalysis = "It executes '" + (topVariant.getNodeCount()-variant.getNodeCount()) + "' steps more than the 'Top Variant'.";
        }
        else {
            stepAnalysis = "It executes '" + (topVariant.getNodeCount()-variant.getNodeCount()) + "' steps less than the 'Top Variant'.";
            if (topVariantToVariant.isSlower()) {
                stepAnalysis += " But incidentally, it is still taking more time. Recommend ";
            }
        }

        return stepAnalysis;
    }

    private String runVariantReworkAnalysis(final int count, final ProcessMiningModelVariant variant)
    {
        ReworkLoopDetector loopDetector = new ReworkLoopDetector(variant);
        StringBuffer sb = new StringBuffer();

        // Function call
        if (loopDetector.hasReworkCycles()) {
            String route = variant.getPath().toString();
            for (String node : variant.getDistinctNodes()) {
                String nodeName = getModel().getNodes().get(node).getName();
                route = route.replaceAll(node, nodeName);
            }

            sb.append("The variant/route (" + route + ") has rework loops.\nThese loops are: ");
            for (int i=0; i < loopDetector.getLoops().size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                String path = loopDetector.getLoops().get(i).toString();
                for (String node : variant.getDistinctNodes()) {
                    String nodeName = getModel().getNodes().get(node).getName();
                    path = path.replaceAll(node, nodeName);
                }
                sb.append("(" + (i+1) + "): (" + path + ")");
            }
            sb.append(".");
        }
        else {
            sb.append("The variant/route does not has rework loops.");
        }

        return sb.toString();
    }

    private boolean runComplianceAnalysis()
    {
        ComplianceDetector detector = new ComplianceDetector(getModel());
        boolean result  = detector.run();
        DataSourceAnalysis analysis = new DataSourceComplianceReferencePathAnalysis();
        analysis.setDescription("This analysis reviews variants compliance against specific compliant path(s).");

        for (ProcessMiningModelVariant v : getModel().getVariants().values()) {
            DataSourceVariantFindingComplianceReferencePath variantFinding = new DataSourceVariantFindingComplianceReferencePath();
            variantFinding.setVariationPathAverage(v.getAvgDuration());
            if (v.getReferenceVariant() != null) {
                variantFinding.setReferencePathAverage(v.getReferenceVariant().getAvgDuration());
            }
            variantFinding.setVariationTotalCases(v.getFrequency());

            DataSourceFindingContent content = new DataSourceFindingContent();
            boolean processedFirstElement = false;
            for (String variantComplianceNote : v.getNonComplianceDetails()) {
                if (!processedFirstElement) {
                    content.setItem(variantComplianceNote);
                }
                else {
                    content.addSubItem(variantComplianceNote);
                }
                processedFirstElement = true;
            }
            variantFinding.addContent(content);
            analysis.getFindings().add(variantFinding);
        }
        findings.addAnalysis(analysis);

        return result;
    }
}
