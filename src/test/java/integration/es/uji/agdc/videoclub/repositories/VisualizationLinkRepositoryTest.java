package integration.es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.*;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.repositories.VisualizationLinkRepository;
import es.uji.agdc.videoclub.services.MovieService;
import es.uji.agdc.videoclub.services.UserService;
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
 * Created by alberto on 10/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class VisualizationLinkRepositoryTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private VisualizationLinkRepository repository;

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
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(3);

        userService.create(user);
        movieService.create(movie);

        visualizationLink = new VisualizationLink(user, movie);

    }

    @Test
    public void createVisualizationLink() throws Exception {
        VisualizationLink savedVisualizationLink = repository.save(visualizationLink);
        assertNotNull(savedVisualizationLink.getId());
    }

    @Test
    public void findVisualizationLinkByToken() throws Exception {
        repository.save(visualizationLink);

        Optional<VisualizationLink> savedVisualizationLink =
                repository.findByToken(visualizationLink.getToken());

        assertTrue(savedVisualizationLink.isPresent());
    }

    @Test
    public void findVisualizationLinksByMovieId() throws Exception {
        repository.save(visualizationLink);

        Stream<VisualizationLink> visualizationLinks =
                repository.findByMovie_Id(visualizationLink.getMovie().getId());

        assertEquals(1, visualizationLinks.count());
    }

    @Test
    public void findVisualizationLinksByUserId() throws Exception {
        repository.save(visualizationLink);

        Stream<VisualizationLink> visualizationLinks =
                repository.findByUser_Id(visualizationLink.getUser().getId());

        assertEquals(1, visualizationLinks.count());
    }

    @Test
    public void findVisualizationLinkByUserAndMovie() throws Exception {
        repository.save(visualizationLink);

        Optional<VisualizationLink> possibleLink =
                repository.findByUserAndMovie(user, movie);

        assertTrue(possibleLink.isPresent());
        assertEquals(user.getUsername(), possibleLink.get().getUser().getUsername());
        assertEquals(movie.getTitle(), possibleLink.get().getMovie().getTitle());
    }

    @Test
    public void updateVisualizationLink() throws Exception {
        VisualizationLink savedVisualizationLink = repository.save(visualizationLink);

        LocalDateTime expeditionDate = LocalDateTime.now();
        savedVisualizationLink.setExpeditionDate(expeditionDate);
        VisualizationLink modifiedVisualizationLink = repository.save(savedVisualizationLink);

        System.out.println(modifiedVisualizationLink);

        assertEquals(expeditionDate, modifiedVisualizationLink.getExpeditionDate());
    }

    @Test
    public void deleteVisualizationLink() throws Exception {
        VisualizationLink savedVisualizationLink = repository.save(visualizationLink);
        repository.delete(savedVisualizationLink);
        Optional<VisualizationLink> noVisualizationLink =
                repository.findByToken(visualizationLink.getToken());
        assertFalse(noVisualizationLink.isPresent());
    }

    @Test
    public void deleteByExpeditionDateBefore_deletesOneBefore() throws Exception {
        VisualizationLink visualizationLink = repository.save(this.visualizationLink);
        repository.deleteByExpeditionDateBefore(LocalDateTime.now().plusSeconds(1));
        Optional<VisualizationLink> noVisualizationLink =
                repository.findByToken(visualizationLink.getToken());
        assertFalse(noVisualizationLink.isPresent());
    }

    @Test
    public void deleteByExpeditionDateBefore_doesNotDeleteAtTheExactTime() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        VisualizationLink visualizationLink = repository.save(this.visualizationLink);
        repository.deleteByExpeditionDateBefore(now);
        Optional<VisualizationLink> possibleVisualizationLink =
                repository.findByToken(visualizationLink.getToken());
        assertTrue(possibleVisualizationLink.isPresent());
    }

    @Test
    public void deleteByExpeditionDateBefore_doesNotDeleteAfter() throws Exception {
        VisualizationLink visualizationLink = repository.save(this.visualizationLink);
        repository.deleteByExpeditionDateBefore(LocalDateTime.now().minusSeconds(2));
        Optional<VisualizationLink> possibleVisualizationLink =
                repository.findByToken(visualizationLink.getToken());
        assertTrue(possibleVisualizationLink.isPresent());
    }

    @Test
    public void deleteByUser() throws Exception {
        VisualizationLink visualizationLink = repository.save(this.visualizationLink);
        repository.deleteByUser_Id(user.getId());
        Optional<VisualizationLink> noVisualizationLink =
                repository.findByToken(visualizationLink.getToken());
        assertFalse(noVisualizationLink.isPresent());
    }

    @After
    public void tearDown() throws Exception {
        user = null;
        movie = null;
        visualizationLink = null;
    }
}