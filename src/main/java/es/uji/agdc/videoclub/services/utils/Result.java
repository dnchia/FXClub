package es.uji.agdc.videoclub.services.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides a handy way to return informative results form service calls
 */
public class Result {
    public enum Type {
        OK, ERROR
    }

    private final Type type;
    private final String msg;
    private final List<String> fields;

    /**
     * Creates a result with the given type
     * @param type {@link Type}
     */
    public Result(Type type) {
        this(type, "");
    }

    /**
     * Creates a result with the given type and message
     * @param type {@link Type}
     * @param msg {@link String} containing the result message
     */
    public Result(Type type, String msg) {
        this(type, msg, new LinkedList<>());
    }

    /**
     * Creates a result with the given type, message and list of fields. Low level.
     * @param type {@link Type}
     * @param msg {@link String} the result message
     * @param fields {@link List<String>} a list of fields affected by the result
     */
    private Result(Type type, String msg, List<String> fields) {
        this.type = type;
        this.msg = msg;
        this.fields = fields;
    }

    public boolean isOk() {
        return type.equals(Type.OK);
    }

    public  boolean isError() {
        return !isOk();
    }

    public String getMsg() {
        return msg;
    }

    public String[] getFields() {
        return fields.toArray(new String[fields.size()]);
    }

    public Result addField(String field) {
        fields.add(field);
        return this;
    }

    @Override
    public String toString() {
        if (isOk()) {
            return "Result: OK";
        }
        return "Result: ERROR(" + msg +")" + fields.toString();
    }
}
