package unit.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.repositories.MovieRepository;
import es.uji.agdc.videoclub.services.*;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.validators.MovieValidator;
import es.uji.agdc.videoclub.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Alberto on 11/12/2016.
 */
public class MovieServiceDBTest {

    private MovieService service;
    private MovieRepository movieRepository;
    private MovieAssetService assetService;

    private Movie movie;
    private Movie savedMovie;

    @Before
    public void setUp() throws Exception {
        movieRepository = mock(MovieRepository.class);
        assetService = mock(MovieAssetService.class);
        Validator<Movie> validator = new MovieValidator();
        service = new MovieServiceDB(movieRepository, assetService, validator);
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

        savedMovie = new Movie()
                .setTitle(movie.getTitle())
                .setTitleOv(movie.getTitleOv())
                .setYear(movie.getYear())
                .setActors(movie.getActors())
                .setDirectors(movie.getDirectors())
                .setGenres(movie.getGenres())
                .setDescription(movie.getDescription())
                .setAvailableCopies(movie.getAvailableCopies());
        savedMovie.setId(0L);

        when(movieRepository.save(any(Movie.class))).thenReturn(new Movie());

        when(movieRepository.findOne(anyLong())).thenReturn(Optional.empty());
        when(movieRepository.findByTitleIgnoreCaseAndYear(anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(movieRepository.findByTitleContainsIgnoreCase(anyString())).thenReturn(Stream.empty());
        when(movieRepository.findByTitleOvContainsIgnoreCase(anyString())).thenReturn(Stream.empty());
        when(movieRepository.findByActors_NameContainsIgnoreCase(anyString())).thenReturn(Stream.empty());
        when(movieRepository.findByDirectors_NameContainsIgnoreCase(anyString())).thenReturn(Stream.empty());
        when(movieRepository.findByGenres_NameContainsIgnoreCase(anyString())).thenReturn(Stream.empty());
        when(movieRepository.findByYear(anyInt())).thenReturn(Stream.empty());
        when(movieRepository.findAll()).thenReturn(Stream.empty());

        when(assetService.findActorByName(anyString())).thenReturn(Optional.empty());
        when(assetService.findDirectorByName(anyString())).thenReturn(Optional.empty());
        when(assetService.findGenreByName(anyString())).thenReturn(Optional.empty());
    }

    @Test
    public void create_nonExistingValidMovie_returnsOk() throws Exception {
        Result result = service.create(movie);

        verify(movieRepository, times(1))
                .findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear());
        verify(movieRepository, times(1)).save(movie);

        assertTrue(result.isOk());
    }


    @Test
    public void create_existingMovie_returnsError() throws Exception {
        when(movieRepository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()))
                .thenReturn(Optional.of(movie));

        Result result = service.create(movie);

        verify(movieRepository, never()).save(movie);

        assertTrue(result.isError());
        assertEquals("MOVIE_ALREADY_EXISTS", result.getMsg());
    }

    @Test
    public void create_movieIsNotNew_returnError() throws Exception {
        Movie movie = mock(Movie.class);
        when(movie.getId()).thenReturn(Long.valueOf(1));

        Result result = service.create(movie);
        verify(movieRepository, never())
                .findByTitleIgnoreCaseAndYear(anyString(), anyInt());
        verify(movieRepository, never()).save(movie);

        assertTrue(result.isError());
        assertEquals("MOVIE_ALREADY_EXISTS", result.getMsg());
    }

    @Test
    public void create_invalidMovie_returnError() throws Exception {
        movie.setDescription("");

        Result result = service.create(movie);
        verify(movieRepository, never())
                .findByTitleIgnoreCaseAndYear(anyString(), anyInt());
        verify(movieRepository, never()).save(movie);

        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void create_addsExistingActor_returnsOk() throws Exception {
        Actor actor = new Actor("Chris Evans".toLowerCase());
        when(assetService.findActorByName("Chris Evans")).thenReturn(Optional.of(actor));
        service.create(movie);
        assertTrue(movie.getActors().stream().anyMatch(actor1 -> actor1.getName().equals(actor.getName())));
    }

    @Test
    public void create_addsMultipleExistingActors_returnsOk() throws Exception {
        Actor actor1 = new Actor("Chris Evans".toLowerCase());
        Actor actor2 = new Actor("Hayley Atwell".toLowerCase());
        when(assetService.findActorByName("Chris Evans")).thenReturn(Optional.of(actor1));
        when(assetService.findActorByName("Hayley Atwell")).thenReturn(Optional.of(actor2));
        service.create(movie);
        assertTrue(movie.getActors().stream()
                .filter(actor -> actor.getName().equals(actor1.getName()) || actor.getName().equals(actor2.getName()))
                .count() == 2
        );
    }

    @Test
    public void create_addsExistingDirector_returnsOk() throws Exception {
        Director director = new Director("Joe Johnston".toLowerCase());
        when(assetService.findDirectorByName("Joe Johnston")).thenReturn(Optional.of(director));
        service.create(movie);
        assertTrue(movie.getDirectors().stream().anyMatch(director1 -> director1.getName().equals(director.getName())));
    }

    @Test
    public void create_addsMultipleExistingDirectors_returnsOk() throws Exception {
        movie.addDirector(new Director("Director 2"));
        Director director1 = new Director("Joe Johnston".toLowerCase());
        Director director2 = new Director("Director 2".toLowerCase());

        when(assetService.findDirectorByName("Joe Johnston")).thenReturn(Optional.of(director1));
        when(assetService.findDirectorByName("Director 2")).thenReturn(Optional.of(director2));
        service.create(movie);
        assertTrue(movie.getDirectors().stream()
                .filter(director -> director.getName().equals(director1.getName()) || director.getName().equals(director2.getName()))
                .count() == 2
        );
    }

    @Test
    public void create_addsExistingGenre_returnsOk() throws Exception {
        Genre genre = new Genre("Comedy".toLowerCase());
        when(assetService.findGenreByName("Comedy")).thenReturn(Optional.of(genre));
        service.create(movie);
        assertTrue(movie.getGenres().stream().anyMatch(genre1 -> genre1.getName().equals(genre.getName())));
    }

    @Test
    public void create_addsMultipleExistingGenres_returnsOk() throws Exception {
        Genre genre1 = new Genre("Comedy".toLowerCase());
        Genre genre2 = new Genre("Drama".toLowerCase());
        when(assetService.findGenreByName("Comedy")).thenReturn(Optional.of(genre1));
        when(assetService.findGenreByName("Drama")).thenReturn(Optional.of(genre2));
        service.create(movie);
        assertTrue(movie.getGenres().stream()
                .filter(genre -> genre.getName().equals(genre1.getName()) || genre.getName().equals(genre2.getName()))
                .count() == 2
        );
    }

    @Test
    public void findAll_empty() throws Exception {
        Stream<Movie> allMovies = service.findAll();
        assertEquals(0, allMovies.count());
    }

    @Test
    public void findAll_nonEmpty() throws Exception {
        when(movieRepository.findAll()).thenReturn(Stream.of(movie));
        Stream<Movie> allMovies = service.findAll();
        assertEquals(1, allMovies.count());
    }

    @Test
    public void findBy_withNoValues_returnsEmpty() throws Exception {
        Optional<Movie> possibleMovie = service.findBy(MovieQueryTypeSingle.ID);
        assertFalse(possibleMovie.isPresent());
    }

    @Test
    public void findBy_idWithValidLong_returnsMovie() throws Exception {
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(movie));
        Optional<Movie> possibleMovie = service.findBy(MovieQueryTypeSingle.ID, String.valueOf(0));
        assertTrue(possibleMovie.isPresent());
    }

    @Test
    public void findBy_idWithEmptyLong_returnsEmpty() throws Exception {
        Optional<Movie> possibleMovie = service.findBy(MovieQueryTypeSingle.ID);
        verify(movieRepository, never()).findOne(anyLong());
        assertFalse(possibleMovie.isPresent());
    }

    @Test
    public void findBy_idWithNonValidLong_returnsEmpty() throws Exception {
        Optional<Movie> possibleMovie = service.findBy(MovieQueryTypeSingle.ID, "blabla");
        verify(movieRepository, never()).findOne(anyLong());
        assertFalse(possibleMovie.isPresent());
    }

    @Test
    public void findBy_titleAndYearWithValidTitleAndYear_returnsMovie() throws Exception {
        when(movieRepository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()))
                .thenReturn(Optional.of(movie));
        Optional<Movie> possibleMovie =
                service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR, movie.getTitle(), String.valueOf(movie.getYear()));
        assertTrue(possibleMovie.isPresent());
    }

    @Test
    public void findBy_titleAndYearWithEmptyYear_returnsEmpty() throws Exception {
        Optional<Movie> possibleMovie =
                service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR, movie.getTitle());
        verify(movieRepository, never()).findByTitleIgnoreCaseAndYear(anyString(), anyInt());
        assertFalse(possibleMovie.isPresent());
    }

    @Test
    public void findBy_titleAndYearWithInvalidYear_returnsEmpty() throws Exception {
        Optional<Movie> possibleMovie =
                service.findBy(MovieQueryTypeSingle.TITLE_AND_YEAR, movie.getTitle(), "dos mil once");
        verify(movieRepository, never()).findByTitleIgnoreCaseAndYear(anyString(), anyInt());
        assertFalse(possibleMovie.isPresent());
    }

    @Test
    public void findAllBy_titleEmptyString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE, "");

        assertEquals(0, movies.count());
    }


    @Test
    public void findAllBy_titleNullString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE, null);

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_titleInvalidString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE, " ");

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_titleWithOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String[] titleWords = movie.getTitle().split(" ");
        Arrays.stream(titleWords).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE, titleWords[0]);

        verify(movieRepository, times(1)).findByTitleContainsIgnoreCase(titleWords[0]);
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_titleWithAllWords_returnsAMovie() throws Exception {
        movie.setId(0L);
        String[] titleWords = movie.getTitle().split(" ");
        Arrays.stream(titleWords).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE, movie.getTitle());

        Arrays.stream(titleWords).forEach(word ->
                verify(movieRepository, times(1)).findByTitleContainsIgnoreCase(word));

        List<Movie> movieList = movies.collect(Collectors.toList());
        assertEquals(movie.getTitle(), movieList.get(0).getTitle());
        assertEquals(1, movieList.size());
    }

    @Test
    public void findAllBy_multipleMoviesWithSameTitleMatching_returnsBothMovies() throws Exception {
        String[] titleWords = movie.getTitle().split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().setTitle(movie.getTitle());
        anotherMovie.setId(1L);

        Arrays.stream(titleWords).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.of(movie, anotherMovie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE, titleWords[0]);

        verify(movieRepository, times(1)).findByTitleContainsIgnoreCase(titleWords[0]);
        assertEquals(2, movies.count());
    }

    @Test
    public void findAllBy_multipleMoviesWithDifferentTitleMatching_returnsBothMoviesSortedByDescendingMatchNumber() throws Exception {
        String[] titleWords = movie.getTitle().split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().setTitle(titleWords[0]);
        anotherMovie.setId(1L);


        when(movieRepository.findByTitleContainsIgnoreCase(titleWords[0])).thenReturn(Stream.of(movie, anotherMovie));
        when(movieRepository.findByTitleContainsIgnoreCase(titleWords[1])).thenReturn(Stream.of(movie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE,
                String.format("%s %s", titleWords[0], titleWords[1]));

        verify(movieRepository, times(1)).findByTitleContainsIgnoreCase(titleWords[0]);
        verify(movieRepository, times(1)).findByTitleContainsIgnoreCase(titleWords[1]);
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_titleOvEmptyString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE_OV, "");

        assertEquals(0, movies.count());
    }


    @Test
    public void findAllBy_titleOvNullString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE_OV, null);

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_titleOvInvalidString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE_OV, " ");

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_titleOvWithOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String[] titleOvWords = movie.getTitleOv().split(" ");
        Arrays.stream(titleOvWords).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE_OV, titleOvWords[0]);

        verify(movieRepository, times(1)).findByTitleOvContainsIgnoreCase(titleOvWords[0]);
        assertEquals(movie.getTitleOv(), movies.findFirst().get().getTitleOv());
    }

    @Test
    public void findAllBy_titleOvWithAllWords_returnsAMovie() throws Exception {
        movie.setId(0L);
        String[] titleOvWords = movie.getTitleOv().split(" ");
        Arrays.stream(titleOvWords).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE_OV, movie.getTitleOv());

        Arrays.stream(titleOvWords).forEach(word ->
                verify(movieRepository, times(1)).findByTitleOvContainsIgnoreCase(word));

        List<Movie> movieList = movies.collect(Collectors.toList());
        assertEquals(movie.getTitleOv(), movieList.get(0).getTitleOv());
        assertEquals(1, movieList.size());
    }

    @Test
    public void findAllBy_multipleMoviesWithSameTitleOvMatching_returnsBothMovies() throws Exception {
        String[] titleOvWords = movie.getTitleOv().split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().setTitleOv(movie.getTitleOv());
        anotherMovie.setId(1L);

        Arrays.stream(titleOvWords).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.of(movie, anotherMovie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE_OV, titleOvWords[0]);

        verify(movieRepository, times(1)).findByTitleOvContainsIgnoreCase(titleOvWords[0]);
        assertEquals(2, movies.count());
    }

    @Test
    public void findAllBy_multipleMoviesWithDifferentTitleOvMatching_returnsBothMoviesSortedByDescendingMatchNumber() throws Exception {
        String[] titleOvWords = movie.getTitleOv().split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().setTitleOv(titleOvWords[0]);
        anotherMovie.setId(1L);


        when(movieRepository.findByTitleOvContainsIgnoreCase(titleOvWords[0])).thenReturn(Stream.of(movie, anotherMovie));
        when(movieRepository.findByTitleOvContainsIgnoreCase(titleOvWords[1])).thenReturn(Stream.of(movie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.TITLE_OV,
                String.format("%s %s", titleOvWords[0], titleOvWords[1]));

        verify(movieRepository, times(1)).findByTitleOvContainsIgnoreCase(titleOvWords[0]);
        verify(movieRepository, times(1)).findByTitleOvContainsIgnoreCase(titleOvWords[1]);
        assertEquals(movie.getTitleOv(), movies.findFirst().get().getTitleOv());
    }

    @Test
    public void findAllBy_actorsEmptyString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, "");

        assertEquals(0, movies.count());
    }


    @Test
    public void findAllBy_actorsNullString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, null);

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_actorsInvalidString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, " ");

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_actorsWithOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String[] actorWords = movie.getActors().get(0).getName().split(" ");
        Arrays.stream(actorWords).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, actorWords[0]);

        verify(movieRepository, times(1)).findByActors_NameContainsIgnoreCase(actorWords[0]);
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_actorsWithAllWords_returnsAMovie() throws Exception {
        movie.setId(0L);
        String actorName = movie.getActors().get(0).getName();
        String[] actorWords = actorName.split(" ");
        Arrays.stream(actorWords).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, actorName);

        Arrays.stream(actorWords).forEach(word ->
                verify(movieRepository, times(1)).findByActors_NameContainsIgnoreCase(word));

        List<Movie> movieList = movies.collect(Collectors.toList());
        assertEquals(movie.getTitle(), movieList.get(0).getTitle());
        assertEquals(1, movieList.size());
    }

    @Test
    public void findAllBy_multipleMoviesWithSameActorMatching_returnsBothMovies() throws Exception {
        Actor firstActor = movie.getActors().get(0);
        String actorName = firstActor.getName();
        String[] actorWords = actorName.split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().addActor(firstActor);
        anotherMovie.setId(1L);

        Arrays.stream(actorWords).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie, anotherMovie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS, actorWords[0]);

        verify(movieRepository, times(1)).findByActors_NameContainsIgnoreCase(actorWords[0]);
        assertEquals(2, movies.count());
    }

    @Test
    public void findAllBy_multipleMoviesWithDifferentActorMatching_returnsBothMoviesSortedByDescendingMatchNumber() throws Exception {
        Actor firstActor = movie.getActors().get(0);
        String actorName = firstActor.getName();
        String[] actorWords = actorName.split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().addActor(new Actor(actorWords[0]));
        anotherMovie.setId(1L);

        when(movieRepository.findByActors_NameContainsIgnoreCase(actorWords[0])).thenReturn(Stream.of(movie, anotherMovie));
        when(movieRepository.findByActors_NameContainsIgnoreCase(actorWords[1])).thenReturn(Stream.of(movie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ACTORS,
                String.format("%s %s", actorWords[0], actorWords[1]));

        verify(movieRepository, times(1)).findByActors_NameContainsIgnoreCase(actorWords[0]);
        verify(movieRepository, times(1)).findByActors_NameContainsIgnoreCase(actorWords[1]);
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_directorsEmptyString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.DIRECTORS, "");

        assertEquals(0, movies.count());
    }


    @Test
    public void findAllBy_directorsNullString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.DIRECTORS, null);

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_directorsInvalidString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.DIRECTORS, " ");

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_directorsWithOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String[] directorWords = movie.getDirectors().get(0).getName().split(" ");
        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.DIRECTORS, directorWords[0]);

        verify(movieRepository, times(1)).findByDirectors_NameContainsIgnoreCase(directorWords[0]);
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_directorsWithAllWords_returnsAMovie() throws Exception {
        movie.setId(0L);
        String directorName = movie.getDirectors().get(0).getName();
        String[] directorWords = directorName.split(" ");
        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.DIRECTORS, directorName);

        Arrays.stream(directorWords).forEach(word ->
                verify(movieRepository, times(1)).findByDirectors_NameContainsIgnoreCase(word));

        List<Movie> movieList = movies.collect(Collectors.toList());
        assertEquals(movie.getTitle(), movieList.get(0).getTitle());
        assertEquals(1, movieList.size());
    }

    @Test
    public void findAllBy_multipleMoviesWithSameDirectorMatching_returnsBothMovies() throws Exception {
        Director firstDirector = movie.getDirectors().get(0);
        String directorName = firstDirector.getName();
        String[] directorWords = directorName.split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().addDirector(firstDirector);
        anotherMovie.setId(1L);

        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie, anotherMovie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.DIRECTORS, directorWords[0]);

        verify(movieRepository, times(1)).findByDirectors_NameContainsIgnoreCase(directorWords[0]);
        assertEquals(2, movies.count());
    }

    @Test
    public void findAllBy_multipleMoviesWithDifferentDirectorMatching_returnsBothMoviesSortedByDescendingMatchNumber() throws Exception {
        Director firstDirector = movie.getDirectors().get(0);
        String directorName = firstDirector.getName();
        String[] directorWords = directorName.split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().addDirector(new Director(directorWords[0]));
        anotherMovie.setId(1L);

        when(movieRepository.findByDirectors_NameContainsIgnoreCase(directorWords[0])).thenReturn(Stream.of(movie, anotherMovie));
        when(movieRepository.findByDirectors_NameContainsIgnoreCase(directorWords[1])).thenReturn(Stream.of(movie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.DIRECTORS,
                String.format("%s %s", directorWords[0], directorWords[1]));

        verify(movieRepository, times(1)).findByDirectors_NameContainsIgnoreCase(directorWords[0]);
        verify(movieRepository, times(1)).findByDirectors_NameContainsIgnoreCase(directorWords[1]);
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_genresEmptyString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.GENRES, "");

        assertEquals(0, movies.count());
    }


    @Test
    public void findAllBy_genresNullString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.GENRES, null);

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_genresInvalidString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.GENRES, " ");

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_genresWithOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String[] genreWords = movie.getGenres().get(0).getName().split(" ");
        Arrays.stream(genreWords).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.GENRES, genreWords[0]);

        verify(movieRepository, times(1)).findByGenres_NameContainsIgnoreCase(genreWords[0]);
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_genresWithAllWords_returnsAMovie() throws Exception {
        movie.setId(0L);
        String genreName = movie.getGenres().get(0).getName();
        String[] genreWords = genreName.split(" ");
        Arrays.stream(genreWords).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.GENRES, genreName);

        Arrays.stream(genreWords).forEach(word ->
                verify(movieRepository, times(1)).findByGenres_NameContainsIgnoreCase(word));

        List<Movie> movieList = movies.collect(Collectors.toList());
        assertEquals(movie.getTitle(), movieList.get(0).getTitle());
        assertEquals(1, movieList.size());
    }

    @Test
    public void findAllBy_multipleMoviesWithSameGenreMatching_returnsBothMovies() throws Exception {
        Genre firstGenre = movie.getGenres().get(0);
        String genreName = firstGenre.getName();
        String[] directorWords = genreName.split(" ");
        movie.setId(0L);

        Movie anotherMovie = new Movie().addGenre(firstGenre);
        anotherMovie.setId(1L);

        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie, anotherMovie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.GENRES, directorWords[0]);

        verify(movieRepository, times(1)).findByGenres_NameContainsIgnoreCase(directorWords[0]);
        assertEquals(2, movies.count());
    }

    @Test
    public void findAllBy_multipleMoviesWithDifferentGenreMatching_returnsBothMoviesSortedByDescendingMatchNumber() throws Exception {
        Genre firstGenre = movie.getGenres().get(0);
        String genreName = firstGenre.getName();

        String anotherGenreName = String.format("Family %s", genreName);
        Movie anotherMovie = new Movie().addGenre(new Genre(anotherGenreName)).setTitle("A family comedy");
        anotherMovie.setId(1L);

        String[] genreWords = anotherGenreName.split(" ");
        movie.setId(0L);

        when(movieRepository.findByGenres_NameContainsIgnoreCase(genreWords[0])).thenReturn(Stream.of(anotherMovie));
        when(movieRepository.findByGenres_NameContainsIgnoreCase(genreWords[1])).thenReturn(Stream.of(movie, anotherMovie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.GENRES,
                anotherGenreName);

        verify(movieRepository, times(1)).findByGenres_NameContainsIgnoreCase(genreWords[0]);
        verify(movieRepository, times(1)).findByGenres_NameContainsIgnoreCase(genreWords[1]);
        assertEquals(anotherMovie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_yearEmptyString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, "");

        assertEquals(0, movies.count());
    }


    @Test
    public void findAllBy_yearNullString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, null);

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_yearInvalidString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, " ");

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_matchingYear_returnsOneRecord() throws Exception {
        movie.setId(0L);
        when(movieRepository.findByYear(movie.getYear())).thenReturn(Stream.of(movie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, String.valueOf(movie.getYear()));

        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_invalidYear_returnsOneRecord() throws Exception {
        movie.setId(0L);
        when(movieRepository.findByYear(movie.getYear())).thenReturn(Stream.of(movie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, "dos mil once");

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_withYearAndString_returnsOneRecord() throws Exception {
        movie.setId(0L);
        when(movieRepository.findByYear(movie.getYear())).thenReturn(Stream.of(movie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, String.format("Something %d", movie.getYear()));

        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_withYearMatchingMultiple_returnsMultipleRecords() throws Exception {
        movie.setId(0L);

        Movie anotherMovie = new Movie().setYear(this.movie.getYear());
        anotherMovie.setId(1L);

        when(movieRepository.findByYear(movie.getYear())).thenReturn(Stream.of(movie, anotherMovie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR, String.valueOf(movie.getYear()));

        assertEquals(2, movies.count());
    }

    @Test
    public void findAllBy_withMultipleYearMatchingMultiple_returnsMultipleRecords() throws Exception {
        movie.setId(0L);

        int movieYear = movie.getYear();
        int anotherMovieYear = movieYear - 1;
        Movie anotherMovie = new Movie().setYear(anotherMovieYear);
        anotherMovie.setId(1L);

        when(movieRepository.findByYear(movieYear)).thenReturn(Stream.of(movie));
        when(movieRepository.findByYear(anotherMovieYear)).thenReturn(Stream.of(anotherMovie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.YEAR,
                String.format("%d %d", movieYear, anotherMovieYear));

        assertEquals(2, movies.count());
    }

    @Test
    public void findAllBy_allEmptyString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, "");

        assertEquals(0, movies.count());
    }


    @Test
    public void findAllBy_allNullString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, null);

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_allInvalidString_returnsNoRecords() throws Exception {
        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, " ");

        assertEquals(0, movies.count());
    }

    @Test
    public void findAllBy_allWithTitleOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String movieTitle = movie.getTitle();
        String[] titleWords = movieTitle.split(" ");
        Arrays.stream(titleWords).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));
        Arrays.stream(titleWords).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(titleWords).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(titleWords).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(titleWords).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, movieTitle);

        Arrays.stream(titleWords).forEach(word ->
                verify(movieRepository, times(1)).findByTitleContainsIgnoreCase(word));
        assertEquals(movieTitle, movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_allWithTitleOvOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String movieTitleOv = movie.getTitleOv();
        String[] titleOvWords = movieTitleOv.split(" ");
        Arrays.stream(titleOvWords).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(titleOvWords).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));
        Arrays.stream(titleOvWords).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(titleOvWords).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(titleOvWords).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, movieTitleOv);

        Arrays.stream(titleOvWords).forEach(word ->
                verify(movieRepository, times(1)).findByTitleOvContainsIgnoreCase(word));
        assertEquals(movieTitleOv, movies.findFirst().get().getTitleOv());
    }

    @Test
    public void findAllBy_allWithActorOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String actorName = movie.getActors().get(0).getName();
        String[] actorWords = actorName.split(" ");
        Arrays.stream(actorWords).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(actorWords).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(actorWords).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));
        Arrays.stream(actorWords).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(actorWords).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, actorName);

        Arrays.stream(actorWords).forEach(word ->
                verify(movieRepository, times(1)).findByActors_NameContainsIgnoreCase(word));

        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_allWithDirectorOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String directorName = movie.getDirectors().get(0).getName();
        String[] directorWords = directorName.split(" ");
        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));
        Arrays.stream(directorWords).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, directorName);

        Arrays.stream(directorWords).forEach(word ->
                verify(movieRepository, times(1)).findByDirectors_NameContainsIgnoreCase(word));
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_allWithGenreOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String genreName = movie.getGenres().get(0).getName();
        String[] genreWords = genreName.split(" ");
        Arrays.stream(genreWords).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(genreWords).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(genreWords).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(genreWords).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(genreWords).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, genreName);

        Arrays.stream(genreWords).forEach(word ->
                verify(movieRepository, times(1)).findByDirectors_NameContainsIgnoreCase(word));
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_allWithYearOneWord_returnsAMovie() throws Exception {
        movie.setId(0L);
        String movieYear = String.valueOf(movie.getYear());
        String[] movieYearParts = movieYear.split(" ");
        Arrays.stream(movieYearParts).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(movieYearParts).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(movieYearParts).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(movieYearParts).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(movieYearParts).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(movieYearParts).forEach(word ->
                when(movieRepository.findByYear(Integer.parseInt(word))).thenReturn(Stream.of(movie)));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, movieYear);

        Arrays.stream(movieYearParts).forEach(word ->
                verify(movieRepository, times(1)).findByDirectors_NameContainsIgnoreCase(word));
        assertEquals(movie.getTitle(), movies.findFirst().get().getTitle());
    }

    @Test
    public void findAllBy_allMultipleFields() throws Exception {
        movie.setId(0L);

        Movie anotherMovie = new Movie().setYear(2001);
        anotherMovie.setId(1L);

        String query = String.format("%s %d", movie.getTitle(), anotherMovie.getYear());
        String[] queryParts = query.split(" ");

        Arrays.stream(queryParts).forEach(word ->
                when(movieRepository.findByTitleContainsIgnoreCase(word)).thenReturn(Stream.of(movie)));
        Arrays.stream(queryParts).forEach(word ->
                when(movieRepository.findByTitleOvContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(queryParts).forEach(word ->
                when(movieRepository.findByActors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(queryParts).forEach(word ->
                when(movieRepository.findByDirectors_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));
        Arrays.stream(queryParts).forEach(word ->
                when(movieRepository.findByGenres_NameContainsIgnoreCase(word)).thenReturn(Stream.empty()));

        when(movieRepository.findByYear(anotherMovie.getYear())).thenReturn(Stream.of(anotherMovie));

        Stream<Movie> movies = service.findAllBy(MovieQueryTypeMultiple.ALL, query);
        assertEquals(2, movies.count());
    }

    @Test
    public void update_unChangedMovie_saveCalledReturnsOk() throws Exception {
        movie.setId(0L);
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
    }

    @Test
    public void update_unExistingMovie_returnsError() throws Exception {
        movie.setId(0L);
        Result result = service.update(movie);

        verify(movieRepository, never()).save(movie);
        assertTrue(result.isError());
        assertEquals("MOVIE_NOT_FOUND", result.getMsg());
    }

    @Test
    public void update_existingMovieWithMatchingTitleAndYear_returnsError() throws Exception {
        movie.setId(0L);
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Movie fakeMovie = new Movie();
        fakeMovie.setId(1L);
        when(movieRepository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()))
                .thenReturn(Optional.of(fakeMovie));

        Result result = service.update(movie);

        verify(movieRepository, never()).save(movie);
        assertTrue(result.isError());
        assertEquals("MOVIE_ALREADY_EXISTS", result.getMsg());
    }

    @Test
    public void update_invalidMovie_returnError() throws Exception {
        movie.setId(0L);
        movie.setDescription("");
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);
        verify(movieRepository, never()).save(movie);

        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Description]", result.toString());
    }

    @Test
    public void update_changedTitleMovie_saveCalledReturnsOkAndIsModified() throws Exception {
        movie.setId(0L);
        movie.setTitle("New title");
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
        assertEquals(movie.getTitle(), savedMovie.getTitle());
    }

    @Test
    public void update_changedTitleOvMovie_saveCalledReturnsOkAndIsModified() throws Exception {
        movie.setId(0L);
        movie.setTitleOv("New title");
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
        assertEquals(movie.getTitleOv(), savedMovie.getTitleOv());
    }

    @Test
    public void update_changedYearMovie_saveCalledReturnsOkAndIsModified() throws Exception {
        movie.setId(0L);
        movie.setYear(2010);
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
        assertEquals(movie.getYear(), savedMovie.getYear());
    }

    @Test
    public void update_addActorMovie_saveCalledReturnsOkAndIsModified() throws Exception {
        movie.setId(0L);
        movie.addActor(new Actor("New actor"));
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
        assertEquals(movie.getActors().size(), savedMovie.getActors().size());
    }

    @Test
    public void update_addDirectorMovie_saveCalledReturnsOkAndIsModified() throws Exception {
        movie.setId(0L);
        movie.addDirector(new Director("New director"));
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
        assertEquals(movie.getDirectors().size(), savedMovie.getDirectors().size());
    }

    @Test
    public void update_addGenreMovie_saveCalledReturnsOkAndIsModified() throws Exception {
        movie.setId(0L);
        movie.addGenre(new Genre("New genre"));
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
        assertEquals(movie.getGenres().size(), savedMovie.getGenres().size());
    }

    @Test
    public void update_changedDescriptionMovie_saveCalledReturnsOkAndIsModified() throws Exception {
        movie.setId(0L);
        movie.setDescription(new String(new char[200]).replace('\0', 'a'));
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
        assertEquals(movie.getDescription(), savedMovie.getDescription());
    }


    @Test
    public void update_changedAvailableCopiesMovie_saveCalledReturnsOkAndIsModified() throws Exception {
        movie.setId(0L);
        movie.setAvailableCopies(0);
        when(movieRepository.findOne(0L)).thenReturn(Optional.of(savedMovie));

        Result result = service.update(movie);

        verify(movieRepository, times(1)).save(movie);
        assertTrue(result.isOk());
        assertEquals(movie.getAvailableCopies(), savedMovie.getAvailableCopies());
    }

    @Test
    public void remove_withExistingMovie_updatesMovieAvailability() throws Exception {
        movie.setId(0L);
        when(movieRepository.findOne(movie.getId())).thenReturn(Optional.of(movie));

        Result result = service.remove(movie.getId());

        verify(movieRepository, times(1)).save(movie);
        verify(movieRepository, never()).delete(movie);
        assertTrue(result.isOk());
        assertEquals(0, movie.getAvailableCopies());
    }

    @Test
    public void remove_withNonExistingMovie_returnsError() throws Exception {
        movie.setId(0L);
        Result result = service.remove(movie.getId());

        verify(movieRepository, never()).save(movie);
        verify(movieRepository, never()).delete(movie);
        assertTrue(result.isError());
        assertEquals("MOVIE_NOT_FOUND", result.getMsg());
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        movieRepository = null;
        assetService = null;
        movie = null;
        savedMovie = null;
    }

}