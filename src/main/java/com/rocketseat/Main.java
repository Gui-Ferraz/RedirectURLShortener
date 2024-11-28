package com.rocketseat;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Main class implementing the {@link com.amazonaws.services.lambda.runtime.RequestHandler} interface.
 * 
 * <p>This class handles HTTP requests triggered by an AWS Lambda function. It processes input parameters
 * from an HTTP POST request and generates an 8-character UUID as the response.</p>
 * 
 * @author Guilherme M. Ferraz
 * @see com.amazonaws.services.lambda.runtime.RequestHandler
 */
public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final S3Client s3Client = S3Client.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();   // Handles JSON data

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {  // input: https://example.com/12345
        String pathParameters = (String) input.get("rawPath");                          // rawPath: /12345
        String shortURLCode = pathParameters.replace("/", "");           // shortURLCode: 12345

        if(shortURLCode == null || shortURLCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: valid URL code is required");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket("url-shortener-storage-g").key(shortURLCode + ".json").build();

        InputStream s3ObjectStream;

        try {
            s3ObjectStream = s3Client.getObject(getObjectRequest);
        } catch(Exception exception) {
            throw new RuntimeException("Error fetching URL data from S3: " + exception.getMessage(), exception);
        }

        URLData urlData;

        try {
            urlData = objectMapper.readValue(s3ObjectStream, URLData.class);
        } catch(Exception exception) {
            throw new RuntimeException("Error deseralizing URL data: " + exception.getLocalizedMessage(), exception);
        }

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;

        Map<String, Object> response = new HashMap<>();

        if(urlData.getExpirationTime() < currentTimeInSeconds) {
            
            response.put("statusCode", 410);
            response.put("body", "URL has expired");

            return response;
        }

        response.put("statusCode", 302);
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", urlData.getOriginalURL());
        response.put("headers", headers);

        return response;
    }
}