package es.uji.agdc.videoclub.models.utils;

import es.uji.agdc.videoclub.models.User;

/**
 * Created by Alberto on 03/12/2016.
 */
public class UserFactory {
    public static User createMember() {
        return new User().setRole(User.Role.MEMBER);
    }

    public static User createAdmin() {
        return new User().setRole(User.Role.ADMIN);
    }
}
