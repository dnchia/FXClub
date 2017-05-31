package integration.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.AuthenticationService;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 03/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class AuthenticationServiceDBTest {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserService userService;

    private static User user;

    @BeforeClass
    public static void setUp() throws Exception {
        user = UserFactory.createMember()
                .setDni("10614397N")
                .setName("Paco Sánchez Díaz")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pacosd@hotmail.com")
                .setUsername("paquito69")
                .setPassword("pacosd69");
    }

    @Test
    public void auth() throws Exception {
        // Create user first
        userService.create(user);

        // Try to authenticate user
        Result result = authService.auth(user.getUsername(), "pacosd69");

        // Assert that the user was successfully authenticated
        assertTrue(result.isOk());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        user = null;
    }
}