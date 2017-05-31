package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.utils.Result;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Movie service interface that acts as a gateway between the user and the business logic.
 *
 * Every movie service must implement this interface.
 */
public interface MovieService {

    /**
     * Persists the given new movie to the database. If something goes wrong returns an ERROR {@link Result}
     * @param movie The new movie to become persisted
     * @return An OK {@link Result}, if everything went fine. If not an ERROR.
     */
    Result create(Movie movie);

    /**
     * Finds a single movie based on the given query and its values
     * @param query One of {@link MovieQueryTypeSingle}
     * @param values Comma-separated list of parameters
     * @return A filled {@link Optional} with the {@link Movie} that was found
     * for the given search parameters. An empty one if not found.
     */
    Optional<Movie> findBy(MovieQueryTypeSingle query, String ...values);

    /**
     * Finds a set of movies that match the given query and params
     * @param query One of {@link MovieQueryTypeMultiple}
     * @param text Sentence that has to be partially matched
     * @return A {@link Stream} containing every {@link Movie} that matched the query. An empty one if no result was found
     */
    Stream<Movie> findAllBy(MovieQueryTypeMultiple query, String text);

    /**
     * Finds all movies on the database without applying any filtering
     * @return A stream with all the movies of the database
     * @deprecated Provisional, for debugging purposes
     */
    @Deprecated
    Stream<Movie> findAll(); //FIXME Remove

    /**
     * Update a non-new to the database. If something goes wrong returns an ERROR {@link Result}
     * @param movie The non-new movie to become persisted
     * @return An OK {@link Result}, if everything went fine. If not an ERROR.
     */
    Result update(Movie movie);

    /**
     * Tries to remove a {@link Movie} from the database based on its title and year
     * @param movieId The id of the movie to become deleted
     * @return An OK {@link Result}, if everything went fine. If not an ERROR.
     */
    Result remove(long movieId);
}
