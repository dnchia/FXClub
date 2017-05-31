package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.validators.UserValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.LocalDate;

/**
 * Created by daniel on 15/12/16.
 */

public class PersonalDataScreenController extends Controller {

    @FXML
    private GridPane gridPane;
    @FXML
    private TextField dni_TextField;
    @FXML
    private TextField name_TextField;
    @FXML
    private TextField dir_TextField;
    @FXML
    private TextField phone_TextField;
    @FXML
    private TextField email_TextField;
    @FXML
    private DatePicker lastPayment_TextField;
    @FXML
    private TextField username_TextField;
    @FXML
    private TextField password_passwordField;
    @FXML
    private Button editData_Button;
    @FXML
    private CheckBox showPassword_checkbox;

    private Stage personalDataStage;

    private boolean editingData = false;
    private UserService userService = Services.getUserService();

    @FXML
    public void initialize() {
        changeEditableProperty_In_TextFields(false);

        DecimalFormat format = new DecimalFormat( "#.0" );
        phone_TextField.setTextFormatter(new TextFormatter<>(c ->
        {
            if (c.getControlNewText().isEmpty())
                return c;

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;

            else
                return c;
        }));
    }


    public void setUserData(User user) {
        dni_TextField.setText(user.getDni());
        name_TextField.setText(user.getName());
        dir_TextField.setText(user.getAddress());
        phone_TextField.setText(Integer.toString(user.getPhone()));
        email_TextField.setText(user.getEmail());
        LocalDate lastPayment = user.getLastPayment();
        if (lastPayment != null)
            lastPayment_TextField.setUserData(user.getLastPayment());

        lastPayment_TextField.setEditable(false);

        username_TextField.setText(user.getUsername());

        password_passwordField.setText(user.getPassword());
    }
    public void setStage(Stage dialogStage) {
        this.personalDataStage = dialogStage;
    }

    private void changeEditableProperty_In_TextFields(boolean newValue) {
        dni_TextField.setEditable(newValue);
        dni_TextField.setMouseTransparent(!newValue);
        dni_TextField.setFocusTraversable(newValue);

        name_TextField.setEditable(newValue);
        name_TextField.setMouseTransparent(!newValue);
        name_TextField.setFocusTraversable(newValue);

        dir_TextField.setEditable(newValue);
        dir_TextField.setMouseTransparent(!newValue);
        dir_TextField.setFocusTraversable(newValue);

        phone_TextField.setEditable(newValue);
        phone_TextField.setMouseTransparent(!newValue);
        phone_TextField.setFocusTraversable(newValue);

        email_TextField.setEditable(newValue);
        email_TextField.setMouseTransparent(!newValue);
        email_TextField.setFocusTraversable(newValue);

        lastPayment_TextField.setEditable(newValue);
        lastPayment_TextField.setMouseTransparent(!newValue);
        lastPayment_TextField.setFocusTraversable(newValue);

        username_TextField.setEditable(newValue);
        username_TextField.setMouseTransparent(!newValue);
        username_TextField.setFocusTraversable(newValue);

        password_passwordField.setEditable(newValue);
        password_passwordField.setMouseTransparent(!newValue);
        password_passwordField.setFocusTraversable(newValue);
    }

    @FXML
    public void editData() {
        if (editingData == false) {
            changeEditableProperty_In_TextFields(true);
            editData_Button.setText("Guardar cambios");
            editingData = true;
        }
        else {
            changeEditableProperty_In_TextFields(false);
            editData_Button.setText("Editar datos");
            editingData = false;

            UserValidator userValidator = new UserValidator();
            User editedUser = new User();

            editedUser.setDni(dni_TextField.getText());
            editedUser.setName(name_TextField.getText());
            editedUser.setAddress(dir_TextField.getText());
            editedUser.setPhone(Integer.parseInt(phone_TextField.getText()));
            editedUser.setEmail(email_TextField.getText());
            editedUser.setLastPayment(lastPayment_TextField.getValue());
            editedUser.setUsername(username_TextField.getText());
            editedUser.setPassword(password_passwordField.getText());

            Result validationResult = userValidator.validate(editedUser);

            if (validationResult.isOk())
                // TODO Check that this is ok
                userService.update(editedUser, editedUser.getId());

        }
    }

    @FXML
    public void closeWindow() {
        personalDataStage.close();
    }

    @FXML
    public void showPassword() {
        String introducedPassword = password_passwordField.getText();
        boolean editable = password_passwordField.editableProperty().get();
        boolean mouseTransparent = password_passwordField.mouseTransparentProperty().get();
        boolean focusTrasversable = password_passwordField.focusTraversableProperty().get();

        gridPane.getChildren().remove(password_passwordField);

        if (showPassword_checkbox.isSelected()) {
            TextField password = new TextField();
            password.setId("password_passwordField");
            password.setText(introducedPassword);
            password.setEditable(editable);
            password.setMouseTransparent(mouseTransparent);
            password.setFocusTraversable(focusTrasversable);
            gridPane.add(password, 1, 7);
            password_passwordField = password;
        }
        else {
            PasswordField password = new PasswordField();
            password.setId("password_passwordField");
            password.setText(introducedPassword);
            password.setEditable(editable);
            password.setMouseTransparent(mouseTransparent);
            password.setFocusTraversable(focusTrasversable);
            gridPane.add(password, 1, 7);
            password_passwordField = password;
        }
    }
}
