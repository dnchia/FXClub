package integration.es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.repositories.DirectorRepository;
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
public class DirectorRepositoryTest {

    @Autowired
    private DirectorRepository repository;

    private Director director;

    @Before
    public void setUp() throws Exception {
        director = new Director("John Doe");
    }

    @Test
    public void createDirector() throws Exception {
        Director savedDirector = repository.save(director);
        assertNotNull(savedDirector.getId());
    }

    @Test
    public void findByName_matchingCase() throws Exception {
        repository.save(director);
        Optional<Director> possibleDirector = repository.findByNameIgnoreCase(director.getName());
        assertTrue(possibleDirector.isPresent());
        assertEquals(director.getName(), possibleDirector.get().getName());
    }

    @Test
    public void findByName_lowerCase() throws Exception {
        repository.save(director);
        Optional<Director> possibleDirector = repository.findByNameIgnoreCase(director.getName().toLowerCase());
        assertTrue(possibleDirector.isPresent());
        assertEquals(director.getName(), possibleDirector.get().getName());
    }

    @Test
    public void findByName_upperCase() throws Exception {
        repository.save(director);
        Optional<Director> possibleDirector = repository.findByNameIgnoreCase(director.getName().toUpperCase());
        assertTrue(possibleDirector.isPresent());
        assertEquals(director.getName(), possibleDirector.get().getName());
    }

    @Test
    public void newDirectorHasNoMovies() throws Exception {
        Director savedDirector = repository.save(director);
        assertNotNull(savedDirector.getMovies());
        assertEquals(0, savedDirector.getMovies().size());
    }

    @Test
    public void modifyDirector() throws Exception {
        Director savedDirector = repository.save(director);
        String newName = "John Doe";
        savedDirector.setName(newName);
        Director modifiedDirector = repository.save(savedDirector);
        assertEquals(newName, modifiedDirector.getName());
    }

    @Test
    public void removeDirector() throws Exception {
        Director savedDirector = repository.save(director);
        repository.delete(savedDirector);
        Optional<Director> possibleDirector = repository.findByNameIgnoreCase(director.getName());
        assertFalse(possibleDirector.isPresent());
    }

    @After
    public void tearDown() throws Exception {
        director = null;
    }

}