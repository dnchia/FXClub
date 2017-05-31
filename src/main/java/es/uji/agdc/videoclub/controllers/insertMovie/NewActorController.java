package es.uji.agdc.videoclub.controllers.insertMovie;

import es.uji.agdc.videoclub.controllers.Controller;
import es.uji.agdc.videoclub.controllers.Form;
import es.uji.agdc.videoclub.controllers.RootController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Created by daniel on 5/01/17.
 */
public class NewActorController extends Controller implements Form {

    @FXML
    private TextField actorName_textField;
    @FXML
    private Button addActor;

    private boolean valid_actor = false;
    private InsertMovie_2Controller rootController = null;

    @FXML
    public void checkActorName_TextField() {
        if (validActor(actorName_textField.getText())) {
            actorName_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_actor = true;
        }
        else {
            actorName_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_actor = false;
        }
        addActor.setDisable(!valid_actor);
    }

    private boolean validActor(String actor) {
        if (actor != null && actor.length() > 10 && !rootController.isActorInList(actor))
            return true;

        return false;
    }

    @FXML
    public void submitActor() {
        rootController.addActorToList(actorName_textField.getText());
        super.stage.close();
    }

    @Override
    public boolean allFieldsValid() {
        return valid_actor;
    }

    @Override
    public String[] getAllData() {
        return new String[] {actorName_textField.getText()};
    }

    @Override
    public void setAllData(String[] data) {
        actorName_textField.setText(data[0]);
        checkActorName_TextField();
    }

    @Override
    public void setRootController(RootController controller) {
        this.rootController = (InsertMovie_2Controller) controller;
    }
}
