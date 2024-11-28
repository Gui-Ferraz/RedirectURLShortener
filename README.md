# URL Shortener with AWS

This application provides a simple and eficient URL shortening service using **AWS Lambda**, **S3**, and **API Gateaway**. The service comprises two Lambda functions:

1. [URL Shortener](https://github.com/Gui-Ferraz/URLShortener):
    - Accepts a POST request with an expiration time and a URL.
    - Generates a unique UUID.
    - Stores the UUID and the provided URL in an S3 bucket along with the expiration time.

2. URL Redirector:
    - Accepts a GET request with the UUID as the parameter.
    - Checks if the UUID exists in the S3 bucket and verifies the expiration time.
    - If valid, redirects the users to the original URL associated with the UUID.

The two Lambda functions are seamlessly linked using AWS API Gateaway, enabling easy interaction trhough RESTful APIs.

## Features

- Serverless Architecture: Leveraging AWS Lambda for on-demand function execution.
- Scalable Sotrage: Using Amazon S3 to store URL and UUID mappings.
- Redirection: Automatically redirects users to the original URL when a valid UUID is provided.

## API Endpoints

1. URL Shortener
    - Method: `POST`
    -Endpoint: https://9qihzzqyz3.execute-api.us-east-2.amazonaws.com/create
    - Request Body (JSON):
        ```
        {
            "expirationTime": "1924387200",
            "originalURL": "https://github.com/amzn"
        }
        ```
    - Response:
        ```
        {
            "code": "acdf7703"
        }
        ```
        This code refers to UUID.
2. URL Redirect
    - Method: `GET`
    - Endpoint: https://9qihzzqyz3.execute-api.us-east-2.amazonaws.com/{uuid}
    - Response: if the UUID provided is valid and URL time is not expired, it redirects to the original URL.

## Technologies Used
- Programming Language: Java
- Cloud services:
    - AWS Lambda
    - Amazon S3
    - AWS API Gateaway

## Setup and Deployment

### Prerequisites
- AWS services configured.
- Java SDK and Maven installed.

### Steps
1. Build the Lambda functions:
    - Package the Java applications using Maven:
    
        `mvn clean package`

2. Deploy Lambda Functions:
    - Upload the `.jar` file to AWS Lambda.

3. Configure S3 Bucket:
    - Create an S3 bucket to store URL mappings.
    - Remember to replace the S3 bucket name in `Main.java`.

4. Set Up API Gateaway
    - Create API Gateaway endpoints and link them to the respective Lambda functions.

## Usage 
1. Use a tool like **Postman** or **curl** to send a POST request to shorten a URL.

2. Access the shortened URL through the GET endpoint to test the redirection functionality.

## Future improvements
- Add a front-end interface for users.
- Implement a "garbage collector" to delete expired URLs.
- Support custom short URLs.