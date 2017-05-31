package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.helpers.PasswordEncryptor;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.repositories.UserRepository;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.services.utils.ResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * AuthenticationService with a DB source
 *
 * Uses {@link UserRepository} interface to access to the underlying DB
 *
 * Use @Autowire to create an instance from @Component or @Bean
 */
@Service
public class AuthenticationServiceDB implements AuthenticationService {

    private static Logger log = LoggerFactory.getLogger(AuthenticationServiceDB.class);

    private final UserService repository;

    private final PasswordEncryptor encryptor;

    @Autowired
    public AuthenticationServiceDB(UserService service, PasswordEncryptor encryptor) {
        this.repository = service;
        this.encryptor = encryptor;
    }

    @Override
    public Result auth(String username, String password) {
        Optional<User> possibleUser = repository.findBy(UserQueryTypeSingle.USERNAME, username);
        Result error = ResultBuilder.error("LOGIN_FAILED");

        if (possibleUser.isPresent()) {
            User user = possibleUser.get();
            if (encryptor.equals(password, user.getPassword())) {
                log.info("User " + username + " authorized");
                return ResultBuilder.ok();
            }
            log.warn("Failed authentication attempt for user: " + username);
            return error.addField("PASSWORD");
        }
        return error.addField("USERNAME");
    }
}
