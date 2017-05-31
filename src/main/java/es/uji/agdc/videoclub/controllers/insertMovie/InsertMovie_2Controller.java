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
public class InsertMovie_2Controller extends Controller implements Form, RootController {

    @FXML
    private ListView<String> actorsList_ListView;

    private InsertMovieController rootController;

    private boolean valid_listOfActors = false;

    Stage newActorStage = null;
    Controller newActor_controller = null;


    public void insertNewActor() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/app/mainSection/adminOptions/insertMovie/new_actor.fxml"));
        BorderPane loadedSection;

        try {
            loadedSection = (BorderPane) loader.load();
            newActorStage = new Stage();
            newActorStage.setTitle("Nuevo actor");
            newActorStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(loadedSection);
            newActorStage.setScene(scene);

            newActor_controller = loader.getController();
            Form actor_controller = (Form) newActor_controller;
            actor_controller.setRootController(this);
            newActor_controller.setStage(newActorStage);

            newActorStage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addActorToList(String newActor) {
        actorsList_ListView.getItems().add(newActor);
        checkActorsList();
    }

    public boolean isActorInList(String actor) {
        return actorsList_ListView.getItems().contains(actor);
    }

    public void deleteSelectedActor() {
        ObservableList<String> selectedActors = actorsList_ListView.selectionModelProperty().get().getSelectedItems();
        if (selectedActors.size() > 0) {
            actorsList_ListView.getItems().removeAll(selectedActors.subList(0, selectedActors.size()));
            checkActorsList();
        }
        else {
            Alert notSelectedActors = new Alert(Alert.AlertType.INFORMATION);
            notSelectedActors.setTitle("Ningún actor seleccionado");
            notSelectedActors.setHeaderText("Se tiene que seleccionar un actor para poder eliminarlo");
            notSelectedActors.showAndWait();
        }
    }

    public void checkActorsList() {
        if (actorsList_ListView.getItems().size() > 0) {
            actorsList_ListView.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_listOfActors = true;
        }
        else {
            actorsList_ListView.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_listOfActors = false;
        }
        rootController.updateFormState(allFieldsValid(), 2);
        rootController.finishedForm();
    }

    @Override
    public boolean allFieldsValid() {
        return valid_listOfActors;
    }

    @Override
    public String[] getAllData() {
        ObservableList<String> actors = actorsList_ListView.getItems();
        String[] actorsArray = new String[actors.size()];
        actorsArray = actors.toArray(actorsArray);
        return actorsArray;
    }

    @Override
    public void setAllData(String[] data) {
        ObservableList<String> list = actorsList_ListView.getItems();
        list.clear();

        for (int i = 0; i < data.length; i++)
            list.add(data[i]);

        checkActorsList();
    }

    @Override
    public void setRootController(RootController controller) {
        this.rootController = (InsertMovieController) controller;
    }
}
