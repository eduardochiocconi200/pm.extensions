package com.servicenow.processmining.extensions.sn.core;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Base64;

import javax.net.ssl.SSLHandshakeException;

import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceNowRESTService
{
    private ServiceNowInstance snInstance = null;
    private int errorStatusCode = -1;
    private String errorMessage = "";

    public ServiceNowRESTService(final ServiceNowInstance instance) {
        logger.debug("Enter ServiceNowRESTService.ServiceNowRESTService()");
        this.snInstance = instance;
        this.errorStatusCode = -1;
        this.errorMessage = "";
        logger.debug("Exit ServiceNowRESTService.ServiceNowRESTService()");
    }

    private ServiceNowInstance getInstance() {
        return this.snInstance;
    }

    public int getErrorStatusCode() {
        return this.errorStatusCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String executePostRequest(final String url, final String payload)
    {
        return executePostPutRequest(url, payload, true);
    }

    public String executePutRequest(final String url, final String payload)
    {
        return executePostPutRequest(url, payload, false);
    }

    private String executePostPutRequest(final String url, final String payload, final boolean isPost)
    {
        logger.debug("Enter ServiceNowRESTService.executePostRequest(" + url + ")");
        String response = null;
        this.errorStatusCode = -1;
        this.errorMessage = "";
        int retryCount = 0;
        boolean retry = false;
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            do {
                try {
                    HttpUriRequestBase request = null;
                    if (isPost) {
                        request = getPostRequest(url);
                    }
                    else {
                        request = getPutRequest(url);
                    }
                    StringEntity signingPayload = new StringEntity(payload);
                    request.setEntity(signingPayload);
                    HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
                    response = httpclient.execute(request, responseHandler);
                    retry = false;
                } catch (HttpResponseException e) {
                    errorStatusCode = e.getStatusCode();
                    if (errorStatusCode == 401 || errorStatusCode == 403) {
                        this.errorMessage = "The current user: (" + getInstance().getUser() + ") is not authorized to access the resource: (" + url + "). Properly authorize this user to have READ access and try again. Make sure credentials are valid and correct!";
                    }
                    else {
                        this.errorMessage = "The current user: (" + getInstance().getUser() + ") could not execute REST request. Error Status Code: (" + errorStatusCode + ")";
                    }

                    logger.error(errorMessage);
                    logger.debug("Exit ServiceNowRESTService.executePostRequest(" + url + ") = null");
                    return null;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") = null (2)");
                    return null;
                } catch (IOException e) {
                    if (e instanceof SSLHandshakeException || e instanceof SocketException || e instanceof SocketTimeoutException || e instanceof EOFException || e instanceof ConnectTimeoutException) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        retry = true;
                        if (retryCount > MAX_RETRY_TIMES) {
                            retry = false;
                        }
                        retryCount++;
                    }
                    else {
                        e.printStackTrace();
                        logger.debug("Exit ServiceNowRESTService.executePostRequest(" + url + ") != null");
                        return null;
                    }
                }
            } while (retry);
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("Exit ServiceNowRESTService.executePostRequest(" + url + ") != null");
            return null;
        }

        logger.debug("Exit ServiceNowRESTService.executePostRequest(" + url + ") != null");
        return response;
    }

    private HttpPost getPostRequest(final String url)
    {
        HttpPost request = new HttpPost(url);
        addRequestHeaders(request);

        return request;
    }

    private HttpPut getPutRequest(final String url)
    {
        HttpPut request = new HttpPut(url);
        addRequestHeaders(request);

        return request;
    }

    public String executeGetRequest(final String url)
    {
        logger.debug("Enter ServiceNowRESTService.executeGetRequest(" + url + ")");
        String response = null;
        this.errorStatusCode = -1;
        this.errorMessage = "";
        int retryCount = 0;
        boolean retry = false;
        do {
            try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
                HttpGet getRequest = getGetRequest(url);
                HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
                response = httpclient.execute(getRequest, responseHandler);
                retry = false;
            } catch (HttpResponseException e) {
                errorStatusCode = e.getStatusCode();
                if (errorStatusCode == 401 || errorStatusCode == 403) {
                    this.errorMessage = "The current user: (" + getInstance().getUser() + ") is not authorized to access the resource: (" + url + "). Properly authorize this user to have READ access and try again. Make sure credentials are valid and correct!";
                }
                else {
                    this.errorMessage = "The current user: (" + getInstance().getUser() + ") could not execute REST request. Error Status Code: (" + errorStatusCode + ")";
                    e.printStackTrace();
                }

                logger.error(errorMessage);
                logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") = null (1)");
                return null;
            } catch (UnknownHostException e) {
                logger.error("Unknown Host. Please specific a valid host name. Error: (" + e.getLocalizedMessage() + ")");
                logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") = null (2)");
                return null;
            } catch (IOException e) {
                if (e instanceof SSLHandshakeException || e instanceof SocketException || e instanceof SocketTimeoutException || e instanceof EOFException || e instanceof ConnectTimeoutException) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    retry = true;
                    if (retryCount > MAX_RETRY_TIMES) {
                        retry = false;
                    }
                    retryCount++;
                }
                else {
                    e.printStackTrace();
                    logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") != null");
                    return null;
                }
            }
        } while (retry);

        logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") != null");
        return response;
    }

    private HttpGet getGetRequest(String url)
    {
        HttpGet request = new HttpGet(url);
        addRequestHeaders(request);

        return request;
    }

    public String executeDeleteRequest(final String url)
    {
        logger.debug("Enter ServiceNowRESTService.executeDeleteRequest(" + url + ")");
        String response = null;
        this.errorStatusCode = -1;
        this.errorMessage = "";
        int retryCount = 0;
        boolean retry = false;
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            do {
                try {
                    HttpDelete deleteRequest = getDeleteRequest(url);
                    HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
                    response = httpclient.execute(deleteRequest, responseHandler);
                    retry = false;
                } catch (HttpResponseException e) {
                    errorStatusCode = e.getStatusCode();
                    if (errorStatusCode == 401 || errorStatusCode == 403) {
                        this.errorMessage = "The current user: (" + getInstance().getUser() + ") is not authorized to access the resource: (" + url + "). Properly authorize this user to have READ access and try again. Make sure credentials are valid and correct!";
                    }
                    else {
                        this.errorMessage = "The current user: (" + getInstance().getUser() + ") could not execute REST request. Error Status Code: (" + errorStatusCode + ")";
                    }

                    logger.error(errorMessage);
                    logger.debug("Exit ServiceNowRESTService.executeDeleteRequest(" + url + ") = null");
                    return null;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") = null (2)");
                    return null;
                } catch (IOException e) {
                    if (e instanceof SSLHandshakeException || e instanceof SocketException || e instanceof SocketTimeoutException || e instanceof EOFException || e instanceof ConnectTimeoutException) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        retry = true;
                        if (retryCount > MAX_RETRY_TIMES) {
                            retry = false;
                        }
                        retryCount++;
                    }
                    else {
                        e.printStackTrace();
                        logger.debug("Exit ServiceNowRESTService.executeDeleteRequest(" + url + ") != null");
                        return null;
                    }
                }
            } while (retry);
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("Exit ServiceNowRESTService.executeDeleteRequest(" + url + ") != null");
            return null;
        }

        logger.debug("Exit ServiceNowRESTService.executeDeleteRequest(" + url + ") != null");
        return response;
    }

    private HttpDelete getDeleteRequest(final String url)
    {
        HttpDelete request = new HttpDelete(url);
        addRequestHeaders(request);

        return request;
    }

    private void addRequestHeaders(HttpUriRequestBase request)
    {
        request.addHeader("Accept", "application/json, application/xml");
        request.addHeader("Content-Type", "application/json, application/xml");
        String auth = getInstance().getUser() + ":" + getInstance().getPassword();
        request.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
    }

    private static final int MAX_RETRY_TIMES = 3;
    private final static Logger logger = LoggerFactory.getLogger(ServiceNowRESTService.class);
}