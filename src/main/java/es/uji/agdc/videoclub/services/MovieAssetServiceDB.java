package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.repositories.ActorRepository;
import es.uji.agdc.videoclub.repositories.DirectorRepository;
import es.uji.agdc.videoclub.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Alberto on 11/12/2016.
 */
@Service
public class MovieAssetServiceDB implements MovieAssetService {

    private ActorRepository actorRepository;
    private DirectorRepository directorRepository;
    private GenreRepository genreRepository;

    @Autowired
    public MovieAssetServiceDB(ActorRepository actorRepository, DirectorRepository directorRepository, GenreRepository genreRepository) {
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<Actor> findActorByName(String name) {
        if (isNameInvalid(name)) return Optional.empty();
        return actorRepository.findByNameIgnoreCase(name);
    }

    @Override
    public Optional<Director> findDirectorByName(String name) {
        if (isNameInvalid(name)) return Optional.empty();
        return directorRepository.findByNameIgnoreCase(name);
    }

    @Override
    public Optional<Genre> findGenreByName(String name) {
        if (isNameInvalid(name)) return Optional.empty();
        return genreRepository.findByNameIgnoreCase(name);
    }

    private boolean isNameInvalid(String name) {
        return name == null || name.isEmpty();
    }
}
