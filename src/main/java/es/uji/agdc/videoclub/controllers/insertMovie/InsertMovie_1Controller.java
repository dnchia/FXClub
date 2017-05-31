package es.uji.agdc.videoclub.controllers.insertMovie;

import es.uji.agdc.videoclub.controllers.Controller;
import es.uji.agdc.videoclub.controllers.Form;
import es.uji.agdc.videoclub.controllers.RootController;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.MovieQueryTypeSingle;
import es.uji.agdc.videoclub.services.MovieService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.LocalDate;

/**
 * Created by daniel on 5/01/17.
 */
public class InsertMovie_1Controller extends Controller implements Form {

    @FXML
    private TextField movieTitle_textField;
    @FXML
    private TextField movieTitleVO_textField;
    @FXML
    private TextField movieYear_textField;
    @FXML
    private TextField movieCopies_textField;

    private boolean valid_title = false;
    private boolean valid_titleVO = false;
    private boolean valid_year = false;
    private boolean valid_copies = false;

    private MovieService movieService = Services.getMovieService();
    private InsertMovieController rootController = null;


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
        movieYear_textField.setTextFormatter(onlyIntegers);
        movieCopies_textField.setTextFormatter(new TextFormatter<>(c ->
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

    @FXML
    public void checkTitle_TextField() {
        if (validTitle(movieTitle_textField.getText())) {
            movieTitle_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_title = true;
            checkIfActualMovieExists();
        }
        else {
            movieTitle_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_title = false;
        }
        rootController.updateFormState(allFieldsValid(), 1);
        rootController.finishedForm();
    }

    private boolean validTitle(String title) {
        if (title.length() > 0 && title.length() <= 20)
            return true;

        return false;
    }

    @FXML
    public void checkTitleVO_TextField() {
        if (validTitle(movieTitleVO_textField.getText())) {
            movieTitleVO_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_titleVO = true;
            checkIfActualMovieExists();
        }
        else {
            movieTitleVO_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_titleVO = false;
        }
        rootController.updateFormState(allFieldsValid(), 1);
        rootController.finishedForm();
    }

    @FXML
    public void checkYear_TextField() {
        if (movieYear_textField.getText().length() > 0 && validYear(Integer.parseInt(movieYear_textField.getText()))) {
            movieYear_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_year = true;
            checkIfActualMovieExists();
        }
        else {
            movieYear_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_year = false;
        }
        rootController.updateFormState(allFieldsValid(), 1);
        rootController.finishedForm();
    }

    private boolean validYear(int year) {
        if (year > 1900 && year <= LocalDate.now().getYear())
            return true;

        return false;
    }

    /** To control movies with the same title and original version title
     *  but with different year.
     *  With the movies in the database, real-time control of the fields. */
    private void checkIfActualMovieExists() {
        if (movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR, movieTitle_textField.getText(), movieYear_textField.getText()).isPresent()) {

            if (!rootController.isEditing()) {
                movieTitle_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                valid_title = false;

                movieYear_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                valid_year = false;
            }
            else {
                Movie movie = rootController.getMovieToEdit();
                if (movieTitle_textField.getText().equals(movie.getTitle()) && movieYear_textField.getText().equals(String.valueOf(movie.getYear()))) {
                    movieYear_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
                    valid_year = true;

                    movieTitle_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
                    valid_title = true;
                }
                else {
                    movieTitle_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                    valid_title = false;

                    movieYear_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                    valid_year = false;
                }
            }
        }
        else {
            if (movieYear_textField.getText().length() > 0 && validYear(Integer.parseInt(movieYear_textField.getText()))) {
                movieYear_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
                valid_year = true;
            }

            if (validTitle(movieTitle_textField.getText())) {
                movieTitle_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
                valid_title = true;
            }
        }
    }

    @FXML
    public void checkCopies_TextField() {
        if (movieCopies_textField.getText().length() > 0 && validNumberOfCopies(Integer.parseInt(movieCopies_textField.getText()))) {
            movieCopies_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_copies = true;
        }
        else {
            movieCopies_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_copies = false;
        }
        rootController.updateFormState(allFieldsValid(), 1);
        rootController.finishedForm();
    }

    private boolean validNumberOfCopies(int copies) {
        if (copies >= 0 && copies < Integer.MAX_VALUE)
            return true;

        return false;
    }

    @Override
    public boolean allFieldsValid() {
        return valid_title && valid_titleVO && valid_year && valid_copies;
    }

    @Override
    public String[] getAllData() {
        return new String[] {
                movieTitle_textField.getText(),
                movieTitleVO_textField.getText(),
                movieYear_textField.getText(),
                movieCopies_textField.getText()
        };
    }

    @Override
    public void setAllData(String[] data) {
        movieTitle_textField.setText(data[0]);
        checkTitle_TextField();

        movieTitleVO_textField.setText(data[1]);
        checkTitleVO_TextField();

        movieYear_textField.setText(data[2]);
        checkYear_TextField();

        movieCopies_textField.setText(data[3]);
        checkCopies_TextField();
    }

    @Override
    public void setRootController(RootController controller) {
        this.rootController = (InsertMovieController) controller;
    }
}
