package unit.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.models.Movie;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.VisualizationLink;
import es.uji.agdc.videoclub.repositories.VisualizationLinkRepository;
import es.uji.agdc.videoclub.services.*;
import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Alberto on 10/01/2017.
 */
public class VisualizationLinkServiceDBTest {

    private UserService userService;
    private MovieService movieService;
    private VisualizationLinkRepository repository;

    private VisualizationLinkService service;

    private User user;
    private Movie movie;
    private VisualizationLink link;

    @Before
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        movieService = mock(MovieService.class);
        repository = mock(VisualizationLinkRepository.class);

        service = new VisualizationLinkServiceDB(userService, movieService, repository);

        when(userService.findBy(any(), anyString())).thenReturn(Optional.empty());
        when(movieService.findBy(any(), anyString())).thenReturn(Optional.empty());

        when(repository.findByToken(anyString())).thenReturn(Optional.empty());
        when(repository.findByMovie_Id(anyLong())).thenReturn(Stream.empty());
        when(repository.findByUser_Id(anyLong())).thenReturn(Stream.empty());
        when(repository.findByUserAndMovie(any(), any())).thenReturn(Optional.empty());

        user = new User();
        user.setId(0L);
        when(userService.findBy(UserQueryTypeSingle.ID, "0")).thenReturn(Optional.of(user));

        movie = new Movie();
        movie.setId(0L);
        movie.setAvailableCopies(1);
        when(movieService.findBy(MovieQueryTypeSingle.ID, "0")).thenReturn(Optional.of(movie));

        link = new VisualizationLink(user, movie);
    }

    @Test
    public void create_validVisualizationLink_returnsOk() throws Exception {
        Result result = service.create(link);

        verify(userService, only()).findBy(UserQueryTypeSingle.ID, user.getId().toString());
        verify(movieService, only()).findBy(MovieQueryTypeSingle.ID, movie.getId().toString());
        verify(repository, times(1)).findByMovie_Id(movie.getId());
        verify(repository, times(1)).findByUserAndMovie(any(), any());

        assertTrue(result.isOk());
    }

    @Test
    public void create_newUser_returnsError() throws Exception {
        user.setId(null);
        link.setUser(user);

        Result result = service.create(link);

        verify(userService, never()).findBy(any(), anyString());
        assertTrue(result.isError());
        assertEquals("USER_NOT_FOUND", result.getMsg());
    }

    @Test
    public void create_nonExistingUser_returnsError() throws Exception {
        user.setId(1L);
        link.setUser(user);

        Result result = service.create(link);

        verify(userService, only()).findBy(UserQueryTypeSingle.ID, "1");
        assertTrue(result.isError());
        assertEquals("USER_NOT_FOUND", result.getMsg());
    }

    @Test
    public void create_newMovie_returnsError() throws Exception {
        movie.setId(null);
        link.setMovie(movie);

        Result result = service.create(link);

        verify(movieService, never()).findBy(any(), anyString());
        assertTrue(result.isError());
        assertEquals("MOVIE_NOT_FOUND", result.getMsg());
    }

    @Test
    public void create_nonExistingMovie_returnsError() throws Exception {
        movie.setId(1L);
        link.setMovie(movie);

        Result result = service.create(link);

        verify(movieService, only()).findBy(MovieQueryTypeSingle.ID, "1");
        assertTrue(result.isError());
        assertEquals("MOVIE_NOT_FOUND", result.getMsg());
    }

    @Test
    public void create_nullExpeditionDate_returnsError() throws Exception {
        link.setExpeditionDate(null);
        Result result = service.create(link);

        assertTrue(result.isError());
        assertEquals("INVALID_EXPEDITION_DATE", result.getMsg());
    }

    @Test
    public void create_invalidExpeditionDate_returnsError() throws Exception {
        link.setExpeditionDate(LocalDateTime.now().plusSeconds(1));
        Result result = service.create(link);

        assertTrue(result.isError());
        assertEquals("INVALID_EXPEDITION_DATE", result.getMsg());
    }

    @Test
    public void create_userWithMatchingLink_returnsError() throws Exception {
        when(repository.findByUserAndMovie(any(), any())).thenReturn(Optional.of(new VisualizationLink(user, movie)));
        Result result = service.create(link);

        verify(repository, times(1)).findByUserAndMovie(any(), any());

        assertTrue(result.isError());
        assertEquals("ALREADY_WATCHING", result.getMsg());
    }

    @Test
    public void create_movieWithNoCopies_returnsError() throws Exception {
        when(repository.findByMovie_Id(movie.getId())).thenReturn(Stream.of(new VisualizationLink(user, movie)));
        Result result = service.create(link);

        verify(repository, times(1)).findByMovie_Id(movie.getId());

        assertTrue(result.isError());
        assertEquals("NO_COPIES_AVAILABLE", result.getMsg());
    }

    @Test
    public void findBy_tokenNullToken_returnsEmptyOptional() throws Exception {
        Optional<VisualizationLink> possibleLink = service.findBy(VisualizationLinkQueryTypeSimple.TOKEN);

        verify(repository, never()).findByToken(anyString());

        assertFalse(possibleLink.isPresent());
    }

    @Test
    public void findBy_tokenEmptyToken_returnsEmptyOptional() throws Exception {
        Optional<VisualizationLink> possibleLink = service.findBy(VisualizationLinkQueryTypeSimple.TOKEN, "");

        verify(repository, never()).findByToken(anyString());

        assertFalse(possibleLink.isPresent());
    }

    @Test
    public void findBy_tokenWithOneParam_returnsEmptyOptional() throws Exception {
        try {
            service.findBy(VisualizationLinkQueryTypeSimple.TOKEN, "a");
            fail();
        } catch (Error e) {
            verify(repository, never()).findByToken(anyString());
            assertEquals("MISSING_USER_ID", e.getMessage());
        }
    }

    @Test
    public void findBy_tokenNoMatchingToken_returnsEmptyOptional() throws Exception {
        Optional<VisualizationLink> possibleLink = service.findBy(VisualizationLinkQueryTypeSimple.TOKEN, "a", user.getId().toString());

        verify(repository, times(1)).findByToken(anyString());

        assertFalse(possibleLink.isPresent());
    }

    @Test
    public void findBy_tokenExistingTokenForUserLink_returnsLink() throws Exception {
        String token = link.getToken();
        when(repository.findByToken(token)).thenReturn(Optional.of(link));

        Optional<VisualizationLink> possibleLink = service.findBy(VisualizationLinkQueryTypeSimple.TOKEN, token, user.getId().toString());

        verify(repository, times(1)).findByToken(anyString());

        assertTrue(possibleLink.isPresent());
        assertEquals(token, possibleLink.get().getToken());
    }

    @Test
    public void findBy_tokenExistingTokenForDifferentUserLink_returnsLink() throws Exception {
        String token = link.getToken();
        when(repository.findByToken(token)).thenReturn(Optional.of(link));

        try {
            service.findBy(VisualizationLinkQueryTypeSimple.TOKEN, token, "1");
            fail();
        } catch (Error e) {
            verify(repository, times(1)).findByToken(anyString());
            assertEquals("FOREIGN_LINK", e.getMessage());
        }
    }

    @Test
    public void findAllBy_movieNullId_returnsEmptyStream() throws Exception {
        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.MOVIE, null);

        verify(repository, never()).findByMovie_Id(anyLong());

        assertEquals(0, links.count());
    }

    @Test
    public void findAllBy_movieEmptyId_returnsEmptyStream() throws Exception {
        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.MOVIE, "");

        verify(repository, never()).findByMovie_Id(anyLong());

        assertEquals(0, links.count());
    }

    @Test
    public void findAllBy_movieInvalidId_returnsEmptyStream() throws Exception {
        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.MOVIE, "id");

        verify(repository, never()).findByMovie_Id(anyLong());

        assertEquals(0, links.count());
    }

    @Test
    public void findAllBy_movieNoMatchingId_returnsEmptyStream() throws Exception {
        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.MOVIE, "0");

        verify(repository, only()).findByMovie_Id(anyLong());

        assertEquals(0, links.count());
    }

    @Test
    public void findAllBy_movieMatchingId_returnsEmptyStream() throws Exception {
        when(repository.findByMovie_Id(0)).thenReturn(Stream.of(link));

        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.MOVIE, "0");

        verify(repository, only()).findByMovie_Id(anyLong());

        assertEquals(1, links.count());
    }

    @Test
    public void findAllBy_userNullId_returnsEmptyStream() throws Exception {
        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.USER, null);

        verify(repository, never()).findByUser_Id(anyLong());

        assertEquals(0, links.count());
    }

    @Test
    public void findAllBy_userEmptyId_returnsEmptyStream() throws Exception {
        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.USER, "");

        verify(repository, never()).findByUser_Id(anyLong());

        assertEquals(0, links.count());
    }

    @Test
    public void findAllBy_userInvalidId_returnsEmptyStream() throws Exception {
        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.USER, "id");

        verify(repository, never()).findByUser_Id(anyLong());

        assertEquals(0, links.count());
    }

    @Test
    public void findAllBy_userNoMatchingId_returnsEmptyStream() throws Exception {
        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.USER, "0");

        verify(repository, only()).findByUser_Id(anyLong());

        assertEquals(0, links.count());
    }

    @Test
    public void findAllBy_userMatchingId_returnsEmptyStream() throws Exception {
        when(repository.findByUser_Id(0)).thenReturn(Stream.of(link));

        Stream<VisualizationLink> links =
                service.findAllBy(VisualizationLinkQueryTypeMultiple.USER, "0");

        verify(repository, only()).findByUser_Id(anyLong());

        assertEquals(1, links.count());
    }

    @Test
    public void remove_nullTokenAndNullUser_returnsEmptyOptional() throws Exception {
        Result result = service.remove(null, null);

        verify(repository, never()).delete(any(VisualizationLink.class));

        assertTrue(result.isError());
        assertEquals("LINK_NOT_FOUND", result.getMsg());
    }

    @Test
    public void remove_emptyTokenAndUser_returnsEmptyOptional() throws Exception {
        Result result = service.remove("", "");

        verify(repository, never()).delete(any(VisualizationLink.class));

        assertTrue(result.isError());
        assertEquals("LINK_NOT_FOUND", result.getMsg());
    }

    @Test
    public void remove_withTokenAndNullUser_returnsEmptyOptional() throws Exception {
        when(repository.findByToken(link.getToken())).thenReturn(Optional.of(link));
        Result result = service.remove(link.getToken(), null);

        verify(repository, never()).delete(any(VisualizationLink.class));

        assertTrue(result.isError());
        assertEquals("MISSING_USER_ID", result.getMsg());
    }

    @Test
    public void remove_withTokenAndEmptyUser_returnsEmptyOptional() throws Exception {
        when(repository.findByToken(link.getToken())).thenReturn(Optional.of(link));
        Result result = service.remove(link.getToken(), "");

        verify(repository, never()).delete(any(VisualizationLink.class));

        assertTrue(result.isError());
        assertEquals("MISSING_USER_ID", result.getMsg());
    }

    @Test
    public void remove_existingTokenForUserLink_returnsLink() throws Exception {
        when(repository.findByToken(link.getToken())).thenReturn(Optional.of(link));
        Result result = service.remove(link.getToken(), user.getId().toString());

        verify(repository, times(1)).delete(any(VisualizationLink.class));

        assertTrue(result.isOk());
    }

    @Test
    public void remove_existingTokenForDifferentUserLink_returnsLink() throws Exception {
        when(repository.findByToken(link.getToken())).thenReturn(Optional.of(link));
        Result result = service.remove(link.getToken(), "1");

        verify(repository, never()).delete(any(VisualizationLink.class));

        assertTrue(result.isError());
        assertEquals("FOREIGN_LINK", result.getMsg());
    }

    @Test
    public void removeTimedOutLinks_callsRepository() throws Exception {
        service.removeTimedOutLinks();

        verify(repository, only()).deleteByExpeditionDateBefore(any());
    }

    @After
    public void tearDown() throws Exception {
        userService = null;
        movieService = null;
        repository = null;
        service = null;

        user = null;
        movie = null;
        link = null;
    }

}