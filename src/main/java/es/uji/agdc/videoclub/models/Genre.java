package es.uji.agdc.videoclub.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

/**
 * Genre entity from the business logic
 */
@Entity
@Table(name = "genres")
public class Genre extends AbstractEntity {
    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies = new LinkedList<>();

    protected Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Genre setName(String name) {
        this.name = name;
        return this;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public Genre addMovie(Movie movie) {
        List<Movie> movies = getMovies();
        movies.add(movie);
        return this;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
