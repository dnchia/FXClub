package es.uji.agdc.videoclub.validators;

import es.uji.agdc.videoclub.models.User;
import es.uji.agdc.videoclub.services.utils.Result;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Created by daniel on 15/12/16.
 */
@Component
public class UserValidator extends Validator<User> {

    @Override
    protected Result checkIfHasEmptyFields(User user, Result emptyError) {

        if (isFieldEmpty(user.getDni()))
            emptyError.addField("DNI");

        if (isFieldEmpty(user.getName()))
            emptyError.addField("Name");

        if (isFieldEmpty(user.getAddress()))
            emptyError.addField("Address");

        if (isFieldEmpty(user.getEmail()))
            emptyError.addField("Email");

        if (isFieldEmpty(user.getUsername()))
            emptyError.addField("Username");

        if (isFieldEmpty(user.getPassword()))
            emptyError.addField("Password");

        return emptyError;
    }

    @Override
    protected Result checkIfHasInvalidFields(User user, Result invalidError) {

        if (!isDni(user.getDni()))
            invalidError.addField("DNI");

        if (user.getName().length() < 2 || user.getName().length() > 50)
            invalidError.addField("Name");

        //FIXME: Encontrar un buen método para asegurar esto
        if (user.getAddress().length() < 10 || user.getAddress().length() > 100)
            invalidError.addField("Address");

        if (user.getPhone() < 100000000 || user.getPhone() > 999999999)
            invalidError.addField("Phone");

        if (!isEmail(user.getEmail()))
            invalidError.addField("Email");

        LocalDate actualDate = LocalDate.now();
        LocalDate lastPayment = user.getLastPayment();
        if (lastPayment != null && (lastPayment.getYear() < 1950 || lastPayment.compareTo(actualDate) > 0))
            invalidError.addField("LastPayment");

        if (user.getUsername().length() < 4 || user.getUsername().length() > 20)
            invalidError.addField("Username");

        if (user.getPassword().length() < 8 || user.getPassword().length() > 20)
            invalidError.addField("Password");


        return invalidError;
    }

    private boolean isEmail(String email) {
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern p = Pattern.compile(EMAIL_PATTERN);

        return p.matcher(email).matches();
    }

    private boolean isDni(String dni) {
        if (dni.length() == 9) {
            String DNI_PATTERN = "(([X-Z]{1})([-]?)(\\d{7})([-]?)([A-Z]{1}))|((\\d{8})([-]?)([A-Z]{1}))";
            Pattern p = Pattern.compile(DNI_PATTERN);

            boolean dni_valid_format = p.matcher(dni).matches();

            if (dni_valid_format)
                return true;
        }

        return false;
    }
}
