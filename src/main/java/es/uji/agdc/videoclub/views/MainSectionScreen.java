package es.uji.agdc.videoclub.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by daniel on 9/12/16.
 */

@Component
public class MainSectionScreen extends AbstractScreen {

    // MainSection screen elements
    private BorderPane main_section;

    // MainSection screen configuration
    private String title = "Aplicación videoclub - Sección principal";


    /** Window functionality */

    @Override
    public void showScreen() {
        buildMainSection();
        showScene();
    }

    private void buildMainSection() {
        try {
            this.main_section = loadMainSection();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BorderPane loadMainSection() throws IOException {
        //return super.loadScreen("/views/app/mainSection/main_section.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/app/mainSection/main_section.fxml"));
        return (BorderPane) loader.load();
    }

    private void showScene() {
        Scene scene = new Scene(main_section);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
    }
}
