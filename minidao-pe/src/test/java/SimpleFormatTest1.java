import org.jeecgframework.minidao.util.SimpleFormat;
import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleFormatTest1 {

    @Test
    public void testIn_NormalInput() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("a1,b2,c3");
        assertEquals("'a1','b2','c3'", result);
    }
    
    @Test
    public void testIn_DATE() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("2025-08-15,2021-08-15,2025-02-15,2025/08/15");
        System.out.println("拼接in SQL=" + result);
    }

    @Test
    public void testIn_NormalInput2() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("A1,B2,C3");
        assertEquals("'A1','B2','C3'", result);
    }

    @Test
    public void testIn_EmptyInput() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("");
        assertNull(result);
    }

    @Test
    public void testIn_SingleValue() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("abc123");
        assertEquals("'abc123'", result);
    }

    @Test
    public void testIn_IllegalInput() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("abc,1 OR 1=1");
        System.out.println(result);
    }
    
    @Test
    public void testIn_IllegalInput2() {
        SimpleFormat format = new SimpleFormat();
        String result = format.in("=100')+OR+1=2--+-");
        System.out.println(result);
    }
}