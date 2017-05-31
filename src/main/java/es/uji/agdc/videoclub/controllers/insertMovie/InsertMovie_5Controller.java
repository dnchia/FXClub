package es.uji.agdc.videoclub.controllers.insertMovie;

import es.uji.agdc.videoclub.controllers.Controller;
import es.uji.agdc.videoclub.controllers.Form;
import es.uji.agdc.videoclub.controllers.RootController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by daniel on 5/01/17.
 */
public class InsertMovie_5Controller extends Controller implements Form, RootController {

    @FXML
    private ListView<String> genresList_ListView;

    private InsertMovieController rootController;

    private boolean valid_listOf_genres = false;

    Stage newGenreStage = null;
    Controller newGenre_controller = null;


    public void insertNewGenre() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/app/mainSection/adminOptions/insertMovie/new_genre.fxml"));
        BorderPane loadedSection;

        try {
            loadedSection = (BorderPane) loader.load();
            newGenreStage = new Stage();
            newGenreStage.setTitle("Nuevo género");
            newGenreStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(loadedSection);
            newGenreStage.setScene(scene);

            newGenre_controller = loader.getController();
            Form genre_controller = (Form) newGenre_controller;
            genre_controller.setRootController(this);
            newGenre_controller.setStage(newGenreStage);

            newGenreStage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addGenreToList(String genre) {
        genresList_ListView.getItems().add(genre);
        checkGenresList();
    }

    public void deleteSelectedGenre() {
        ObservableList<String> selectedGenres = genresList_ListView.selectionModelProperty().get().getSelectedItems();
        if (selectedGenres.size() > 0) {
            genresList_ListView.getItems().removeAll(selectedGenres.subList(0, selectedGenres.size()));
            checkGenresList();
        }
        else {
            Alert notSelectedGenres = new Alert(Alert.AlertType.INFORMATION);
            notSelectedGenres.setTitle("Ningún género seleccionado");
            notSelectedGenres.setHeaderText("Se tiene que seleccionar un género para poder eliminarlo");
            notSelectedGenres.showAndWait();
        }
    }

    public void checkGenresList() {
        if (genresList_ListView.getItems().size() > 0) {
            genresList_ListView.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_listOf_genres = true;
        }
        else {
            genresList_ListView.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_listOf_genres = false;
        }
        rootController.updateFormState(allFieldsValid(), 5);
        rootController.finishedForm();
    }

    public boolean isGenreInList(String genre) {
        return genresList_ListView.getItems().contains(genre);
    }

    @Override
    public boolean allFieldsValid() {
        return valid_listOf_genres;
    }

    @Override
    public String[] getAllData() {
        ObservableList<String> genres = genresList_ListView.getItems();
        String[] genresArray = new String[genres.size()];
        genresArray = genres.toArray(genresArray);
        return genresArray;
    }

    @Override
    public void setAllData(String[] data) {
        ObservableList<String> list = genresList_ListView.getItems();
        list.clear();

        for (int i = 0; i < data.length; i++)
            list.add(data[i]);

        checkGenresList();
    }

    @Override
    public void setRootController(RootController controller) {
        this.rootController = (InsertMovieController) controller;
    }
}
