package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

public class AuthorNameFormatterTest {

    private AuthorNameFormatter formatter = new AuthorNameFormatter();

    @Test
    public void testFormatPublicationForenameInitials() {

        Assert.assertEquals("A.G.", formatter.formatForenameInitials("Alexey G"));
    }

    @Test
    public void testFormatPublicationForenameInitialsFromInitials() {

        Assert.assertEquals("A.G.", formatter.formatForenameInitials("A G"));
    }

    @Test
    public void testFormatPublicationForenameInitialsWithDash1() {

        Assert.assertEquals("J.-C.", formatter.formatForenameInitials("Jean-Christophe"));
    }

    @Test
    public void testFormatPublicationForenameInitialsWithDash2() {

        Assert.assertEquals("J.-C.", formatter.formatForenameInitials("J-C"));
    }

    @Test
    public void testFormatPublicationForenameInitialsWithDashIdempotent() {

        Assert.assertEquals("J.-C.", formatter.formatForenameInitials("J.-C."));
    }

    // http://www.uniprot.org/citations/25061178
    @Test
    public void testFormatPublicationForenameInitialsWithSuffixJr() {

        Assert.assertEquals("J.D. Jr.", formatter.formatForenameInitials("John D", "Jr"));
    }

    //http://www.uniprot.org/citations/16793063
    @Test
    public void testFormatPublicationForenameInitialsWithSuffixNth() {

        Assert.assertEquals("A.W. IV", formatter.formatForenameInitials("Alexander W", "4th"));
    }

    //Rocha	"Francisco Dário"	Filho
    @Test
    public void testFormatPublicationForenameInitialsWithSuffixFilho() {

        Assert.assertEquals("F.D. Jr.", formatter.formatForenameInitials("Francisco Dário", "Filho"));
    }

    @Test
    public void testFormatPublicationSuffixNth() {

        Assert.assertEquals("I", formatter.formatSuffix("1st"));
        Assert.assertEquals("I", formatter.formatSuffix("I"));
        Assert.assertEquals("II", formatter.formatSuffix("2nd"));
        Assert.assertEquals("III", formatter.formatSuffix("3rd"));
        Assert.assertEquals("IV", formatter.formatSuffix("4th"));
        Assert.assertEquals("V", formatter.formatSuffix("5th"));
        Assert.assertEquals("V", formatter.formatSuffix("V"));
        Assert.assertEquals("VI", formatter.formatSuffix("6th"));
    }

    @Test
    public void testFormatPublicationSuffixJunior() {

        Assert.assertEquals("Jr.", formatter.formatSuffix("Jr"));
    }

    @Test
    public void testFormatPublicationSuffixJunior2() {

        Assert.assertEquals("Jr.", formatter.formatSuffix("Filho"));
    }

    @Test
    public void testFormatPublicationSuffixSenior() {

        Assert.assertEquals("Sr.", formatter.formatSuffix("Sr"));
    }

    @Test
    public void testFormatPublicationForenameEmpty() {

        Assert.assertEquals("", formatter.formatForenameInitials("  "));
    }

    @Test
    public void testFormatPublicationForenameEmpty2() {

        Assert.assertEquals("", formatter.formatForenameInitials(""));
    }

    // example publication with resource_id = 48939226, related to NX_P41159
    @Test 
    public void testFormatPublicationForenameIsDash() {

        Assert.assertEquals("-", formatter.formatForenameInitials("-"));
    }

    @Test
    public void testFormatPublicationWithForenameContainingMultipleSpaces() {

        Assert.assertEquals("J.P.", formatter.formatForenameInitials("Jean   Paul"));
    }

    @Test
    public void testWeirdFormatFromP02741() {

        Assert.assertEquals("C.P.-Y.", formatter.formatForenameInitials("Christine P -Y"));
    }
}