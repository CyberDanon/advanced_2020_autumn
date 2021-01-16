package entity;

public class CompareResult {
    private double classic;
    private double correlation;
    private String path;
    private boolean isDuplicates;
    private String comparedTo;

    public String getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(String comparedTo) {
        this.comparedTo = comparedTo;
    }

    public double getClassic() {
        return classic;
    }

    public void setClassic(double classic) {
        this.classic = classic;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDuplicates() {
        return isDuplicates;
    }

    public void setDuplicates(boolean duplicates) {
        isDuplicates = duplicates;
    }
}
