package com.relayr.product.comparison.restproxyproductstreamprocessor.streaming;


import com.relayr.product.comparison.restproxyproductstreamprocessor.model.ProductModel;
import com.relayr.product.models.Product;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Optional;


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
    public void testCheckValidityForValidModelWithAdditionalInfo(){
        Assert.assertTrue(streamEnricher.checkValidity("{\n" +
                "      \"category\": \"automotive\",\n" +
                "      \"brand\": \"tesla\",\n" +
                "      \"product\": \"modelY\",\n" +
                "      \"price\": 43092.6,\n" +
                "      \"additional\": {\n" +
                "        \"engine\": \"electric\",\n" +
                "        \"color\": \"gray\",\n" +
                "        \"model\": \"sedan\"\n" +
                "      }\n" +
                "    }\n" +
                "  }]\n" +
                "}"));
    }

    @Test
    public void testCheckValidityForValidModelWithoutAdditionalInfo(){
        Assert.assertTrue(streamEnricher.checkValidity("{\n" +
                "      \"category\": \"automotive\",\n" +
                "      \"brand\": \"tesla\",\n" +
                "      \"product\": \"modelY\",\n" +
                "      \"price\": 43092.6,\n" +
                "      \"additional\": {}" +
                "    }\n" +
                "  }]\n" +
                "}"));
    }

    @Test
    public void testCheckValidityForInvalidModelWithoutAdditionalInfo(){
        Assert.assertFalse(streamEnricher.checkValidity("{\n" +
                "      \"catgory\": \"automotive\",\n" +
                "      \"brand\": \"tesla\",\n" +
                "      \"product\": \"modelY\",\n" +
                "      \"price\": 43092.6,\n" +
                "      \"additional\": {}" +
                "    }\n" +
                "  }]\n" +
                "}"));
    }

    @Test
    public void testCheckValidityForInvalidModelWithAdditionalInfo(){
        Assert.assertFalse(streamEnricher.checkValidity("{\n" +
                "      \"category\": \"automotive\",\n" +
                "      \"brand\": \"tesla\",\n" +
                "      \"product\": \"modelY\",\n" +
                "      \"price\": 43092.6,\n" +
                "      \"addional\": {\n" +
                "        \"engine\": \"electric\",\n" +
                "        \"color\": \"gray\",\n" +
                "        \"model\": \"sedan\"\n" +
                "      }\n" +
                "    }\n" +
                "  }]\n" +
                "}"));
    }

    @Test
    public void testValueTransformation(){
        Assert.assertTrue(streamEnricher.valueTransformation("     TeSt-String}   ")
                .equals("test-string}"));
    }
}
