package es.uji.agdc.videoclub.views;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by daniel on 9/12/16.
 */

@Component
public class AuthScreen extends AbstractScreen {

    // Auth screen elements
    private BorderPane root_Auth;

    // Auth screen configuration
    private String title = "Aplicación videoclub - Autentificación";


    /** Window functionality */

    @Override
    public void showScreen() {
        buildRootLayout();
        showScene();
    }

    private void buildRootLayout() {
        try {
            root_Auth = (BorderPane) super.loadScreen("/views/app/root.fxml");
            BorderPane login = (BorderPane) loadLoginLayout();
            root_Auth.setCenter(login);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pane loadLoginLayout() {
        try {
            Pane login = super.loadScreen("/views/app/auth/login.fxml");
            return login;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showScene() {
        Scene scene = new Scene(root_Auth);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /** Getters & setters */

    public String getTitle() {
        return new String(title);
    }
}
