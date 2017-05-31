package unit.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.repositories.ActorRepository;
import es.uji.agdc.videoclub.repositories.DirectorRepository;
import es.uji.agdc.videoclub.repositories.GenreRepository;
import es.uji.agdc.videoclub.services.MovieAssetService;
import es.uji.agdc.videoclub.services.MovieAssetServiceDB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Alberto on 11/12/2016.
 */
public class MovieAssetServiceDBTest {

    private ActorRepository actorRepository;
    private DirectorRepository directorRepository;
    private GenreRepository genreRepository;

    private MovieAssetService service;

    @Before
    public void setUp() throws Exception {
        actorRepository = mock(ActorRepository.class);
        directorRepository = mock(DirectorRepository.class);
        genreRepository = mock(GenreRepository.class);

        service = new MovieAssetServiceDB(actorRepository, directorRepository, genreRepository);

        when(actorRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(directorRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(genreRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());

        when(actorRepository.findByNameIgnoreCase("Scarlett Johansson"))
                .thenReturn(Optional.of(new Actor("Scarlett Johansson")));
        when(directorRepository.findByNameIgnoreCase("Steven Spielberg"))
                .thenReturn(Optional.of(new Director("Steven Spielberg")));
        when(genreRepository.findByNameIgnoreCase("Comedy"))
                .thenReturn(Optional.of(new Genre("Comedy")));
    }

    @Test
    public void findActorByName_present() throws Exception {
        Optional<Actor> possibleActor = service.findActorByName("Scarlett Johansson");
        assertTrue(possibleActor.isPresent());
    }

    @Test
    public void findActorByName_notPresent() throws Exception {
        Optional<Actor> possibleActor = service.findActorByName("Scarlet Johanson");
        assertFalse(possibleActor.isPresent());
    }

    @Test
    public void findActorByName_nameIsEmpty() throws Exception {
        Optional<Actor> possibleActor = service.findActorByName("");
        verify(actorRepository, never()).findByNameIgnoreCase("");
        assertFalse(possibleActor.isPresent());
    }

    @Test
    public void findActorByName_nameIsNull() throws Exception {
        Optional<Actor> possibleActor = service.findActorByName(null);
        verify(actorRepository, never()).findByNameIgnoreCase(null);
        assertFalse(possibleActor.isPresent());
    }

    @Test
    public void findDirectorByName_present() throws Exception {
        Optional<Director> possibleDirector = service.findDirectorByName("Steven Spielberg");
        assertTrue(possibleDirector.isPresent());
    }
    @Test
    public void findDirectorByName_notPresent() throws Exception {
        Optional<Director> possibleDirector = service.findDirectorByName("Steven Spilberg");
        assertFalse(possibleDirector.isPresent());
    }

    @Test
    public void findDirectorByName_isEmpty() throws Exception {
        Optional<Director> possibleDirector = service.findDirectorByName("");
        verify(directorRepository, never()).findByNameIgnoreCase("");
        assertFalse(possibleDirector.isPresent());
    }

    @Test
    public void findDirectorByName_isNull() throws Exception {
        Optional<Director> possibleDirector = service.findDirectorByName(null);
        verify(directorRepository, never()).findByNameIgnoreCase(null);
        assertFalse(possibleDirector.isPresent());
    }

    @Test
    public void findGenreByName_present() throws Exception {
        Optional<Genre> possibleGenre = service.findGenreByName("Comedy");
        assertTrue(possibleGenre.isPresent());
    }

    @Test
    public void findGenreByName_notPresent() throws Exception {
        Optional<Genre> possibleGenre = service.findGenreByName("Commedy");
        assertFalse(possibleGenre.isPresent());
    }

    @Test
    public void findGenreByName_isEmpty() throws Exception {
        Optional<Genre> possibleGenre = service.findGenreByName("");
        verify(genreRepository, never()).findByNameIgnoreCase("");
        assertFalse(possibleGenre.isPresent());
    }

    @Test
    public void findGenreByName_isNull() throws Exception {
        Optional<Genre> possibleGenre = service.findGenreByName(null);
        verify(genreRepository, never()).findByNameIgnoreCase(null);
        assertFalse(possibleGenre.isPresent());
    }

    @After
    public void tearDown() throws Exception {
        actorRepository = null;
        directorRepository = null;
        genreRepository = null;

        service = null;
    }
}