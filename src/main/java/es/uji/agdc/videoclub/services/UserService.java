package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.utils.Result;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * User service interface that acts as a gateway between the user and the business logic.
 *
 * Every user service must implement this interface.
 */
public interface UserService {
    /**
     * Creates a user if it meets business logic constraints. If not returns an ERROR {@link Result} telling what went wrong.
     * @param user The {@link User} to be created.
     * @return An OK {@link Result}, if everything went fine. If not an ERROR with a message and the fields that do not meet the requisites.
     */
    Result create(User user);

    /**
     * Looks for a user by a determinate field
     * @param field search by one of {@link UserQueryTypeSingle}.
     * @param value attribute that must be matched.
     * @return A filled {@link Optional} if a user was found or an empty one if not.
     */
    Optional<User> findBy(UserQueryTypeSingle field, String value);

    /**
     * Looks for multiple users given a determinate field
     * @param field search by one of {@link UserQueryTypeMultiple}.
     * @param value attribute that must be matched.
     * @return A {@link Stream} with one or more {@link User}
     */
    Stream<User> findAllBy(UserQueryTypeMultiple field, String value);

    /**
     * Retrieves a {@link Stream} containing all the users who are considered defaulters
     * @return A {@link Stream} with one or more {@link User}
     */
    Stream<User> findDefaulterUsers();

    /**
     * Updates the given user entity, it must be a non-new entity
     * @param user A non-new user entity
     * @param userIdPerformsUpdate The id of the user that performs the update
     * @return An OK {@link Result}, if everything went fine. If not an ERROR with a message and the fields that do not meet the requisites.
     */
    Result update(User user, long userIdPerformsUpdate);

    /**
     * Deletes a user by a given id. Returns error if the user was not found
     * @param userId The id of the user to be deleted
     * @return An OK {@link Result}, if everything went fine. If not an ERROR.
     */
    Result remove(long userId);
}
