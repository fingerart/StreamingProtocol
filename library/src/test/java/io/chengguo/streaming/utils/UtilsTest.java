package io.chengguo.streaming.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testTrimSafely() {
        String result = Utils.trimSafely(" abc ");
        assertThat(result, is("abc"));
    }

    @Test
    public void testTrimSafely_null() {
        String result = Utils.trimSafely(null);
        assertThat(result, is(""));
    }

    @Test
    public void testSplitSafely() {
        String[] results = Utils.splitSafely("a b c", " ");
        assertThat(results, is(new String[]{"a", "b", "c"}));
    }

    @Test
    public void testSplitSafely_null() {
        String[] results = Utils.splitSafely(null, " ");
        assertNotNull(results);
    }

    @Test
    public void testIsEmpty_string() {
        assertThat(Utils.isEmpty("abc"), is(false));
        assertThat(Utils.isEmpty(""), is(true));
        assertThat(Utils.isEmpty((String) null), is(true));
    }

    @Test
    public void testIsEmpty_byte() {
        assertThat(Utils.isEmpty(new byte[]{1, 2, 3}), is(false));
        assertThat(Utils.isEmpty(new byte[]{}), is(true));
        assertThat(Utils.isEmpty((byte[]) null), is(true));
    }
}