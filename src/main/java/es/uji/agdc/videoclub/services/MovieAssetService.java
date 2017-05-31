package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.*;

import java.util.Optional;

/**
 * Movie asset service interface that acts as a gateway between the user and the business logic.
 *
 * Provides information about every {@link Actor}, {@link Director} and {@link Genre} that is available on the system
 *
 * Every movie service must implement this interface.
 */
public interface MovieAssetService {

    /**
     * Tries to find an {@link Actor} by its name. Case does not matter.
     * @param name The name of the {@link Actor} to be found
     * @return An {@link Optional} filled with an {@link Actor} if it was found
     */
    Optional<Actor> findActorByName(String name);

    /**
     * Tries to find a {@link Director} by its name. Case does not matter.
     * @param name The name of the {@link Director} to be found
     * @return An {@link Optional} filled with a {@link Director} if it was found
     */
    Optional<Director> findDirectorByName(String name);

    /**
     * Tries to find a {@link Genre} by its name. Case does not matter.
     * @param name The name of the {@link Genre} to be found
     * @return An {@link Optional} filled with a {@link Genre} if it was found
     */
    Optional<Genre> findGenreByName(String name);
}
