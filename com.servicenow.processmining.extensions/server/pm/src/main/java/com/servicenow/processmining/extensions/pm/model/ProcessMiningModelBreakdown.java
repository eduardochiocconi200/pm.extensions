package com.servicenow.processmining.extensions.pm.model;

import com.servicenow.processmining.extensions.pm.report.data.DataSourceBreakdownFinding;
import com.servicenow.processmining.extensions.pm.report.data.DataSourceFindingContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class ProcessMiningModelBreakdown 
{
    private String entityId = null;
    private String field = null;
    private String label = null;
    private String displayName = null;
    private int aggregateCaseCount = -1;
    private ArrayList<ProcessMiningModelBreakdownStat> values = null;
    private ArrayList<ProcessMiningModelBreakdownStat> variantToCaseCountRatio = null;

    public ProcessMiningModelBreakdown()
    {
    }

    public void setEntityId(final String entityId)
    {
        this.entityId = entityId;
    }

    public String getEntityId()
    {
        return this.entityId;
    }

    public void setField(final String field)
    {
        this.field = field;
    }

    public String getField()
    {
        return this.field;
    }

    public void setLabel(final String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return this.label;
    }

    public void setDisplayName(final String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public int getAggregateCaseCount()
    {
        if (this.aggregateCaseCount == -1) {
            this.aggregateCaseCount = 0;
            for (ProcessMiningModelBreakdownStat stat : getValue()) {
                this.aggregateCaseCount += stat.getCaseCount();
            }
        }

        return this.aggregateCaseCount;
    }

    public boolean addBreakdownStat(final ProcessMiningModelBreakdownStat stat)
    {
        return getValue().add(stat);
    }

    public ArrayList<ProcessMiningModelBreakdownStat> getValue()
    {
        if (values == null) {
            values = new ArrayList<ProcessMiningModelBreakdownStat>();
        }

        return values;
    }

    public ArrayList<ProcessMiningModelBreakdownStat> getVariantToCaseCountRatioAnalysis()
    {
        if (this.variantToCaseCountRatio == null) {
            TreeMap<Integer, ProcessMiningModelBreakdownStat> elements = new TreeMap<Integer, ProcessMiningModelBreakdownStat>(Collections.reverseOrder());
            int breakEvenPoint = getAggregateCaseCount() / getValue().size();
            for (ProcessMiningModelBreakdownStat stat : getValue()) {
                // We initially obtain the route variations to case ratio.
                // We are hoping a large volume of cases are routed through a very small number of routes.
                // If the ratio is very low (trending to zero), then it means we are reaching this goal (high volume with low variability).
                // However, if the ratio is high (trending to one), then there is high variability to the breakdown case.
                double variationToTotalCaseRatio = ((double)stat.getVariantCount() / (double)stat.getCaseCount());

                // Then we need to filter this ratio by the relevance of the breakdown. Is it a dominant breakdown value?
                // We then have the following score categories:
                // A: High volume with Low Variability with High relevance (dominant breakdown value).
                // B: High Volume with High Variability with High relevant (dominant breakdown value).
                // C: Low Volume with Low Variability with Low relevance (not dominant breakdown value).
                // D: Low Volumn with High Variability with Low relevance (not dominant breakdown value).
                // Case A:
                if (stat.getCaseCount() >= breakEvenPoint && variationToTotalCaseRatio < HEALTHY_VARIANT_TO_CASE_COUNT_RATIO) {
                    stat.setHighVolume(true);
                    stat.setHighVariability(false);
                }
                // Case B:
                else if (stat.getCaseCount() >= breakEvenPoint && variationToTotalCaseRatio >= HEALTHY_VARIANT_TO_CASE_COUNT_RATIO) {
                    stat.setHighVolume(true);
                    stat.setHighVariability(true);
                }
                // Case C:
                else if (stat.getCaseCount() < breakEvenPoint && variationToTotalCaseRatio < HEALTHY_VARIANT_TO_CASE_COUNT_RATIO) {
                    stat.setHighVolume(false);
                    stat.setHighVariability(false);

                }
                // Case D:
                else if (stat.getCaseCount() < breakEvenPoint && variationToTotalCaseRatio >= HEALTHY_VARIANT_TO_CASE_COUNT_RATIO) {
                    stat.setHighVolume(false);
                    stat.setHighVariability(true);
                }
                else {
                    throw new RuntimeException("Unclassified Variant to Case Count Category. Case Count: (" + getAggregateCaseCount() + "), Break Even Point: (" + breakEvenPoint + "), Variation to Case Ratio: (" + variationToTotalCaseRatio + ")");
                }
                elements.put(stat.getCaseCount(), stat);
            }
            this.variantToCaseCountRatio = new ArrayList<ProcessMiningModelBreakdownStat>();
            for (int key : elements.keySet()) {
                this.variantToCaseCountRatio.add(elements.get(key));
            }
        }

        return this.variantToCaseCountRatio;
    }

    public ArrayList<DataSourceBreakdownFinding> getFindings()
    {
        ArrayList<ProcessMiningModelBreakdownStat> hvohvaStat = new ArrayList<ProcessMiningModelBreakdownStat>();
        ArrayList<ProcessMiningModelBreakdownStat> hvolvaStat = new ArrayList<ProcessMiningModelBreakdownStat>();
        ArrayList<ProcessMiningModelBreakdownStat> lvohvaStat = new ArrayList<ProcessMiningModelBreakdownStat>();
        ArrayList<ProcessMiningModelBreakdownStat> lvolvaStat = new ArrayList<ProcessMiningModelBreakdownStat>();

        for (ProcessMiningModelBreakdownStat stat : getValue()) {
            boolean highVolume = stat.getHighVolume();
            boolean highVariability = stat.getHighVariability();
            if (highVolume && highVariability) {
                hvohvaStat.add(stat);
            }
            else if (highVolume && !highVariability) {
                hvolvaStat.add(stat);
            }
            else if (!highVolume && highVariability) {
                lvohvaStat.add(stat);
            }
            else if (!highVolume && !highVariability) {
                lvolvaStat.add(stat);
            }
        }

        ArrayList<DataSourceBreakdownFinding> findings = new ArrayList<DataSourceBreakdownFinding>();
        DataSourceBreakdownFinding finding = new DataSourceBreakdownFinding();
        findings.add(finding);
        int count = 1;
        String findingItem = "";
        if (hvohvaStat != null && hvohvaStat.size() > 0) {
            DataSourceFindingContent content = new DataSourceFindingContent();
            finding.addContent(content);
            findingItem += "The following are high impact '" + getField() + "' breakdown value(s) (they represent more than 5% of the filtered cases and are over the average distribution): ";
            content.setItem(findingItem);
            for (ProcessMiningModelBreakdownStat hvohva : hvohvaStat) {
                if (count > 1) {
                    findingItem += ", ";
                }
                int avgDuration = hvohva.getAvgDuration()/3600;
                double pct = ((hvohva.getVariantCount()) * 100 / aggregateCaseCount);
                String findingSubItem = "Label/Value: '" + hvohva.getLabel() + "'', Count: " + hvohva.getVariantCount() + " (" + pct + "% of all routes volume), Avg Duration: '" + avgDuration + "' hours.";
                content.addSubItem(findingSubItem);
                count++;
            }
            if (count > 2) {
                String recommendation = "Recommendation: Mine each one of these breakdown values and look for differences and similarities (volume, avg time, rework loops, long transitions, etc) across them. You may create filters and use the 'Compare' feature.";
                content.addSubItem(recommendation);
            }
        }

        count = 1;
        if (hvolvaStat != null && hvolvaStat.size() > 0) {
            DataSourceFindingContent content = new DataSourceFindingContent();
            finding.addContent(content);
            findingItem = "The following are medium impact '" + getField() + "' breakdown value(s) (they represent more than 5% of the filtered cases and are below the average distribution): ";
            content.setItem(findingItem);
            for (ProcessMiningModelBreakdownStat hvolva : hvolvaStat) {
                if (count > 1) {
                    findingItem += ", ";
                }
                int avgDuration = hvolva.getAvgDuration()/3600;
                double pct = ((hvolva.getVariantCount()) * 100 / aggregateCaseCount);
                String findingSubItem = "Label/Value: '" + hvolva.getLabel() + "'', Count: " + hvolva.getVariantCount() + " (" + pct + "% of all routes volume), Avg Duration: '" + avgDuration + "' hours.";
                content.addSubItem(findingSubItem);
                count++;
            }
        }

        count = 1;
        if (lvohvaStat != null && lvohvaStat.size() > 0) {
            DataSourceFindingContent content = new DataSourceFindingContent();
            finding.addContent(content);
            findingItem = "The following are medium impact '" + getField() + "' breakdown value(s) (they represent less than 5% of the filtered cases and are over the average distribution): ";
            content.setItem(findingItem);
            for (ProcessMiningModelBreakdownStat lvohva : lvohvaStat) {
                if (count > 1) {
                    findingItem += ", ";
                }
                int avgDuration = lvohva.getAvgDuration()/3600;
                double pct = ((lvohva.getVariantCount()) * 100 / aggregateCaseCount);
                String findingSubItem = "Label/Value: '" + lvohva.getLabel() + "'', Count: " + lvohva.getVariantCount() + " (" + pct + "% of all routes volume), Avg Duration: '" + avgDuration + "' hours.";
                content.addSubItem(findingSubItem);
                count++;
            }
        }

        count = 1;
        if (lvolvaStat != null && lvolvaStat.size() > 0) {
            DataSourceFindingContent content = new DataSourceFindingContent();
            finding.addContent(content);
            findingItem = "The following are low impact dominant '" + getField() + "' breakdown value(s) (they represent less than 5% of the filtered cases and are below the average distribution): ";
            content.setItem(findingItem);
            for (ProcessMiningModelBreakdownStat lvolva : lvolvaStat) {
                if (count > 1) {
                    findingItem += ", ";
                }
                int avgDuration = lvolva.getAvgDuration()/3600;
                double pct = ((lvolva.getVariantCount()) * 100 / aggregateCaseCount);
                String findingSubItem = "Label/Value: '" + lvolva.getLabel() + "'', Count: " + lvolva.getVariantCount() + " (" + pct + "% of all routes volume), Avg Duration: '" + avgDuration + "' hours.";
                content.addSubItem(findingSubItem);
                count++;
            }
        }

        return findings;
    }

    // 5% 
    private final static double HEALTHY_VARIANT_TO_CASE_COUNT_RATIO = 0.05;
}
