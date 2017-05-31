package acceptance.es.uji.agdc.videoclub.usermanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.*;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.*;
import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alberto on 11/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class UserDeletionTest {
    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private VisualizationLinkService linkService;

    private User user;
    private Movie movieOne;
    private Movie movieTwo;
    private Movie movieThree;


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

        movieOne = new Movie()
                .setTitle("Capitán América")
                .setTitleOv("Captain America")
                .setYear(2011)
                .addActor(new Actor("Chris Evans"))
                .addDirector(new Director("Paul Gutiérrez"))
                .addGenre(new Genre("Acción"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(6);

        movieTwo = new Movie()
                .setTitle("Los Vengadores")
                .setTitleOv("Avengers")
                .setYear(1987)
                .addActor(new Actor("Sr. X"))
                .addDirector(new Director("Tipode Incognito"))
                .addGenre(new Genre("Misterio"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(4);

        movieThree = new Movie()
                .setTitle("Bichos")
                .setTitleOv("Bugs")
                .setYear(1979)
                .addActor(new Actor("Super Mario"))
                .addDirector(new Director("Hitoshi Kudeiro"))
                .addGenre(new Genre("Aventura"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(4);
    }

    @Test
    public void userDeletion_withNoRentals_returnsOk() throws Exception {
        // Given a registered user with no rentals
        userService.create(user);
        User savedUsr = userService.findBy(UserQueryTypeSingle.USERNAME,
                user.getUsername()).get();

        // When an administrator removes it
        Result result = userService.remove(user.getId());

        // The system successfully removes it
        Optional<User> noUser = userService.findBy(UserQueryTypeSingle.USERNAME,
                user.getUsername());
        assertTrue(result.isOk());
        assertFalse(noUser.isPresent());
    }

    @Test
    public void userDeletionWithRentals_returnsOkAndRemovesRentals() throws Exception {
        // Given a registered user
        userService.create(user);
        Long userId = userService.findBy(UserQueryTypeSingle.USERNAME,
                user.getUsername()).get().getId();

        // Three movies
        movieService.create(movieOne);
        movieService.create(movieTwo);
        movieService.create(movieThree);

        // And three links
        linkService.create(new VisualizationLink(user, movieOne));
        linkService.create(new VisualizationLink(user, movieTwo));
        linkService.create(new VisualizationLink(user, movieThree));

        // When an administrator removes it
        Result result = userService.remove(user.getId());

        // The system successfully removes it and all of his links
        Optional<User> noUser = userService.findBy(UserQueryTypeSingle.USERNAME,
                user.getUsername());
        Stream<VisualizationLink> userLinks = linkService.findAllBy(VisualizationLinkQueryTypeMultiple.USER,
                userId.toString());
        assertTrue(result.isOk());
        assertFalse(noUser.isPresent());
        assertEquals(0, userLinks.count());
    }

    @Test
    public void userDeletion_withNonExistingUser_returnsError_USER_NOT_FOUND() throws Exception {
        // Given no registered users

        // When an administrator removes a non existing user
        Result result = userService.remove(Long.MAX_VALUE);

        // The system complains about it
        assertTrue(result.isError());
        assertEquals("USER_NOT_FOUND", result.getMsg());
    }

    @After
    public void tearDown() throws Exception {
        user = null;
        movieOne = null;
        movieTwo = null;
        movieThree = null;
    }
}
