package es.uji.agdc.videoclub.helpers;

/**
 * Created by Alberto on 04/12/2016.
 */
public interface PasswordEncryptor {
    String hash(String password);

    boolean equals(String password, String hash);
}
