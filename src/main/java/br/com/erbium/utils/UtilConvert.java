package br.com.erbium.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilConvert {

    public static String convertToJsonString(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String body = null;
        try {
            body = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException jpe) {
            System.out.println(jpe);
        }
        return body;
    }

    public static String toJsonTring(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static String fileInputStreamToString(FileInputStream fileInputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        fileInputStream.close();
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }

    public static FileInputStream stringToFileInputStream(String str) throws IOException {
        File tempFile = File.createTempFile("tempFile", ".txt");
        tempFile.deleteOnExit();

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
            writer.write(str);
        }

        return new FileInputStream(tempFile);
    }
}
