package es.uji.agdc.videoclub.initializers;

import es.uji.agdc.videoclub.models.*;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by Alberto on 04/12/2016.
 */
@Component
public class AdminInitializer {
    public static final String
            ADMIN_USERNAME = "admin",
            ADMIN_DEFAULT_PASSWORD = "12345678";

    private static Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserService service;

    @Autowired
    public AdminInitializer(UserService service) {
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        User superAdmin = UserFactory.createAdmin()
                .setDni("20334033D")
                .setName("Videoclub Admin")
                .setAddress("C/El Mejor Videoclub, nª1, bajo")
                .setPhone(693582471)
                .setEmail("admin@videoclub.com")
                .setUsername(ADMIN_USERNAME)
                .setPassword(ADMIN_DEFAULT_PASSWORD);

        Optional<User> possibleAdmin = service.findBy(UserQueryTypeSingle.USERNAME, ADMIN_USERNAME);
        if (!possibleAdmin.isPresent()) {
            log.info("No default admin was found, creating one");
            service.create(superAdmin);
        }
    }
}
