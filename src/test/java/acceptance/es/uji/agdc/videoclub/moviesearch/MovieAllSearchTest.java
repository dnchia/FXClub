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
public class MovieAllSearchTest {

    @Autowired
    private MovieService service;
    
    private Movie movieOne;
    private Movie movieTwo;
    private Movie movieThree;
    private Movie movieFour;
    private Movie movieFive;

    @Before
    public void setUp() throws Exception {
        movieOne = new Movie()
                .setTitle("Capitán América")
                .setTitleOv("Captain America")
                .setYear(2011)
                .addActor(new Actor("Chris Evans"))
                .addDirector(new Director("Paul Gutiérrez"))
                .addGenre(new Genre("Acción"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(6);

        movieTwo = new Movie()
                .setTitle("Capitán F")
                .setTitleOv("Arrow Captain F")
                .setYear(1987)
                .addActor(new Actor("Sr. X"))
                .addDirector(new Director("Tipode Incognito"))
                .addGenre(new Genre("Misterio"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(4);

        movieThree = new Movie()
                .setTitle("Zelda")
                .setTitleOv("The Legend of Zelda")
                .setYear(1979)
                .addActor(new Actor("Super Mario"))
                .addDirector(new Director("Hitoshi Kudeiro"))
                .addGenre(new Genre("Aventura"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(4);

        movieFour = new Movie()
                .setTitle("El resplandor")
                .setTitleOv("The shining")
                .setYear(1980)
                .addActor(new Actor("Danny Lloyd"))
                .addDirector(new Director("Stanley Kubrick"))
                .addGenre(new Genre("Terror"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(4);

        movieFive = new Movie()
                .setTitle("La Pantera Rosa")
                .setTitleOv("The Pink Panther")
                .setYear(2000)
                .addActor(new Actor("Rudolf Harrison"))
                .addDirector(new Director("Stanley Kubrick"))
                .addGenre(new Genre("Comedia"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(4);
    }

    @Test
    public void findAll_withTitle_returnsAllFullAndPartialMatchingMoviesOrdered() throws Exception {
        // Given five different movies on the database
        service.create(movieOne);
        service.create(movieTwo);
        service.create(movieThree);
        service.create(movieFour);
        service.create(movieFive);

        // When the system performs a query to search them
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, "evans arrow aventura lloyd 2000");

        // Assert that it returns both of them ordered by matching
        List<Movie> movieList = movies.collect(Collectors.toList());
        movieList.stream().forEach(System.out::println);
        assertEquals(5, movieList.size());
        assertEquals(movieTwo.getTitle(), movieList.get(0).getTitle());
        assertEquals(movieOne.getTitle(), movieList.get(1).getTitle());
        assertEquals(movieFour.getTitle(), movieList.get(2).getTitle());
        assertEquals(movieThree.getTitle(), movieList.get(3).getTitle());
        assertEquals(movieFive.getTitle(), movieList.get(4).getTitle());
    }

    @Test
    public void findAll_withNothing_returnsEmptyListOfMovies() throws Exception {
        // Given all the movies that are available on the system
        service.create(movieOne);
        service.create(movieTwo);
        service.create(movieThree);
        service.create(movieFour);
        service.create(movieFive);

        // When the system performs an empty query
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, "");

        // Assert that it returns an empty list
        assertEquals(0, movies.count());
    }
    @After
    public void tearDown() throws Exception {
        movieOne = null;
        movieTwo = null;
        movieThree = null;
        movieFour = null;
        movieFive = null;
    }
}
