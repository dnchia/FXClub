package es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.VisualizationLink;
import java.util.stream.Stream;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository (DAO) that permits CRUD operations on user entities
 */
public interface VisualizationLinkRepository extends CrudRepositoryJ8<VisualizationLink, Long> {

    /**
     * Tries to find a visualization link via its token
     * @param string the token of the visualization link
     * @return A filled {@link Optional} with the {@link VisualizationLink} that was found
     * or an empty one if no {@link VisualizationLink} was found
     */
    Optional<VisualizationLink> findByToken(String string);

    /**
     * Tries to find visualization links via their movie
     * @param movieId the id of the movie of the visualization links
     * @return A filled {@link Stream} that contains the visualization links that match the statement
     * or an empty one if no {@link VisualizationLink} was found
     */
    Stream<VisualizationLink> findByMovie_Id(long movieId);

    /**
     * Tries to find visualization links via their user
     * @param userId the id of the user of the visualization links
     * @return A filled {@link Stream} that contains the visualization links that match the statement
     * or an empty one if no {@link VisualizationLink} was found
     */
    Stream<VisualizationLink> findByUser_Id(long userId);


    /**
     * Tries to find a visualization link by a user and a movie
     * @param user the user of the link
     * @param movie the movie of the link
     * @return A filled {@link Optional} with the {@link Optional} that was found
     * or an empty one if no {@link VisualizationLink} was found
     */
    Optional<VisualizationLink> findByUserAndMovie(User user, Movie movie);

    /**
     * Deletes all link whose expedition time dates from before a given time
     * @param time the moment that serves as reference
     */
    void deleteByExpeditionDateBefore(LocalDateTime time);

    /**
     * Deletes all links from a given user
     * @param userId the id of the user whose links are going to be deleted
     */
    void deleteByUser_Id(long userId);

}
