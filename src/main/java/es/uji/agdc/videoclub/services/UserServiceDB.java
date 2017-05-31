package es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.helpers.PasswordEncryptor;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.VisualizationLink;
import es.uji.agdc.videoclub.repositories.UserRepository;
import es.uji.agdc.videoclub.repositories.VisualizationLinkRepository;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.services.utils.ResultBuilder;
import es.uji.agdc.videoclub.validators.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Alberto on 04/12/2016.
 */
@Service
public class UserServiceDB implements UserService{
    private Logger log = LoggerFactory.getLogger(UserServiceDB.class);
    private static final int UNPAID_MONTHS_TO_BECOME_A_DEFAULTER = 1;

    private final UserRepository repository;
    private final VisualizationLinkRepository linkRepository;
    private final PasswordEncryptor encryptor;
    private final Validator<User> validator;

    @Autowired
    public UserServiceDB(UserRepository repository, VisualizationLinkRepository linkRepository,
                         PasswordEncryptor encryptor, Validator<User> validator) {
        this.repository = repository;
        this.linkRepository = linkRepository;
        this.encryptor = encryptor;
        this.validator = validator;
    }

    @Override
    @Transactional
    public Result create(User user) {

        if (!user.isNew()) {
            log.warn("create(): called with a non-new user");
            return ResultBuilder.error("OLD_USER");
        }

        Result validatorResult = validator.validate(user);
        if (validatorResult.isError()) {
            return validatorResult;
        }

        Result alreadyExists = ResultBuilder.error("USER_ALREADY_EXISTS");

        Optional<User> userByDni = findBy(UserQueryTypeSingle.DNI, user.getDni());
        if (userByDni.isPresent()) {
            return alreadyExists.addField("DNI");
        }

        Optional<User> userByUsername = findBy(UserQueryTypeSingle.USERNAME, user.getUsername());
        if (userByUsername.isPresent()) {
            return alreadyExists.addField("Username");
        }

        Optional<User> userByEmail = findBy(UserQueryTypeSingle.EMAIL, user.getEmail());
        if (userByEmail.isPresent()) {
            return alreadyExists.addField("Email");
        }

        try {
            user.setPassword(encryptor.hash(user.getPassword()));
            repository.save(user);
            return ResultBuilder.ok();
        } catch (Exception e) {
            return ResultBuilder.error(e.getMessage());
        }
    }

    @Override
    public Optional<User> findBy(UserQueryTypeSingle field, String value) {
        if (isNonValidValue(value)) {
            log.warn("findBy(" + field + "): Called with null or empty value");
            return Optional.empty();
        }

        switch (field) {
            case ID:
                return findOneIfValidId(value);
            case DNI:
                return repository.findByDni(value);
            case USERNAME:
                return repository.findByUsername(value);
            case EMAIL:
                return repository.findByEmail(value);
            default:
                throw new Error("Unimplemented");
        }
    }

    private boolean isNonValidValue(String value) {
        return value == null || value.isEmpty();
    }

    private Optional<User> findOneIfValidId(String value) {
        try {
            return repository.findOne(Long.valueOf(value));
        } catch (NumberFormatException e) {
            log.warn("ID couldn't be parsed. " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Stream<User> findAllBy(UserQueryTypeMultiple field, String value) {
        if (isNonValidValue(value)) {
            log.warn("findAllBy(" + field + "): Called with null or empty value");
            return Stream.empty();
        }

        switch (field) {
            case ROLE:
                return findAllIfValidRole(value);
            default:
                throw new Error("Unimplemented");
        }
    }

    private Stream<User> findAllIfValidRole(String value) {
        try {
            return repository.findByRole(User.Role.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return Stream.empty();
        }
    }

    @Override
    public Stream<User> findDefaulterUsers() {
        return repository.findByLastPaymentBefore(LocalDate.now().minusMonths(UNPAID_MONTHS_TO_BECOME_A_DEFAULTER));
    }

    @Override
    @Transactional
    public Result update(User user, long userIdThatPerformsUpdate) {

        Optional<User> possibleUser = repository.findOne(user.getId());
        if (!possibleUser.isPresent()) {
            log.warn("update(): called with non-existing user");
            return ResultBuilder.error("USER_NOT_FOUND");
        }

        User userToModify = possibleUser.get();

        Optional<User> possibleModifier = repository.findOne(userIdThatPerformsUpdate);
        if (!possibleModifier.isPresent() ||
                (userIdThatPerformsUpdate != userToModify.getId() &&
                        possibleModifier.get().getRole() != User.Role.ADMIN)) {
            log.warn("update(): called with unauthorized user: " + userIdThatPerformsUpdate);
            return ResultBuilder.error("FOREIGN_USER");
        }

        User userThatModifies = possibleModifier.get();

        Result alreadyExists = ResultBuilder.error("USER_ALREADY_EXISTS");

        Optional<User> userByDni = findBy(UserQueryTypeSingle.DNI, user.getDni());
        if (userByDni.isPresent() && !userByDni.get().getId().equals(userToModify.getId())) {
            return alreadyExists.addField("DNI");
        }

        Optional<User> userByUsername = findBy(UserQueryTypeSingle.USERNAME, user.getUsername());
        if (userByUsername.isPresent() && !userByUsername.get().getId().equals(userToModify.getId())) {
            return alreadyExists.addField("Username");
        }

        Optional<User> userByEmail = findBy(UserQueryTypeSingle.EMAIL, user.getEmail());
        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(userToModify.getId())) {
            return alreadyExists.addField("Email");
        }

        User updatedUser = fillUserWithChanges(userToModify, user, userThatModifies.getRole());

        Result validatorResult = validator.validate(updatedUser);
        boolean containsPasswordError = Arrays.asList(validatorResult.getFields()).contains("Password");
        boolean hasChangedPassword = !isEmpty(user.getPassword());

        if (validatorResult.isError() && containsPasswordError && hasChangedPassword
                || validatorResult.isError() && !containsPasswordError) {
            return validatorResult;
        }

        if (hasChangedPassword) {
            updatedUser.setPassword(encryptor.hash(updatedUser.getPassword()));
        }

        try {
            repository.save(updatedUser);
            return ResultBuilder.ok();
        } catch (Exception e) {
            return ResultBuilder.error(e.getMessage());
        }
    }

    private User fillUserWithChanges(User toModify, User changes, User.Role whoChanges) {
        toModify.setDni(changes.getDni());
        toModify.setName(changes.getName());
        toModify.setAddress(changes.getAddress());
        toModify.setPhone(changes.getPhone());
        toModify.setEmail(changes.getEmail());
        toModify.setUsername(changes.getUsername());
        if (!isEmpty(changes.getPassword())) {
            toModify.setPassword(changes.getPassword());
        }
        if (whoChanges == User.Role.ADMIN) {
            toModify.setLastPayment(changes.getLastPayment());
        }

        return toModify;
    }

    private boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    @Override
    @Transactional
    public Result remove(long userId) {
        Optional<User> possibleUser = repository.findOne(userId);
        if (!possibleUser.isPresent()) {
            log.warn("remove(): called with a non-existing user id: " + userId);
            return ResultBuilder.error("USER_NOT_FOUND");
        }
        User user = possibleUser.get();
        repository.delete(user);
        linkRepository.deleteByUser_Id(user.getId());
        return ResultBuilder.ok();
    }
}
