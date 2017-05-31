package es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.models.Actor;

import java.util.Optional;

/**
 * Actor repository for basic actor database-side operations
 */
public interface ActorRepository extends CrudRepositoryJ8<Actor, Long> {

    /**
     * Tries to find an {@link Actor} by a given name. Non case sensitive.
     * @param name The name of the actor that we try to find
     * @return A filled {@link Optional} if an actor was found. Empty if not.
     */
    Optional<Actor> findByNameIgnoreCase(String name);
}
