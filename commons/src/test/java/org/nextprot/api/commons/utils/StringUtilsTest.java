package org.nextprot.api.commons.utils;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 22/04/15.
 */
public class StringUtilsTest {

    @Test
    public void testWrappingRec() {

        String text ="MGDREQLLQRARLAEQAERYDDMASAMKAVTELNEPLSNEDRNLLSVAYKNVVGARRSSW" +
                "RVISSIEQKTMADGNEKKLEKVKAYREKIEKELETVCNDVLSLLDKFLIKNCNDFQYESK" +
                "VFYLKMKGDYYRYLAEVASGEKKNSVVEASEAAYKEAFEISKEQMQPTHPIRLGLALNFS" +
                "VFYYEIQNAPEQACLLAKQAFDDAIAELDTLNEDSYKDSTLIMQLLRDNLTLWTSDQQDE" +
                "EAGEGN";

        String expectedText = "MGDREQLLQRARLAEQAERYDDMASAMKAVTELNEPLSNEDRNLLSVAYKNVVGARRSSW\n" +
                "RVISSIEQKTMADGNEKKLEKVKAYREKIEKELETVCNDVLSLLDKFLIKNCNDFQYESK\n" +
                "VFYLKMKGDYYRYLAEVASGEKKNSVVEASEAAYKEAFEISKEQMQPTHPIRLGLALNFS\n" +
                "VFYYEIQNAPEQACLLAKQAFDDAIAELDTLNEDSYKDSTLIMQLLRDNLTLWTSDQQDE\n" +
                "EAGEGN";

        Assert.assertEquals(expectedText, StringUtils.wrapTextRec(text, 60, new StringBuilder()));
    }
}