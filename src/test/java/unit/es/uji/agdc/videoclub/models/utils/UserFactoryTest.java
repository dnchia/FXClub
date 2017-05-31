package unit.es.uji.agdc.videoclub.models.utils;

import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 03/12/2016.
 */
public class UserFactoryTest {
    @Test
    public void create_member_isMember() throws Exception {
        User user = UserFactory.createMember();
        assertTrue(user.isMember());
    }

    @Test
    public void create_admin_isAdmin() throws Exception {
        User user = UserFactory.createAdmin();
        assertTrue(user.isAdmin());
    }
}