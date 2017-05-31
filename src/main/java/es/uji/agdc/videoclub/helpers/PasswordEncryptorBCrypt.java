package es.uji.agdc.videoclub.helpers;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Created by Alberto on 04/12/2016.
 */
@Component
public class PasswordEncryptorBCrypt implements PasswordEncryptor{

    @Override
    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public boolean equals(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
