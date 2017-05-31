package acceptance.es.uji.agdc.videoclub.moviemanagement;

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

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alberto on 11/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class MovieUnsubscribeTest {
    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    @Autowired
    private VisualizationLinkService linkService;

    private Movie movie;
    private User userOne;
    private User userTwo;
    private User userThree;

    @Before
    public void setUp() throws Exception {
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
                .setAvailableCopies(3);

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
                .setUsername("paquito68")
                .setPassword("pacosd69")
                .setLastPayment(LocalDate.now().minusMonths(1).minusDays(1));

        userThree = UserFactory.createMember()
                .setDni("51085104B")
                .setName("Pedro Ramirez López")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pedro@hotmail.com")
                .setUsername("paquito67")
                .setPassword("pacosd69")
                .setLastPayment(LocalDate.now().minusMonths(2).minusDays(1));
    }

    @Test
    public void unsubscribeMovie_movieWithoutRentals_deactivatesSuccessfully() throws Exception {
        // Given an active movie on the system
        movieService.create(movie);
        Movie savedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // When an administrator deactivates it
        Result result = movieService.remove(savedMovie.getId());

        // Then the movie no longer has available copies
        Movie deactivatedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();
        assertTrue(result.isOk());
        assertEquals(0, deactivatedMovie.getAvailableCopies());
    }

    @Test
    public void unsubscribeMovie_movieWithRentals_deactivatesSuccessfullyAndKeepsRentals() throws Exception {
        // Given an active movie on the system
        movieService.create(movie);
        Movie savedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // Three users
        userService.create(userOne);
        userService.create(userTwo);
        userService.create(userThree);

        // And three links
        linkService.create(new VisualizationLink(userOne, movie));
        linkService.create(new VisualizationLink(userTwo, movie));
        linkService.create(new VisualizationLink(userThree, movie));

        // When an administrator deactivates it
        Result result = movieService.remove(savedMovie.getId());

        // Then the movie no longer has available copies but keeps existing copies
        Movie deactivatedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();
        assertTrue(result.isOk());
        assertEquals(0, deactivatedMovie.getAvailableCopies());
        assertEquals(3, movie.getVisualizationLinks().size());
    }

    @Test
    public void unsubscribeMovie_nonExistingMovie_returnsError_MOVIE_NOT_FOUND() throws Exception {
        // Given an active movie on the system
        movieService.create(movie);
        Movie savedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // When an tries to deactivate a non-existing movie
        Result result = movieService.remove(savedMovie.getId() + 1);

        // Then the system complains about it
        Movie deactivatedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();
        assertTrue(result.isError());
        assertEquals("MOVIE_NOT_FOUND", result.getMsg());
    }

    @Test
    public void unsubscribeMovie_rentingADeactivatedMovieWithLinks_returnsError_NO_COPIES_AVAILABLE() throws Exception {
        // Given an active movie on the system
        movieService.create(movie);
        Movie savedMovie = movieService.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // Three users
        userService.create(userOne);
        userService.create(userTwo);


        // And three links
        linkService.create(new VisualizationLink(userOne, movie));

        // When an administrator deactivates it
        movieService.remove(savedMovie.getId());

        // And a user tries to get a link
        Result result = linkService.create(new VisualizationLink(userTwo, movie));

        // Then the movie no longer has available copies but keeps existing copies
        assertTrue(result.isError());
        assertEquals("NO_COPIES_AVAILABLE", result.getMsg());
    }

    @After
    public void tearDown() throws Exception {
        movie = null;
        userOne = null;
        userTwo = null;
        userThree = null;
    }
}
