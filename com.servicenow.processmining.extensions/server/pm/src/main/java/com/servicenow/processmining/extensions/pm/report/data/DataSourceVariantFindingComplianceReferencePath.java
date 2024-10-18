package com.servicenow.processmining.extensions.pm.report.data;

public class DataSourceVariantFindingComplianceReferencePath
    extends DataSourceFinding
{
    private String variantId = null;
    private String referencePathVariantId = null;
    private boolean isCompliant = false;
    private int variationPathAverage = -1;
    private int referencePathAverage = -1;
    private int variationTotalCases = -1;

    public DataSourceVariantFindingComplianceReferencePath(final String variantId)
    {
        super();
        this.variantId = variantId;
        this.referencePathVariantId = null;
        this.isCompliant = false;
    }

    public String getVariantId()
    {
        return this.variantId;
    }

    public void setVariantIsCompliant(boolean isCompliant)
    {
        this.isCompliant = isCompliant;
    }

    public boolean getVariantIsCompliant()
    {
        return this.isCompliant;
    }

    public void setVariationPathAverage(int avgDuration)
    {
        this.variationPathAverage = avgDuration;
    }

    public int getVariationPathAverage()
    {
        return this.variationPathAverage;
    }

    public void setReferencePathAverage(int avgDuration)
    {
        this.referencePathAverage = avgDuration;
    }

    public int getReferencePathAverage()
    {
        return this.referencePathAverage;
    }

    public int getDelta()
    {
        return getVariationPathAverage() - getReferencePathAverage();
    }

    public void setVariationTotalCases(int frequency)
    {
        this.variationTotalCases = frequency;
    }

    public int getVariationTotalCases()
    {
        return this.variationTotalCases;
    }

    public int getVariationTotalPossibleImprovementTime()
    {
        return getDelta() * getVariationPathAverage();
    }

    public String toString()
    {
        return "Variation Avg: " + getVariationPathAverage() + ", Reference Path Avg: " + getReferencePathAverage() + ", Delta: " + getDelta() + ", Variation Total Cases: " + getVariationTotalCases() + ", Possible Improvement Time: " + getVariationTotalPossibleImprovementTime();
    }

    public void setReferencePathVariantId(String id)
    {
        this.referencePathVariantId = id;
    }

    public String getReferencePathVariantId()
    {
        return this.referencePathVariantId;
    }
}
