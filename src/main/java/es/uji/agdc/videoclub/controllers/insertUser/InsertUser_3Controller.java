package es.uji.agdc.videoclub.controllers.insertUser;

import es.uji.agdc.videoclub.controllers.Controller;
import es.uji.agdc.videoclub.controllers.Form;
import es.uji.agdc.videoclub.controllers.RootController;
import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * Created by daniel on 4/01/17.
 */
public class InsertUser_3Controller extends Controller implements Form {

    @FXML
    private GridPane gridPane;
    @FXML
    private CheckBox showPassword;
    @FXML
    private TextField user_username_textField;
    @FXML
    private TextField userPassword_passwordField;
    @FXML
    private DatePicker userLastPaymentDate_datePicker;
    @FXML
    private Label date_label;

    private UserService userService = Services.getUserService();

    private boolean valid_username = false;
    private boolean valid_password = false;
    private boolean valid_lastPaymentDate = true;

    private InsertUserController rootController = null;

    private String password = "";


    @FXML
    public void initialize() {
        userLastPaymentDate_datePicker.valueProperty().addListener(e -> checkDate_datePicker());
        User loggedUser = ApplicationStateData.getLoggedUser();
        if (loggedUser.isMember()) {
            gridPane.getChildren().remove(userLastPaymentDate_datePicker);
            gridPane.getChildren().remove(date_label);
        }
    }

    @FXML
    public void showPassword() {
        password = userPassword_passwordField.getText();
        gridPane.getChildren().remove(userPassword_passwordField);

        if (showPassword.isSelected()) {
            TextField newPasswordField = new TextField();
            newPasswordField.setText(password);
            newPasswordField.setId("userPassword_passwordField");
            newPasswordField.setOnKeyReleased(e -> checkPassword_textField());
            gridPane.add(newPasswordField, 1, 1);
            userPassword_passwordField = newPasswordField;
        }

        else {
            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setText(password);
            newPasswordField.setId("userPassword_passwordField");
            newPasswordField.setOnKeyReleased(e -> checkPassword_textField());
            gridPane.add(newPasswordField, 1, 1);
            userPassword_passwordField = newPasswordField;
        }

        checkPassword_textField();
    }

    @FXML
    public void checkUsername_textField() {
        checkDate_datePicker();
        checkPassword_textField();
        if (checkUsername(user_username_textField.getText())) {
            user_username_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_username = true;
        }
        else {
            user_username_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_username = false;
        }
        rootController.updateFormState(allFieldsValid(), 3);
        rootController.finishedForm();
    }

    private boolean checkUsername(String username) {
        if (rootController.isEditing()) {
            User loggedUser = ApplicationStateData.getLoggedUser();

            if (loggedUser.isAdmin()) {
                User user = rootController.getUserToEdit();
                return (username.length() >= 4 && username.length() <= 15) && (!userService.findBy(UserQueryTypeSingle.USERNAME, username).isPresent()) || username.equals(user.getUsername());
            }
            else
                return (username.length() >= 4 && username.length() <= 15) && !userService.findBy(UserQueryTypeSingle.USERNAME, username).isPresent() || username.equals(loggedUser.getUsername());
        }
        else
            return (username.length() >= 4 && username.length() <= 15) && !userService.findBy(UserQueryTypeSingle.USERNAME, username).isPresent();

    }

    @FXML
    public void checkPassword_textField() {
        checkDate_datePicker();
        password = userPassword_passwordField.getText();

        if (checkPassword(password)) {
            userPassword_passwordField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_password = true;
        }
        else {
            userPassword_passwordField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_password = false;
        }
        rootController.updateFormState(allFieldsValid(), 3);
        rootController.finishedForm();
    }

    private boolean checkPassword(String password) {
        if (password.length() < 8 || password.length() > 20)
            return false;

        return true;
    }

    @FXML
    public void checkDate_datePicker() {
        if (userLastPaymentDate_datePicker.getValue() != null && LocalDate.now().isAfter(userLastPaymentDate_datePicker.getValue()) && userLastPaymentDate_datePicker.getValue().getYear() > 1950) {
            if (checkDate(userLastPaymentDate_datePicker.getValue().toString())) {
                userLastPaymentDate_datePicker.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
                valid_lastPaymentDate = true;
            }
            else {
                userLastPaymentDate_datePicker.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                valid_lastPaymentDate = false;
            }
        }
        else if (userLastPaymentDate_datePicker.getValue() == null) {
            userLastPaymentDate_datePicker.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_lastPaymentDate = true;
        }

        else {
            userLastPaymentDate_datePicker.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_lastPaymentDate = false;
        }

        rootController.updateFormState(allFieldsValid(), 3);
        rootController.finishedForm();
    }

    private boolean checkDate(String date) {
        String DATE_PATTERN = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";

        Pattern p = Pattern.compile(DATE_PATTERN);

        return p.matcher(date).matches() || date.equals("");
    }

    @Override
    public boolean allFieldsValid() {
        return valid_username && valid_password && valid_lastPaymentDate;
    }

    @Override
    public String[] getAllData() {
        if (userLastPaymentDate_datePicker.getValue() != null)
            return new String[] {user_username_textField.getText(), userPassword_passwordField.getText(), userLastPaymentDate_datePicker.getValue().toString()};

        else
            return new String[] {user_username_textField.getText(), userPassword_passwordField.getText(), ""};
    }

    @Override
    public void setAllData(String[] data) {
        user_username_textField.setText(data[0]);
        checkUsername_textField();

        userPassword_passwordField.setText(data[1]);
        checkPassword_textField();

        userLastPaymentDate_datePicker.setPromptText(data[2]);
        checkDate_datePicker();
    }

    @Override
    public void setRootController(RootController rootController) {
        this.rootController = (InsertUserController) rootController;
    }
}