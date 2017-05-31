package acceptance.es.uji.agdc.videoclub.moviemanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.repositories.MovieRepository;
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

import static org.junit.Assert.*;

/**
 * Created by Alberto on 11/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class MovieCreationTest {
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
    public void create_allFieldsAreValid_resultIsOk() throws Exception {
        Result result = service.create(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void create_descriptionIsEmpty_EMPTY_PARAMETER_DESCRIPTION() throws Exception {
        movie.setDescription("");
        Result result = service.create(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void create_descriptionIsTooShort_INVALID_PARAMETER_DESCRIPTION() throws Exception {
        movie.setDescription("Sinopsis demasiado corta");
        Result result = service.create(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void create_movieAlreadyExists_MOVIE_ALREADY_EXISTS() throws Exception {
        service.create(movie);
        Movie movie2 = new Movie()
                .setTitle(movie.getTitle())
                .setTitleOv(movie.getTitleOv())
                .setYear(movie.getYear())
                    .addActor(new Actor("Sr. X"))
                    .addActor(new Actor("Batman"))
                .addDirector(new Director("Tipo de incógnito"))
                    .addGenre(new Genre("Misterio"))
                    .addGenre(new Genre("Comedia"))
                .setDescription("Reina en mi espíritu una alegría admirable, muy parecida a las dulces " +
                        "alboradas de la primavera, de que gozo aquí con delicia. Estoy solo, y me felicito " +
                        "de vivir en este país, el más a propósito para.")
                .setAvailableCopies(4);

        Result result = service.create(movie2);
        assertTrue(result.isError());
        assertEquals("MOVIE_ALREADY_EXISTS", result.getMsg());
    }

    @After
    public void tearDown() throws Exception {
        movie = null;
    }
}
