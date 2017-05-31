package es.uji.agdc.videoclub.controllers.insertMovie;

import es.uji.agdc.videoclub.controllers.Controller;
import es.uji.agdc.videoclub.controllers.Form;
import es.uji.agdc.videoclub.controllers.RootController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Created by daniel on 5/01/17.
 */
public class InsertMovie_4Controller extends Controller implements Form {

    @FXML
    private TextArea movieDescription_TextArea;

    private InsertMovieController rootController;
    private boolean valid_description = false;


    @FXML
    public void checkDescription_TextArea() {
        if (isValid_description(movieDescription_TextArea.getText())) {
            movieDescription_TextArea.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_description= true;
        }
        else {
            movieDescription_TextArea.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_description = false;
        }

        rootController.updateFormState(allFieldsValid(), 4);
        rootController.finishedForm();
    }

    private boolean isValid_description(String description) {
        if (description.length() >= 200 && description.length() <= 500)
            return true;

        return false;
    }

    @Override
    public boolean allFieldsValid() {
        return valid_description;
    }

    @Override
    public String[] getAllData() {
        return new String[] {movieDescription_TextArea.getText()};
    }

    @Override
    public void setAllData(String[] data) {
        movieDescription_TextArea.setText(data[0]);
        checkDescription_TextArea();
    }

    @Override
    public void setRootController(RootController controller) {
        this.rootController = (InsertMovieController) controller;
    }
}
