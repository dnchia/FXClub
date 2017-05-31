package integration.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.repositories.ActorRepository;
import es.uji.agdc.videoclub.repositories.DirectorRepository;
import es.uji.agdc.videoclub.repositories.GenreRepository;
import es.uji.agdc.videoclub.services.MovieAssetService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 11/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class MovieAssetServiceDBTest {

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieAssetService service;


    @Test
    public void findActorByName() throws Exception {
        String actorName = "Scarlett Johansson";
        actorRepository.save(new Actor(actorName));
        Optional<Actor> possibleActor = service.findActorByName(actorName);
        assertTrue(possibleActor.isPresent());
        assertEquals(actorName, possibleActor.get().getName());
    }

    @Test
    public void findDirectorByName() throws Exception {
        String directorName = "Steven Spielberg";
        directorRepository.save(new Director(directorName));
        Optional<Director> possibleDirector = service.findDirectorByName(directorName);
        assertTrue(possibleDirector.isPresent());
        assertEquals(directorName, possibleDirector.get().getName());
    }

    @Test
    public void findGenreByName() throws Exception {
        String genreName = "Comedy";
        genreRepository.save(new Genre(genreName));
        Optional<Genre> possibleGenre = service.findGenreByName(genreName);
        assertTrue(possibleGenre.isPresent());
        assertEquals(genreName, possibleGenre.get().getName());
    }

}