package unit.es.uji.agdc.videoclub.services.utils;

import es.uji.agdc.videoclub.services.utils.Result;
import es.uji.agdc.videoclub.services.utils.ResultBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 03/12/2016.
 */
public class ResultBuilderTest {
    @Test
    public void ok() throws Exception {
        assertTrue(ResultBuilder.ok().isOk());
    }

    @Test
    public void errorWithMsg() throws Exception {
        Result result = ResultBuilder.error("Something wrong");
        assertTrue(result.isError() && !result.getMsg().isEmpty());
    }

    @Test
    public void errorWithMsgAndFields() throws Exception {
        Result result = ResultBuilder.error("Something wrong", "Field 1");
        assertTrue(result.isError() && !result.getMsg().isEmpty() && result.getFields().length == 1);
    }
}