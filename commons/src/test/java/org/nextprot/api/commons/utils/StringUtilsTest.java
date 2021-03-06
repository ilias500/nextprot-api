package org.nextprot.api.commons.utils;


import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void testXCaseBuilderCamelCase() {

        String cc = StringUtils.createXCaseBuilder("nextprot-anatomy-cv").camelFirstWordLetterLowerCase(false).format();
        Assert.assertEquals("NextprotAnatomyCv", cc);
    }

    @Test
    public void testSnakeCase() {

        String cc = StringUtils.camelToSnakeCase("NextprotAnatomyCv");
        Assert.assertEquals("nextprot_anatomy_cv", cc);
    }

    
    @Test
    public void testToCamelCase() {
        String cc = StringUtils.toCamelCase("full name", true);
        Assert.assertEquals("fullName", cc);
    }
    
    @Test
    public void testSlugReplaceWSPunctuations() {

        String cc = StringUtils.slug("nextprot: anatomy;cv");
        Assert.assertEquals("nextprot__anatomy_cv", cc);
    }

    @Test
    public void testSlugRemoveNonWordChars() {

        String cc = StringUtils.slug("hel@l#o.world");
        Assert.assertEquals("hello_world", cc);
    }

    @Test
    public void testWrapping() {

        String text ="MGDREQLLQRARLAEQAERYDDMASAMKAVTELNEPLSNEDRNLLSVAYKNVVGARRSSW" +
                "RVISSIEQKTMADGNEKKLEKVKAYREKIEKELETVCNDVLSLLDKFLIKNCNDFQYESK" +
                "VFYLKMKGDYYRYLAEVASGEKKNSVVEASEAAYKEAFEISKEQMQPTHPIRLGLALNFS" +
                "VFYYEIQNAPEQACLLAKQAFDDAIAELDTLNEDSYKDSTLIMQLLRDNLTLWTSDQQDE" +
                "EAGEGN";

        String expectedText = "MGDREQLLQRARLAEQAERYDDMASAMKAVTELNEPLSNEDRNLLSVAYKNVVGARRSSW\r\n" +
                "RVISSIEQKTMADGNEKKLEKVKAYREKIEKELETVCNDVLSLLDKFLIKNCNDFQYESK\r\n" +
                "VFYLKMKGDYYRYLAEVASGEKKNSVVEASEAAYKEAFEISKEQMQPTHPIRLGLALNFS\r\n" +
                "VFYYEIQNAPEQACLLAKQAFDDAIAELDTLNEDSYKDSTLIMQLLRDNLTLWTSDQQDE\r\n" +
                "EAGEGN";

        Assert.assertEquals(expectedText, StringUtils.wrapText(text, 60));
    }
    
    @Test
    public void testHtmlTagsRemoval() {
        String cc = StringUtils.removeHtmlTags("<hello>world</hello>");
        Assert.assertEquals("world", cc);
    }
    
	@Test
	public void testSortedValueFromPipeSeparatedField() {
		String result = StringUtils.getSortedValueFromPipeSeparatedField("cosmic:COSM4859577 | cosmic:COSM1149023 | cosmic:COSM720040");
		assertEquals("cosmic:COSM1149023 | cosmic:COSM4859577 | cosmic:COSM720040", result);
	}

	@Test
    public void testConcat() {

	    String[] strings = new String[] {"bob", "sponge"};

	    Assert.assertEquals("bobsponge", StringUtils.concat(strings));
    }

    @Test
    public void testConcatWithNullString() {

        String[] strings = new String[] {"bob", "sponge", null};

        Assert.assertEquals("bobsponge", StringUtils.concat(strings));
    }

    @Test
    public void lastLineNeverEndsWithEOLAfterWrapping() {

        String text ="MAARCSTRWLLVVVGTPRLPAISGRGARPPREGVVGAWLSRKLSVPAFASSLTSCGPRAL" +
                "LTLRPGVSLTGTKHNPFICTASFHTSAPLAKEDYYQILGVPRNASQKEIKKAYYQLAKKY" +
                "HPDTNKDDPKAKEKFSQLAEAYEVLSDEVKRKQYDAYGSAGFDPGASGSQHSYWKGGPTV" +
                "DPEELFRKIFGEFSSSSFGDFQTVFDQPQEYFMELTFNQAAKGVNKEFTVNIMDTCERCN" +
                "GKGNEPGTKVQHCHYCGGSGMETINTGPFVMRSTCRRCGGRGSIIISPCVVCRGAGQAKQ" +
                "KKRVMIPVPAGVEDGQTVRMPVGKREIFITFRVQKSPVFRRDGADIHSDLFISIAQALLG" +
                "GTARAQGLYETINVTIPPGTQTDQKIRMGGKGIPRINSYGYGDHYIHIKIRVPKRLTSRQ" +
                "QSLILSYAEDETDVEGTVNGVTLTSSGGSTMDSSAGSKARREAGEDEEGFLSKLKKMFTS";

        String expectedText = "MAARCSTRWLLVVVGTPRLPAISGRGARPPREGVVGAWLSRKLSVPAFASSLTSCGPRAL\r\n" +
                "LTLRPGVSLTGTKHNPFICTASFHTSAPLAKEDYYQILGVPRNASQKEIKKAYYQLAKKY\r\n" +
                "HPDTNKDDPKAKEKFSQLAEAYEVLSDEVKRKQYDAYGSAGFDPGASGSQHSYWKGGPTV\r\n" +
                "DPEELFRKIFGEFSSSSFGDFQTVFDQPQEYFMELTFNQAAKGVNKEFTVNIMDTCERCN\r\n" +
                "GKGNEPGTKVQHCHYCGGSGMETINTGPFVMRSTCRRCGGRGSIIISPCVVCRGAGQAKQ\r\n" +
                "KKRVMIPVPAGVEDGQTVRMPVGKREIFITFRVQKSPVFRRDGADIHSDLFISIAQALLG\r\n" +
                "GTARAQGLYETINVTIPPGTQTDQKIRMGGKGIPRINSYGYGDHYIHIKIRVPKRLTSRQ\r\n" +
                "QSLILSYAEDETDVEGTVNGVTLTSSGGSTMDSSAGSKARREAGEDEEGFLSKLKKMFTS";

        Assert.assertEquals(expectedText, StringUtils.wrapText(text, 60));
    }
}