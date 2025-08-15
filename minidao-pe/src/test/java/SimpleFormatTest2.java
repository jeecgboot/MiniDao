// 文件：minidao-pe/src/test/java/SimpleFormatTest.java

import org.jeecgframework.minidao.util.SimpleFormat;
import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleFormatTest2 {

    @Test
    public void testConcat_TwoParams() {
        SimpleFormat format = new SimpleFormat();
        String result = format.concat("2023-09-25", " 00:00:00");
        assertEquals("2023-09-25 00:00:00", result);
    }

    @Test
    public void testConcat_Variables() {
        SimpleFormat format = new SimpleFormat();
        String result = format.concat("${beginTime}", "${endTime}");
        assertEquals("${beginTime}${endTime}", result);
    }

    @Test
    public void testIn_SingleParam() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("user1");
        assertEquals("'user1'", result);
    }

    @Test
    public void testIn_MultiParams() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("2023-09-25, 00:00:00");
        assertEquals("'2023-09-25',' 00:00:00'", result);
    }

    @Test
    public void testInNumber_MultiParams() {
        SimpleFormat format = new SimpleFormat();
        String result = format.inNumber("18,20,22");
        assertEquals("18,20,22", result);
    }
}