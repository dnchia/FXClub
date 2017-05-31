package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.VisualizationLink;
import es.uji.agdc.videoclub.repositories.VisualizationLinkRepository;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.services.utils.ResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by alberto on 10/1/17.
 */
@Service
public class VisualizationLinkServiceDB implements VisualizationLinkService {

    private static final int HOURS_TO_CONSIDER_AS_TIMED_OUT = 48;

    private static Logger log = LoggerFactory.getLogger(VisualizationLinkServiceDB.class);

    private UserService userService;
    private MovieService movieService;
    private VisualizationLinkRepository repository;

    @Autowired
    public VisualizationLinkServiceDB(UserService userService,
                                      MovieService movieService, VisualizationLinkRepository repository) {
        this.userService = userService;
        this.movieService = movieService;
        this.repository = repository;
    }

    @Override
    @Transactional
    public Result create(VisualizationLink visualizationLink) {

        User user = visualizationLink.getUser();
        Result userNotFound = ResultBuilder.error("USER_NOT_FOUND");
        if (user.isNew()) {
            log.warn("create(): Called with unsaved user");
            return userNotFound;
        }

        Optional<User> possibleUser = userService.findBy(UserQueryTypeSingle.ID, user.getId().toString());
        if (!possibleUser.isPresent()) {
            log.warn("create(): Called with non-existing userId: " + user.getId());
            return userNotFound;
        }

        Movie movie = visualizationLink.getMovie();
        Result movieNotFound = ResultBuilder.error("MOVIE_NOT_FOUND");
        if (movie.isNew()) {
            log.warn("create(): Called with unsaved movie");
            return movieNotFound;
        }

        Optional<Movie> possibleMovie = movieService.findBy(MovieQueryTypeSingle.ID, movie.getId().toString());
        if (!possibleMovie.isPresent()) {
            log.warn("create(): Called with non-existing movie: " + movie.getId());
            return movieNotFound;
        }

        LocalDateTime expeditionDate = visualizationLink.getExpeditionDate();
        if (expeditionDate == null || expeditionDate.compareTo(LocalDateTime.now()) > 0) {
            log.warn("create(): Called with invalid expedition date");
            return ResultBuilder.error("INVALID_EXPEDITION_DATE");
        }

        long linksCount = repository.findByMovie_Id(movie.getId()).count();

        Optional<VisualizationLink> possibleLink = repository.findByUserAndMovie(user, movie);
        if (possibleLink.isPresent()) {
            log.warn(String.format("create(): User: %s was already watching movie: %s", user.getUsername(), movie.getTitle()));
            return ResultBuilder.error("ALREADY_WATCHING");
        }

        if (linksCount >= movie.getAvailableCopies())
            return ResultBuilder.error("NO_COPIES_AVAILABLE");

        visualizationLink
                .setUser(possibleUser.get())
                .setMovie(possibleMovie.get());

        VisualizationLink link = repository.save(visualizationLink);
        log.info(String.format("create(): Link (%s) created for user: %s and movie: %s", (link != null) ?
                link.getToken() : "virtual", user.getUsername(), movie.getTitle()));

        return ResultBuilder.ok();
    }

    @Override
    public Optional<VisualizationLink> findBy(VisualizationLinkQueryTypeSimple field, String ...values) {
        if (values.length == 0) {
            log.error(String.format("findBy(%s): called with no values", field));
            return Optional.empty();
        }
        if (isEmpty(values[0])) {
            log.warn(String.format("findBy(%s): called with first value empty", field));
            return Optional.empty();
        }

        switch (field) {
            case TOKEN:
                if (values.length != 2 || isEmpty(values[1])) {
                    log.error(String.format("findBy(%s): called with unexpected number of values", field));
                    throw new Error("MISSING_USER_ID");
                }
                String token = values[0];
                String userId = values[1];

                Optional<VisualizationLink> possibleLink = repository.findByToken(token);
                if (!possibleLink.isPresent()) return Optional.empty();

                VisualizationLink link = possibleLink.get();
                if (!userId.equals(link.getUser().getId().toString()))
                    throw new Error("FOREIGN_LINK");
                return possibleLink;
            default:
                throw new Error("Unimplemented");
        }
    }

    @Override
    public Stream<VisualizationLink> findAllBy(VisualizationLinkQueryTypeMultiple field, String value) {
        if (isEmpty(value)) {
            log.warn(String.format("findAllBy(%s): called with empty value", field));
            return Stream.empty();
        }

        switch (field) {
            case MOVIE:
                return findIfValidMovieId(value);
            case USER:
                return findIfValidUserId(value);
            default:
                throw new Error("Unimplemented");
        }
    }

    private Stream<VisualizationLink> findIfValidMovieId(String id) {
        if (!isLong(id)) return Stream.empty();
        return repository.findByMovie_Id(Long.parseLong(id));
    }

    private Stream<VisualizationLink> findIfValidUserId(String id) {
        if (!isLong(id)) return Stream.empty();
        return repository.findByUser_Id(Long.parseLong(id));
    }

    private boolean isLong(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    @Override
    @Transactional
    public Result remove(String token, String userId) {
        try {
            Optional<VisualizationLink> possibleLink = findBy(VisualizationLinkQueryTypeSimple.TOKEN, token, userId);
            if (!possibleLink.isPresent()) return ResultBuilder.error("LINK_NOT_FOUND");

            repository.delete(possibleLink.get());
            return ResultBuilder.ok();
        } catch (Error e) {
            return ResultBuilder.error(e.getMessage());
        }
    }

    @Override
    public void removeTimedOutLinks() {
        repository.deleteByExpeditionDateBefore(LocalDateTime.now().minusHours(HOURS_TO_CONSIDER_AS_TIMED_OUT));
    }
}
