package es.uji.agdc.videoclub.controllers.insertUser;

import es.uji.agdc.videoclub.controllers.Controller;
import es.uji.agdc.videoclub.controllers.Form;
import es.uji.agdc.videoclub.controllers.RootController;
import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.utils.Result;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Created by daniel on 3/01/17.
 */
public class InsertUserController extends Controller implements RootController {

    @FXML
    private Label label;
    @FXML
    private Pagination insertUserPagination;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Button submit_button;

    private String user_01 = "/views/app/mainSection/adminOptions/insertUser/insert_user_1.fxml";
    private String user_02 = "/views/app/mainSection/adminOptions/insertUser/insert_user_2.fxml";
    private String user_03 = "/views/app/mainSection/adminOptions/insertUser/insert_user_3.fxml";

    private Form user_01_controller = null;
    private Form user_02_controller = null;
    private Form user_03_controller = null;

    private BorderPane user_01_section = null;
    private BorderPane user_02_section = null;
    private BorderPane user_03_section = null;

    private String[] data_user_01 = null;
    private String[] data_user_02 = null;
    private String[] data_user_03 = null;

    private boolean valid_01 = false;
    private boolean valid_02 = false;
    private boolean valid_03 = false;

    private User user;

    private int actualPage = 0;

    private boolean editing = false;
    private boolean hasErrors = false;


    public void editUser(User user, String labelText) {
        editing = true;
        this.user = user;

        data_user_01 = new String[3];
        data_user_01[0] = this.user.getDni();
        data_user_01[1] = this.user.getUsername();
        data_user_01[2] = this.user.getAddress();

        data_user_02 = new String[2];
        data_user_02[0] = String.valueOf(this.user.getPhone());
        data_user_02[1] = this.user.getEmail();

        data_user_03 = new String[3];
        data_user_03[0] = this.user.getUsername();
        data_user_03[1] = "";

        if (this.user.getLastPayment() != null)
            data_user_03[2] = this.user.getLastPayment().toString();
        else
            data_user_03[2] = "";

        label.setText(labelText);

        user_01_controller.setAllData(data_user_01);
        user_02_controller.setAllData(data_user_02);
        user_03_controller.setAllData(data_user_03);

        valid_01 = true;
        valid_02 = true;
        valid_03 = false;

        finishedForm();
    }

    public User getUserToEdit() {
        return user;
    }

    public boolean isEditing() {
        return editing;
    }

    @FXML
    public void initialize() {
        loadResource(user_01, 0);
        loadResource(user_02, 1);
        loadResource(user_03, 2);
        insertUserPagination.setPageFactory(param -> changedPage());
    }

    @FXML
    public BorderPane changedPage() {
        int newPage = insertUserPagination.getCurrentPageIndex();
        BorderPane loadedResource = null;

        switch (actualPage) {
            case 0:
                data_user_01 = user_01_controller.getAllData();
                break;

            case 1:
                data_user_02 = user_02_controller.getAllData();
                break;

            case 2:
                data_user_03 = user_03_controller.getAllData();
                break;
        }

        switch (newPage) {
            case 0:
                if (data_user_01 != null)
                    user_01_controller.setAllData(data_user_01);

                loadedResource = user_01_section;
                borderPane.setCenter(loadedResource);
                actualPage = 0;
                break;

            case 1:
                if(data_user_02 != null)
                    user_02_controller.setAllData(data_user_02);

                loadedResource = user_02_section;
                borderPane.setCenter(loadedResource);
                actualPage = 1;
                break;

            case 2:
                if(data_user_03 != null)
                    user_03_controller.setAllData(data_user_03);

                loadedResource = user_03_section;
                borderPane.setCenter(loadedResource);
                actualPage = 2;
                break;
        }

        return loadedResource;
    }

    private void loadResource(String dir, int formPage) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(dir));
        BorderPane loadedSection;

        try {
            loadedSection = (BorderPane) loader.load();

            switch (formPage) {
                case 0:
                    user_01_controller = loader.getController();
                    user_01_controller.setRootController(this);
                    user_01_section = loadedSection;
                    break;

                case 1:
                    user_02_controller = loader.getController();
                    user_02_controller.setRootController(this);
                    user_02_section = loadedSection;
                    break;

                case 2:
                    user_03_controller = loader.getController();
                    user_03_controller.setRootController(this);
                    user_03_section = loadedSection;
                    break;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateFormState(boolean allFieldsValid, int formNumber) {
        switch (formNumber) {
            case 1:
                valid_01 = allFieldsValid;
                break;

            case 2:
                valid_02 = allFieldsValid;
                break;

            case 3:
                valid_03 = allFieldsValid;
                break;
        }
    }

    @FXML
    public boolean finishedForm() {
        submit_button.setDisable(!(valid_01 && valid_02 && valid_03));
        return valid_01 && valid_02 && valid_03;
    }

    @FXML
    public void submitForm() {
        if (!editing) {
            user = UserFactory.createMember();
            setAllUserData();
            if (!Services.getUserService().create(user).isOk()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al insertar un usuario");
                alert.setHeaderText("Ha habido un error al insertar la información, ¿está conectado a la base de datos?");
                hasErrors = true;
                alert.showAndWait();
            }
        }
        else {
            setAllUserData();
            Result result = Services.getUserService().update(user, ApplicationStateData.getLoggedUser().getId());
            if (!result.isOk()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al actualizar");
                alert.setHeaderText("Ha habido un error al actualizar la información, ¿está conectado a la base de datos?");
                hasErrors = true;
                alert.showAndWait();
            }
        }

        super.stage.close();
    }

    private void setAllUserData() {
        String[] page1 = user_01_controller.getAllData();
        user.setDni(page1[0]);
        user.setName(page1[1]);
        user.setAddress(page1[2]);

        String[] page2 = user_02_controller.getAllData();
        user.setPhone(Integer.parseInt(page2[0]));
        user.setEmail(page2[1]);

        String[] page3 = user_03_controller.getAllData();
        user.setUsername(page3[0]);
        user.setPassword(page3[1]);

        if (page3[2].equals(""))
            user.setLastPayment(null);
        else
            user.setLastPayment(LocalDate.parse(page3[2]));
    }

    public boolean hasErrors() {
        return hasErrors;
    }
}