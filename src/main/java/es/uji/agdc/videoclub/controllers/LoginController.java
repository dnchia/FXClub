package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.helpers.AlertFactory;
import es.uji.agdc.videoclub.helpers.ApplicationState;
import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.AuthenticationService;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.utils.Result;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by daniel on 1/12/16.
 */

@Component
public class LoginController extends Controller {

    // View elements
    @FXML
    TextField login_UsernameTextfield;
    @FXML
    PasswordField login_PasswordField;
    @FXML
    Button loginButton;

    // Utility objects
    private AuthenticationService authService;
    private UserService userService;
    private User loggedUser;


    /** Constructors */

    @Autowired
    public LoginController(AuthenticationService service, UserService userService) {
        this.authService = service;
        this.userService = userService;
    }

    // Method used by JavaFX to initialize the FXML elements
    @FXML
    private void initialize() {

    }

    /** Controller actions */

    @FXML
    public void handleLoginAction() {
        String introducedUsername = getIntroducedUsername();
        String introducedPassword = getIntroducedPassword();
        boolean voidUsername = introducedUsername.trim().equals("");
        boolean voidPassword = introducedPassword.trim().equals("");

        if (voidUsername) {
            Alert voidUsername_Alert = AlertFactory.create_voidUsername_Alert();
            login_UsernameTextfield.clear();
            voidUsername_Alert.showAndWait();
        }

        else if (voidPassword) {
            Alert voidPassword_Alert = AlertFactory.create_voidPassword_Alert();
            login_PasswordField.clear();
            voidPassword_Alert.showAndWait();
        }
        else {
            Result loginResult = authService.auth(introducedUsername, introducedPassword);

            if (loginResult.isOk()) {
                loggedUser = userService.findBy(UserQueryTypeSingle.USERNAME, introducedUsername).get();
                successfulLogin_Process();
            }

            else
                unsuccessfulLogin_Process();
        }
    }

    private String getIntroducedUsername() {
        return login_UsernameTextfield.getText();
    }

    private String getIntroducedPassword() {
        return login_PasswordField.getText();
    }


    private void successfulLogin_Process() {
        ApplicationStateData.setLoggedUser(loggedUser);
        ApplicationStateData.setNewState(ApplicationState.MAIN_SECTION);
    }

    private void unsuccessfulLogin_Process() {
        Alert incorrectLogin_Alert = AlertFactory.create_incorrectLogin_Alert();
        login_PasswordField.clear();
        incorrectLogin_Alert.showAndWait();
    }
}
