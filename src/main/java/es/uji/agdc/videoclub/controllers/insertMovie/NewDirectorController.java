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
public class NewDirectorController extends Controller implements Form {

    @FXML
    private TextField directorName_textField;
    @FXML
    private Button addDirector;

    private InsertMovie_3Controller rootController;

    private boolean valid_directorName = false;

    @FXML
    public void checkDirectorName_TextField() {
        if (validDirectorName(directorName_textField.getText())) {
            directorName_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_directorName = true;
        }
        else {
            directorName_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_directorName = false;
        }
        addDirector.setDisable(!valid_directorName);
    }

    private boolean validDirectorName(String director) {
        if (director.length() >= 10 && director.length() <= 25 && !rootController.isDirectorInList(director))
            return true;

        return false;
    }

    @FXML
    public void submitDirector() {
        rootController.addDirectorToList(directorName_textField.getText());
        super.stage.close();
    }

    @Override
    public boolean allFieldsValid() {
        return valid_directorName;
    }

    @Override
    public String[] getAllData() {
        return new String[] {directorName_textField.getText()};
    }

    @Override
    public void setAllData(String[] data) {
        directorName_textField.setText(data[0]);
        checkDirectorName_TextField();
    }

    @Override
    public void setRootController(RootController controller) {
        this.rootController = (InsertMovie_3Controller) controller;
    }
}
