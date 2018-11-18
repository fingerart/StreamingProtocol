package io.chengguo.live;

import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void name() throws InterruptedException {
        LinkedBlockingDeque<Integer> is = new LinkedBlockingDeque<>();
        is.put(1);
        is.put(2);
        is.put(3);
        is.put(4);
        while (true) {
            System.out.println(is.take());
        }
    }
}