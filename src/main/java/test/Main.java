package test;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Программа выполняет команду ping для массива ip адресов, используя многопоточность,
 * выводит на экран результаты в мс для каждого ip и выбирает ip с наименьшим значением пинга
 */

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        GridPane gr_in = new GridPane();
        GridPane gridPane_Result = new GridPane();
        Button pingButton = new Button("Ping...");
        Label label = new Label("Results: ");
        label.setPadding(new Insets(10, 0, 10, 0));
        label.setWrapText(true);
        label.setMaxHeight(Double.MAX_VALUE);
        gr_in.getChildren().add(pingButton);
        gr_in.add(gridPane_Result, 0, 2);
        gr_in.add(label, 0, 1);
        gr_in.setPadding(new Insets(10));
        Scene scene = new Scene(gr_in, 300, 400);
        stage.setTitle("Ping IP");
        stage.setScene(scene);

        pingButton.setOnMouseClicked(MouseEvent -> {
            try {
                //очистить панель при повторном нажатии
                gridPane_Result.getChildren().clear();

                //список результатов запроса
                ConcurrentHashMap<String, Integer> listOfPingResult = Ping.getPingList();

                // отступ строк лэйблов
                int labelHigh = 3;

                for (Map.Entry<String, Integer> entry : listOfPingResult.entrySet()) {
                    String k = entry.getKey();
                    Integer v = entry.getValue();
                    //999 добавлялось в массив вместо строки, означая нет ответа
                    if (v == 999) {
                        gridPane_Result.add(new Label(k + ":" + " no response \n"), 0, labelHigh);
                    } else {
                        gridPane_Result.add(new Label(k + ":" + " " + v + " ms" + "\n"), 0, labelHigh);
                    }
                    labelHigh++;
                }
                //вывод на экран ip с минимальным пингом
                gridPane_Result.add(new Label((String) Ping.getMinValueOfPing(listOfPingResult).get()), 0, labelHigh);

            } catch (InterruptedException e) {
                e.getMessage();
            }
        });
        stage.show();
    }
}

