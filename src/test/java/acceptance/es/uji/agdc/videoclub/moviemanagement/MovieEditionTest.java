package acceptance.es.uji.agdc.videoclub.moviemanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.MovieQueryTypeSingle;
import es.uji.agdc.videoclub.services.MovieService;
import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alberto on 11/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class MovieEditionTest {
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
                    .addGenre(new Genre("Acción"))
                    .addGenre(new Genre("Comedia"))
                .setDescription("Una mañana, tras un sueño intranquilo, Gregorio Samsa se despertó\n" +
                        "convertido en un monstruoso insecto. Estaba echado de espaldas sobre un duro " +
                        "caparazón y, al alzar la cabeza, vio su vientre convexo y")
                .setAvailableCopies(3);

        anotherMovie = new Movie()
                .setTitle("Capitán América")
                .setTitleOv("Captain America")
                .setYear(1990)
                    .addActor(new Actor("Matt Salinger"))
                    .addActor(new Actor("Ronny Cox"))
                .addDirector(new Director("Albert Pyun"))
                    .addGenre(new Genre("Acción"))
                    .addGenre(new Genre("Comedia"))
                .setDescription("Una mañana, tras un sueño intranquilo, Gregorio Samsa se despertó\n" +
                        "convertido en un monstruoso insecto. Estaba echado de espaldas sobre un duro " +
                        "caparazón y, al alzar la cabeza, vio su vientre convexo y")
                .setAvailableCopies(4);
    }

    @Test
    public void updateMovie_withValidFields_returnsOk() throws Exception {
        // Given a saved movie
        service.create(movie);
        Movie savedMovie = service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // When the movie is modified with valid changes
        String newDescription = "Y, viéndole don Quijote de aquella manera, con muestras de tanta\n" +
                "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no hace más que" +
                " otro. Todas estas borrascas que nos suceden son. Y entonces el Quijote se murió.";
        savedMovie.setDescription(newDescription);
        Result result = service.update(savedMovie);

        // Then the system returns that everything went ok
        Movie updatedMovie = service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();
        assertTrue(result.isOk());
        assertEquals(newDescription, updatedMovie.getDescription());
    }

    @Test
    public void updateMovie_withEmptyFields_returnsError_EMPTY_PARAMETER() throws Exception {
        // Given a saved movie
        service.create(movie);
        Movie savedMovie = service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // When the movie is modified with an empty parameter
        savedMovie.setDescription("");
        Result result = service.update(savedMovie);

        // Then the system complains about it
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void updateMovie_withInvalidFields_returnsError_INVALID_PARAMETER() throws Exception {
        // Given a saved movie
        service.create(movie);
        Movie savedMovie = service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                movie.getTitle(), Integer.toString(movie.getYear())).get();

        // When the movie is modified with an invalid parameter
        savedMovie.setYear(20115);
        Result result = service.update(savedMovie);

        // Then the system complains about it
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Year]", result.toString());
    }

    @Test
    public void updateMovie_withInvalidFields_returnsError_ALREADY_EXISTS() throws Exception {
        // Given two saved movies with same title but different year
        service.create(movie);
        service.create(anotherMovie);
        Movie savedMovie = service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR,
                anotherMovie.getTitle(), Integer.toString(anotherMovie.getYear())).get();

        // When the second movie is modified to match the first movie year
        Movie modifiedMovie = new Movie() // Because inside a transaction we need to create a new object
                .setTitle(savedMovie.getTitle())
                .setTitleOv(savedMovie.getTitleOv())
                .setYear(2011)
                .setActors(savedMovie.getActors())
                .setDirectors(savedMovie.getDirectors())
                .setGenres(savedMovie.getGenres())
                .setDescription(savedMovie.getDescription())
                .setAvailableCopies(savedMovie.getAvailableCopies());
        modifiedMovie.setId(savedMovie.getId());
        Result result = service.update(modifiedMovie);

        // Then the system complains about it
        assertTrue(result.isError());
        assertEquals("MOVIE_ALREADY_EXISTS", result.getMsg());
    }

    @After
    public void tearDown() throws Exception {
        movie = null;
        anotherMovie = null;
    }
}
