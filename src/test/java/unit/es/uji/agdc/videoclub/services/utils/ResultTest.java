package unit.es.uji.agdc.videoclub.services.utils;

import es.uji.agdc.videoclub.services.utils.Result;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 03/12/2016.
 */
public class ResultTest {
    @Test
    public void result_ok_isOkWithNoMsgAndNoFields() throws Exception {
        Result result = new Result(Result.Type.OK);
        assertTrue(result.isOk() && result.getMsg().isEmpty() && result.getFields().length == 0);
    }

    @Test
    public void result_error_isErrorWithMsgAndNoFields() throws Exception {
        Result result = new Result(Result.Type.ERROR, "Something wrong");
        assertTrue(result.isError() && !result.getMsg().isEmpty() && result.getFields().length == 0);
    }

    @Test
    public void result_error_isErrorWithMsgAndFields() throws Exception {
        Result result = new Result(Result.Type.ERROR, "Something wrong")
                .addField("Field 1");
        assertTrue(result.isError() && !result.getMsg().isEmpty() && result.getFields().length == 1);
    }

    @Test
    public void result_ok_toString() throws Exception {
        Result result = new Result(Result.Type.OK);
        assertEquals("Result: OK", result.toString());
    }

    @Test
    public void result_error_toString() throws Exception {
        String msg = "Something wrong";
        String field = "Field 1";
        Result result = new Result(Result.Type.ERROR, msg)
                .addField(field);
        assertEquals("Result: ERROR("+msg+")["+field+"]", result.toString());
    }
}