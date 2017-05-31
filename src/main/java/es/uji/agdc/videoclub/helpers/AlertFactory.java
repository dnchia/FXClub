package es.uji.agdc.videoclub.helpers;

import javafx.scene.control.Alert;

/**
 * Created by daniel on 10/12/16.
 */
public class AlertFactory {

    public static Alert create_voidUsername_Alert() {
        Alert voidUsernameAlert = new Alert(Alert.AlertType.ERROR);
        voidUsernameAlert.setTitle("Usuario vacío");
        voidUsernameAlert.setHeaderText("No se ha introducido ningún nombre de usuario");
        return voidUsernameAlert;
    }

    public static Alert create_voidPassword_Alert() {
        Alert voidPasswordAlert = new Alert(Alert.AlertType.ERROR);
        voidPasswordAlert.setTitle("Contraseña vacía");
        voidPasswordAlert.setHeaderText("No se ha introducido ninguna contraseña");
        return voidPasswordAlert;
    }

    public static Alert create_incorrectLogin_Alert() {
        Alert incorrectLogin = new Alert(Alert.AlertType.ERROR);
        incorrectLogin.setTitle("Autentificación fallida");
        incorrectLogin.setHeaderText("Se ha introducido un nombre de usuario inexistente o una contraseña incorrecta.");
        return incorrectLogin;
    }
}
