package entity;

public class DuplicationInfo {
    private String textString;
    private long text1Counter;
    private long text2Counter;

    public String getTextString() {
        return textString;
    }

    public void setTextString(String textString) {
        this.textString = textString;
    }

    public long getText1Counter() {
        return text1Counter;
    }

    public void setText1Counter(long text1Counter) {
        this.text1Counter = text1Counter;
    }

    public long getText2Counter() {
        return text2Counter;
    }

    public void setText2Counter(long text2Counter) {
        this.text2Counter = text2Counter;
    }
}
