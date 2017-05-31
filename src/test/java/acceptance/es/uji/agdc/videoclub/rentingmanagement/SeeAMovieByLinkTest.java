package acceptance.es.uji.agdc.videoclub.rentingmanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.*;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.MovieService;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.VisualizationLinkQueryTypeSimple;
import es.uji.agdc.videoclub.services.VisualizationLinkService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alberto on 10/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class SeeAMovieByLinkTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private VisualizationLinkService linkService;

    private User userOne;
    private User userTwo;
    private Movie movieOne;
    private Movie movieTwo;

    @Before
    public void setUp() throws Exception {
        userOne = UserFactory.createMember()
                .setDni("10614397N")
                .setName("Paco Sánchez Díaz")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pacosd@hotmail.com")
                .setUsername("paquito69")
                .setPassword("pacosd69");

        userTwo = UserFactory.createMember()
                .setDni("20614397N")
                .setName("Paco Sánchez Díaz")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("paco@hotmail.com")
                .setUsername("paquito")
                .setPassword("pacosd69");

        movieOne = new Movie()
                .setTitle("Capitán América")
                .setTitleOv("Captain America")
                .setYear(2011)
                .addActor(new Actor("Hayley Atwell"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Acción"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(6);

        movieTwo = new Movie()
                .setTitle("Star Wars VII")
                .setTitleOv("Star Wars VII")
                .setYear(2015)
                .addActor(new Actor("Chris Evans"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Drama"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(6);

    }

    @Test
    public void seeByLink_linkExistsAndBelongsToTheUser_userGetsAccess() throws Exception {
        // Given a registered user
        userService.create(userOne);

        // Two movies
        movieService.create(movieOne);
        movieService.create(movieTwo);

        // And two links from the given user
        VisualizationLink linkOne = new VisualizationLink(userOne, movieOne);
        linkService.create(linkOne);
        VisualizationLink linkTwo = new VisualizationLink(userOne, movieTwo);
        linkService.create(linkTwo);

        // When the user tries to see the movie via its link
        Optional<VisualizationLink> possibleLink =
                linkService.findBy(VisualizationLinkQueryTypeSimple.TOKEN, linkOne.getToken(), userOne.getId().toString());

        // Then the system returns the link with the related movie
        assertTrue(possibleLink.isPresent());
        assertEquals(movieOne.getTitle(), possibleLink.get().getMovie().getTitle());
    }

    @Test
    public void seeByLink_linkDoesNotExist_linkNotReturned() throws Exception {
        // Given a registered user with no links
        userService.create(userOne);

        // When the user tries to see the movie via its link
        Optional<VisualizationLink> possibleLink =
                linkService.findBy(VisualizationLinkQueryTypeSimple.TOKEN, "token", userOne.getId().toString());

        // Then the system returns nothing
        assertFalse(possibleLink.isPresent());
    }

    @Test
    public void seeByLink_linkExistsAndButBelongsToAnotherUser_userGetsAnError() throws Exception {
        // Given two registered users
        userService.create(userOne);
        userService.create(userTwo);

        // One movie
        movieService.create(movieOne);

        // And one link from the second user
        VisualizationLink linkOne = new VisualizationLink(userTwo, movieOne);
        linkService.create(linkOne);

        try {
            // When the first user tries to see the movie via its link
            linkService.findBy(VisualizationLinkQueryTypeSimple.TOKEN, linkOne.getToken(), userOne.getId().toString());
        } catch (Error e) {
            // Then the system returns an error telling the user that that link does not belong to him
            assertEquals("FOREIGN_LINK", e.getMessage());
        }

    }

    @After
    public void tearDown() throws Exception {
        userOne = null;
        userTwo = null;
        movieOne = null;
        movieTwo = null;
    }
}
