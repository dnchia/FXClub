package acceptance.es.uji.agdc.videoclub.rentingmanagement;

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
import static org.junit.Assert.assertTrue;

/**
 * Created by Alberto on 10/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class ListUserRentalsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private VisualizationLinkService linkService;

    private User user;
    private Movie movieOne;
    private Movie movieTwo;

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
                .addActor(new Actor("Hayley Atwell"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Comedy"))
                .addGenre(new Genre("Drama"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(6);

        movieTwo = new Movie()
                .setTitle("Star Wars VII")
                .setTitleOv("Star Wars VII")
                .setYear(2015)
                .addActor(new Actor("Chris Evans"))
                .addActor(new Actor("Hayley Atwell"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Comedy"))
                .addGenre(new Genre("Drama"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(6);
    }

    @Test
    public void listUserRentals_withPendingRentals_returnsAll() throws Exception {
        // Given a registered user
        userService.create(user);
        User savedUser = userService.findBy(UserQueryTypeSingle.USERNAME, user.getUsername()).get();

        // Two different movies
        movieService.create(movieOne);
        movieService.create(movieTwo);

        // And a rental for each of them
        linkService.create(new VisualizationLink(user, movieOne));
        linkService.create(new VisualizationLink(user, movieTwo));

        // When we try to list the pending user rentals
        Stream<VisualizationLink> userLinks =
                linkService.findAllBy(VisualizationLinkQueryTypeMultiple.USER, savedUser.getId().toString());

        // Then check that both links are returned
        assertTrue(userLinks.allMatch(link ->
                link.getMovie().getTitle().equals(movieOne.getTitle()) ||
                        link.getMovie().getTitle().equals(movieTwo.getTitle())));
    }

    @Test
    public void listUserRentals_withNoPendingRentals_returnsNone() throws Exception {
        // Given a registered user with no pending rentals
        userService.create(user);
        User savedUser = userService.findBy(UserQueryTypeSingle.USERNAME, user.getUsername()).get();

        // When we try to list the pending user rentals
        Stream<VisualizationLink> userLinks =
                linkService.findAllBy(VisualizationLinkQueryTypeMultiple.USER, savedUser.getId().toString());

        // Then check that no links are returned
        assertEquals(0, userLinks.count());
    }

    @After
    public void tearDown() throws Exception {
        user = null;
        movieOne = null;
        movieTwo = null;
    }
}
