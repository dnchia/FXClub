package es.uji.agdc.videoclub.models;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Movie entity from the business domain, contains actors, directors and genres
 */
@Entity
@Table(name = "movies")
public class Movie extends AbstractEntity {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String titleOv;

    @Column(nullable = false)
    private int year;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "movie_actors")
    private List<Actor> actors = new LinkedList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "movie_directors")
    private List<Director> directors = new LinkedList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "movie_genres")
    private List<Genre> genres = new LinkedList<>();

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private int availableCopies;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "movie", orphanRemoval = true)
    private List<VisualizationLink> visualizationLinks = new LinkedList<>();

    public String getTitle() {
        return title;
    }

    public Movie setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitleOv() {
        return titleOv;
    }

    public Movie setTitleOv(String titleOv) {
        this.titleOv = titleOv;
        return this;
    }

    public int getYear() {
        return year;
    }

    public Movie setYear(int year) {
        this.year = year;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Movie setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getActualAvailableCopies() {
        int actualAvailableCopies = availableCopies - getVisualizationLinks().size();
        return actualAvailableCopies < 0 ? 0 : actualAvailableCopies;
    }

    public Movie setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
        return this;
    }

    public List<Actor> getActors() {

        return actors;
    }

    public Movie setActors(List<Actor> actors) {
        this.actors = actors;
        return this;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public Movie setDirectors(List<Director> directors) {
        this.directors = directors;
        return this;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Movie setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public Movie addActor(Actor actor) {
        List<Actor> actors = getActors();
        long count = actors.stream().filter(actor1 -> actor1.getName().equals(actor.getName())).count();
        if (count == 0) {
            actors.add(actor);
            actor.addMovie(this);
        }
        return this;
    }

    public Movie addDirector(Director director) {
        List<Director> directors = getDirectors();
        long count = directors.stream().filter(director1 -> director1.getName().equals(director.getName())).count();
        if (count == 0) {
            directors.add(director);
            director.addMovie(this);
        }
        return this;
    }

    public Movie addGenre(Genre genre) {
        List<Genre> genres = getGenres();
        long count = genres.stream().filter(genre1 -> genre1.getName().equals(genre.getName())).count();
        if (count == 0) {
            genres.add(genre);
            genre.addMovie(this);
        }
        return this;
    }

    public List<VisualizationLink> getVisualizationLinks() {
        return visualizationLinks;
    }

    public Movie addVisualizationLink(VisualizationLink link) {
        List<VisualizationLink> visualizationLinks = getVisualizationLinks();
        if (!visualizationLinks.contains(link))
            visualizationLinks.add(link);
        return this;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", titleOv='" + titleOv + '\'' +
                ", year=" + year +
                ", actors=" + actors +
                ", directors=" + directors +
                ", genres=" + genres +
                ", description='" + description + '\'' +
                ", availableCopies=" + availableCopies +
                "} " + super.toString();
    }
}
