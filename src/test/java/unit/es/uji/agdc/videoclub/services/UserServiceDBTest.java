package unit.es.uji.agdc.videoclub.services;

import es.uji.agdc.videoclub.helpers.PasswordEncryptor;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.repositories.UserRepository;
import es.uji.agdc.videoclub.repositories.VisualizationLinkRepository;
import es.uji.agdc.videoclub.services.*;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.validators.UserValidator;
import es.uji.agdc.videoclub.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Alberto on 04/12/2016.
 */
public class UserServiceDBTest {

    private UserRepository repository;
    private VisualizationLinkRepository linkRepository;
    private PasswordEncryptor encryptor;
    private UserService service;

    private User user;
    private User savedUser;
    private User secondaryUser;

    @Before
    public void setUp() throws Exception {
        repository = mock(UserRepository.class);
        linkRepository = mock(VisualizationLinkRepository.class);
        encryptor = mock(PasswordEncryptor.class);
        Validator<User> validator = new UserValidator();
        service = new UserServiceDB(repository, linkRepository, encryptor, validator);

        user = new User()
                .setDni("10614397N")
                .setName("Paco Sánchez Díaz")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pacosd@hotmail.com")
                .setUsername("paquito69")
                .setPassword("pacosd69")
                .setRole(User.Role.MEMBER);

        savedUser = new User()
                .setDni(user.getDni())
                .setName(user.getName())
                .setAddress(user.getAddress())
                .setPhone(user.getPhone())
                .setEmail(user.getEmail())
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setRole(user.getRole());
        savedUser.setId(0L);

        secondaryUser = new User()
                .setDni("20614397N")
                .setName("Paco Sánchez Día")
                .setAddress("C/Falsa, 12, 1º")
                .setPhone(693582471)
                .setEmail("paco@hotmail.com")
                .setUsername("paquito6")
                .setPassword("pacosd689")
                .setRole(User.Role.MEMBER);

        when(repository.findOne(anyLong())).thenReturn(Optional.empty());
        when(repository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repository.findByDni(anyString())).thenReturn(Optional.empty());

        when(repository.findByLastPaymentBefore(any())).thenReturn(Stream.empty());
        when(encryptor.hash(anyString())).thenReturn("password");
    }

    @Test
    public void create_validUser_saveCalledAndReturnsOk() throws Exception {
        String userPassword = user.getPassword();
        Result result = service.create(user);
        verify(encryptor, only()).hash(userPassword);
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
    }

    @Test
    public void create_inValidUser_saveNotCalledAndReturnsError() throws Exception {
        user.setName("");
        Result result = service.create(user);
        verify(encryptor, never()).hash(anyString());
        verify(repository, never()).save(user);
        assertTrue(result.isError());
    }

    @Test
    public void create_twoUsersWithoutCollisions_saveCalledAndReturnsOk() throws Exception {
        service.create(user);

        when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findByDni(user.getDni())).thenReturn(Optional.of(user));

        Result result = service.create(secondaryUser);
        verify(encryptor, times(2)).hash(anyString());
        verify(repository, times(2)).save(any(User.class));
        assertTrue(result.isOk());
    }

    @Test
    public void create_twoUsersWithDniCollision_saveCalledOnceAndReturnsError() throws Exception {
        service.create(user);

        when(repository.findByDni(user.getDni())).thenReturn(Optional.of(user));

        secondaryUser.setDni(user.getDni());
        Result result = service.create(secondaryUser);
        verify(encryptor, times(1)).hash(anyString());
        verify(repository, times(1)).save(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(USER_ALREADY_EXISTS)[DNI]", result.toString());
    }

    @Test
    public void create_twoUsersWithUsernameCollision_saveCalledOnceAndReturnsError() throws Exception {
        service.create(user);

        when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        secondaryUser.setUsername(user.getUsername());
        Result result = service.create(secondaryUser);
        verify(encryptor, times(1)).hash(anyString());
        verify(repository, times(1)).save(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(USER_ALREADY_EXISTS)[Username]", result.toString());
    }

    @Test
    public void create_twoUsersWithEmailCollision_saveCalledOnceAndReturnsError() throws Exception {
        service.create(user);

        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        secondaryUser.setEmail(user.getEmail());
        Result result = service.create(secondaryUser);
        verify(encryptor, times(1)).hash(anyString());
        verify(repository, times(1)).save(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(USER_ALREADY_EXISTS)[Email]", result.toString());
    }

    @Test
    public void create_oldUser_fails() throws Exception {
        User oldUser = mock(User.class);
        when(oldUser.getId()).thenReturn(new Long(0));
        Result result = service.create(oldUser);
        verify(repository, never()).save(user);
        assertTrue(result.isError());
        assertEquals("OLD_USER", result.getMsg());
    }

    @Test
    public void findBy_username_found() throws Exception {
        when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.USERNAME, user.getUsername());
        assertTrue(possibleUser.isPresent());
    }

    @Test
    public void findBy_username_notFound() throws Exception {
        when(repository.findByUsername(anyString())).thenReturn(Optional.empty());
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.USERNAME, user.getUsername());
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_username_nullValueReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.USERNAME, null);
        verify(repository, never()).findByUsername(null);
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_username_emptyStringReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.USERNAME, "");
        verify(repository, never()).findByUsername("");
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_id_found() throws Exception {
        when(repository.findOne(0L)).thenReturn(Optional.of(user));
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.ID, Long.toString(0));
        assertTrue(possibleUser.isPresent());
    }

    @Test
    public void findBy_id_notFound() throws Exception {
        when(repository.findOne(anyLong())).thenReturn(Optional.empty());
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.ID, Long.toString(0));
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_id_nullValueReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.ID, null);
        verify(repository, never()).findOne(null);
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_id_emptyStringReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.ID, "");
        verify(repository, never()).findOne(any());
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_id_nonValidNumberReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.ID, "not_valid");
        verify(repository, never()).findOne(any());
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_dni_found() throws Exception {
        when(repository.findByDni(user.getDni())).thenReturn(Optional.of(user));
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.DNI, user.getDni());
        assertTrue(possibleUser.isPresent());
    }

    @Test
    public void findBy_dni_notFound() throws Exception {
        when(repository.findByDni(anyString())).thenReturn(Optional.empty());
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.DNI, user.getDni());
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_dni_nullValueReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.DNI, null);
        verify(repository, never()).findByDni(null);
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_dni_emptyStringReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.DNI, "");
        verify(repository, never()).findByDni(any());
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_email_found() throws Exception {
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.EMAIL, user.getEmail());
        assertTrue(possibleUser.isPresent());
    }

    @Test
    public void findBy_email_notFound() throws Exception {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.EMAIL, user.getEmail());
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_email_nullValueReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.EMAIL, null);
        verify(repository, never()).findByEmail(null);
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findBy_email_emptyStringReturnsEmpty() throws Exception {
        Optional<User> possibleUser = service.findBy(UserQueryTypeSingle.EMAIL, "");
        verify(repository, never()).findByEmail(any());
        assertTrue(!possibleUser.isPresent());
    }

    @Test
    public void findAll_role_foundOneMemberUpperCase() throws Exception {
        when(repository.findByRole(User.Role.MEMBER)).thenReturn(Stream.of(user));
        Stream<User> members = service.findAllBy(UserQueryTypeMultiple.ROLE, User.Role.MEMBER.toString());
        assertEquals(1, members.count());
    }

    @Test
    public void findAll_role_foundOneMemberLowerCase() throws Exception {
        when(repository.findByRole(User.Role.MEMBER)).thenReturn(Stream.of(user));
        Stream<User> members = service.findAllBy(UserQueryTypeMultiple.ROLE, "member");
        assertEquals(1, members.count());
    }

    @Test
    public void findAll_role_foundNoMemberWithNonValidRole() throws Exception {
        when(repository.findByRole(User.Role.MEMBER)).thenReturn(Stream.of(user));
        Stream<User> members = service.findAllBy(UserQueryTypeMultiple.ROLE, "membe");
        assertEquals(0, members.count());
    }

    @Test
    public void findAll_role_foundNoneWithNullRole() throws Exception {
        Stream<User> members = service.findAllBy(UserQueryTypeMultiple.ROLE, null);
        verify(repository, never()).findByRole(null);
        assertEquals(0, members.count());
    }

    @Test
    public void findAll_role_foundNoneWithEmptyRole() throws Exception {
        Stream<User> members = service.findAllBy(UserQueryTypeMultiple.ROLE, "");
        verify(repository, never()).findByRole(any());
        assertEquals(0, members.count());
    }

    @Test
    public void findAllDefaulters() throws Exception {
        user.setLastPayment(LocalDate.now().minusMonths(1).minusDays(1));
        when(repository.findByLastPaymentBefore(LocalDate.now().minusMonths(1))).thenReturn(Stream.of(user));

        Stream<User> defaulterUsers = service.findDefaulterUsers();

        verify(repository, only()).findByLastPaymentBefore(LocalDate.now().minusMonths(1));
        assertEquals(1, defaulterUsers.count());
    }

    @Test
    public void update_validUserWithNoChangesAndValidModifier_saveCalledAndReturnsOk() throws Exception {
        user.setId(0L);
        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(savedUser));
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(savedUser));
        when(repository.findByDni(user.getDni())).thenReturn(Optional.of(savedUser));

        Result result = service.update(user, user.getId());
        System.out.println(result);
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
    }

    @Test
    public void update_validUserWithNoChangesAndInvalidModifier_saveNotCalledReturnsError() throws Exception {
        user.setId(0L);
        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));
        when(repository.findOne(1L)).thenReturn(Optional.of(new User().setRole(User.Role.MEMBER)));

        Result result = service.update(user, 1);
        System.out.println(result);
        verify(repository, times(0)).save(user);
        assertTrue(result.isError());
        assertEquals("FOREIGN_USER", result.getMsg());
    }

    @Test
    public void update_validUserWithNoChangesAndValidAdminModifier_saveCalledReturnsOk() throws Exception {
        user.setId(0L);
        user.setPassword("");
        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));
        when(repository.findOne(1L)).thenReturn(Optional.of(new User().setRole(User.Role.ADMIN)));

        Result result = service.update(user, 1);
        System.out.println(result);
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
    }

    @Test
    public void update_validUserWithValidChangesAndValidModifier_saveCalledAndReturnsOk() throws Exception {
        user.setId(0L);
        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));
        user.setUsername("paquito");
        Result result = service.update(user, user.getId());
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
    }

    @Test
    public void update_inValidUser_saveNotCalledAndReturnsError() throws Exception {
        user.setId(0L);
        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));
        user.setName("");
        Result result = service.update(user, user.getId());
        verify(repository, never()).save(user);
        assertTrue(result.isError());
    }

    @Test
    public void update_userWithDniCollision_saveNotCalledAndReturnsError() throws Exception {
        user.setId(0L);
        secondaryUser.setId(1L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));
        when(repository.findByDni(secondaryUser.getDni())).thenReturn(Optional.of(secondaryUser));

        user.setDni(secondaryUser.getDni());
        Result result = service.update(user, user.getId());
        verify(repository, times(0)).save(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(USER_ALREADY_EXISTS)[DNI]", result.toString());
    }

    @Test
    public void update_userWithUsernameCollision_saveNotCalledAndReturnsError() throws Exception {
        user.setId(0L);
        secondaryUser.setId(1L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));
        when(repository.findByUsername(secondaryUser.getUsername())).thenReturn(Optional.of(secondaryUser));

        user.setUsername(secondaryUser.getUsername());
        Result result = service.update(user, user.getId());
        verify(repository, times(0)).save(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(USER_ALREADY_EXISTS)[Username]", result.toString());
    }

    @Test
    public void update_userWithEmailCollision_saveNotCalledAndReturnsError() throws Exception {
        user.setId(0L);
        secondaryUser.setId(1L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));
        when(repository.findByEmail(secondaryUser.getEmail())).thenReturn(Optional.of(secondaryUser));

        user.setEmail(secondaryUser.getEmail());
        Result result = service.update(user, user.getId());
        verify(repository, times(0)).save(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(USER_ALREADY_EXISTS)[Email]", result.toString());
    }

    @Test
    public void update_userChangeDni_saveCalledReturnsOkAndIsModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        user.setDni(secondaryUser.getDni());
        Result result = service.update(user, user.getId());
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertEquals(secondaryUser.getDni(), savedUser.getDni());
    }

    @Test
    public void update_userChangeName_saveCalledReturnsOkAndIsModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        user.setName(secondaryUser.getName());
        Result result = service.update(user, user.getId());
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertEquals(secondaryUser.getName(), savedUser.getName());
    }

    @Test
    public void update_userChangeAddress_saveCalledReturnsOkAndIsModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        user.setAddress(secondaryUser.getAddress());
        Result result = service.update(user, user.getId());
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertEquals(secondaryUser.getAddress(), savedUser.getAddress());
    }

    @Test
    public void update_userChangePhone_saveCalledReturnsOkAndIsModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        user.setPhone(secondaryUser.getPhone());
        Result result = service.update(user, user.getId());
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertEquals(secondaryUser.getPhone(), savedUser.getPhone());
    }

    @Test
    public void update_userChangeEmail_saveCalledReturnsOkAndIsModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        user.setEmail(secondaryUser.getEmail());
        Result result = service.update(user, user.getId());
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertEquals(secondaryUser.getEmail(), savedUser.getEmail());
    }

    @Test
    public void update_userChangeUsername_saveCalledReturnsOkAndIsModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        user.setUsername(secondaryUser.getUsername());
        Result result = service.update(user, user.getId());
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertEquals(secondaryUser.getUsername(), savedUser.getUsername());
    }

    @Test
    public void update_userChangePassword_saveCalledReturnsOkAndIsModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        String originalPassword = savedUser.getPassword();
        String newPassword = "newpassword";
        user.setPassword(newPassword);
        Result result = service.update(user, user.getId());
        verify(encryptor, times(1)).hash(newPassword);
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertNotEquals(originalPassword, savedUser.getPassword());
    }

    @Test
    public void update_userChangePaymentDate_saveCalledReturnsOkAndIsNotModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));

        user.setLastPayment(LocalDate.now());
        Result result = service.update(user, user.getId());
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertEquals(null, savedUser.getLastPayment());
    }

    @Test
    public void update_adminChangePaymentDate_saveCalledReturnsOkAndIsNotModified() throws Exception {
        user.setId(0L);

        when(repository.findOne(0L)).thenReturn(Optional.of(savedUser));
        when(repository.findOne(1L)).thenReturn(Optional.of(new User().setRole(User.Role.ADMIN)));

        LocalDate now = LocalDate.now();
        user.setLastPayment(now);
        Result result = service.update(user, 1);
        verify(repository, times(1)).save(user);
        assertTrue(result.isOk());
        assertEquals(now, savedUser.getLastPayment());
    }

    @Test
    public void removeUser_existingUser_callsDeleteByUserAndDelete() throws Exception {
        user.setId(1L);
        when(repository.findOne(1L)).thenReturn(Optional.of(user));

        Result result = service.remove(user.getId());

        verify(linkRepository, only()).deleteByUser_Id(user.getId());
        verify(repository, times(1)).delete(user);
        assertTrue(result.isOk());
    }

    @Test
    public void removeUserLinks_nonExistingUser_callsDeleteByUser() throws Exception {
        user.setId(1L);

        Result result = service.remove(user.getId());

        verify(linkRepository, never()).deleteByUser_Id(user.getId());
        verify(repository, never()).delete(user);
        assertTrue(result.isError());
        assertEquals("USER_NOT_FOUND", result.getMsg());
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        linkRepository = null;
        service = null;
        user = null;
        secondaryUser = null;
    }

}