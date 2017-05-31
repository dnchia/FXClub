package acceptance.es.uji.agdc.videoclub.usermanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.helpers.PasswordEncryptor;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.AuthenticationService;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
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
 * Created by Alberto on 11/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class AdminEditsUserTest {
    @Autowired
    private UserService service;

    @Autowired
    private PasswordEncryptor encryptor;

    private User user;

    @Before
    public void setUp() throws Exception {
        user = UserFactory.createMember()
                .setDni("10614397N")
                .setName("Paco Sánchez Díaz")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pacosd@hotmail.com")
                .setLastPayment(LocalDate.of(2016, 10, 1))
                .setUsername("paquito69")
                .setPassword("pacosd69");
    }

    @Test
    public void adminEditsUser_allFieldsAreValid_returnsOk() throws Exception {
        // Given a registered user and an admin on the system
        service.create(user);
        User savedUser = service.findBy(UserQueryTypeSingle.USERNAME, user.getUsername()).get();
        User admin = service.findBy(UserQueryTypeSingle.USERNAME, "admin").get();

        // When the admin modifies the user
        String dni = "10614397Z";
        String name = "Paco Sánchez Pérez";
        String address = "C/Falsa, 123, 2º";
        int phone = 693582472;
        String email = "pacosdk@hotmail.com";
        LocalDate lastPayment = LocalDate.of(2016, 11, 1);
        String username = "paquito69";
        String password = "pacosd69";
        savedUser.setDni(dni)
                .setName(name)
                .setAddress(address)
                .setPhone(phone)
                .setEmail(email)
                .setLastPayment(lastPayment)
                .setUsername(username)
                .setPassword(password);
        Result result = service.update(savedUser, admin.getId());

        // Then the system updates the user and returns OK
        User modifiedUser = service.findBy(UserQueryTypeSingle.USERNAME, savedUser.getUsername()).get();
        assertTrue(result.isOk());
        assertEquals(dni, modifiedUser.getDni());
        assertEquals(name, modifiedUser.getName());
        assertEquals(address, modifiedUser.getAddress());
        assertEquals(phone, modifiedUser.getPhone());
        assertEquals(email, modifiedUser.getEmail());
        assertEquals(lastPayment, modifiedUser.getLastPayment());
        assertEquals(username, modifiedUser.getUsername());
        assertTrue(encryptor.equals(password, modifiedUser.getPassword()));
    }

    @Test
    public void adminEditsUser_someFieldsAreEmpty_returnsError_EMPTY_PARAMETER() throws Exception {
        // Given a registered user and an admin on the system
        service.create(user);
        User savedUser = service.findBy(UserQueryTypeSingle.USERNAME, user.getUsername()).get();
        User admin = service.findBy(UserQueryTypeSingle.USERNAME, "admin").get();

        // When the admin modifies the user making a mistake
        String dni = "10614397N";
        String name = "";
        String address = "C/Falsa, 123, 1º";
        int phone = 693582471;
        String email = "pacosd@hotmail.com";
        LocalDate lastPayment = null;
        String username = "paquito69";
        String password = "pacosd69";
        savedUser.setDni(dni)
                .setName(name)
                .setAddress(address)
                .setPhone(phone)
                .setEmail(email)
                .setLastPayment(lastPayment)
                .setUsername(username)
                .setPassword(password);
        Result result = service.update(savedUser, admin.getId());

        // Then the system does not update the user and returns an ERROR
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Name]", result.toString());
    }

    @Test
    public void adminEditsUser_someFieldsAreInvalid_returnsError_INVALID_PARAMETER() throws Exception {
        // Given a registered user and an admin on the system
        service.create(user);
        User savedUser = service.findBy(UserQueryTypeSingle.USERNAME, user.getUsername()).get();
        User admin = service.findBy(UserQueryTypeSingle.USERNAME, "admin").get();

        // When the admin modifies the user making a mistake
        String dni = "10614397N";
        String name = "Paco Sánchez Díaz";
        String address = "C/Falsa, 123, 1º";
        int phone = 693582471;
        String email = "pacosdhotmail.com";
        LocalDate lastPayment = null;
        String username = "paquito69";
        String password = "pacosd69";
        savedUser.setDni(dni)
                .setName(name)
                .setAddress(address)
                .setPhone(phone)
                .setEmail(email)
                .setLastPayment(lastPayment)
                .setUsername(username)
                .setPassword(password);
        Result result = service.update(savedUser, admin.getId());

        // Then the system does not update the user and returns an ERROR
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Email]", result.toString());
    }

    @After
    public void tearDown() throws Exception {
        user = null;
    }
}
