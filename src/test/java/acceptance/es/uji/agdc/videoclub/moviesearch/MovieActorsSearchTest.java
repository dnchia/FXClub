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
public class MovieActorsSearchTest {

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
                .addActor(new Actor("Sr. Q"))
                .addActor(new Actor("Lambda Gómez"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Comedy"))
                .addGenre(new Genre("Drama"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(4);
    }

    @Test
    public void findAll_withActorsByName_returnsAllFullAndPartialMatchingMoviesOrdered() throws Exception {
        // Given multiple movies stored on the system
        service.create(movie);
        service.create(anotherMovie);

        // When we try to find a movie by actor name
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, "Chris");

        // Then the system returns the only movie with that actor
        List<Movie> movieList = movies.collect(Collectors.toList());
        assertEquals(1, movieList.size());
        assertEquals(movie.getTitle(), movieList.get(0).getTitle());
    }

    @Test
    public void findAll_withActorsBySurname_returnsAllFullAndPartialMatchingMoviesOrdered() throws Exception {
        // Given multiple movies stored on the system
        service.create(movie);
        service.create(anotherMovie);

        // When we try to find a movie by actor surname
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, "Evans");

        // Then the system returns the only movie with that actor
        List<Movie> movieList = movies.collect(Collectors.toList());
        assertEquals(1, movieList.size());
        assertEquals(movie.getTitle(), movieList.get(0).getTitle());
    }

    @Test
    public void findAll_withActorsByFullName_returnsAllFullAndPartialMatchingMoviesOrdered() throws Exception {
        // Given multiple movies stored on the system
        service.create(movie);
        service.create(anotherMovie);

        // When we try to find a movie by actor full name
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, "Chris Evans");

        // Then the system returns the only movie with that actor
        List<Movie> movieList = movies.collect(Collectors.toList());
        assertEquals(1, movieList.size());
        assertEquals(movie.getTitle(), movieList.get(0).getTitle());
    }

    @Test
    public void findAll_withNoActorName_returnsEmptyListOfMovies() throws Exception {
        // Given two different movies on the database that have a similar actor name
        service.create(movie);
        service.create(anotherMovie);

        // When the system performs an empty query
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, "");

        // Assert that it returns an empty list
        assertEquals(0, movies.count());
    }

    @After
    public void tearDown() throws Exception {
        movie = null;
        anotherMovie = null;
    }
}
