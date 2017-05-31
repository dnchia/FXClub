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
public class InsertMovie_3Controller extends Controller implements Form, RootController {

    @FXML
    private ListView<String> directorsList_ListView;

    private InsertMovieController rootController;

    private boolean valid_listOf_Directors = false;

    Stage newDirectorStage = null;
    Controller newDirector_controller = null;


    public void insertNewDirector() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/app/mainSection/adminOptions/insertMovie/new_director.fxml"));
        BorderPane loadedSection;

        try {
            loadedSection = (BorderPane) loader.load();
            newDirectorStage = new Stage();
            newDirectorStage.setTitle("Nuevo director");
            newDirectorStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(loadedSection);
            newDirectorStage.setScene(scene);

            newDirector_controller = loader.getController();
            Form director_controller = (Form) newDirector_controller;
            director_controller.setRootController(this);
            newDirector_controller.setStage(newDirectorStage);

            newDirectorStage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDirectorToList(String director) {
        directorsList_ListView.getItems().add(director);
        checkDirectorsList();
    }

    public void deleteSelectedDirector() {
        ObservableList<String> selectedDirectors = directorsList_ListView.selectionModelProperty().get().getSelectedItems();
        if (selectedDirectors.size() > 0) {
            directorsList_ListView.getItems().removeAll(selectedDirectors.subList(0, selectedDirectors.size()));
            checkDirectorsList();
        }
        else {
            Alert notSelectedDirectors = new Alert(Alert.AlertType.INFORMATION);
            notSelectedDirectors.setTitle("Ningún director seleccionado");
            notSelectedDirectors.setHeaderText("Se tiene que seleccionar un director para poder eliminarlo");
            notSelectedDirectors.showAndWait();
        }
    }

    public void checkDirectorsList() {
        if (directorsList_ListView.getItems().size() > 0) {
            directorsList_ListView.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_listOf_Directors = true;
        }
        else {
            directorsList_ListView.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_listOf_Directors = false;
        }
        rootController.updateFormState(allFieldsValid(), 3);
        rootController.finishedForm();
    }

    public boolean isDirectorInList(String director) {
        return directorsList_ListView.getItems().contains(director);
    }

    @Override
    public boolean allFieldsValid() {
        return valid_listOf_Directors;
    }

    @Override
    public String[] getAllData() {
        ObservableList<String> directors = directorsList_ListView.getItems();
        String[] directorsArray = new String[directors.size()];
        directorsArray = directors.toArray(directorsArray);
        return directorsArray;
    }

    @Override
    public void setAllData(String[] data) {
        ObservableList<String> list = directorsList_ListView.getItems();
        list.clear();

        for (int i = 0; i < data.length; i++)
            list.add(data[i]);

        checkDirectorsList();
    }

    @Override
    public void setRootController(RootController controller) {
        this.rootController = (InsertMovieController) controller;
    }
}
