package integration.es.uji.agdc.videoclub.repositories;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.repositories.ActorRepository;
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
public class ActorRepositoryTest {

    @Autowired
    private ActorRepository repository;

    private Actor actor;

    @Before
    public void setUp() throws Exception {
        actor = new Actor("Foo Bar");
    }

    @Test
    public void createActor() throws Exception {
        Actor savedActor = repository.save(actor);
        assertNotNull(savedActor.getId());
    }

    @Test
    public void findByName_matchingCase() throws Exception {
        repository.save(actor);
        Optional<Actor> possibleActor = repository.findByNameIgnoreCase(actor.getName());
        assertTrue(possibleActor.isPresent());
        assertEquals(actor.getName(), possibleActor.get().getName());
    }

    @Test
    public void findByName_lowerCase() throws Exception {
        repository.save(actor);
        Optional<Actor> possibleActor = repository.findByNameIgnoreCase(actor.getName().toLowerCase());
        assertTrue(possibleActor.isPresent());
        assertEquals(actor.getName(), possibleActor.get().getName());
    }

    @Test
    public void findByName_upperCase() throws Exception {
        repository.save(actor);
        Optional<Actor> possibleActor = repository.findByNameIgnoreCase(actor.getName().toUpperCase());
        assertTrue(possibleActor.isPresent());
        assertEquals(actor.getName(), possibleActor.get().getName());
    }

    @Test
    public void newActorHasNoMovies() throws Exception {
        Actor savedActor = repository.save(actor);
        assertNotNull(savedActor.getMovies());
        assertEquals(0, savedActor.getMovies().size());
    }

    @Test
    public void modifyActor() throws Exception {
        Actor savedActor = repository.save(actor);
        String newActorName = "John Doe";
        savedActor.setName(newActorName);
        Actor modifiedActor = repository.save(savedActor);
        assertEquals(newActorName, modifiedActor.getName());
    }

    @Test
    public void deleteActor() throws Exception {
        Actor savedActor = repository.save(actor);
        repository.delete(savedActor);
        Optional<Actor> possibleActor = repository.findByNameIgnoreCase(savedActor.getName());
        assertFalse(possibleActor.isPresent());
    }

    @After
    public void tearDown() throws Exception {
        actor = null;
    }
}