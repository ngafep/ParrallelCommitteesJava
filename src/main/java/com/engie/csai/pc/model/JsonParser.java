package com.engie.csai.pc.model;


public class JsonParser
{

    private final static String JSON_DATA =
            "{\"PDT_0\":" +
                    "{\"validity status0_file_0\":[\"FALSE_0\"]," +
                    "\"calculated fee based on size of data_0in JSON file_0\":[\"feeToSpend[numberOfPDTinJSON]0\"]," +
                    "\"receiver address_0_file_0\":[\"RECEIVER-ADDRESS_0\"]," +
                    "\"data_0_file_0\":[\"data[fileIndex][numberOfPDTinJSON]0\"]," +
                    "\"tokenToSend_0_file_0\":[\"tokenToSend__0\"]," +
                    "\"sender signature_0_file_0\":[\"SIGNATURE_0\"]" +
                    "}" +
                    "}";

    public JsonParser()
    {

    }

   /* public statis void parser(String json) throws JSONException, IOException, ParseException {

        com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
        //JsonElement element = jsonParser.parse(JSON_DATA);
        com.google.gson.JsonElement element = jsonParser.parse("JSON_DATA");

    }*/
}
