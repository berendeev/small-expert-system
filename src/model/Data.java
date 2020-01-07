package model;

import java.util.List;

public class Data {
    private List<String> questions;
    private List<Item> items;

    public Data(List<String> questions, List<Item> items) {
        this.questions = questions;
        this.items = items;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public List<Item> getItems() {
        return items;
    }
}
