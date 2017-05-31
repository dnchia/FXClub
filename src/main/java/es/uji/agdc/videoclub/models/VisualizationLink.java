package es.uji.agdc.videoclub.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by alberto on 10/1/17.
 */
@Entity
@Table(name = "visualization_links")
public class VisualizationLink extends AbstractEntity {

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User user;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Movie movie;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expeditionDate;

    protected VisualizationLink() {
    }

    public VisualizationLink(User user, Movie movie) {
        setUser(user);
        setMovie(movie);
        token = UUID.randomUUID().toString();
        expeditionDate = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public VisualizationLink setUser(User user) {
        this.user = user;
        user.addVisualizationLink(this);
        return this;
    }

    public Movie getMovie() {
        return movie;
    }

    public VisualizationLink setMovie(Movie movie) {
        this.movie = movie;
        movie.addVisualizationLink(this);
        return this;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpeditionDate() {
        return expeditionDate;
    }

    public VisualizationLink setExpeditionDate(LocalDateTime expeditionDate) {
        this.expeditionDate = expeditionDate;
        return this;
    }

    @Override
    public String toString() {
        return "VisualizationLink{" +
                "user=" + user +
                ", movie=" + movie +
                ", linkToken='" + token + '\'' +
                ", expeditionDate=" + expeditionDate +
                "} " + super.toString();
    }
}
