package sample;

import model.Data;
import model.Item;
import model.QuestionData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {

    public static Data parse(File file) throws Exception {
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     new FileInputStream(file), "windows-1251"))) {

            skipDescription(reader);
            List<String> questions = getQuestions(reader);
            List<String> itemsStrings = getItemsStrings(reader);

            Data data = makeData(questions, itemsStrings);
//            validate(data);
            return data;
        }
    }

    private static void validate(Data data) throws Exception {
        List<Item> items = data.getItems();

        for (Item item : items) {
            Map<Integer, QuestionData> questionData = item.getQuestionData();

            if (data.getQuestions().size() < questionData.size()) {
                throw new Exception("Out of bounds exception");
            }

            for (int i = 1; i <= questionData.size(); i++) {
                if (!questionData.containsKey(i)) {
                    questionData.put(i, new QuestionData(i, 0, 1));
                }
            }
        }

    }

    private static Data makeData(List<String> questions, List<String> itemsStrings) {
        List<Item> items = new ArrayList<>();

        for (String itemString : itemsStrings) {
            String[] parsedItemString = itemString.split(",");

            String name = parsedItemString[0];
            double init = Double.parseDouble(parsedItemString[1]);

            Map<Integer, QuestionData> map = new HashMap<>();
            for (int i = 2; i < parsedItemString.length; i += 3) {
                int index = Integer.parseInt(parsedItemString[i].trim());
                double positive = Double.parseDouble(parsedItemString[i + 1].trim());
                double negative = Double.parseDouble(parsedItemString[i + 2].trim());

                map.put(index, new QuestionData(index, positive, negative));
            }
            items.add(new Item(name, init, map));
        }

        return new Data(questions, items);
    }

    private static List<String> getItemsStrings(BufferedReader reader) throws IOException {
        return reader.lines().collect(Collectors.toList());
    }

    private static void skipDescription(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (!line.matches("[А-я]*:")) {
            line = reader.readLine();
        }
    }

    private static List<String> getQuestions(BufferedReader reader) throws IOException {
        boolean isQuestion = true;
        List<String> questions = new ArrayList<>();

        while (isQuestion) {
            String line = reader.readLine();
            if (line.equals("")) {
                isQuestion = false;
            } else {
                questions.add(line);
            }
        }
        return questions;
    }
}
