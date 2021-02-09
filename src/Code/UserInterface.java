package Code;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

/**
 * Contains the UI components of the application and the methods to control their behaviour.
 */
public class UserInterface {

    /************************************************************************************************************
     * Instance Variables
     ************************************************************************************************************/
    private GridPane mainWindow;
    private Alert messageDialog;
    private Label fromLabel;
    private Label toLabel;
    private ComboBox fromSelect;
    private ComboBox toSelect;
    private TextField fromTextField;
    private TextField toTextField;
    private String currFromSelect;
    private String currToSelect;
    private double conversionRate;
    private boolean currencyChange;

    /**
     * Default constructor
     * All display controls are initialized by creating their objects.
     * All fields with primitive types are initialized to their default values.
     * @throws IOException Handles the configureControls().
     */
    public UserInterface() throws IOException {
        mainWindow = new GridPane();
        messageDialog = new Alert(Alert.AlertType.ERROR,"", ButtonType.OK);
        fromLabel = new Label();
        toLabel = new Label();
        fromSelect = new ComboBox();
        toSelect = new ComboBox();
        fromTextField = new TextField();
        toTextField = new TextField();
        currFromSelect = "select";
        currToSelect = "select";
        currencyChange = true;
        setMainWindowProperties();
        configureControls();
        addControls();
    }

    /**************************************************************************************************************
     * Private Methods
     **************************************************************************************************************/

    /**
     * Adds the controls to the GridPane.
     */
    private void addControls() {
        mainWindow.add(fromLabel, 0, 1);
        mainWindow.add(fromSelect, 1, 1);
        mainWindow.add(fromTextField, 2, 1);
        mainWindow.add(toLabel, 0, 2);
        mainWindow.add(toSelect, 1, 2);
        mainWindow.add(toTextField, 2, 2);
    }

    /**
     * Computes the user provided amount to the target currency.
     * It multiplies the user given amount by the conversion rate.
     * The conversion rate is obtained from the 'getRate()' of the Currencies.java class.
     * @return The converted amount of the target currency.
     * @throws IOException Handles Currencies class methods and showMessageDialog();
     */
    private BigDecimal computeConversion() throws IOException{
        Currencies currency = new Currencies();
        String selectedFrom = fromSelect.getValue().toString();
        selectedFrom = currency.getNameToId(selectedFrom);
        String selectedTo = toSelect.getValue().toString();
        selectedTo = currency.getNameToId(selectedTo);
        Currency fromCurrency = Currency.getInstance(selectedFrom);
        Currency toCurrency = Currency.getInstance(selectedTo);
        if (!currFromSelect.equals(selectedFrom) || !currToSelect.equals(selectedTo)) {
            currFromSelect = selectedFrom;
            currToSelect = selectedTo;
            conversionRate = currency.getRate(fromCurrency, toCurrency);
            if (conversionRate == -1) {
                showMessageDialog();
                return BigDecimal.valueOf(0);
            }
        }
        BigInteger amount = new BigInteger(fromTextField.getText());
        BigDecimal finalAmount = currency.convert(amount, conversionRate);
        return finalAmount;
    }

    /**
     * Initializes the Combo Boxes to their default state.
     * @param comboBox Combo Box to be configured.
     * @throws IOException Handles Currencies class constructor.
     */
    private void configureComboBoxes(ComboBox comboBox) throws IOException {
        int noOfItems = comboBox.getItems().size();
        if(noOfItems == 0) {
            Currencies currency = new Currencies();
            comboBox.getItems().addAll(currency.getCurrencyNames());
        }
        comboBox.setValue("Select");
    }

    /**
     * Initializes the controls of the application to their default state.
     * @throws IOException Handles configureComboBoxes().
     */
    private void configureControls() throws IOException {
        fromLabel.setText("FROM");
        toLabel.setText("TO");
        configureComboBoxes(fromSelect);
        configureComboBoxes(toSelect);
        fromTextField.setVisible(false);
        toTextField.setEditable(false);
        toTextField.setVisible(false);
    }

    /**
     * Configures the message box to prompt errors to the user.
     * In this case, it prompts the user when the application cannot connect to the server.
     */
    private void configureMessageDialog() {
        messageDialog = new Alert(Alert.AlertType.ERROR,"", ButtonType.OK);
        Stage stage = (Stage)messageDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(UserInterface.class.getResourceAsStream("/Resources/Images/logo.png")));
        DialogPane dialogPane = messageDialog.getDialogPane();
        dialogPane.getStylesheets().add("/Resources/css/styles.css");
        messageDialog.setHeaderText("Currency Converter is unable to connect to the server. Please check your internet connection.");
        messageDialog.setTitle("Currency Converter");
        messageDialog.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
    }

    /**
     * Resets the application controls to their defaults.
     * @throws IOException Handles configureComboBoxes().
     */
    private void resetDisplay() throws IOException {
        configureComboBoxes(fromSelect);
        fromTextField.setText("");
        fromTextField.setVisible(false);
        configureComboBoxes(toSelect);
        toTextField.setText("");
        toTextField.setVisible(false);
    }

    /**
     * Sets the display properties of the user interface.
     */
    private void setMainWindowProperties() {
        mainWindow.setAlignment(Pos.TOP_CENTER);
        mainWindow.setHgap(50);
        mainWindow.setVgap(50);
        mainWindow.setPadding(new Insets(25, 25, 25, 25));
    }

    /*******************************************************************************************************************
     * Public Methods
     *******************************************************************************************************************/

    /**
     * @return Returns the combo box used for selecting base currencies.
     */
    public ComboBox getFromSelect() {
        return fromSelect;
    }

    /**
     * @return Returns the text field used for taking user input, i.e. amount.
     */
    public TextField getFromTextField() {
        return fromTextField;
    }

    /**
     * @return Returns the Grid Pane.
     */
    public GridPane getMainWindow() {
        return mainWindow;
    }

    /**
     * @return Returns the combo box used for selecting the target currency.
     */
    public ComboBox getToSelect() {
        return toSelect;
    }

    /**
     * Un-hides the text field used for taking user input, i.e. the amount.
     * Invoked by an Action event on the combo box used for selecting the base currency.
     * @param event An event that detects a change of state.
     * @exception IOException
     */
    public void handleFromSelection(Event event) throws IOException{
        fromTextField.setText("");
        toTextField.setText("");
        fromTextField.setVisible(true);
    }

    /**
     * Transfers control to the first item starting with the entered key in the From combo box.
     * It also sets the selected item to the currently accessed item in the combo box.
     * @param event An event that detects a key press action.
     * @throws IOException
     */
    public void handleKeyFromSelectPress(Event event) throws IOException {
        KeyCode keyCode = ((KeyEvent)event).getCode();
        if (keyCode.isLetterKey()) {
            char key = keyCode.getName().charAt(0);
            SingleSelectionModel<String> fromSelectModel = fromSelect.getSelectionModel();
            fromSelectModel.select(0);
            int options = fromSelect.getItems().size();
            for (int i = 0; i < options; i++) {
                if(fromSelectModel.getSelectedItem().charAt(0) == key) {
                    fromSelectModel.select(i);
                    ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) fromSelect.getSkin();
                    ListView<?> list = (ListView<?>) skin.getPopupContent();
                    list.scrollTo(i);
                    currFromSelect = fromSelect.getValue().toString();
                    return;
                }
                else
                    fromSelectModel.selectNext();
            }
        }
    }

    /**
     * Transfers control to the first item starting with the entered key in the To combo box.
     * It also sets the selected item to the currently accessed item in the combo box.
     * @param event An event that detects a key press action.
     * @throws IOException
     */
    public void handleKeyToSelectPress(Event event) throws IOException {
        KeyCode keyCode = ((KeyEvent)event).getCode();
        if (keyCode.isLetterKey()) {
            char key = keyCode.getName().charAt(0);
            SingleSelectionModel<String> toSelectModel = toSelect.getSelectionModel();
            toSelectModel.select(0);
            int options = toSelect.getItems().size();
            for (int i = 0; i < options; i++) {
                if(toSelectModel.getSelectedItem().charAt(0) == key) {
                    toSelectModel.select(i);
                    ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) toSelect.getSkin();
                    ListView<?> list = (ListView<?>) skin.getPopupContent();
                    list.scrollTo(i);
                    currToSelect = toSelect.getValue().toString();
                    return;
                }
                else
                    toSelectModel.selectNext();
            }
        }
    }

    /**
     * Dynamically invokes the conversion method on user input to update the target currency text field.
     * Invoked when the user enters values in the text field.
     * @param observable Generates a change event.
     * @param oldValue The old value in the text field.
     * @param newValue The new value in the text field.
     */
    public void handleTextFieldChange(ObservableValue observable, String oldValue, String newValue) {
        String selectedFrom =(String)fromSelect.getValue();
        String selectedTo =(String)toSelect.getValue();
        try {
            if (selectedFrom != "Select" && selectedTo != "Select") {
                toTextField.setVisible(true);
                toTextField.setText("");
                if (!(fromTextField.getText().equals(""))) {
                    BigDecimal finalAmount = computeConversion();
                    toTextField.setText(finalAmount + "");
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Un-hides the text field used for displaying the converted amount.
     * The method is invoked when a value is selected from the target currency combo box.
     * @param event An event that detects change when a value is selected in the target currency combo box.
     * @exception IOException
     */
    public void handleToSelection(Event event) throws IOException{
        String selected = fromSelect.getValue().toString();
        if (fromTextField.isVisible() && selected != "Select") {
            fromTextField.setText("");
            toTextField.setText("");
            toTextField.setVisible(true);
        }
    }

    /**
     * Restricts user input to only whole numbers in the amount textfield.
     * @param changeInput The state representing a change in the text field.
     * @return Returns the changed input if valid or a null if invalid text.
     */
    public TextFormatter.Change restrictInput(TextFormatter.Change changeInput) {
        String input = changeInput.getText();
        if (input.matches("[0-9]*")) {
            return changeInput;
        }
        return null;
    }

    /**
     * Displays the Alert window with the desired message.
     * @throws IOException Handles resetDisplay().
     */
    public void showMessageDialog() throws IOException {
        configureMessageDialog();
        messageDialog.showAndWait();
        resetDisplay();
    }
}
