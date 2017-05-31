package acceptance.es.uji.agdc.videoclub.moviesearch;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.MovieQueryTypeMultiple;
import es.uji.agdc.videoclub.services.MovieService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alberto on 11/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class MovieYearSearchTest {

    @Autowired
    private MovieService service;
    
    private Movie movie;
    private Movie anotherMovie;
    
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
                .setAvailableCopies(6);

        anotherMovie = new Movie()
                .setTitle("Capitán F")
                .setTitleOv("Arrow Captain F")
                .setYear(1987)
                .addActor(new Actor("Chris Evans"))
                .addActor(new Actor("Hayley Atwell"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Comedy"))
                .addGenre(new Genre("Drama"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(4);
    }

    @Test
    public void findAll_withNoMatchingYear_returnsNoRecord() throws Exception {
        // Given two different movies on the database that have different years
        service.create(movie);
        service.create(anotherMovie);

        // When the system performs a with a completely different year
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, "1980");

        // Assert that it returns none of them
        assertEquals(0, movies.count());
    }

    @Test
    public void findAll_withMatchingYear_returnsOneRecord() throws Exception {
        // Given two different movies on the database that have different years
        service.create(movie);
        service.create(anotherMovie);

        // When the system performs a query to search one them
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, "2011");

        // Assert that it returns the one that matches with that year
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAll_withNoTitle_returnsEmptyListOfMovies() throws Exception {
        // Given two different movies on the database that have different years
        service.create(movie);
        service.create(anotherMovie);

        // When the system performs an empty query
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, "");

        // Assert that it returns an empty list
        assertEquals(0, movies.count());
    }
    @After
    public void tearDown() throws Exception {
        movie = null;
        anotherMovie = null;
    }
}
