package Hclient;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class RoutesGUIApp extends Application {

    public static final ObservableList routeNames =
            FXCollections.observableArrayList();
    static String secureselect = null;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        FileInputStream fxmlstream = new FileInputStream("routegui.fxml");
        AnchorPane root = loader.load(fxmlstream);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Eve Online Route Finder");
        Button getRoutes = (Button) scene.lookup("#routeGetButton");
        ListView routeList = (ListView) scene.lookup("#routeTable");
        TextField otext = (TextField) scene.lookup("#oriTextBox");
        TextField atext = (TextField) scene.lookup("#destTextBox");
        ComboBox secure = (ComboBox) scene.lookup("#securitydrop");
        secure.setOnAction(actionEvent -> {
            StringBuilder secget = new StringBuilder();
            secget.setLength(0);
            secget.append(secure.getValue().toString());
            secureselect = secget.toString();
            System.out.print(secget.toString());
                });
        getRoutes.setOnAction(actionEvent -> {
            routeNames.clear();
            routeList.isEditable();
            routeList.getItems().clear();
            routeList.refresh();
            String a = otext.getText();
            String b = atext.getText();
            try {
                Reader.main(a,b,secureselect);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<String> table;
            table = Reader.sysnames;
            routeList.setItems(routeNames);
            routeNames.addAll(table);
            routeList.setCellFactory(ComboBoxListCell.forListView(routeNames));
        });
        stage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
