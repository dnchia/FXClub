package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.controllers.insertMovie.InsertMovieController;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.MovieQueryTypeSingle;
import es.uji.agdc.videoclub.services.MovieService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by daniel on 3/01/17.
 */
public class EditMovieController extends Controller {

    @FXML
    private MenuButton movieEditSearchBy_menuButton;
    @FXML
    private TextField movieEditTitle_textField;
    @FXML
    private TextField movieEditYear_textField;
    @FXML
    private Button movieEditSearchBy_button;

    private boolean valid_title = false;
    private boolean valid_year = false;


    @FXML
    public void initialize() {
        DecimalFormat format = new DecimalFormat( "#.0" );
        TextFormatter onlyIntegers = new TextFormatter<>(c ->
        {
            if (c.getControlNewText().isEmpty())
                return c;

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;

            else
                return c;
        });
        movieEditYear_textField.setTextFormatter(onlyIntegers);
        movieEditSearchBy_button.setDisable(true);

        movieEditSearchBy_menuButton.getItems().add(new CheckMenuItem("Título"));
        movieEditSearchBy_menuButton.getItems().add(new CheckMenuItem("Título VO"));
    }

    public void checkTitle() {
        if (isValidTitle(movieEditTitle_textField.getText())) {
            movieEditTitle_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_title = true;
            checkValidData();
        }
        else {
            movieEditTitle_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_title = false;
        }
    }

    private boolean isValidTitle(String title) {
        if (title.length() > 0 && title.length() <= 20)
            return true;

        return false;
    }

    public void checkYear() {
        if (movieEditYear_textField.getText().length() > 0 && isValidYear(Integer.parseInt(movieEditYear_textField.getText()))) {
            movieEditYear_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_year = true;
            checkValidData();
        }
        else {
            movieEditYear_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_year = false;
        }
    }

    private boolean isValidYear(int year) {
        if (year >= 1900 && year < LocalDate.now().getYear())
            return true;

        return false;
    }

    private void checkValidData() {
        movieEditSearchBy_button.setDisable(!(valid_title && valid_year));
    }

    @FXML
    public void searchMovie() {
        InsertMovieController movieController = new InsertMovieController();
        MovieService movieService = Services.getMovieService();
        Movie toFind = null;

        for (MenuItem item : movieEditSearchBy_menuButton.getItems()) {
            CheckMenuItem menuItem = (CheckMenuItem) item;
            Optional<Movie> movieOptional;

            if (menuItem.isSelected()) {

                switch (menuItem.getText()) {
                    case "Título":
                        movieOptional = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR, movieEditTitle_textField.getText(), movieEditYear_textField.getText());
                        if (movieOptional.isPresent())
                            toFind = movieOptional.get();
                        break;

                    //case "Título VO":
                        //TODO: Find by TitleVO & year

                    //    break;
                }

            }
        }

        if (toFind != null)
            movieController.editMovie(toFind);

        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Película no encontrada");
            alert.setHeaderText("No se ha encontrado una película con los parámetros seleccionados");
            alert.showAndWait();
        }
    }
}
