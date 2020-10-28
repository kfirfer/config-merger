package com.tatzan.config.merger.service;

import com.tatzan.config.merger.service.impl.JsonMergerImpl;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonMergerTest {

    JsonMerger jsonMerger = new JsonMergerImpl();

    @Test
    public void testMergeJsonStrings() throws IOException, ParseException {
        String str1 = "{\n" +
                "  \"property1\": \"value1\",\n" +
                "  \"deep_property\": {\n" +
                "    \"array1\": [\n" +
                "      \"arr1\",\n" +
                "      \"arr2\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";


        String str2 = "{\n" +
                "  \"property2\": \"value2\",\n" +
                "  \"deep_property\": {\n" +
                "    \"array1\": [\n" +
                "      \"arr3\",\n" +
                "      \"arr4\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        List<String> stringList = new ArrayList<>();
        stringList.add(str1);
        stringList.add(str2);

        String result = jsonMerger.mergeJsonStrings(stringList);
        System.out.println(result);
    }

}
