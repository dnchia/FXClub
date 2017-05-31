package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.models.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by daniel on 10/01/17.
 */
public class MovieViewController extends Controller {

    @FXML
    private Label movieView_label;

    private Movie movie = null;


    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void initView() {
        if (movie != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("Está viendo: ");
            builder.append(movie.getTitle());
            movieView_label.setText(builder.toString());
        }
    }
}
