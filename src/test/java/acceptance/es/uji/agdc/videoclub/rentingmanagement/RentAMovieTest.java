package acceptance.es.uji.agdc.videoclub.rentingmanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.*;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.MovieQueryTypeSingle;
import es.uji.agdc.videoclub.services.MovieService;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.VisualizationLinkService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alberto on 10/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class RentAMovieTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private VisualizationLinkService linkService;

    private User user;
    private Movie movie;

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
    public void create_allConditionsAreFine_resultIsOk() throws Exception {
        // Given a registered member
        userService.create(user);

        // And a movie with available copies
        movieService.create(movie);

        // When the user tries to rent the movie
        Result result = linkService.create(new VisualizationLink(user, movie));
        Optional<Movie> possibleMovie = movieService.findBy(MovieQueryTypeSingle.ID, movie.getId().toString());

        // Then the system returns OK
        assertTrue(result.isOk());
        assertEquals(5, possibleMovie.get().getActualAvailableCopies());
    }

    @Test
    public void create_movieWithoutCopies_returnsError_NO_COPIES_AVAILABLE() throws Exception {
        // Given a registered member
        userService.create(user);

        // And a movie with no available copies
        movie.setAvailableCopies(0);
        movieService.create(movie);

        // When the user tries to rent the movie
        Result result = linkService.create(new VisualizationLink(user, movie));

        // Then the system returns an ERROR
        assertTrue(result.isError());
        assertEquals("NO_COPIES_AVAILABLE", result.getMsg());
    }

    @Test
    public void create_nonExistingMovie_returnsError_MOVIE_NOT_FOUND() throws Exception {
        // Given a registered member
        userService.create(user);

        // And a non-created movie
        movie.setId(1L);
        movieService.create(movie);

        // When the user tries to rent the movie
        Result result = linkService.create(new VisualizationLink(user, movie));

        // Then the system returns an ERROR
        assertTrue(result.isError());
        assertEquals("MOVIE_NOT_FOUND", result.getMsg());
    }

    @Test
    public void create_userAlreadyWatchingMovie_returnsError_ALREADY_WATCHING() throws Exception {
        // Given a registered member
        userService.create(user);

        // ,a movie
        movieService.create(movie);

        // And a link for the given user and movie
        linkService.create(new VisualizationLink(user, movie));

        // When the user tries to rent the movie
        Result result = linkService.create(new VisualizationLink(user, movie));

        // Then the system returns an ERROR
        assertTrue(result.isError());
        assertEquals("ALREADY_WATCHING", result.getMsg());
    }

    @After
    public void tearDown() throws Exception {
        user = null;
        movie = null;
    }
}
