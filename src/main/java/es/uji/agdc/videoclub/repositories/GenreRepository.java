package es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.models.Genre;

import java.util.Optional;

/**
 * Movie genre repository for basic movie genre database-side operations
 */
public interface GenreRepository extends CrudRepositoryJ8<Genre, Long> {
    /**
     * Tries to find a genre by a given name, case is ignored
     * @param name The name of the genre to be found
     * @return A filled {@link Optional} with the {@link Genre} that was found. If not found, returns an empty one
     */
    Optional<Genre> findByNameIgnoreCase(String name);
}
