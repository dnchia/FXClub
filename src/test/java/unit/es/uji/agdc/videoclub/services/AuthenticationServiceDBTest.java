package unit.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.helpers.PasswordEncryptor;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.AuthenticationService;
import es.uji.agdc.videoclub.services.AuthenticationServiceDB;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Alberto on 03/12/2016.
 */
public class AuthenticationServiceDBTest {

    private AuthenticationService authService;
    private UserService service;
    private PasswordEncryptor encryptor;
    private User user;

    @Before
    public void setUp() throws Exception {
        service = mock(UserService.class);
        encryptor = mock(PasswordEncryptor.class);
        authService = new AuthenticationServiceDB(service, encryptor);
        user = new User()
                .setUsername("paquito69")
                .setPassword("pacosd69");

        when(service.findBy(any(), anyString())).thenReturn(Optional.empty());
        when(service.findBy(UserQueryTypeSingle.USERNAME, user.getUsername())).thenReturn(Optional.of(user));

        when(encryptor.equals(anyString(), anyString())).thenReturn(false);
        when(encryptor.equals(user.getPassword(), user.getPassword())).thenReturn(true);
    }

    @Test
    public void auth_rightUsernameAndPassword_ok() throws Exception {
        Result result = authService.auth(user.getUsername(), "pacosd69");
        assertTrue(result.isOk());
    }

    @Test
    public void auth_badUsername_usernameError() throws Exception {
        Result result = authService.auth("paquito", "pacosd69");
        assertEquals("Result: ERROR(LOGIN_FAILED)[USERNAME]", result.toString());
    }

    @Test
    public void auth_badUsername_passwordError() throws Exception {
        Result result = authService.auth(user.getUsername(), "pacosd6");
        assertEquals("Result: ERROR(LOGIN_FAILED)[PASSWORD]", result.toString());
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        authService = null;
    }

}