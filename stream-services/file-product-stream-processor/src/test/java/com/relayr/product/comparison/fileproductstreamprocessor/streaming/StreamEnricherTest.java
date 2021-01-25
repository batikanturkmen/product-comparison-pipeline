package com.relayr.product.comparison.fileproductstreamprocessor.streaming;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class StreamEnricherTest {

    private StreamEnricher streamEnricher = new StreamEnricher();

    @Test
    public void testCheckValidityEmptyString(){
        Assert.assertFalse(streamEnricher.checkValidity(""));
    }

    @Test
    public void testCheckValiditySpacedString(){
        Assert.assertFalse(streamEnricher.checkValidity("   "));
    }

    @Test
    public void testCheckAdditionalDataParserThreeAdditional(){
        String data = "clothing,Wrangler,jeans,40,size,medium,color,purple,length,34";
        String result = "{\"size\":\"medium\",\"color\":\"purple\",\"length\":\"34\"}";
        Assert.assertTrue(streamEnricher.additionalDataParser(data).equals(result));
    }

    @Test
    public void testCheckAdditionalDataParserTwoAdditional(){
        String data = "clothing,polo,shirt,10,size,medium,color,purple";
        String result = "{\"size\":\"medium\",\"color\":\"purple\"}";
        Assert.assertTrue(streamEnricher.additionalDataParser(data).equals(result));
    }

    @Test
    public void testValueTransformation(){
        Assert.assertTrue(streamEnricher.valueTransformation("     TeSt-String}   ")
                .equals("test-string}"));
    }
}
