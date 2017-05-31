package acceptance.es.uji.agdc.videoclub.rentingmanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.*;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Created by alberto on 11/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class RemoveTimedOutTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private VisualizationLinkService linkService;

    private User userOne;
    private User userTwo;
    private User userThree;

    private Movie movie;

    private VisualizationLink linkOne;
    private VisualizationLink linkTwo;
    private VisualizationLink linkThree;

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

        userThree = UserFactory.createMember()
                .setDni("51085104B")
                .setName("Pedro Ramirez López")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pedro@hotmail.com")
                .setUsername("prueba2")
                .setPassword("pacosd69");

        movie = new Movie()
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

        linkOne = new VisualizationLink(userOne, movie);
        linkTwo = new VisualizationLink(userTwo, movie);
        linkThree = new VisualizationLink(userThree, movie);
    }

    @Test
    public void removeTimedOutLinks_withTimedOut_removesTimedOutLinks() throws Exception {
        // Given three different registered users
        userService.create(userOne);
        userService.create(userTwo);
        userService.create(userThree);

        // A movie
        movieService.create(movie);
        Movie savedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // And three different links for that movie, two of them timed out
        linkOne.setExpeditionDate(LocalDateTime.now().minusHours(48));
        linkTwo.setExpeditionDate(LocalDateTime.now().minusHours(47).minusSeconds(59));
        linkThree.setExpeditionDate(LocalDateTime.now().minusHours(72));

        linkService.create(linkOne);
        linkService.create(linkTwo);
        linkService.create(linkThree);

        // When an administrator removes all timed out links
        linkService.removeTimedOutLinks();

        // Then the system only keeps the link that is still valid
        Stream<VisualizationLink> links =
                linkService.findAllBy(VisualizationLinkQueryTypeMultiple.MOVIE, savedMovie.getId().toString());
        List<VisualizationLink> linkList = links.collect(Collectors.toList());
        assertEquals(1, linkList.size());
        assertEquals(linkTwo.getUser().getUsername(), linkList.get(0).getUser().getUsername());
    }

    @Test
    public void removeTimedOutLinks_withNoTimedOut_removesNothing() throws Exception {
        // Given a registered user
        userService.create(userOne);

        // A movie
        movieService.create(movie);
        Movie savedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // And a links that has not been timed out yet
        linkOne.setExpeditionDate(LocalDateTime.now().minusHours(47).minusSeconds(59));
        linkService.create(linkOne);

        // When an administrator removes all timed out links
        linkService.removeTimedOutLinks();

        // Then the system only keeps the link that is still valid
        Stream<VisualizationLink> links =
                linkService.findAllBy(VisualizationLinkQueryTypeMultiple.MOVIE, savedMovie.getId().toString());
        List<VisualizationLink> linkList = links.collect(Collectors.toList());
        assertEquals(1, linkList.size());
        assertEquals(linkOne.getUser().getUsername(), linkList.get(0).getUser().getUsername());
    }

    @After
    public void tearDown() throws Exception {
        userOne = null;
        userTwo = null;
        userThree = null;

        movie = null;

        linkOne = null;
        linkTwo = null;
        linkThree = null;
    }
}
