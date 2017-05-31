package integration.es.uji.agdc.videoclub.helpers;

import es.uji.agdc.videoclub.helpers.PasswordEncryptor;
import es.uji.agdc.videoclub.helpers.PasswordEncryptorBCrypt;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 04/12/2016.
 */
public class PasswordEncryptorBCryptTest {
    private static PasswordEncryptor encryptor;

    @BeforeClass
    public static void setUp() throws Exception {
        encryptor = new PasswordEncryptorBCrypt();
    }

    @Test
    public void passwordEncryption() throws Exception {
        String password = "12345";
        String hash = encryptor.hash(password);

        assertNotEquals(password, hash);
        assertTrue(encryptor.equals(password, hash));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        encryptor = null;
    }
}