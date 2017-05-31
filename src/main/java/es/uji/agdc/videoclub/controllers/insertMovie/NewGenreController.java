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
public class NewGenreController extends Controller implements Form {

    @FXML
    private TextField genreName_textField;
    @FXML
    private Button addGenre;

    private InsertMovie_5Controller rootController;

    private boolean valid_genreName = false;


    @FXML
    public void checkGenreName_TextField() {
        if (validGenreName(genreName_textField.getText())) {
            genreName_textField.setStyle("-fx-border-color: lawngreen ; -fx-border-width: 2px ;");
            valid_genreName = true;
        }
        else {
            genreName_textField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            valid_genreName = false;
        }
        addGenre.setDisable(!valid_genreName);
    }

    private boolean validGenreName(String genreName) {
        if (genreName.length() > 0 && genreName.length() <= 20 && !rootController.isGenreInList(genreName))
            return true;

        return false;
    }

    @FXML
    public void submitGenre() {
        rootController.addGenreToList(genreName_textField.getText());
        super.stage.close();
    }

    @Override
    public boolean allFieldsValid() {
        return valid_genreName;
    }

    @Override
    public String[] getAllData() {
        return new String[] {genreName_textField.getText()};
    }

    @Override
    public void setAllData(String[] data) {
        genreName_textField.setText(data[0]);
        checkGenreName_TextField();
    }

    @Override
    public void setRootController(RootController controller) {
        this.rootController = (InsertMovie_5Controller) controller;
    }
}
