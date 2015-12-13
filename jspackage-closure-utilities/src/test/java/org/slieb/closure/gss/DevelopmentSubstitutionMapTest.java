package org.slieb.closure.gss;


import org.junit.Assert;
import org.junit.Test;

import static org.slieb.closure.gss.DevelopmentSubstitutionMap.getStringValue;


public class DevelopmentSubstitutionMapTest {


    @Test
    public void testTracksFirstDimmension() throws Exception {

        String[] alphabet = new String[]{"a"};
        Assert.assertEquals("a", getStringValue(alphabet, 0));
        Assert.assertEquals("aa", getStringValue(alphabet, 1));
        Assert.assertEquals("aaa", getStringValue(alphabet, 2));
        Assert.assertEquals("aaaa", getStringValue(alphabet, 3));
        Assert.assertEquals("aaaaa", getStringValue(alphabet, 4));
        Assert.assertEquals("aaaaaa", getStringValue(alphabet, 5));
        Assert.assertEquals("aaaaaaa", getStringValue(alphabet, 6));
    }

    @Test
    public void testTracksSecondDimmension() throws Exception {

        String[] alphabet = new String[]{"a", "b"};
        Assert.assertEquals("a", getStringValue(alphabet, 0));
        Assert.assertEquals("b", getStringValue(alphabet, 1));
        Assert.assertEquals("aa", getStringValue(alphabet, 2));
        Assert.assertEquals("ab", getStringValue(alphabet, 3));
        Assert.assertEquals("ba", getStringValue(alphabet, 4));
        Assert.assertEquals("bb", getStringValue(alphabet, 5));
        Assert.assertEquals("aaa", getStringValue(alphabet, 6));
    }

    @Test
    public void testTracksInThirdDimmension() throws Exception {
        String[] alphabet = new String[]{"a", "b", "c"};
        Assert.assertEquals("a", getStringValue(alphabet, 0));
        Assert.assertEquals("b", getStringValue(alphabet, 1));
        Assert.assertEquals("c", getStringValue(alphabet, 2));
        Assert.assertEquals("aa", getStringValue(alphabet, 3));
        Assert.assertEquals("ab", getStringValue(alphabet, 4));
        Assert.assertEquals("ac", getStringValue(alphabet, 5));
        Assert.assertEquals("ba", getStringValue(alphabet, 6));
        Assert.assertEquals("bb", getStringValue(alphabet, 7));
        Assert.assertEquals("bc", getStringValue(alphabet, 8));
        Assert.assertEquals("ca", getStringValue(alphabet, 9));
        Assert.assertEquals("cb", getStringValue(alphabet, 10));
        Assert.assertEquals("cc", getStringValue(alphabet, 11));
        Assert.assertEquals("aaa", getStringValue(alphabet, 12));
    }


}