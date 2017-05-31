package acceptance.es.uji.agdc.videoclub.usermanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.*;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.MovieService;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alberto on 08/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class UserCreationTest {
    @Autowired
    private UserService service;

    private User user;

    @Before
    public void setUp() throws Exception {
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
    public void create_allFieldsAreValid_resultIsOk() throws Exception {
        // Given a valid user

        // Make the system save it
        Result result = service.create(user);

        // Assert that has been successfully stored
        assertTrue(result.isOk());
    }

    @Test
    public void create_aFieldIsEmpty_resultEMPTY_PARAMETER_Password() throws Exception {
        // Given a user with an empty field
        user.setPassword("");

        // Make the system try to save it
        Result result = service.create(user);

        // Assert that the system complains about the empty field
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Password]", result.toString());
    }

    @Test
    public void create_aFieldIsInvalid_resultINVALID_FIELD_DNI() throws Exception {
        // Given a user with an invalid field
        user.setDni("1234567891011D");

        // Make the system try to save it
        Result result = service.create(user);

        // Assert that the system complains about the invalid field
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void create_userExists_resultUSER_ALREADY_EXISTS_DNI() throws Exception {
        // Given an existing user
        service.create(user);

        // And a new user that shares the DNI of the previous one
        User anotherUser = UserFactory.createMember()
                .setDni("10614397N")
                .setName("Josefino Gutiérrez Vazquez")
                .setAddress("C/De verdad, 123, 1º")
                .setPhone(963852741)
                .setEmail("josegv@hotmail.com")
                .setLastPayment(LocalDate.of(2016, 10, 1))
                .setUsername("gvjosef")
                .setPassword("josefinogutierrez");

        // Make the system try to save it
        Result result = service.create(anotherUser);

        // Assert that the system complains about the duplicated DNI
        assertTrue(result.isError());
        assertEquals("Result: ERROR(USER_ALREADY_EXISTS)[DNI]", result.toString());
    }

    @After
    public void tearDown() throws Exception {
        user = null;
    }
}
