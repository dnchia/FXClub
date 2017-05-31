package es.uji.agdc.videoclub.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

/**
 * Director entity from the business logic
 */
@Entity
@Table(name = "directors")
public class Director extends AbstractEntity {
    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "directors")
    private List<Movie> movies = new LinkedList<>();

    protected Director() {
    }

    public Director(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Director setName(String name) {
        this.name = name;
        return this;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public Director addMovie(Movie movie) {
        List<Movie> movies = getMovies();
        movies.add(movie);
        return this;
    }

    @Override
    public String toString() {
        return "Director{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
