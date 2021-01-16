package entity;

public class ShingleDTO {
    private int documentId;
    private int shingleText;
    private int shingleMaxValue;

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getShingleText() {
        return shingleText;
    }

    public void setShingleText(int shingleText) {
        this.shingleText = shingleText;
    }

    public int getShingleMaxValue() {
        return shingleMaxValue;
    }

    public void setShingleMaxValue(int shingleMaxValue) {
        this.shingleMaxValue = shingleMaxValue;
    }
}
