package integration.es.uji.agdc.videoclub.initializers;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.initializers.AdminInitializer;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 04/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class AdminInitializerTest {
    @Autowired
    private UserService service;

    @Test
    public void ensureAdminWasCreated() throws Exception {
        // Every integration test initializes SpringBoot application context
        // By so, a default admin should be created if it doesn't exists
        // The unavoidable condition is that the default admin must exist

        // Try to find the admin
        Optional<User> possibleAdmin = service.findBy(UserQueryTypeSingle.USERNAME, AdminInitializer.ADMIN_USERNAME);

        // Assert that the admin is present
        assertTrue(possibleAdmin.isPresent());
        assertEquals(User.Role.ADMIN, possibleAdmin.get().getRole());
        assertEquals(AdminInitializer.ADMIN_USERNAME, possibleAdmin.get().getUsername());
    }
}