package integration.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.repositories.ActorRepository;
import es.uji.agdc.videoclub.repositories.DirectorRepository;
import es.uji.agdc.videoclub.repositories.GenreRepository;
import es.uji.agdc.videoclub.repositories.MovieRepository;
import es.uji.agdc.videoclub.services.MovieQueryTypeMultiple;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 11/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class MovieServiceDBTest {
    @Autowired
    private MovieService service;

    @Autowired
    private MovieRepository repository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private GenreRepository genreRepository;

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
                .setAvailableCopies(3);

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
                .setAvailableCopies(3);
    }

    @Test
    public void create() throws Exception {
        service.create(movie);
        Optional<Movie> possibleMovie = repository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear());
        assertTrue(possibleMovie.isPresent());
        Movie recoveredMovie = possibleMovie.get();
        assertTrue(!recoveredMovie.isNew());
        assertEquals(this.movie.getTitle(), recoveredMovie.getTitle());
        assertEquals(this.movie.getTitleOv(), recoveredMovie.getTitleOv());
        assertEquals(this.movie.getActors().size(), recoveredMovie.getActors().size());
        assertEquals(this.movie.getDirectors().size(), recoveredMovie.getDirectors().size());
        assertEquals(this.movie.getGenres().size(), recoveredMovie.getGenres().size());
        assertEquals(this.movie.getDescription(), recoveredMovie.getDescription());
        assertEquals(this.movie.getDescription(), recoveredMovie.getDescription());
    }

    @Test
    public void create_sharingResources() throws Exception {
        service.create(movie);
        service.create(anotherMovie);

        assertEquals(2, actorRepository.count());
        assertEquals(1, directorRepository.count());
        assertEquals(2, genreRepository.count());
    }

    @Test
    public void findBy() throws Exception {
        service.create(movie);
        Optional<Movie> possibleMovie =
                service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR, movie.getTitle(), String.valueOf(movie.getYear()));
        assertTrue(possibleMovie.isPresent());
    }

    @Test
    public void findAllBy_title() throws Exception {
        service.create(movie);
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE,
                movie.getTitle().split(" ")[0]);

        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_titleOv() throws Exception {
        service.create(movie);
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE_OV,
                movie.getTitleOv().split(" ")[0]);

        assertEquals(movie.getTitleOv(), movies.findFirst().get().getTitleOv());
    }

    @Test
    public void findAllBy_actors() throws Exception {
        service.create(movie);

        String actorName = movie.getActors().get(0).getName();
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS,
                actorName.split(" ")[0]);

        assertTrue(movies.findFirst().get().getActors().stream()
                .anyMatch(actor -> actor.getName().equals(actorName)));
    }

    @Test
    public void findAllBy_directors() throws Exception {
        service.create(movie);

        String directorName = movie.getDirectors().get(0).getName();
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.DIRECTORS,
                directorName.split(" ")[0]);

        assertTrue(movies.findFirst().get().getDirectors().stream()
                .anyMatch(director -> director.getName().equals(directorName)));
    }

    @Test
    public void findAllBy_genres() throws Exception {
        service.create(movie);

        String genreName = movie.getGenres().get(0).getName();
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.GENRES,
                genreName.split(" ")[0]);

        assertTrue(movies.findFirst().get().getGenres().stream()
                .anyMatch(genre -> genre.getName().equals(genreName)));
    }

    @Test
    public void findAllBy_year() throws Exception {
        service.create(movie);

        int movieYear = movie.getYear();
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR,
                String.valueOf(movieYear));

        assertEquals(movieYear, movies.findFirst().get().getYear());
    }

    @Test
    public void findAllBy_all() throws Exception {
        service.create(movie);

        String query = String.format("%s %d", movie.getTitle(), movie.getYear());

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, query);
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAll() throws Exception {
        service.create(movie);
        Stream<Movie> allMovies = service.findAll();
        assertTrue(allMovies.count() > 0);
    }

    @Test
    public void update_simpleFields() throws Exception {
        service.create(movie);

        Movie savedMovie = repository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()).get();

        String newTitle = "Capitán América: El Primer Vengador";
        savedMovie.setTitle(newTitle);
        Result result = service.update(movie);

        Movie updatedMovie = repository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()).get();
        assertTrue(result.isOk());
        assertEquals(newTitle, updatedMovie.getTitle());
    }

    @Test
    public void update_addComplexField() throws Exception {
        service.create(movie);

        Movie savedMovie = repository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()).get();

        int numberOfActors = savedMovie.getActors().size();
        Actor actor = new Actor("Samuel L. Jackson");
        savedMovie.addActor(actor);
        Result result = service.update(movie);

        Movie updatedMovie = repository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()).get();
        assertTrue(result.isOk());
        assertEquals(numberOfActors + 1, updatedMovie.getActors().size());
    }

    @Test
    public void update_removeComplexField() throws Exception {
        service.create(movie);

        Movie savedMovie = repository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()).get();

        List<Actor> actors = savedMovie.getActors();
        int numberOfActors = actors.size();
        actors.remove(0);
        savedMovie.setActors(actors);
        Result result = service.update(movie);

        Movie updatedMovie = repository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()).get();
        assertTrue(result.isOk());
        assertEquals(numberOfActors - 1, updatedMovie.getActors().size());
    }

    @Test
    public void remove() throws Exception {
        service.create(movie);
        Long movieId = movie.getId();
        Result result = service.remove(movieId);

        Movie movie = repository.findOne(movieId).get();
        assertTrue(result.isOk());
        assertEquals(0, movie.getAvailableCopies());
    }

    @After
    public void tearDown() throws Exception {
        movie = null;
        anotherMovie = null;
    }

}