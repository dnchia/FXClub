package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.MovieQueryTypeMultiple;
import es.uji.agdc.videoclub.services.MovieService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by daniel on 3/01/17.
 */

@Component
public class MainSectionController extends Controller {

    @FXML
    TabPane tabs;
    @FXML
    private TextField searchBar_textField;
    @FXML
    private Button searchBar_button;
    @FXML
    private Label search_label;
    @FXML
    private ChoiceBox<String> searchBy_ChoiceBox;
    @FXML
    private VBox searchResult_VBox;

    private MovieService movieService = Services.getMovieService();


    @FXML
    public void initialize() {
        searchBy_ChoiceBox.getItems().add("Título");
        searchBy_ChoiceBox.getItems().add("Título VO");
        searchBy_ChoiceBox.getItems().add("Año");
        searchBy_ChoiceBox.getItems().add("Actor");
        searchBy_ChoiceBox.getItems().add("Director");
        searchBy_ChoiceBox.getItems().add("Género");
        searchBy_ChoiceBox.getItems().add("Por cualquiera");

        searchBar_button.setDisable(true);

        User loggedUser = ApplicationStateData.getLoggedUser();
        int ADMIN_TAB_POSITION = 1;

        if (!loggedUser.isAdmin()) {
            tabs.getTabs().remove(ADMIN_TAB_POSITION);
        }
    }

    public void searchMovies() {
        if (!searchBy_ChoiceBox.getValue().equals("")) {

            switch (searchBy_ChoiceBox.getValue()) {
                case "Título":
                    searchBy(MovieQueryTypeMultiple.TITLE);
                    break;

                case "Título VO":
                    searchBy(MovieQueryTypeMultiple.TITLE_OV);
                    break;

                case "Año":
                    searchBy(MovieQueryTypeMultiple.YEAR);
                    break;

                case "Actor":
                    searchBy(MovieQueryTypeMultiple.ACTORS);
                    break;

                case "Director":
                    searchBy(MovieQueryTypeMultiple.DIRECTORS);
                    break;

                case "Género":
                    searchBy(MovieQueryTypeMultiple.GENRES);
                    break;

                case "Por cualquiera":
                    searchBy(MovieQueryTypeMultiple.ALL);
                    break;
            }

            search_label.setText("Se ha buscado: " + searchBar_textField.getText());
        }
    }

    public void checkChoiceBox() {
        if (!searchBy_ChoiceBox.getValue().equals(""))
            searchBar_button.setDisable(false);
    }

    private void searchBy(MovieQueryTypeMultiple element) {
        String searchedBy = searchBar_textField.getText();

        searchResult_VBox.getChildren().clear();

        Iterator<Movie> movies = movieService.findAllBy(element, searchedBy).iterator();

        while (movies.hasNext()) {
            Movie movie = movies.next();
            if (movie.getActualAvailableCopies() > 0)
                searchResult_VBox.getChildren().add(generateSearchContainer(movie));
        }
    }

    private HBox generateSearchContainer(Movie movie) {
        FXMLLoader loader = new FXMLLoader();

        try {
            loader.setLocation(getClass().getResource("/views/app/mainSection/search_result.fxml"));
            HBox resultContainer = (HBox) loader.load();
            resultContainer.setStyle("-fx-border-width: 1px; -fx-border-color: black");

            ResultController controller = loader.getController();
            controller.setMovie(movie);
            controller.initState();

            return resultContainer;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
