package integration.es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.repositories.GenreRepository;
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
 * Created by alberto on 9/12/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    private Genre genre;

    @Before
    public void setUp() throws Exception {
        genre = new Genre("Comedy");
    }

    @Test
    public void createGenre() throws Exception {
        Genre savedGenre = repository.save(genre);
        assertNotNull(savedGenre.getId());
    }

    @Test
    public void findByName_matchingCase() throws Exception {
        repository.save(genre);
        Optional<Genre> possibleGenre = repository.findByNameIgnoreCase(genre.getName());
        assertTrue(possibleGenre.isPresent());
        assertEquals(genre.getName(), possibleGenre.get().getName());
    }

    @Test
    public void findByName_lowerCase() throws Exception {
        repository.save(genre);
        Optional<Genre> possibleGenre = repository.findByNameIgnoreCase(genre.getName().toLowerCase());
        assertTrue(possibleGenre.isPresent());
        assertEquals(genre.getName(), possibleGenre.get().getName());
    }

    @Test
    public void findByName_upperCase() throws Exception {
        repository.save(genre);
        Optional<Genre> possibleGenre = repository.findByNameIgnoreCase(genre.getName().toUpperCase());
        assertTrue(possibleGenre.isPresent());
        assertEquals(genre.getName(), possibleGenre.get().getName());
    }

    @Test
    public void newGenreHasNoMovies() throws Exception {
        Genre savedGenre = repository.save(genre);
        assertNotNull(savedGenre.getMovies());
        assertEquals(0, savedGenre.getMovies().size());
    }

    @Test
    public void modifyGenre() throws Exception {
        Genre savedGenre = repository.save(genre);
        String newName = "Drama";
        savedGenre.setName(newName);
        Genre modifiedGenre = repository.save(savedGenre);
        assertEquals(newName, modifiedGenre.getName());
    }

    @Test
    public void deleteGenre() throws Exception {
        Genre savedGenre = repository.save(genre);
        repository.delete(savedGenre);
        Optional<Genre> possibleGenre = repository.findByNameIgnoreCase(genre.getName());
        assertFalse(possibleGenre.isPresent());
    }

    @After
    public void tearDown() throws Exception {
        genre = null;
    }

}