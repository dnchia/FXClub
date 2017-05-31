package acceptance.es.uji.agdc.videoclub.systemauth;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.initializers.AdminInitializer;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.AuthenticationService;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 07/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class AdminAuthTest {
    private static int FIRST_FIELD = 0;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserService userService;

    private User admin1;

    @Before
    public void setUp() throws Exception {
        admin1 = UserFactory.createAdmin()
                .setDni("10614397N")
                .setName("Admin 1")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("admin01@videoclub.com")
                .setUsername("admin01")
                .setPassword("admin4life");

    }

    @Test
    public void auth_correctAuthentication_returnsOk() throws Exception {
        // Given that we've created an admin1
        userService.create(admin1);

        // Then, we try to authenticate it
        Result authResult = authService.auth(admin1.getUsername(), "admin4life");

        // Assert that auth returns OK
        assertTrue(authResult.isOk());
    }

    @Test
    public void auth_incorrectAuthenticationWrongUsername_returnsLOGIN_FAILED_USERNAME() throws Exception {
        // Given that we've created an admin1
        userService.create(admin1);

        // Then, we try to authenticate it and we fail to introduce his/her username
        Result authResult = authService.auth("admin001", "foo");

        // Assert that auth returns an error with message: LOGIN_FAILED and the field that fails is USERNAME
        assertTrue(authResult.isError());
        assertEquals("LOGIN_FAILED", authResult.getMsg());
        assertEquals("USERNAME", authResult.getFields()[FIRST_FIELD]);
    }


    @Test
    public void auth_incorrectAuthenticationWrongPassword_returnsLOGIN_FAILED_PASSWORD() throws Exception {
        // Given that we've created an admin1
        userService.create(admin1);

        // Then, we try to authenticate it and we fail to introduce his/her password
        Result authResult = authService.auth(admin1.getUsername(), "bar");

        // Assert that auth returns an error with message: LOGIN_FAILED and the field that fails is PASSWORD
        assertTrue(authResult.isError());
        assertEquals("LOGIN_FAILED", authResult.getMsg());
        assertEquals("PASSWORD", authResult.getFields()[FIRST_FIELD]);
    }
}
