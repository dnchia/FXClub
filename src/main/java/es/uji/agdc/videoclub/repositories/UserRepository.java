package es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.models.User;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Repository (DAO) that permits CRUD operations on user entities
 */
public interface UserRepository extends CrudRepositoryJ8<User, Long> {

    /**
     * Search for a user by a given dni. If no user is found and empty {@link Optional} is returned.
     * @param dni DNI of the user to be found
     * @return Optional with the user if found
     */
    Optional<User> findByDni(String dni);

    /**
     * Search for a user by a given username. If no user is found and empty {@link Optional} is returned.
     * @param username Username of the user to be found
     * @return Optional with the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Search for a user by a given email. If no user is found and empty {@link Optional} is returned.
     * @param email Email of the user to be found
     * @return Optional with the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Search for a users by a given role. If no user is found and empty {@link Stream} is returned.
     * @param role One of the types declared at {@link User.Role}
     * @return A stream with the users that have the given role
     */
    Stream<User> findByRole(User.Role role);

    /**
     * Search for all users whose last payment date dates from before a given date
     * @param date the date to pick up as reference for the query
     * @return A stream with the users that meet the given condition
     */
    Stream<User> findByLastPaymentBefore(LocalDate date);
}
