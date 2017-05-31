package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.controllers.insertUser.InsertUserController;
import es.uji.agdc.videoclub.helpers.ApplicationState;
import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by daniel on 10/12/16.
 */

@Component
public class ProfileSectionController extends Controller {

    // View components
    @FXML
    Label username_lb;
    @FXML
    Button personalData_button;
    @FXML
    Button rentedMovies_button;
    @FXML
    Button closeSession_button;

    private Stage personalDataStage = null;
    private Stage rentedMoviesStage = null;
    private Stage administrationPanelStage = null;
    private Stage listOfUsersStage = null;

    private User loggedUser;

    // Data & inner sections
    //private PersonalDataScreen personalData = new PersonalDataScreen();


    // Method used by JavaFX to initialize the FXML elements
    @FXML
    private void initialize() {
        loggedUser = ApplicationStateData.getLoggedUser();
        username_lb.setText(loggedUser.getUsername());
    }

    @FXML
    public void showPersonalData_Screen() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/app/mainSection/adminOptions/insertUser/insert_user_root.fxml"));
        BorderPane page;
        try {
            page = (BorderPane) loader.load();
            personalDataStage = new Stage();
            personalDataStage.setTitle("Datos personales");
            personalDataStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            personalDataStage.setScene(scene);

            InsertUserController controller = loader.getController();
            controller.editUser(loggedUser, "Datos de la cuenta");
            controller.setStage(personalDataStage);

            personalDataStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!username_lb.getText().equals(ApplicationStateData.getLoggedUser().getUsername()))
            username_lb.setText(ApplicationStateData.getLoggedUser().getUsername());
    }

    @FXML
    public void showRentedMovies_Screen() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/app/mainSection/profile/rented_movies.fxml"));
        BorderPane page;
        try {
            page = (BorderPane) loader.load();
            rentedMoviesStage = new Stage();
            rentedMoviesStage.setTitle("Películas alquiladas");
            rentedMoviesStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            rentedMoviesStage.setScene(scene);

            RentedMoviesController controller = loader.getController();
            controller.setStage(rentedMoviesStage);

            rentedMoviesStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void closeSession() {
        if (personalDataStage != null && personalDataStage.isShowing())
            personalDataStage.close();

        if (rentedMoviesStage != null && rentedMoviesStage.isShowing())
            rentedMoviesStage.close();

        if (administrationPanelStage != null && administrationPanelStage.isShowing())
            administrationPanelStage.close();

        if (listOfUsersStage != null && listOfUsersStage.isShowing())
            listOfUsersStage.close();

        ApplicationStateData.setLoggedUser(null);
        ApplicationStateData.setNewState(ApplicationState.LOGIN);
    }


}
