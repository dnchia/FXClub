package acceptance.es.uji.agdc.videoclub.moviemanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.MovieQueryTypeSingle;
import es.uji.agdc.videoclub.services.MovieService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 11/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class MovieRecoveryTest {

    @Autowired
    private MovieService service;
    
    private Movie movie;
    
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
    }

    @Test
    public void findByTitleAndYear_existingMovie_showsUp() throws Exception {
        // Given that the film is on the system
        service.create(movie);

        // We try to find it via its title and year
        Optional<Movie> possibleMovie = service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR, movie.getTitle(), String.valueOf(movie.getYear()));

        // Assert that the movie is present and has that title and year
        assertTrue(possibleMovie.isPresent());
        assertEquals(movie.getTitle(), possibleMovie.get().getTitle());
        assertEquals(movie.getYear(), possibleMovie.get().getYear());
    }

    @Test
    public void findByTitleAndYear_nonexistingMovie_notFound() throws Exception {
        // Given that the film is not on the system

        // We try to find it via its title and year
        Optional<Movie> possibleMovie = service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR, movie.getTitle(), String.valueOf(movie.getYear()));

        // Assert that the movie is present and has that title and year
        assertFalse(possibleMovie.isPresent());
    }

    @After
    public void tearDown() throws Exception {
        movie = null;
    }
}
