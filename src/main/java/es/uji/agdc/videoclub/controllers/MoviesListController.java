package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.controllers.insertMovie.InsertMovieController;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.MovieService;
import es.uji.agdc.videoclub.services.utils.Result;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by daniel on 7/01/17.
 */
public class MoviesListController extends Controller {

    @FXML
    private TableView movies_TableView;
    @FXML
    private TableColumn title_TableColumn;
    @FXML
    private TableColumn titleOV_TableColumn;
    @FXML
    private TableColumn year_TableColumn;
    @FXML
    private TableColumn actors_TableColumn;
    @FXML
    private TableColumn directors_TableColumn;
    @FXML
    private TableColumn genres_TableColumn;
    @FXML
    private TableColumn description_TableColumn;
    @FXML
    private TableColumn availableCopies_TableColumn;
    @FXML
    private Button editMovie_button;
    @FXML
    private Button deleteMovie_button;

    private MovieService movieService = Services.getMovieService();


    @FXML
    public void initialize() {
        TableColumn<Movie, List<Actor>> actors_TableColumn = new TableColumn<>("Actores");
        PropertyValueFactory<Movie, List<Actor>> actorsColFactory = new PropertyValueFactory<>("actors");
        actors_TableColumn.setCellValueFactory(actorsColFactory);

        actors_TableColumn.setCellFactory(col -> new TableCell<Movie, List<Actor>>() {
            @Override
            public void updateItem(List<Actor> actors, boolean empty) {
                super.updateItem(actors, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(actors.stream().map(actor -> actor.getName())
                            .collect(Collectors.joining(", ")));
                }
            }
        });
        this.actors_TableColumn = actors_TableColumn;

        TableColumn<Movie, List<Director>> directors_TableColumn = new TableColumn<>("Directores");
        PropertyValueFactory<Movie, List<Director>> directorsColFactory = new PropertyValueFactory<>("directors");
        directors_TableColumn.setCellValueFactory(directorsColFactory);

        directors_TableColumn.setCellFactory(col -> new TableCell<Movie, List<Director>>() {
            @Override
            public void updateItem(List<Director> directors, boolean empty) {
                super.updateItem(directors, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(directors.stream().map(director -> director.getName())
                            .collect(Collectors.joining(", ")));
                }
            }
        });
        this.directors_TableColumn = directors_TableColumn;

        TableColumn<Movie, List<Genre>> genres_TableColumn = new TableColumn<>("Géneros");
        PropertyValueFactory<Movie, List<Genre>> genresColFactory = new PropertyValueFactory<>("genres");
        genres_TableColumn.setCellValueFactory(genresColFactory);

        genres_TableColumn.setCellFactory(col -> new TableCell<Movie, List<Genre>>() {
            @Override
            public void updateItem(List<Genre> genres, boolean empty) {
                super.updateItem(genres, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(genres.stream().map(genre -> genre.getName())
                            .collect(Collectors.joining(", ")));
                }
            }
        });
        this.genres_TableColumn = genres_TableColumn;

        movies_TableView.getColumns().set(3, this.actors_TableColumn);
        movies_TableView.getColumns().set(4, this.directors_TableColumn);
        movies_TableView.getColumns().set(5, this.genres_TableColumn);

        loadData();
    }

    private void loadData() {
        Stream<Movie> movies = movieService.findAll();
        movies_TableView.getItems().clear();

        Iterator<Movie> moviesIterator = movies.iterator();

        while (moviesIterator.hasNext())
            movies_TableView.getItems().add(moviesIterator.next());

    }

    @FXML
    public void editSelectedMovie() {
        ObservableList<Integer> selectedIndices = movies_TableView.getSelectionModel().getSelectedIndices();

        if (selectedIndices.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Película no seleccionada");
            alert.setHeaderText("Ha de seleccionarse una película para poder editarla");
            alert.showAndWait();
        }
        else if (selectedIndices.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Demasiadas películas seleccionadas");
            alert.setHeaderText("Ha de seleccionarse una única película");
            alert.showAndWait();
        }
        else {
            int selectedIndex = selectedIndices.get(0);
            Movie movie = (Movie) movies_TableView.getItems().get(selectedIndex);
            editSelectedMovie(movie);
        }
    }

    Stage stage = null;

    private void editSelectedMovie(Movie movie) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/app/mainSection/adminOptions/insertMovie/insert_movie_root.fxml"));
        try {
            BorderPane loaded = (BorderPane) loader.load();
            InsertMovieController movieController = (InsertMovieController) loader.getController();
            movieController.editMovie(movie);
            stage = new Stage();
            stage.setTitle("Editar una película");
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(loaded);
            stage.setScene(scene);

            movieController.setStage(stage);

            stage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void disableSelectedMovie() {
        ObservableList<Integer> selectedIndices = movies_TableView.getSelectionModel().getSelectedIndices();

        if (selectedIndices.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Película no seleccionada");
            alert.setHeaderText("Ha de seleccionarse una película para poder deshabilitarla");
            alert.showAndWait();
        }
        else if (selectedIndices.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Demasiadas películas seleccionadas");
            alert.setHeaderText("Ha de seleccionarse una única película");
            alert.showAndWait();
        }
        else {
            int selectedIndex = selectedIndices.get(0);
            Movie movie = (Movie) movies_TableView.getItems().get(selectedIndex);
            deleteSelectedMovie(movie);
        }
    }

    private void deleteSelectedMovie(Movie movie) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmación de deshabilitación");
        confirmation.setHeaderText("¿Está seguro de querer deshabilitar la película seleccionada?");

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


        confirmation.setContentText("Información del usuario: \n\n" +
                "Título: " + movie.getTitle() + "\n" +
                "Título VO: " + movie.getTitleOv() + "\n" +
                "Año de estreno: " + movie.getYear() + "\n\n" +
                "Actores: " + actors + "\n\n" +
                "Directores: " + directors + "\n\n" +
                "Géneros: " + genres + "\n\n" +
                "Descripción: " + movie.getDescription() + "\n" +
                "Copias disponibles: " + movie.getAvailableCopies()
        );

        Optional<ButtonType> answer = confirmation.showAndWait();
        if (answer.isPresent() && answer.get().getButtonData().isDefaultButton()) {
            MovieService service = Services.getMovieService();
            Result result = service.remove(movie.getId());

            if (result.isOk()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Película correctamente deshabilitada");
                alert.setHeaderText("La película se ha deshabilitado correctamente");
                alert.showAndWait();
                loadData();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al deshabilitar la película");
                alert.setHeaderText("No se ha podido eliminar la película por un error en el sistema.");
                alert.showAndWait();
            }
        }
    }
}
