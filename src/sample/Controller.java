package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Data;
import model.Item;
import model.QuestionData;

import javax.swing.*;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Controller {
    private static final double MIN = -5;
    private static final double AVG = 0;
    private static final double MAX = 5;

    private Button button;

    private TableView tableView;
    private ListView questions;
    private ListView questionsHistory;
    private TextField answerInput;
    private Label currentQuestionLabel;

    private Data data;

    private boolean isStarted = false;
    private int currentQuestion;
    private Stack<Integer> questionNumbers;


    public Controller(Stage stage) {
        Scene scene = stage.getScene();

        button = (Button) scene.lookup("#submit");
        currentQuestionLabel = (Label) scene.lookup("#currentQuestion");
        answerInput = (TextField) scene.lookup("#answerInput");
        tableView = (TableView) scene.lookup("#itemTable");
        questions = (ListView) scene.lookup("#questions");
        questionsHistory = (ListView) scene.lookup("#questionsHistory");

        MenuBar menuBar = (MenuBar) scene.lookup("#menuBar");
        ObservableList<Menu> menus = menuBar.getMenus();

        // open
        menus.get(0).getItems().get(0).setOnAction(event -> {
            File file = new FileChooser().showOpenDialog(null);
            try {
                data = Parser.parse(file);
                isStarted = false;
                menus.get(1).getItems().get(0).setText("Start");

                //questions
                ObservableList<String> questions = FXCollections.observableArrayList(data.getQuestions());
                this.questions.setItems(questions);

                //items
                TableColumn p = (TableColumn) tableView.getColumns().get(0);
                p.setCellValueFactory(new PropertyValueFactory<>("apost"));

                TableColumn item = (TableColumn) tableView.getColumns().get(1);
                item.setCellValueFactory(new PropertyValueFactory<>("name"));

                ObservableList<Item> items = FXCollections.observableArrayList(data.getItems());
                tableView.setItems(items);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Invalid data",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // start
        menus.get(1).getItems().get(0).setOnAction(event -> {
            isStarted = !isStarted;
            if (isStarted) {
                start();
                menus.get(1).getItems().get(0).setText("Stop");
                data.getItems().forEach(item -> {
                    item.setApost(item.getInit());
                    item.setpApr(item.getInit());
                });
            } else {
                menus.get(1).getItems().get(0).setText("Start");
                currentQuestionLabel.setText("Not started yet");
            }
        });

        button.setOnMouseClicked(event -> {
            if (isStarted && !answerInput.getText().isEmpty()) {
                int answer = Integer.parseInt(answerInput.getText());
                if (answer >= -5 && answer <= 5) {
                    calculate(answer);
                    questionsHistory.getItems().add(String.format(
                            "Answer: %d. Question: %s",
                            answer,
                            data.getQuestions().get(currentQuestion - 1)));
                    next();
                }
            }
        });
    }

    private void calculate(int answer) {
        List<Item> items = data.getItems();
        if (answer < AVG) {
            for (Item item : items) {
                if (item.getQuestionData().containsKey(currentQuestion)) {
                    QuestionData questionData = item.getQuestionData().get(currentQuestion);

                    double tmp = 1 - questionData.getPositive();
                    double pApos = (tmp * item.getApr()) / (tmp * item.getApr() + (1 - questionData.getNegative()) * (1 - item.getApr()));
                    item.setApost(pApos);

                    double diff = answer - MIN;
                    double apr = pApos + (diff * item.getApr() - pApos) / (AVG - MIN);
                    item.setpApr(apr);
                }
            }
        } else {
            for (Item item : items) {
                if (item.getQuestionData().containsKey(currentQuestion)) {
                    QuestionData questionData = item.getQuestionData().get(currentQuestion);

                    double tmp = questionData.getPositive() * item.getApr();
                    double pApos = (tmp) / (tmp + questionData.getNegative() * (1 - item.getApr()));
                    item.setApost(pApos);

                    double apr = item.getApr() + ((answer - AVG) * pApos - item.getApr()) / (MAX - AVG);
                    item.setpApr(apr);
                }
            }
        }
        tableView.refresh();
    }

    private void next() {
        if (questionNumbers.empty()) {
            isStarted = false;
            currentQuestionLabel.setText("The end");
        } else {
            currentQuestion = questionNumbers.pop();
            currentQuestionLabel.setText(data.getQuestions().get(currentQuestion - 1));
        }
    }

    private void start() {
        questionNumbers = new Stack<>();
        for (int i = 1; i <= data.getQuestions().size(); i++) {
            questionNumbers.push(i);
        }
        Collections.shuffle(questionNumbers);
        next();
    }

}
