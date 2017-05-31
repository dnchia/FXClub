package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.VisualizationLink;
import es.uji.agdc.videoclub.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by daniel on 3/01/17.
 */

@Component
public class AdminSectionController extends Controller {

    private Stage nonPaymentsStage = null;
    private Stage newUserStage = null;
    private Stage userEditStage = null;
    private Stage movieInsertionStage = null;
    private Stage movieEditStage = null;
    private Stage listOfUsersStage = null;
    private Stage listOfMoviesStage = null;

    private String nonPaymentsSection = "/views/app/mainSection/adminOptions/users_with_non_payments.fxml";
    private String newUserSection = "/views/app/mainSection/adminOptions/insertUser/insert_user_root.fxml";
    private String userEditSection = "/views/app/mainSection/adminOptions/editUser/edit_user.fxml";
    private String movieInsertionSection = "/views/app/mainSection/adminOptions/insertMovie/insert_movie_root.fxml";
    private String movieEditSection = "/views/app/mainSection/adminOptions/editMovie/edit_movie.fxml";
    private String listOfUsersSection = "/views/app/mainSection/adminOptions/list_of_users.fxml";
    private String listOfMoviesSection = "/views/app/mainSection/adminOptions/list_of_movies.fxml";

    private VisualizationLinkService visualizationLinkService = Services.getVisualizationLinkService();


    private void loadSection(String resource, Stage stage, String title) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        BorderPane loadedSection;

        try {
            loadedSection = (BorderPane) loader.load();
            stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(loadedSection);
            stage.setScene(scene);

            Controller controller = (Controller) loader.getController();
            controller.setStage(stage);

            stage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showNonPaymentsSection() {
        loadSection(nonPaymentsSection, nonPaymentsStage, "Usuarios con impagos");
    }

    @FXML
    public void showNewUserSection() {
        loadSection(newUserSection, newUserStage, "Creación de un nuevo usuario");
    }

    @FXML
    public void showMovieInsertionSection() {
        loadSection(movieInsertionSection, movieInsertionStage, "Inserción de una película");
    }

    @FXML
    public void showListOfUsers() {
        loadSection(listOfUsersSection, listOfUsersStage, "Listado de usuarios");
    }

    @FXML
    public void showListOfMovies() {
        loadSection(listOfMoviesSection, listOfMoviesStage, "Listado de películas");
    }

    @FXML
    public void cleanOldRents() {
        VisualizationLinkService visualizationLinkService = Services.getVisualizationLinkService();

        visualizationLinkService.removeTimedOutLinks();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Devolución automática de películas");
        alert.setHeaderText("Se ha realizado la devolución de copias en alquileres antiguos de forma satisfactoria.");

        alert.showAndWait();
    }
}
