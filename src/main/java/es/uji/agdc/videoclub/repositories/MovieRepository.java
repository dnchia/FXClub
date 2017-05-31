package es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.models.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Movie repository for basic movie database-side operations
 */
public interface MovieRepository extends CrudRepositoryJ8<Movie, Long> {

    /**
     * Tries to find a movie based on its title and the year of its release
     * @param title The title of the movie to be found, not the OV one
     * @param year Release year
     * @return A filled {@link Optional} with the {@link Movie} that was found
     * for the given search parameters. An empty one if not found.
     */
    Optional<Movie> findByTitleIgnoreCaseAndYear(String title, int year);

    /**
     * Tries to find movies based on a string that appears on its title
     * @param string The string that has to appear in the title
     * @return A filled {@link Stream} containing {@link Movie} that match
     * the provided statement
     */
    Stream<Movie> findByTitleContainsIgnoreCase(String string);

    /**
     * Tries to find movies based on a string that appears on its title in original version
     * @param string The string that has to appear in the title in original version
     * @return A filled {@link Stream} containing {@link Movie} that match
     * the provided statement
     */
    Stream<Movie> findByTitleOvContainsIgnoreCase(String string);

    /**
     * Tries to find movies based on a string that appears on the name of its actors
     * @param string The string that has to appear in the actor name
     * @return A filled {@link Stream} containing {@link Movie} that match
     * the provided statement
     */
    Stream<Movie> findByActors_NameContainsIgnoreCase(String string);

    /**
     * Tries to find movies based on a string that appears on the name of its directors
     * @param string The string that has to appear in the director name
     * @return A filled {@link Stream} containing {@link Movie} that match
     * the provided statement
     */
    Stream<Movie> findByDirectors_NameContainsIgnoreCase(String string);

    /**
     * Tries to find movies based on a string that appears on the name of its genres
     * @param string The string that has to appear in the genre name
     * @return A filled {@link Stream} containing {@link Movie} that match
     * the provided statement
     */
    Stream<Movie> findByGenres_NameContainsIgnoreCase(String string);

    /**
     * Tries to find movies based on the year they been launched
     * @param year The year in which the movie has been launched
     * @return A filled {@link Stream} containing {@link Movie} that match
     * the provided statement
     */
    Stream<Movie> findByYear(int year);
}
