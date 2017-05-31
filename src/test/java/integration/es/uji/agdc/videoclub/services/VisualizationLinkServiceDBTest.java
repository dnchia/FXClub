package integration.es.uji.agdc.videoclub.services;

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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 10/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class VisualizationLinkServiceDBTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private VisualizationLinkService linkService;

    private User user;
    private Movie movie;
    private VisualizationLink visualizationLink;

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

        movie = new Movie()
                .setTitle("Capitán América")
                .setTitleOv("Captain America")
                .setYear(2011)
                .addActor(new Actor("Chris Evans"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Acción"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son...")
                .setAvailableCopies(3);

        userService.create(user);
        movieService.create(movie);

        visualizationLink = new VisualizationLink(user, movie);
    }

    @Test
    public void create() throws Exception {
        Result result = linkService.create(visualizationLink);
        assertTrue(result.isOk());
    }

    @Test
    public void findBy_token() throws Exception {
        linkService.create(visualizationLink);

        Optional<VisualizationLink> possibleLink = linkService.findBy(VisualizationLinkQueryTypeSimple.TOKEN, visualizationLink.getToken(), user.getId().toString());

        assertEquals(visualizationLink.getToken(), possibleLink.get().getToken());
    }

    @Test
    public void findAllBy_movie() throws Exception {
        linkService.create(visualizationLink);

        Stream<VisualizationLink> links =
                linkService.findAllBy(VisualizationLinkQueryTypeMultiple.MOVIE, movie.getId().toString());

        assertEquals(movie.getTitle(), links.findFirst().get().getMovie().getTitle());
    }

    @Test
    public void findAllBy_user() throws Exception {
        linkService.create(visualizationLink);

        Stream<VisualizationLink> links =
                linkService.findAllBy(VisualizationLinkQueryTypeMultiple.USER, user.getId().toString());

        assertEquals(user.getUsername(), links.findFirst().get().getUser().getUsername());
    }

    @Test
    public void remove() throws Exception {
        linkService.create(visualizationLink);
        Result result = linkService.remove(visualizationLink.getToken(), user.getId().toString());

        Stream<VisualizationLink> links =
                linkService.findAllBy(VisualizationLinkQueryTypeMultiple.USER, user.getId().toString());

        assertTrue(result.isOk());
        assertEquals(0, links.count());
    }

    @Test
    public void removeTimedOutLinks_removesATimedOutLink() throws Exception {
        visualizationLink.setExpeditionDate(LocalDateTime.now().minusHours(48));
        linkService.create(visualizationLink);

        linkService.removeTimedOutLinks();

        Optional<VisualizationLink> noLink = linkService.findBy(VisualizationLinkQueryTypeSimple.TOKEN,
                visualizationLink.getToken(), visualizationLink.getUser().getId().toString());

        assertFalse(noLink.isPresent());
    }

    @Test
    public void removeTimedOutLinks_doesNotRemoveANotTimedOutLink() throws Exception {
        visualizationLink.setExpeditionDate(LocalDateTime.now().minusHours(47).minusSeconds(59));
        linkService.create(visualizationLink);

        linkService.removeTimedOutLinks();

        Optional<VisualizationLink> possibleLink = linkService.findBy(VisualizationLinkQueryTypeSimple.TOKEN,
                visualizationLink.getToken(), visualizationLink.getUser().getId().toString());

        assertTrue(possibleLink.isPresent());
    }

    @After
    public void tearDown() throws Exception {
        user = null;
        movie = null;
        visualizationLink = null;
    }

}