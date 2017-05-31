package es.uji.agdc.videoclub.helpers;

import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.views.AuthScreen;
import es.uji.agdc.videoclub.views.MainSectionScreen;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;

/**
 * Created by daniel on 11/12/16.
 */
public class ApplicationStateData {

    // Configuration objects
    private static Stage primaryStage = null;
    private static ApplicationContext context = null;

    // Application state data
    private static ApplicationState actualState = ApplicationState.LOGIN;
    private static User loggedUser = null;

    public static void setConfigurationData(Stage primaryStage, ApplicationContext context) {
        if (primaryStage != null && context != null) {
            if (ApplicationStateData.primaryStage == null && ApplicationStateData.context == null) {
                ApplicationStateData.primaryStage = primaryStage;
                ApplicationStateData.context = context;
            }
        }
    }

    public static void setNewState(ApplicationState newState) {
        boolean newStateIs_MainSection = newState == ApplicationState.MAIN_SECTION;
        boolean oldStateIs_Login = ApplicationStateData.actualState == ApplicationState.LOGIN;
        boolean oldStateIs_MainSection = ApplicationStateData.actualState == ApplicationState.MAIN_SECTION;
        boolean newStateIs_Login = newState == ApplicationState.LOGIN;

        if (newStateIs_MainSection && oldStateIs_Login) {
            ApplicationStateData.actualState = ApplicationState.MAIN_SECTION;
            ApplicationStateData.primaryStage.close();

            MainSectionScreen mainScreen = context.getBean(MainSectionScreen.class);
            mainScreen.setPrimaryStage(ApplicationStateData.primaryStage);
            mainScreen.showScreen();
        }
        else if (oldStateIs_MainSection && newStateIs_Login) {
            ApplicationStateData.actualState = ApplicationState.LOGIN;
            ApplicationStateData.primaryStage.close();

            AuthScreen authScreen = context.getBean(AuthScreen.class);
            authScreen.setPrimaryStage(ApplicationStateData.primaryStage);
            authScreen.showScreen();
        }
    }

    public static void setLoggedUser(User user) {
        ApplicationStateData.loggedUser = user;
    }

    public static User getLoggedUser() {
        return ApplicationStateData.loggedUser;
    }
}
