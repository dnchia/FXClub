package es.uji.agdc.videoclub.initializers;

import es.uji.agdc.videoclub.helpers.Services;
import es.uji.agdc.videoclub.models.*;
import es.uji.agdc.videoclub.services.MovieService;
import es.uji.agdc.videoclub.services.UserQueryTypeMultiple;
import es.uji.agdc.videoclub.services.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 11/01/17.
 */
@Component
@Profile("!test")
public class DataInitializer {

    private UserService userService;
    private MovieService movieService;

//    @EventListener(ApplicationReadyEvent.class)
    public void setData() {

        userService = Services.getUserService();
        movieService = Services.getMovieService();

        if (userService.findAllBy(UserQueryTypeMultiple.ROLE, User.Role.MEMBER.toString()).count() == 0) {
            List<User> users = DataInitializer.generate5Users();

            for (User user : users)
                userService.create(user);
        }

        if (movieService.findAll().count() == 0) {
            List<Movie> movies = DataInitializer.generate5Movies();

            for (Movie movie : movies)
                movieService.create(movie);
        }
    }


    public static List<User> generate5Users() {
        ArrayList<User> listOfUsers = new ArrayList<>(5);

        User user = new User()
                .setDni("11614397N")
                .setName("Paco Sánchez Marqués")
                .setAddress("C/Falsa, 124, 2º")
                .setPhone(693582470)
                .setEmail("psm@hotmail.com")
                .setUsername("paquitosm")
                .setPassword("pacosm69")
                .setLastPayment(LocalDate.now().minusDays(13))
                .setRole(User.Role.MEMBER);

        listOfUsers.add(user);

        user = new User()
                .setDni("11614397Q")
                .setName("Javier Sánchez Marqués")
                .setAddress("C/Falsa, 124, 2º")
                .setPhone(693182746)
                .setEmail("jsm@hotmail.com")
                .setUsername("javiersm")
                .setPassword("javiersm69")
                .setLastPayment(LocalDate.now().minusMonths(1))
                .setRole(User.Role.MEMBER);

        listOfUsers.add(user);

        user = new User()
                .setDni("11618797Q")
                .setName("Lara García Velado")
                .setAddress("C/Montserrat, 29, 3º")
                .setPhone(693123746)
                .setEmail("laragarcia@gmail.com")
                .setUsername("larag")
                .setPassword("987654321")
                .setLastPayment(LocalDate.now().minusMonths(2))
                .setRole(User.Role.MEMBER);

        listOfUsers.add(user);

        user = new User()
                .setDni("29614339Z")
                .setName("Ana Marqués Jurado")
                .setAddress("C/Prueba, 210")
                .setPhone(964785412)
                .setEmail("mjkristal@yahoo.es")
                .setUsername("mjkristal")
                .setPassword("contraseña")
                .setLastPayment(LocalDate.now().minusMonths(3))
                .setRole(User.Role.MEMBER);

        listOfUsers.add(user);

        user = new User()
                .setDni("18720548Z")
                .setName("Adrián Jurado García")
                .setAddress("C/Prueba, 10")
                .setPhone(987654321)
                .setEmail("ajg@yahoo.es")
                .setUsername("adriano")
                .setPassword("contraseña")
                .setLastPayment(LocalDate.now().minusMonths(4))
                .setRole(User.Role.MEMBER);

        listOfUsers.add(user);

        return listOfUsers;
    }

    public static List<Movie> generate5Movies() {
        ArrayList<Movie> listOfMovies = new ArrayList<>(5);

        Movie movie = new Movie()
                .setTitle("Película 1")
                .setTitleOv("Movie number 1")
                .setYear(2000)
                .addActor(new Actor("Chris"))
                .addActor(new Actor("Hayley"))
                .addDirector(new Director("Joe Johnston"))
                .addGenre(new Genre("Comedia"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(5);

        listOfMovies.add(movie);

        movie = new Movie()
                .setTitle("Película número 2")
                .setTitleOv("Movie number 2")
                .setYear(2010)
                .addActor(new Actor("Chris"))
                .addActor(new Actor("Hayley"))
                .addActor(new Actor("Tipo de relleno 1"))
                .addDirector(new Director("Johnston"))
                .addDirector(new Director("Steven Spielberg"))
                .addGenre(new Genre("Comedia"))
                .addGenre(new Genre("Terror"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(2);

        listOfMovies.add(movie);

        movie = new Movie()
                .setTitle("Película 3")
                .setTitleOv("Movie number 3")
                .setYear(2014)
                .addActor(new Actor("Tipo de relleno 1"))
                .addDirector(new Director("Steven Spielberg"))
                .addGenre(new Genre("Terror"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(10);

        listOfMovies.add(movie);

        movie = new Movie()
                .setTitle("Película número 4")
                .setTitleOv("Movie number 4")
                .setYear(2012)
                .addActor(new Actor("Tipo de relleno 2"))
                .addActor(new Actor("Tipo de relleno 3"))
                .addActor(new Actor("Tipo de relleno 4"))
                .addActor(new Actor("Tipo de relleno 5"))
                .addActor(new Actor("Tipo de relleno 6"))
                .addDirector(new Director("Sr. Director"))
                .addGenre(new Genre("Histórica"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(8);

        listOfMovies.add(movie);

        movie = new Movie()
                .setTitle("Película 5")
                .setTitleOv("Movie number 5")
                .setYear(2015)
                .addActor(new Actor("Chris"))
                .addDirector(new Director("Johnston"))
                .addGenre(new Genre("Histórica"))
                .addGenre(new Genre("Drama"))
                .setDescription("Y, viéndole don Quijote de aquella manera, con muestras de tanta " +
                        "tristeza, le dijo: Sábete, Sancho, que no es un hombre más que otro si no " +
                        "hace más que otro. Todas estas borrascas que nos suceden son.")
                .setAvailableCopies(8);

        listOfMovies.add(movie);

        return listOfMovies;
    }
}
