package acceptance.es.uji.agdc.videoclub.usermanagement;

import es.uji.agdc.videoclub.Main;
import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.models.utils.UserFactory;
import es.uji.agdc.videoclub.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alberto on 11/01/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@Transactional
public class ListDefaultersTest {
    @Autowired
    private UserService service;

    private User userOne;
    private User userTwo;
    private User userThree;

    @Before
    public void setUp() throws Exception {
        userOne = UserFactory.createMember()
                .setDni("10614397N")
                .setName("Paco Sánchez Díaz")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pacosd@hotmail.com")
                .setUsername("paquito69")
                .setPassword("pacosd69")
                .setLastPayment(LocalDate.now().minusDays(15));

        userTwo = UserFactory.createMember()
                .setDni("20614397N")
                .setName("Paco Sánchez Díaz")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("paco@hotmail.com")
                .setUsername("paquito")
                .setPassword("pacosd69")
                .setLastPayment(LocalDate.now().minusMonths(1).minusDays(1));

        userThree = UserFactory.createMember()
                .setDni("51085104B")
                .setName("Pedro Ramirez López")
                .setAddress("C/Falsa, 123, 1º")
                .setPhone(693582471)
                .setEmail("pedro@hotmail.com")
                .setUsername("prueba2")
                .setPassword("pacosd69")
                .setLastPayment(LocalDate.now().minusMonths(2).minusDays(1));

    }

    @Test
    public void findDefaulterUsers_withDefaulterAndPayers_onlyReturnsDefaulters() throws Exception {
        // Given three different users on the system two of them defaulters and one payer
        service.create(userOne);
        service.create(userTwo);
        service.create(userThree);

        // When we obtain a list of defaulter users
        Stream<User> defaulterUsers = service.findDefaulterUsers();

        // Then we get the two of them that are defaulters
        List<User> defaultersList = defaulterUsers.collect(Collectors.toList());
        assertEquals(2, defaultersList.size());
        User defaulterOne = defaultersList.stream().filter(user ->
                user.getUsername().equals(userTwo.getUsername())).findFirst().get();
        User defaulterTwo = defaultersList.stream().filter(user ->
                user.getUsername().equals(userThree.getUsername())).findFirst().get();
        assertEquals(1, defaulterOne.getUnpaidMonths());
        assertEquals(2, defaulterTwo.getUnpaidMonths());
    }

    @After
    public void tearDown() throws Exception {
        userOne = null;
        userTwo = null;
        userThree = null;
    }
}
