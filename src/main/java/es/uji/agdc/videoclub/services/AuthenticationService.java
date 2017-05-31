package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.services.utils.Result;

/**
 * Authentication service interface. Every authentication service must implement this interface.
 */
public interface AuthenticationService {
    /**
     * Tries to authenticate a user based on a given username and password
     * @param username
     * @param password
     * @return An OK {@link Result} if everything went fine,
     * or an ERROR with the field that was not corresponding
     * with user data.
     */
    Result auth(String username, String password);
}
