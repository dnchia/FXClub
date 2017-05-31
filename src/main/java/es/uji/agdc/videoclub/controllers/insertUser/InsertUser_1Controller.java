package es.uji.agdc.videoclub.controllers.insertUser;

import es.uji.agdc.videoclub.controllers.Controller;
import es.uji.agdc.videoclub.controllers.Form;
import es.uji.agdc.videoclub.controllers.RootController;
import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

/**
 * Created by daniel on 4/01/17.
 */
public class InsertUser_1Controller extends Controller implements Form {

    @FXML
    private TextField userDNI_textField;
    @FXML
    private TextField user_Name_textField;
    @FXML
    private TextField userAddress_textField;

    private boolean valid_dni = false;
    private boolean valid_name = false;
    private boolean valid_address = false;

    private UserService userService = Services.getUserService();
    private InsertUserController rootController = null;

    @FXML
    public void checkDNI_TextField() {
        if (checkDNI(userDNI_textField.getText())) {
            userDNI_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_dni = true;
        }

        else {
            userDNI_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_dni = false;
        }
        rootController.updateFormState(allFieldsValid(), 1);
        rootController.finishedForm();
    }

    private boolean checkDNI(String dni) {
        if (dni.length() == 9) {
            String DNI_PATTERN = "(([X-Z]{1})([-]?)(\\d{7})([-]?)([A-Z]{1}))|((\\d{8})([-]?)([A-Z]{1}))";
            Pattern p = Pattern.compile(DNI_PATTERN);

            if (rootController.isEditing()) {
                User loggedUser = ApplicationStateData.getLoggedUser();

                if (loggedUser.isAdmin()) {
                    User user = rootController.getUserToEdit();
                    return p.matcher(dni).matches() && (!userService.findBy(UserQueryTypeSingle.DNI, dni).isPresent() || dni.equals(user.getDni()));
                }
                else
                    return p.matcher(dni).matches() && !userService.findBy(UserQueryTypeSingle.DNI, dni).isPresent() || dni.equals(loggedUser.getDni());

            }
            else {
                return p.matcher(dni).matches() && !userService.findBy(UserQueryTypeSingle.DNI, dni).isPresent();
            }
        }

        return false;
    }

    @FXML
    public void checkName_TextField() {
        if (checkName(user_Name_textField.getText())) {
            user_Name_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_name = true;
        }
        else {
            user_Name_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_name = false;
        }
        rootController.updateFormState(allFieldsValid(), 1);
        rootController.finishedForm();
    }

    private boolean checkName(String name) {
        if (name.length() > 2)
            return true;

        return false;
    }

    @FXML
    public void checkAddress_TextField() {
        if (checkAddress(userAddress_textField.getText())) {
            userAddress_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_address = true;
        }
        else {
            userAddress_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_address = false;
        }
        rootController.updateFormState(allFieldsValid(), 1);
        rootController.finishedForm();
    }

    private boolean checkAddress(String address) {
        if (address.length() >= 10 && address.length() <= 100)
            return true;

        return false;
    }

    public boolean allFieldsValid() {
        return valid_dni && valid_name && valid_address;
    }

    @Override
    public String[] getAllData() {
        return new String[] {userDNI_textField.getText(), user_Name_textField.getText(), userAddress_textField.getText()};
    }

    @Override
    public void setAllData(String[] data) {
        userDNI_textField.setText(data[0]);
        checkDNI_TextField();

        user_Name_textField.setText(data[1]);
        checkName_TextField();

        userAddress_textField.setText(data[2]);
        checkAddress_TextField();
    }

    public void setRootController(RootController rootController) {
        this.rootController = (InsertUserController) rootController;
    }
}
