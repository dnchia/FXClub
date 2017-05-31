package es.uji.agdc.videoclub.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

/**
 * Actor entity from the business logic
 */
@Entity
@Table(name = "actors")
public class Actor extends AbstractEntity {
    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies = new LinkedList<>();

    protected Actor() {
    }

    public Actor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Actor setName(String name) {
        this.name = name;
        return this;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public Actor addMovie(Movie movie) {
        List<Movie> movies = getMovies();
        movies.add(movie);
        return this;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
