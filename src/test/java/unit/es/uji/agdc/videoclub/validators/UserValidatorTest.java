package unit.es.uji.agdc.videoclub.validators;

import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.validators.UserValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Created by alberto on 15/12/16.
 */
public class UserValidatorTest {

    private UserValidator validator;
    private User user;

    @Before
    public void setUp() throws Exception {
        validator = new UserValidator();
        user = new User() //FIXME Create a fixture factory
                .setDni("10614397N")
                .setName("Paco Sánchez Díaz")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pacosd@hotmail.com")
                .setUsername("paquito69")
                .setPassword("pacosd69")
                .setRole(User.Role.MEMBER);
    }

    @Test
    public void validate_valid_isValid() throws Exception {
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_emptyDni_isInvalid() throws Exception {
        user.setDni("");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void validate_nullDni_isInvalid() throws Exception {
        user.setDni(null);
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void validate_emptyName_isInvalid() throws Exception {
        user.setName("");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Name]", result.toString());
    }

    @Test
    public void validate_nullName_isInvalid() throws Exception {
        user.setName(null);
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Name]", result.toString());
    }

    @Test
    public void validate_emptyAddress_isInvalid() throws Exception {
        user.setAddress("");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Address]", result.toString());
    }

    @Test
    public void validate_nullAddress_isInvalid() throws Exception {
        user.setAddress(null);
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Address]", result.toString());
    }

    @Test
    public void validate_emptyEmail_isInvalid() throws Exception {
        user.setEmail("");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Email]", result.toString());
    }

    @Test
    public void validate_nullEmail_isInvalid() throws Exception {
        user.setEmail(null);
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Email]", result.toString());
    }

    @Test
    public void validate_emptyUsername_isInvalid() throws Exception {
        user.setUsername("");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Username]", result.toString());
    }


    @Test
    public void validate_nullUsername_isInvalid() throws Exception {
        user.setUsername(null);
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Username]", result.toString());
    }

    @Test
    public void validate_emptyPassword_isInvalid() throws Exception {
        user.setPassword("");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Password]", result.toString());
    }

    @Test
    public void validate_nullPassword_isInvalid() throws Exception {
        user.setPassword(null);
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(EMPTY_PARAMETER)[Password]", result.toString());
    }

    @Test
    public void validate_multipleEmptyFields_isInvalid() throws Exception {
        user.setDni("")
            .setName("")
            .setAddress("");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("EMPTY_PARAMETER", result.getMsg());
        assertEquals(3, result.getFields().length);
    }

    @Test
    public void validate_shortDni_isInvalid() throws Exception {
        user.setDni("1061397N");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void validate_longDni_isInvalid() throws Exception {
        user.setDni("106139017N");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void validate_twoCharsDni_isInvalid() throws Exception {
        user.setDni("1061439NN");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void validate_mixedCharsDni_isInvalid() throws Exception {
        user.setDni("10W14397N");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void validate_flippedDni_isInvalid() throws Exception {
        user.setDni("N79341601");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void validate_longName_isInvalid() throws Exception {
        user.setDni("N79341601");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[DNI]", result.toString());
    }

    @Test
    public void validate_1CharName_isInvalid() throws Exception {
        user.setName(new String(new char[1]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Name]", result.toString());
    }

    @Test
    public void validate_2CharName_isValid() throws Exception {
        user.setName(new String(new char[2]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_50CharName_isValid() throws Exception {
        user.setName(new String(new char[50]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_51CharName_isInvalid() throws Exception {
        user.setName(new String(new char[51]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Name]", result.toString());
    }

    @Test
    public void validate_9CharAddress_isInvalid() throws Exception {
        user.setAddress(new String(new char[9]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Address]", result.toString());
    }

    @Test
    public void validate_10CharAddress_isValid() throws Exception {
        user.setAddress(new String(new char[10]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_100CharAddress_isValid() throws Exception {
        user.setAddress(new String(new char[100]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_101CharAddress_isInvalid() throws Exception {
        user.setAddress(new String(new char[101]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Address]", result.toString());
    }

    @Test
    public void validate_tooShortPhone_isInvalid() throws Exception {
        user.setPhone(99999999);
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Phone]", result.toString());
    }

    @Test
    public void validate_lowPhone_isValid() throws Exception {
        user.setPhone(100000000);
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_highPhone_isValid() throws Exception {
        user.setPhone(999999999);
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_tooLongPhone_isValid() throws Exception {
        user.setPhone(1000000000);
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Phone]", result.toString());
    }

    @Test
    public void validate_noAtInEmail_isInvalid() throws Exception {
        user.setEmail("aa.com");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Email]", result.toString());
    }

    @Test
    public void validate_noDomainEmail_isInvalid() throws Exception {
        user.setEmail("a@acom");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Email]", result.toString());
    }

    @Test
    public void validate_singleCharEmail_isInvalid() throws Exception {
        user.setEmail("a");
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Email]", result.toString());
    }

    @Test
    public void validate_shortAsPossibleEmail_isValid() throws Exception {
        user.setEmail("a@a.aa");
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_afterTodayPayment_isInvalid() throws Exception {
        user.setLastPayment(LocalDate.now().plusDays(1));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[LastPayment]", result.toString());
    }

    @Test
    public void validate_todayPayment_isValid() throws Exception {
        user.setLastPayment(LocalDate.now());
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_3CharUsername_isInvalid() throws Exception {
        user.setUsername(new String(new char[3]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Username]", result.toString());
    }

    @Test
    public void validate_4CharUsername_isValid() throws Exception {
        user.setUsername(new String(new char[4]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_20CharUsername_isValid() throws Exception {
        user.setUsername(new String(new char[20]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_21CharUsername_isInvalid() throws Exception {
        user.setUsername(new String(new char[21]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Username]", result.toString());
    }

    @Test
    public void validate_7CharPassword_isInvalid() throws Exception {
        user.setPassword(new String(new char[7]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Password]", result.toString());
    }

    @Test
    public void validate_8CharPassword_isValid() throws Exception {
        user.setPassword(new String(new char[8]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_20CharPassword_isValid() throws Exception {
        user.setPassword(new String(new char[20]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isOk());
    }

    @Test
    public void validate_21CharPassword_isInvalid() throws Exception {
        user.setPassword(new String(new char[21]).replace('\0', 'a'));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("Result: ERROR(INVALID_PARAMETER)[Password]", result.toString());
    }

    @Test
    public void validate_multipleInvalidFields_isInvalid() throws Exception {
        user.setName("a")
            .setDni("aaa4")
            .setAddress("aaaaa")
            .setLastPayment(LocalDate.now().plusDays(1));
        Result result = validator.validate(user);
        assertTrue(result.isError());
        assertEquals("INVALID_PARAMETER", result.getMsg());
        assertEquals(4, result.getFields().length);
    }

    @After
    public void tearDown() throws Exception {
        validator = null;
        user = null;
    }

}
