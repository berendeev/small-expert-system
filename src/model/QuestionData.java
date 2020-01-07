package model;

public class QuestionData {
    private int index;
    private double positive;
    private double negative;

    public QuestionData(int index, double positive, double negative) {
        this.index = index;
        this.positive = positive;
        this.negative = negative;
    }

    public int getIndex() {
        return index;
    }

    public double getPositive() {
        return positive;
    }

    public double getNegative() {
        return negative;
    }
}
