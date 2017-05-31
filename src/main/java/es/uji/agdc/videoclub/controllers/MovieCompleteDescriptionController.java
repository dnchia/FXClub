package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.VisualizationLink;
import es.uji.agdc.videoclub.services.MovieService;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.VisualizationLinkService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * Created by daniel on 7/01/17.
 */
public class MovieCompleteDescriptionController extends Controller {

    @FXML
    private Label movieData_title_label;
    @FXML
    private Label movieData_titleVO_label;
    @FXML
    private Label movieData_year_label;
    @FXML
    private Button movieData_close_button;
    @FXML
    private Button movieData_rent_button;
    @FXML
    private Label movieData_actors_label;
    @FXML
    private Label movieData_directors_label;
    @FXML
    private Label movieData_generes_label;
    @FXML
    private TextArea movieData_description_textArea;
    @FXML
    private Label movieData_copies_label;

    private Movie movie = null;


    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void initWindow() {
        if (movie != null) {
            movieData_title_label.setText(movie.getTitle());
            movieData_titleVO_label.setText(movie.getTitleOv());
            movieData_year_label.setText(String.valueOf(movie.getYear()));

            StringBuilder actorsBuilder = new StringBuilder();

            if (movie.getActors().size() > 0) {
                if (movie.getActors().size() == 1)
                    actorsBuilder.append(movie.getActors().get(0).getName());

                else {
                    for (int i = 0; i < movie.getActors().size() - 1; i++) {
                        actorsBuilder.append(movie.getActors().get(i).getName());
                        actorsBuilder.append(", ");
                    }
                    actorsBuilder.append(movie.getActors().get(movie.getActors().size() - 1).getName());
                }
            }

            String actors = actorsBuilder.toString();

            StringBuilder directorsBuilder = new StringBuilder();

            if (movie.getDirectors().size() > 0) {
                if (movie.getDirectors().size() == 1)
                    directorsBuilder.append(movie.getDirectors().get(0).getName());

                else {
                    for (int i = 0; i < movie.getDirectors().size() -1; i++) {
                        directorsBuilder.append(movie.getDirectors().get(i).getName());
                        directorsBuilder.append(", ");
                    }
                    directorsBuilder.append(movie.getDirectors().get(movie.getDirectors().size() -1).getName());
                }
            }

            String directors = directorsBuilder.toString();

            StringBuilder genresBuilder = new StringBuilder();

            if (movie.getGenres().size() > 0) {
                if (movie.getGenres().size() == 1)
                    genresBuilder.append(movie.getGenres().get(0).getName());

                else {
                    for (int i = 0; i < movie.getGenres().size() -1; i++) {
                        genresBuilder.append(movie.getGenres().get(i).getName());
                        genresBuilder.append(", ");
                    }
                    genresBuilder.append(movie.getGenres().get(movie.getGenres().size() - 1).getName());
                }
            }

            String genres = genresBuilder.toString();

            movieData_actors_label.setText("Actores: " + actors);
            movieData_directors_label.setText("Directores: " + directors);
            movieData_generes_label.setText("Géneros: " + genres);
            movieData_description_textArea.setText(movie.getDescription());
            movieData_copies_label.setText("Copias disponibles: " + String.valueOf(movie.getActualAvailableCopies()));

            User loggedUser = ApplicationStateData.getLoggedUser();
            boolean movie_is_rented_by_logged_user = false;

            for (VisualizationLink link : loggedUser.getVisualizationLinks())
                if (link.getMovie().getId() == movie.getId())
                    movie_is_rented_by_logged_user = true;

            movieData_rent_button.setDisable(movie.getActualAvailableCopies() <= 0 || movie_is_rented_by_logged_user);
        }
    }

    @FXML
    public void closeWindow() {
        super.stage.close();
    }

    @FXML
    public void rentMovie() {
        if (movie != null && movie.getActualAvailableCopies() > 0) {
            User loggedUser = ApplicationStateData.getLoggedUser();
            VisualizationLinkService service = Services.getVisualizationLinkService();
            VisualizationLink link = new VisualizationLink(loggedUser, movie);
            service.create(link);
            initWindow();
        }
    }
}
