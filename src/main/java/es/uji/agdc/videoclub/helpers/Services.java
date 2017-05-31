package es.uji.agdc.videoclub.helpers;

import es.uji.agdc.videoclub.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by daniel on 15/12/16.
 */

@Component
public class Services {

    private static UserService userService;
    private static AuthenticationService authService;
    private static MovieService movieService;
    private static MovieAssetService movieAssetService;
    private static VisualizationLinkService visualizationLinkService;

    @Autowired
    public Services(UserService userService, AuthenticationService authService, MovieService movieService, MovieAssetService movieAssetService, VisualizationLinkService visualizationLinkService) {
        this.userService = userService;
        this.authService = authService;
        this.movieService = movieService;
        this.movieAssetService = movieAssetService;
        this.visualizationLinkService = visualizationLinkService;
    }

    public static UserService getUserService() {
        return Services.userService;
    }

    public static AuthenticationService getAuthenticationService() {
        return Services.authService;
    }

    public static MovieService getMovieService() {
        return Services.movieService;
    }

    public static MovieAssetService getMovieAssetService() {
        return Services.movieAssetService;
    }

    public static VisualizationLinkService getVisualizationLinkService() {
        return Services.visualizationLinkService;
    }
}
