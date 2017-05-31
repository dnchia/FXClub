package es.uji.agdc.videoclub.controllers;

import javafx.stage.Stage;

/**
 * Created by daniel on 3/01/17.
 */
public abstract class Controller {

    protected Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
