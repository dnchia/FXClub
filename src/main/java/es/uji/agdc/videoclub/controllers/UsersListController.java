package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.controllers.insertUser.InsertUserController;
import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.UserQueryTypeMultiple;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.utils.Result;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by daniel on 15/12/16.
 */

@Component
public class UsersListController extends Controller {

    @FXML
    TableView users_TableView;
    @FXML
    TableColumn DNI_TableColumn;
    @FXML
    TableColumn completeName_TableColumn;
    @FXML
    TableColumn address_TableColumn;
    @FXML
    TableColumn phone_TableColumn;
    @FXML
    TableColumn email_TableColumn;
    @FXML
    TableColumn lastPayment_TableColumn;
    @FXML
    TableColumn username_TableColumn;
    @FXML
    TableColumn role_TableColumn;
    @FXML
    Button editUser_button;
    @FXML
    Button deleteUser_button;

    private UserService userService = Services.getUserService();

    @FXML
    public void initialize() {
        loadData();
    }

    public void loadData() {
        Stream<User> members = userService.findAllBy(UserQueryTypeMultiple.ROLE, "MEMBER");
        Stream<User> admins = userService.findAllBy(UserQueryTypeMultiple.ROLE, "ADMIN");
        ObservableList<User> usersToTableView = users_TableView.getItems();
        usersToTableView.clear();

        Iterator<User> membersIterator = members.iterator();
        while (membersIterator.hasNext())
            usersToTableView.add(membersIterator.next());


        Iterator<User> adminsIterator = admins.iterator();
        while (adminsIterator.hasNext())
            usersToTableView.add(adminsIterator.next());

        users_TableView.setItems(usersToTableView);
    }

    @FXML
    public void editSelectedUser() {
        ObservableList<Integer> selectedIndices = users_TableView.getSelectionModel().getSelectedIndices();

        if (selectedIndices.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Usuario no seleccionado");
            alert.setHeaderText("Ha de seleccionarse un usuario para poder editarlo");
            alert.showAndWait();
        }
        else if (selectedIndices.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Demasiados usuarios seleccionados");
            alert.setHeaderText("Ha de seleccionarse un único usuario");
            alert.showAndWait();
        }
        else {
            int selectedIndex = selectedIndices.get(0);
            User user = (User) users_TableView.getItems().get(selectedIndex);
            boolean has_errors = editUser(user);

            if (!has_errors) {
                loadData();
            }
        }
    }

    private boolean editUser(User user) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/app/mainSection/adminOptions/insertUser/insert_user_root.fxml"));
        try {
            BorderPane loaded = (BorderPane) loader.load();
            InsertUserController userController = (InsertUserController) loader.getController();
            userController.editUser(user, "Edición de un usuario");
            stage = new Stage();
            stage.setTitle("Editar un usuario");
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(loaded);
            stage.setScene(scene);

            userController.setStage(stage);

            stage.showAndWait();

            return userController.hasErrors();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    public void deleteSelectedUser() {
        ObservableList<Integer> selectedIndices = users_TableView.getSelectionModel().getSelectedIndices();

        if (selectedIndices.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Usuario no seleccionado");
            alert.setHeaderText("Ha de seleccionarse un usuario para poder borrarlo");
            alert.showAndWait();
        }
        else if (selectedIndices.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Demasiados usuarios seleccionados");
            alert.setHeaderText("Ha de seleccionarse un único usuario");
            alert.showAndWait();
        }
        else {
            int selectedIndex = selectedIndices.get(0);
            User user = (User) users_TableView.getItems().get(selectedIndex);
            deleteUser(user);
        }
    }

    private void deleteUser(User user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmación de borrado");
        confirmation.setHeaderText("¿Está seguro de querer eliminar el usuario seleccionado?");
        confirmation.setContentText("Información del usuario: \n\n" +
        "DNI: " + user.getDni() + "\n" +
        "Nombre: " + user.getName() + "\n" +
        "Nombre de usuario: " + user.getUsername() + "\n" +
        "Email: " + user.getEmail() + "\n" +
        "Teléfono: " + user.getPhone() + "\n" +
        "Dirección: " + user.getAddress());

        Optional<ButtonType> answer = confirmation.showAndWait();

        if (answer.isPresent() && answer.get().getButtonData().isDefaultButton()) {

            if (user.getId() == ApplicationStateData.getLoggedUser().getId() || user.isAdmin()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No se puede eliminar al usuario");
                alert.setContentText("No se puede eliminar al usuario administrador\n o a su propio usuario.");
                alert.showAndWait();
            }
            else {
                UserService service = Services.getUserService();
                Result result = service.remove(user.getId());

                if (result.isOk())
                    loadData();

                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error al eliminar el usuario");
                    alert.setHeaderText("No se ha podido eliminar al usuario, por un error en el sistema o por ser un usuario administrador.");
                }
            }
        }
    }
}
