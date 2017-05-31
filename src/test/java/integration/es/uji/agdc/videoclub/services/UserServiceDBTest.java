package integration.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.repositories.UserRepository;
import es.uji.agdc.videoclub.services.UserQueryTypeMultiple;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 04/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class UserServiceDBTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

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
    public void create() throws Exception {
        // Use the service to create a user
        service.create(user);

        // Fetch the user with the repository to assert that it is stored on the database
        Optional<User> possibleUser = repository.findByUsername("paquito69");

        // Assert that the user has been returned
        assertEquals(user.getName(), possibleUser.get().getName());
    }

    @Test
    public void findBy() throws Exception {
        // Use the service to create a user
        service.create(user);

        // Fetch the user with the service itself to assert that the service is able to access the database
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.USERNAME, user.getUsername());

        // Assert that the user has been found
        assertTrue(possibleUser.isPresent());
        assertEquals(user.getUsername(), possibleUser.get().getUsername());
    }

    @Test
    public void findAll() throws Exception {
        // Use the service to create a user
        service.create(user);

        // Fetching for users by role member should contain the user
        Stream<User> members = service.findAllBy(UserQueryTypeMultiple.ROLE, User.Role.MEMBER.toString());

        // Assert that the user is contained
        assertTrue(members.anyMatch(user1 -> user1.equals(user)));
    }

    @Test
    public void findDefaulterUsers_defaulter() throws Exception {
        user.setLastPayment(LocalDate.now().minusMonths(1).minusDays(1));
        service.create(user);

        Stream<User> defaulterUsers = service.findDefaulterUsers();
        assertEquals(1, defaulterUsers.count());
    }


    @Test
    public void findDefaulterUsers_noDefaulter() throws Exception {
        user.setLastPayment(LocalDate.now().minusMonths(1));
        service.create(user);

        Stream<User> defaulterUsers = service.findDefaulterUsers();
        assertEquals(0, defaulterUsers.count());
    }

    @Test
    public void update() throws Exception {
        service.create(user);
        User savedUser = service.findBy(UserQueryTypeSingle.DNI, this.user.getDni()).get();

        String newName = "Pedro Enrique García";
        savedUser.setName(newName).setPassword("");
        Result result = service.update(savedUser, savedUser.getId());

        User updatedUser = service.findBy(UserQueryTypeSingle.DNI, this.user.getDni()).get();
        assertTrue(result.isOk());
        assertEquals(newName, updatedUser.getName());
        assertEquals(savedUser.getPassword(), updatedUser.getPassword());
    }

    @Test
    public void remove() throws Exception {
        service.create(user);
        User savedUser = service.findBy(UserQueryTypeSingle.DNI, this.user.getDni()).get();

        Result result = service.remove(savedUser.getId());

        Optional<User> noUser = service.findBy(UserQueryTypeSingle.DNI, this.user.getDni());
        assertTrue(result.isOk());
        assertFalse(noUser.isPresent());
    }

    @After
    public void tearDown() throws Exception {
        user = null;
    }

}