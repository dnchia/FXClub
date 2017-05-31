package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.repositories.MovieRepository;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.services.utils.ResultBuilder;
import es.uji.agdc.videoclub.services.utils.StreamMerger;
import es.uji.agdc.videoclub.validators.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alberto on 11/12/2016.
 */
@Service
public class MovieServiceDB implements MovieService{

    private static Logger log = LoggerFactory.getLogger(MovieServiceDB.class);

    private MovieRepository movieRepository;
    private MovieAssetService assetService;
    private Validator<Movie> validator;

    @Autowired
    public MovieServiceDB(MovieRepository movieRepository, MovieAssetService assetService, Validator<Movie> validator) {
        this.movieRepository = movieRepository;
        this.assetService = assetService;
        this.validator = validator;
    }

    @Override
    @Transactional
    public Result create(Movie movie) {

        Result ALREADY_EXISTS = ResultBuilder.error("MOVIE_ALREADY_EXISTS");

        if (!movie.isNew()) {
            log.warn("create() Tried to create an already persisted movie with ID: " + movie.getId());
            return ALREADY_EXISTS;
        }

        Result validatorResult = validator.validate(movie);
        if (validatorResult.isError()) {
            return validatorResult;
        }

        if (movieRepository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear()).isPresent()) {
            return ALREADY_EXISTS;
        }

        cleanupAssets(movie);

        Movie savedMovieWithAssets = movieRepository.save(movie);

        log.info("Movie saved with ID: " + savedMovieWithAssets.getId());
        return ResultBuilder.ok();
    }

    private void cleanupAssets(Movie movie) {
        // TODO find a way to cleanup this

        // Grab those actors that already exist on the database and replace them
        movie.setActors(movie.getActors().stream().map(actor -> {
            if (actor.getId() != null) return actor;
            Optional<Actor> possibleActor = assetService.findActorByName(actor.getName());
            return possibleActor.isPresent() ? possibleActor.get() : actor;
        }).collect(Collectors.toList()));

        // Grab those directors that already exist on the database and replace them
        movie.setDirectors(movie.getDirectors().stream().map(director -> {
            if (director.getId() != null) return director;
            Optional<Director> possibleDirector = assetService.findDirectorByName(director.getName());
            return possibleDirector.isPresent() ? possibleDirector.get() : director;
        }).collect(Collectors.toList()));

        // Grab those genres that already exist on the database and replace them
        movie.setGenres(movie.getGenres().stream().map(genre -> {
            if (genre.getId() != null) return genre;
            Optional<Genre> possibleGenre = assetService.findGenreByName(genre.getName());
            return possibleGenre.isPresent() ? possibleGenre.get() : genre;
        }).collect(Collectors.toList()));
    }

    @Override
    public Optional<Movie> findBy(MovieQueryTypeSingle query, String... values) {
        if (values.length == 0) {
            log.warn("findBy() called with 0 values");
            return Optional.empty();
        }

        switch (query) {
            case ID:
                if (values.length != 1) {
                    log.warn("findBy(ID) called with " + values.length + " values");
                    return Optional.empty();
                }
                return findOneIfValidId(values[0]);
            case TITLE_AND_YEAR:
                if (values.length != 2) {
                    log.warn("findBy(TITLE_AND_YEAR) called with " + values.length + " values");
                    return Optional.empty();
                }
                return findOneIfValidYear(values[0], values[1]);
            default:
                throw new Error("Unimplemented");
        }
    }

    private Optional<Movie> findOneIfValidId(String value) {
        try {
            return movieRepository.findOne(Long.valueOf(value));
        } catch (NumberFormatException e) {
            log.warn("ID couldn't be parsed. " + e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Movie> findOneIfValidYear(String title, String year) {
        try {
            return movieRepository.findByTitleIgnoreCaseAndYear(title, Integer.valueOf(year));
        } catch (NumberFormatException e) {
            log.warn("Year couldn't be parsed. " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Stream<Movie> findAllBy(MovieQueryTypeMultiple query, String text) {

        if (text == null || text.isEmpty()) {
            log.warn(String.format("findAllBy(%s): Called without text", query.toString()));
            return Stream.empty();
        }

        String[] values = text.split(" ");

        if (values.length == 0) {
            log.warn(String.format("findAllBy(%s): Called without words", query.toString()));
            return Stream.empty();
        }

        StreamMerger<Movie> movieStreamMerger = new StreamMerger<>();
        switch (query) {
            case TITLE:
                addTitleSearchToStreamMerger(values, movieStreamMerger);
                return movieStreamMerger.merge();
            case TITLE_OV:
                addTitleOvSearchToStreamMerger(values, movieStreamMerger);
                return movieStreamMerger.merge();
            case ACTORS:
                addActorsSearchToStreamMerger(values, movieStreamMerger);
                return movieStreamMerger.merge();
            case DIRECTORS:
                addDirectorsSearchToStreamMerger(values, movieStreamMerger);
                return movieStreamMerger.merge();
            case GENRES:
                addGenresSearchToStreamMerger(values, movieStreamMerger);
                return movieStreamMerger.merge();
            case YEAR:
                addYearSearchToStreamMerger(values, movieStreamMerger);
                return movieStreamMerger.merge();
            case ALL:
                addTitleSearchToStreamMerger(values, movieStreamMerger);
                addTitleOvSearchToStreamMerger(values, movieStreamMerger);
                addActorsSearchToStreamMerger(values, movieStreamMerger);
                addDirectorsSearchToStreamMerger(values, movieStreamMerger);
                addGenresSearchToStreamMerger(values, movieStreamMerger);
                addYearSearchToStreamMerger(values, movieStreamMerger);
                return movieStreamMerger.merge();
        }

        return Stream.empty();
    }

    private void addTitleSearchToStreamMerger(String[] values, StreamMerger<Movie> streamMerger) {
        Arrays.stream(values)
                .map((value) -> movieRepository.findByTitleContainsIgnoreCase(value))
                .forEach(streamMerger::addStream);
    }

    private void addTitleOvSearchToStreamMerger(String[] values, StreamMerger<Movie> streamMerger) {
        Arrays.stream(values)
                .map((value) -> movieRepository.findByTitleOvContainsIgnoreCase(value))
                .forEach(streamMerger::addStream);
    }

    private void addActorsSearchToStreamMerger(String[] values, StreamMerger<Movie> streamMerger) {
        Arrays.stream(values)
                .map((value) -> movieRepository.findByActors_NameContainsIgnoreCase(value))
                .forEach(streamMerger::addStream);
    }

    private void addDirectorsSearchToStreamMerger(String[] values, StreamMerger<Movie> streamMerger) {
        Arrays.stream(values)
                .map(value -> movieRepository.findByDirectors_NameContainsIgnoreCase(value))
                .forEach(streamMerger::addStream);
    }

    private void addGenresSearchToStreamMerger(String[] values, StreamMerger<Movie> streamMerger) {
        Arrays.stream(values)
                .map(value -> movieRepository.findByGenres_NameContainsIgnoreCase(value))
                .forEach(streamMerger::addStream);
    }

    private void addYearSearchToStreamMerger(String[] values, StreamMerger<Movie> streamMerger) {
        Arrays.stream(values)
                .filter(this::isInt)
                .map(Integer::parseInt)
                .map(value -> movieRepository.findByYear(value))
                .forEach(streamMerger::addStream);
    }

    private boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Stream<Movie> findAll() { //FIXME Remove me
        return movieRepository.findAll();
    }

    @Override
    @Transactional
    public Result update(Movie movie) {

        Optional<Movie> possibleMovie = movieRepository.findOne(movie.getId());
        if (!possibleMovie.isPresent()) {
            log.warn("update() Tried to a non-existing movie with ID: " + movie.getId());
            return ResultBuilder.error("MOVIE_NOT_FOUND");
        }

        Movie movieToModify = possibleMovie.get();

        Optional<Movie> movieByTitleAndYear = movieRepository.findByTitleIgnoreCaseAndYear(movie.getTitle(), movie.getYear());
        if (movieByTitleAndYear.isPresent() && !movieByTitleAndYear.get().getId().equals(movieToModify.getId())) {
            return ResultBuilder.error("MOVIE_ALREADY_EXISTS");
        }

        Movie modifiedMovie = fillMovieWithChanges(movieToModify, movie);

        Result validatorResult = validator.validate(modifiedMovie);
        if (validatorResult.isError()) {
            return validatorResult;
        }

        cleanupAssets(modifiedMovie);

        Movie savedMovieWithAssets = movieRepository.save(modifiedMovie);

        log.info("update(): Movie updated with ID: " +
                (savedMovieWithAssets != null ? savedMovieWithAssets.getId() : "virtual"));
        return ResultBuilder.ok();
    }

    private Movie fillMovieWithChanges(Movie toModify, Movie changes) {
        toModify.setTitle(changes.getTitle());
        toModify.setTitleOv(changes.getTitleOv());
        toModify.setYear(changes.getYear());
        toModify.setActors(changes.getActors());
        toModify.setDirectors(changes.getDirectors());
        toModify.setGenres(changes.getGenres());
        toModify.setDescription(changes.getDescription());
        toModify.setAvailableCopies(changes.getAvailableCopies());
        return toModify;
    }

    @Override
    @Transactional
    public Result remove(long movieId) {
        Optional<Movie> possibleMovie = movieRepository.findOne(movieId);
        if (!possibleMovie.isPresent()) {
            log.warn("remove(): Called with non-existing ID: " + movieId);
            return ResultBuilder.error("MOVIE_NOT_FOUND");
        }
        Movie movieToDisable = possibleMovie.get();
        movieToDisable.setAvailableCopies(0);
        return update(movieToDisable);
    }
}
