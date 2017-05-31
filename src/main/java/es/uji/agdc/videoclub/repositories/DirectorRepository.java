package es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.models.Director;

import java.util.Optional;

/**
 * Director repository for basic director database-side operations
 */
public interface DirectorRepository extends CrudRepositoryJ8<Director, Long> {

    /**
     * Tries to find a {@link Director} by its name, ignoring case
     * @param name The name of the director that has to be found
     * @return A filled {@link Optional} if a {@link Director} was found
     */
    Optional<Director> findByNameIgnoreCase(String name);
}
