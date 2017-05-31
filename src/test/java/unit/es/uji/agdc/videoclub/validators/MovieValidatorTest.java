package unit.es.uji.agdc.videoclub.validators;

import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.validators.MovieValidator;
import es.uji.agdc.videoclub.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 11/12/2016.
 */
public class MovieValidatorTest {

    private Validator<Movie> validator;
    private Movie movie;

    @Before
    public void setUp() throws Exception {
        validator = new MovieValidator();
        movie = new Movie()
                .setTitle("Capitán América")
                .setTitleOv("Captain America")
                .setYear(2011)
                .addActor(new Actor("Chris Evans"))
                .addActor(new Actor("Hayley Atwell"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Comedia"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(3);
    }

    @Test
    public void validate_valid_isValid() throws Exception {
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_emptyTitle_isEmpty() throws Exception {
        movie.setTitle("");
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Title]", result.toString());
    }

    @Test
    public void validate_nullTitle_isEmpty() throws Exception {
        movie.setTitle(null);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Title]", result.toString());
    }

    @Test
    public void validate_emptyTitleOV_isEmpty() throws Exception {
        movie.setTitleOv("");
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[TitleOv]", result.toString());
    }

    @Test
    public void validate_nullTitleOV_isEmpty() throws Exception {
        movie.setTitleOv(null);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[TitleOv]", result.toString());
    }

    @Test
    public void validate_emptyDescription_isEmpty() throws Exception {
        movie.setDescription("");
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void validate_nullDescription_isEmpty() throws Exception {
        movie.setDescription(null);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void validate_emptyActors_isEmpty() throws Exception {
        movie.setActors(new LinkedList<>());
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Actors]", result.toString());
    }

    @Test
    public void validate_nullActors_isEmpty() throws Exception {
        movie.setActors(null);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Actors]", result.toString());
    }

    @Test
    public void validate_emptyDirectors_isEmpty() throws Exception {
        movie.setDirectors(new LinkedList<>());
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Directors]", result.toString());
    }

    @Test
    public void validate_nullDirectors_isEmpty() throws Exception {
        movie.setDirectors(null);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Directors]", result.toString());
    }

    @Test
    public void validate_emptyGenres_isEmpty() throws Exception {
        movie.setGenres(new LinkedList<>());
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Genres]", result.toString());
    }

    @Test
    public void validate_nullGenres_isEmpty() throws Exception {
        movie.setGenres(null);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Genres]", result.toString());
    }

    @Test
    public void validate_emptyActorName_isEmpty() throws Exception {
        LinkedList<Actor> actors = new LinkedList<>();
        actors.add(new Actor(""));
        movie.setActors(actors);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Actor_0]", result.toString());
    }

    @Test
    public void validate_nullActorName_isEmpty() throws Exception {
        LinkedList<Actor> actors = new LinkedList<>();
        actors.add(new Actor(null));
        movie.setActors(actors);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Actor_0]", result.toString());
    }

    @Test
    public void validate_multipleEmptyActorName_isEmpty() throws Exception {
        LinkedList<Actor> actors = new LinkedList<>();
        actors.add(new Actor(""));
        actors.add(new Actor(""));
        movie.setActors(actors);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Actor_0, Actor_1]", result.toString());
    }

    @Test
    public void validate_emptyDirectorName_isEmpty() throws Exception {
        LinkedList<Director> directors = new LinkedList<>();
        directors.add(new Director(""));
        movie.setDirectors(directors);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Director_0]", result.toString());
    }

    @Test
    public void validate_nullDirectorName_isEmpty() throws Exception {
        LinkedList<Director> directors = new LinkedList<>();
        directors.add(new Director(null));
        movie.setDirectors(directors);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Director_0]", result.toString());
    }

    @Test
    public void validate_multipleEmptyDirectorName_isEmpty() throws Exception {
        LinkedList<Director> directors = new LinkedList<>();
        directors.add(new Director(""));
        directors.add(new Director(""));
        movie.setDirectors(directors);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Director_0, Director_1]", result.toString());
    }

    @Test
    public void validate_emptyGenreName_isEmpty() throws Exception {
        LinkedList<Genre> genres = new LinkedList<>();
        genres.add(new Genre(""));
        movie.setGenres(genres);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Genre_0]", result.toString());
    }


    @Test
    public void validate_nullGenreName_isEmpty() throws Exception {
        LinkedList<Genre> genres = new LinkedList<>();
        genres.add(new Genre(null));
        movie.setGenres(genres);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Genre_0]", result.toString());
    }

    @Test
    public void validate_multipleEmptyGenreName_isEmpty() throws Exception {
        LinkedList<Genre> genres = new LinkedList<>();
        genres.add(new Genre(""));
        genres.add(new Genre(""));
        movie.setGenres(genres);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Genre_0, Genre_1]", result.toString());
    }

    @Test
    public void validate_completelyEmpty_isEmpty() throws Exception {
        Result result = validator.validate(new Movie());
        assertTrue(result.isError());
        assertEquals(
                "Result: ERROR(EMPTY_PARAMETER)[Title, TitleOv, Actors, Directors, Genres, Description]",
                result.toString()
        );
    }

    @Test
    public void validate_lowerThan1900year_isInvalid() throws Exception {
        movie.setYear(1899);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Year]", result.toString());
    }

    @Test
    public void validate_1900year_isValid() throws Exception {
        movie.setYear(1900);
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_actualYear_isValid() throws Exception {
        movie.setYear(LocalDate.now().getYear());
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_aboveActualYear_isInvalid() throws Exception {
        movie.setYear(LocalDate.now().getYear() + 1);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Year]", result.toString());
    }

    @Test
    public void validate_moreThan6Actors_isInvalid() throws Exception {
        LinkedList<Actor> actors = new LinkedList<>();
        IntStream.range(0, 7).forEach(value -> actors.add(new Actor("Actor" + value)));
        movie.setActors(actors);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Actors]", result.toString());
    }


    @Test
    public void validate_6Actors_isValid() throws Exception {
        LinkedList<Actor> actors = new LinkedList<>();
        IntStream.range(0, 6).forEach(value -> actors.add(new Actor("Actor" + value)));
        movie.setActors(actors);
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_moreThan4Directors_isInvalid() throws Exception {
        LinkedList<Director> directors = new LinkedList<>();
        IntStream.range(0, 5).forEach(value -> directors.add(new Director("Director" + value)));
        movie.setDirectors(directors);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Directors]", result.toString());
    }

    @Test
    public void validate_4Directors_isValid() throws Exception {
        LinkedList<Director> directors = new LinkedList<>();
        IntStream.range(0, 4).forEach(value -> directors.add(new Director("Director" + value)));
        movie.setDirectors(directors);
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_moreThan4Genres_isInvalid() throws Exception {
        LinkedList<Genre> genres = new LinkedList<>();
        IntStream.range(0, 5).forEach(value -> genres.add(new Genre("Genre" + value)));
        movie.setGenres(genres);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Genres]", result.toString());
    }

    @Test
    public void validate_4Genres_isValid() throws Exception {
        LinkedList<Genre> genres = new LinkedList<>();
        IntStream.range(0, 4).forEach(value -> genres.add(new Genre("Genre" + value)));
        movie.setGenres(genres);
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_descriptionLessThan200Chars_isInvalid() throws Exception {
        movie.setDescription(new String(new char[199]).replace('\0', 'a'));
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void validate_description200Chars_isValid() throws Exception {
        movie.setDescription(new String(new char[200]).replace('\0', 'a'));
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_description300Chars_isValid() throws Exception {
        movie.setDescription(new String(new char[300]).replace('\0', 'a'));
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_descriptionMoreThan300Chars_isInvalid() throws Exception {
        movie.setDescription(new String(new char[301]).replace('\0', 'a'));
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void validate_availableCopiesLessThan0_isInvalid() throws Exception {
        movie.setAvailableCopies(-1);
        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[AvailableCopies]", result.toString());
    }

    @Test
    public void validate_availableCopies0_isValid() throws Exception {
        movie.setAvailableCopies(0);
        Result result = validator.validate(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_everythingIsInvalid_isInvalid() throws Exception {
        movie.setYear(1899);

        LinkedList<Actor> actors = new LinkedList<>();
        IntStream.range(0, 7).forEach(value -> actors.add(new Actor("Actor" + value)));
        movie.setActors(actors);

        LinkedList<Director> directors = new LinkedList<>();
        IntStream.range(0, 5).forEach(value -> directors.add(new Director("Director" + value)));
        movie.setDirectors(directors);

        LinkedList<Genre> genres = new LinkedList<>();
        IntStream.range(0, 5).forEach(value -> genres.add(new Genre("Genre" + value)));
        movie.setGenres(genres);

        movie.setDescription(new String(new char[301]).replace('\0', 'a'));

        movie.setAvailableCopies(-1);

        Result result = validator.validate(movie);
        assertTrue(result.isError());
        assertEquals(
                "Result: ERROR(INVALID_PARAMETER)[Year, Actors, Directors, Genres, Description, AvailableCopies]",
                result.toString()
        );
    }

    @After
    public void tearDown() throws Exception {
        validator = null;
        movie = null;
    }

}