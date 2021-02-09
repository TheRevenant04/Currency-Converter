package Code;

import javafx.application.Application;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * This class is the entry point of the application.
 * The JavaFX stage is configured here.
 * The event handlers are specified in this class.
 *
 * @author Pritesh Parmar
 */

public class CurrencyConverter extends Application {

    /*******************************************************************************************************************
     * Private Methods
     ******************************************************************************************************************/

    /**
     * Sets 'stage' properties.
     * @param stage The stage object represents the primary window of the application
     * @param userInterface The userInterface object which contains all the application controls.
     */
    private void configureStage(Stage stage, UserInterface userInterface) {
        stage.setTitle("Currency Converter");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/Resources/Images/logo.png"));
        Scene scene = new Scene(userInterface.getMainWindow(),700, 280);
        scene.getStylesheets().add("/Resources/css/styles.css");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * All event handlers used in the project are specified here.
     * @param userInterface The userInterface object contains all the application controls and their methods.
     */
    private void configureEventHandlers(UserInterface userInterface) {
        userInterface.getFromSelect().setOnAction(event -> {
            try {
                userInterface.handleFromSelection(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        userInterface.getToSelect().setOnAction(event -> {
            try {
                userInterface.handleToSelection(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        UnaryOperator<TextFormatter.Change> intFilter = changeInput -> userInterface.restrictInput(changeInput);
        TextFormatter<String> input = new TextFormatter<String>(intFilter);
        userInterface.getFromTextField().setTextFormatter(input);
        userInterface.getFromTextField().textProperty().addListener((observable, oldVal, newVal) -> userInterface.handleTextFieldChange(observable, oldVal, newVal));
        userInterface.getFromSelect().setOnKeyPressed(event -> {
            try {
                userInterface.handleKeyFromSelectPress(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        userInterface.getToSelect().setOnKeyPressed(event -> {
            try {
                userInterface.handleKeyToSelectPress(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**********************************************************************************************
     * Public methods
     **********************************************************************************************/

    /**
     * The main entry point for the application
     * @param primaryStage The Stage object that represents the primary window.
     * @throws IOException Throws an exception if the userInterface class throws an Exception.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        UserInterface userInterface = new UserInterface();
        configureStage(primaryStage, userInterface);
        configureEventHandlers(userInterface);
    }

    /**
     * Calls the launch() of Application class.
     * @param args Command Line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
