package net.ptnkjke.gui.main.panes.methodpane;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * Created by Lopatin on 05.07.2014.
 */
public class MethodUtils {

    public static GridPane loadView(MethodModel model) {
        GridPane gridPane = null;

        FXMLLoader fxmlLoader = null;

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        try {
            gridPane = (GridPane) fxmlLoader.load(MethodUtils.class.getResource("/net/ptnkjke/gui/main/panes/methodpane/View.fxml").openStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        gridPane.setMaxWidth(10000);
        gridPane.setMaxHeight(10000);

        MethodController controller = fxmlLoader.getController();
        controller.setMethod(model.getMethodBCEL());
        return gridPane;
    }
}