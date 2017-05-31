package es.uji.agdc.videoclub;

import es.uji.agdc.videoclub.helpers.ApplicationStateData;
import es.uji.agdc.videoclub.views.AuthScreen;
import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * Created by daniel on 1/12/16.
 */

@EnableJpaAuditing
@SpringBootApplication
public class Main extends Application {

    private static Stage primaryStage;
    private static ApplicationContext context;

    private static String[] args;

    public static void main(String[] args) {
        Main.args = args;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        context = SpringApplication.run(Main.class, args);
        AuthScreen authScreen = context.getBean(AuthScreen.class);

        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle(authScreen.getTitle());
        Main.primaryStage.setOnCloseRequest(e -> Platform.exit());

        ApplicationStateData.setConfigurationData(Main.primaryStage, Main.context);

        authScreen.setPrimaryStage(primaryStage);
        authScreen.showScreen();
    }
}