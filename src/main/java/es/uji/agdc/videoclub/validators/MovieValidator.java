package es.uji.agdc.videoclub.validators;

import es.uji.agdc.videoclub.models.Actor;
import es.uji.agdc.videoclub.models.Director;
import es.uji.agdc.videoclub.models.Genre;
import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.services.utils.Result;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Alberto on 11/12/2016.
 */
@Component
public class MovieValidator extends Validator<Movie> {

    @Override
    protected Result checkIfHasEmptyFields(Movie movie, Result emptyError) {
        if (isFieldEmpty(movie.getTitle())) {
            emptyError.addField("Title");
        }

        if (isFieldEmpty(movie.getTitleOv())) {
            emptyError.addField("TitleOv");
        }

        List<Actor> actors = movie.getActors();
        if (isMultiFieldEmpty(actors)) {
            emptyError.addField("Actors");
        } else {
            // FIXME Make Actor, Director and Genre implement the same interface so this code can be encapsulated
            IntStream.range(0, actors.size())
                    .forEach(value -> {
                        if (isFieldEmpty(actors.get(value).getName())) {
                            emptyError.addField("Actor_" + value);
                        }
                    });
        }

        List<Director> directors = movie.getDirectors();
        if (isMultiFieldEmpty(directors)) {
            emptyError.addField("Directors");
        } else {
            IntStream.range(0, directors.size())
                    .forEach(value -> {
                        if (isFieldEmpty(directors.get(value).getName())) {
                            emptyError.addField("Director_" + value);
                        }
                    });
        }

        List<Genre> genres = movie.getGenres();
        if (isMultiFieldEmpty(genres)) {
            emptyError.addField("Genres");
        } else {
            IntStream.range(0, genres.size())
                    .forEach(value -> {
                        if (isFieldEmpty(genres.get(value).getName())) {
                            emptyError.addField("Genre_" + value);
                        }
                    });
        }

        if (isFieldEmpty(movie.getDescription())) {
            emptyError.addField("Description");
        }
        return emptyError;
    }

    @Override
    protected Result checkIfHasInvalidFields(Movie movie, Result invalidError) {

        int actualYear = LocalDate.now().getYear();
        if (movie.getYear() < 1900 || movie.getYear() > actualYear) {
            invalidError.addField("Year");
        }

        if (movie.getActors().size() > 6) {
            invalidError.addField("Actors");
        }

        if (movie.getDirectors().size() > 4) {
            invalidError.addField("Directors");
        }

        if (movie.getGenres().size() > 4) {
            invalidError.addField("Genres");
        }

        int descriptionLength = movie.getDescription().length();
        if (descriptionLength < 200 || descriptionLength > 300) {
            invalidError.addField("Description");
        }

        if (movie.getAvailableCopies() < 0) {
            invalidError.addField("AvailableCopies");
        }

        return invalidError;
    }

}
