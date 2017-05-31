package es.uji.agdc.videoclub.controllers;

import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.VisualizationLink;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.VisualizationLinkService;
import es.uji.agdc.videoclub.services.utils.Result;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by daniel on 9/01/17.
 */
public class RentedMoviesController extends Controller {

    @FXML
    private TableView rentedMovies_TableView;
    @FXML
    private TableColumn rentedMoviesTitle_tableColumn;
    @FXML
    private TableColumn rentedMoviesTitleVO_tableColumn;
    @FXML
    private TableColumn rentedMoviesYear_tableColumn;
    @FXML
    private TableColumn rentedMoviesRentDate_tableColumn;
    @FXML
    private Button returnMovie;
    @FXML
    private Button viewMovie;


    @FXML
    public void initialize() {
        User loggedUser = ApplicationStateData.getLoggedUser();
        Iterator<VisualizationLink> links = loggedUser.getVisualizationLinks().iterator();

        rentedMoviesTitle_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VisualizationLink, String>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<VisualizationLink, String> param) {
                return new ObservableStringValue() {
                    @Override
                    public String get() {
                        return param.getValue().getMovie().getTitle();
                    }

                    @Override
                    public void addListener(ChangeListener<? super String> listener) {

                    }

                    @Override
                    public void removeListener(ChangeListener<? super String> listener) {

                    }

                    @Override
                    public String getValue() {
                        return param.getValue().getMovie().getTitle();
                    }

                    @Override
                    public void addListener(InvalidationListener listener) {

                    }

                    @Override
                    public void removeListener(InvalidationListener listener) {

                    }
                };
            }
        });

        rentedMoviesTitleVO_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VisualizationLink, String>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<VisualizationLink, String> param) {
                return new ObservableStringValue() {
                    @Override
                    public String get() {
                        return param.getValue().getMovie().getTitleOv();
                    }

                    @Override
                    public void addListener(ChangeListener<? super String> listener) {

                    }

                    @Override
                    public void removeListener(ChangeListener<? super String> listener) {

                    }

                    @Override
                    public String getValue() {
                        return param.getValue().getMovie().getTitleOv();
                    }

                    @Override
                    public void addListener(InvalidationListener listener) {

                    }

                    @Override
                    public void removeListener(InvalidationListener listener) {

                    }
                };
            }
        });

        rentedMoviesYear_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VisualizationLink, Integer>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<VisualizationLink, Integer> param) {
                return new ObservableIntegerValue() {
                    @Override
                    public void addListener(InvalidationListener listener) {

                    }

                    @Override
                    public void removeListener(InvalidationListener listener) {

                    }

                    @Override
                    public void addListener(ChangeListener<? super Number> listener) {

                    }

                    @Override
                    public void removeListener(ChangeListener<? super Number> listener) {

                    }

                    @Override
                    public Number getValue() {
                        return param.getValue().getMovie().getYear();
                    }

                    @Override
                    public int intValue() {
                        return param.getValue().getMovie().getYear();
                    }

                    @Override
                    public long longValue() {
                        return param.getValue().getMovie().getYear();
                    }

                    @Override
                    public float floatValue() {
                        return param.getValue().getMovie().getYear();
                    }

                    @Override
                    public double doubleValue() {
                        return param.getValue().getMovie().getYear();
                    }

                    @Override
                    public int get() {
                        return param.getValue().getMovie().getYear();
                    }

                };
            }
        });

        rentedMoviesRentDate_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VisualizationLink, String>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<VisualizationLink, String> param) {
                return new ObservableStringValue() {
                    @Override
                    public String get() {
                        return param.getValue().getExpeditionDate().toString();
                    }

                    @Override
                    public void addListener(ChangeListener<? super String> listener) {

                    }

                    @Override
                    public void removeListener(ChangeListener<? super String> listener) {

                    }

                    @Override
                    public String getValue() {
                        return param.getValue().getExpeditionDate().toString();
                    }

                    @Override
                    public void addListener(InvalidationListener listener) {

                    }

                    @Override
                    public void removeListener(InvalidationListener listener) {

                    }
                };
            }
        });

        while (links.hasNext())
            rentedMovies_TableView.getItems().add(links.next());

    }

    public void refreshButtons() {
        returnMovie.setDisable(rentedMovies_TableView.getSelectionModel().getSelectedIndices().size() == 0);
        viewMovie.setDisable(rentedMovies_TableView.getSelectionModel().getSelectedIndices().size() == 0);
    }

    @FXML
    public void returnMovie() {
        if (rentedMovies_TableView.getSelectionModel().getSelectedIndices().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Película no seleccionada");
            alert.setHeaderText("Ha de seleccionarse una película para poder devolverla");
            alert.showAndWait();
        }
        else if (rentedMovies_TableView.getSelectionModel().getSelectedIndices().size() > 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Demasiadas películas seleccionadas");
            alert.setHeaderText("Ha de seleccionarse una única película para poder devolverla");
            alert.showAndWait();
        }
        else {
            int selectedIndex = rentedMovies_TableView.getSelectionModel().getSelectedIndex();
            VisualizationLink selected = (VisualizationLink) rentedMovies_TableView.getItems().get(selectedIndex);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de devolución");
            alert.setHeaderText("¿Está seguro de querer devolver la película?");
            Optional<ButtonType> answer = alert.showAndWait();

            if (answer.isPresent() && answer.get().getButtonData().isDefaultButton()) {
                VisualizationLinkService service = Services.getVisualizationLinkService();
                Result result = service.remove(selected.getToken(), selected.getUser().getId().toString());

                if (result.isOk()) {
                    ApplicationStateData.getLoggedUser().getVisualizationLinks().remove(selected);
                    rentedMovies_TableView.getItems().remove(selectedIndex);
                }

                else {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Error al devolver la película");
                    alert1.setHeaderText("Ha habido un error interno al devolver la película. Vuelva a intentarlo.");
                    alert1.showAndWait();
                }
            }
        }
    }

    @FXML
    public void viewMovie() {
        if (rentedMovies_TableView.getSelectionModel().getSelectedIndices().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Película no seleccionada");
            alert.setHeaderText("Ha de seleccionarse una película para poder verla");
            alert.showAndWait();
        }
        else if (rentedMovies_TableView.getSelectionModel().getSelectedIndices().size() > 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Demasiadas películas seleccionadas");
            alert.setHeaderText("Ha de seleccionarse una única película para poder verla");
            alert.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/app/mainSection/profile/movie_view.fxml"));

            try {
                BorderPane movieViewContainer = loader.load();
                MovieViewController controller = loader.getController();
                controller.stage = new Stage();
                controller.stage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(movieViewContainer);
                controller.stage.setScene(scene);
                VisualizationLink selectedMovie = (VisualizationLink) rentedMovies_TableView.getSelectionModel().getSelectedItems().get(0);
                controller.setMovie(selectedMovie.getMovie());
                controller.initView();
                controller.stage.showAndWait();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
