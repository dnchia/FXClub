package es.uji.agdc.videoclub.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@link CrudRepository} with Java 8 features
 * @param <T> Entity
 * @param <ID> Entity primary key. Must be serializable.
 */
@NoRepositoryBean
public interface CrudRepositoryJ8<T, ID extends Serializable> extends Repository<T, ID> {

    /**
     * Tries to find one record with the given ID. Returns empty Optional if not found.
     *
     * @param id Id of the entity to be found
     * @return Returns an empty or filled Optional with the record with the specified ID
     */
    Optional<T> findOne(ID id);

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity The entity to be saved
     * @return the saved entity
     */
    <S extends T> S save(S entity);

    /**
     * Saves all given entities.
     *
     * @param entities Entities to be saved
     * @return the saved entities
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    <S extends T> Stream<S> save(Iterable<S> entities);

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    Stream<T> findAll();

    /**
     * Returns all instances of the type with the given IDs.
     *
     * @param ids IDs of the entities to be searched
     * @return List containing search matches. The list is empty if none of the entities is found.
     */
    Stream<T> findAll(Iterable<ID> ids);

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities
     */
    long count();

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    void delete(ID id);

    /**
     * Deletes a given entity.
     *
     * @param entity The entity that is going to be deleted
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    void delete(T entity);

    /**
     * Deletes the given entities.
     *
     * @param entities Iterable containing all the entities that are going to be deleted
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    void delete(Iterable<? extends T> entities);

    /**
     * Deletes all entities managed by the repository.
     */
    void deleteAll();
}
