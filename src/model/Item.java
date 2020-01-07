package model;

import java.util.Map;

public class Item {
    private String name;
    private double init;
    private double apost;
    private double apr;
    private Map<Integer, QuestionData> questionData;

    public Item(String name, double init, Map<Integer, QuestionData> questionData) {
        this.name = name;
        this.init = init;
        apost = init;
        apr = init;
        this.questionData = questionData;
    }

    public double getApr() {
        return apr;
    }

    public void setpApr(double apr) {
        this.apr = apr;
    }

    public String getName() {
        return name;
    }

    public double getInit() {
        return init;
    }

    public double getApost() {
        return apost;
    }

    public void setApost(double apost) {
        this.apost = apost;
    }

    public Map<Integer, QuestionData> getQuestionData() {
        return questionData;
    }
}
