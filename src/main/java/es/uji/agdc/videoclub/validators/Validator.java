package es.uji.agdc.videoclub.validators;

import es.uji.agdc.videoclub.models.AbstractEntity;
import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.services.utils.ResultBuilder;

import java.util.Collection;
import java.util.List;

/**
 * Validates an entity
 */
public abstract class Validator<T extends AbstractEntity> {

    /**
     * Checks an {@link AbstractEntity} against the business rules
     * @param entity The {@link AbstractEntity} to be checked
     * @return An OK result if the entity is valid
     */
    public Result validate(T entity) {
        Result emptyFieldsResult =
                initializeCheckerResult(entity, this::checkIfHasEmptyFields, "EMPTY_PARAMETER");
        if (emptyFieldsResult.isError()) {
            return emptyFieldsResult;
        }

        Result invalidFieldsResult =
                initializeCheckerResult(entity, this::checkIfHasInvalidFields, "INVALID_PARAMETER");
        if (invalidFieldsResult.isError()) {
            return invalidFieldsResult;
        }

        return ResultBuilder.ok();
    }

    private Result initializeCheckerResult(T entity, Checker<T> checker, String msg) {
        Result result = ResultBuilder.error(msg);

        checker.check(entity, result);

        if (result.getFields().length > 0) {
            return result;
        }
        return ResultBuilder.ok();
    }

    /**
     * For a given entity check if it has empty fields based on the business logic rules
     * @param entity The entity to be checked
     * @return The {@link Result} of the validation
     */
    protected abstract Result checkIfHasEmptyFields(T entity, Result emptyError);

    /**
     * For a given entity check if it has invalid fields based on the business logic rules
     * @param entity The entity to be checked
     * @return The {@link Result} of the validation
     */
    protected abstract Result checkIfHasInvalidFields(T entity, Result invalidError);

    /**
     * Utility method that checks if a field is null or empty
     * @param field The field that has to be checked
     * @return Returns true if the field is empty
     */
    protected boolean isFieldEmpty(String field) {
        return field == null || field.isEmpty();
    }

    /**
     * Utility method that checks if a {@link Collection} of fields is null or empty
     * @param fields The collection that has to be checked
     * @return Returns true if the collection is empty
     */
    protected boolean isMultiFieldEmpty(Collection fields) {
        return fields == null || fields.isEmpty();
    }

    /**
     * Checks if a given string contains only numbers
     * @param string
     * @return
     */
    protected boolean isNumber(String string) {
        //FIXME I think that this can be done by trying to parse the number an catching the exception
        char[] chars = string.toCharArray();
        boolean isNumber = true;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] < 48 || chars[i] > 57) {
                isNumber = false;
                break;
            }
        }

        return isNumber;
    }

    private interface Checker<T> {
        Result check(T entity, Result result);
    }

}
