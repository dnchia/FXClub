package es.uji.agdc.videoclub.services.utils;

/**
 * Builder class that helps creating returns for different services
 */
public class ResultBuilder {
    /**
     * Creates an OK result
     * @return OK {@link Result}
     */
    public static Result ok() {
        return new Result(Result.Type.OK);
    }

    /**
     * Creates an ERROR result with a message
     * @param msg Error message
     * @return ERROR {@link Result} with an error message
     */
    public static Result error(String msg) {
        return new Result(Result.Type.ERROR, msg);
    }

    /**
     * Creates an error result with a message and a list containing the fields that caused the error
     * @param msg Error message
     * @param fields Fields that caused the error, separated with commas
     * @return ERROR {@link Result} with an error message and a list of fields affected by the error
     */
    public static Result error(String msg, String ...fields) {
        Result result = new Result(Result.Type.ERROR, msg);
        for (String field : fields) {
            result.addField(field);
        }
        return result;
    }
}
