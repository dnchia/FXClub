package es.uji.agdc.videoclub.controllers;

/**
 * Created by daniel on 4/01/17.
 */
public interface Form {
    boolean allFieldsValid();
    String[] getAllData();
    void setAllData(String[] data);
    void setRootController(RootController controller);
}
