package unit.es.uji.agdc.videoclub.initializers;

import es.uji.agdc.videoclub.initializers.AdminInitializer;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.UserQueryTypeSingle;
import es.uji.agdc.videoclub.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;


import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Alberto on 04/12/2016.
 */
public class AdminInitializerTest {

    private UserService service;
    private AdminInitializer checker;

    @Before
    public void setUp() throws Exception {
        service = mock(UserService.class);
        checker = new AdminInitializer(service);
    }

    @Test
    public void createAdmin_adminDoesNotExists_createCalled() throws Exception {
        when(service.findBy(UserQueryTypeSingle.USERNAME, AdminInitializer.ADMIN_USERNAME)).thenReturn(Optional.empty());

        // Execute initializer
        checker.onApplicationReady();

        // Capture create parameter
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(service, times(1)).create(captor.capture());

        // Assert that the created user is an admin
        assertTrue(captor.getValue().getRole() == User.Role.ADMIN);
    }

    @Test
    public void createAdmin_adminAlreadyExists_createNotCalled() throws Exception {
        when(service.findBy(UserQueryTypeSingle.USERNAME, AdminInitializer.ADMIN_USERNAME)).thenReturn(Optional.of(new User()));

        // Execute initializer
        checker.onApplicationReady();

        // Capture create parameter
        verify(service, never()).create(any());
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        checker = null;
    }
}