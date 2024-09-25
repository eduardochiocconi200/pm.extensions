package com.servicenow.processmining.extensions.sn.core;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Base64;

import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
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
        logger.debug("Enter ServiceNowRESTService.executePostRequest(" + url + ")");
        String response = null;
        this.errorStatusCode = -1;
        this.errorMessage = "";
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            try {
                HttpPost postRequest = getPostRequest(url);
                StringEntity signingPayload = new StringEntity(payload);
                postRequest.setEntity(signingPayload);
                HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
                response = httpclient.execute(postRequest, responseHandler);
            } catch (HttpResponseException e) {
                errorStatusCode = e.getStatusCode();
                if (e.getStatusCode() == 403) {
                    this.errorMessage = "The current user: (" + getInstance().getUser()
                            + ") is not authorized to access the resource: (" + url
                            + "). Properly entitle this user to have READ access and try again.";
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
                e.printStackTrace();
                logger.debug("Exit ServiceNowRESTService.executePostRequest(" + url + ") != null");
                return null;
            }
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

    public String executeGetRequest(final String url)
    {
        logger.debug("Enter ServiceNowRESTService.executeGetRequest(" + url + ")");
        String response = null;
        this.errorStatusCode = -1;
        this.errorMessage = "";
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            HttpGet getRequest = getGetRequest(url);
            HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
            response = httpclient.execute(getRequest, responseHandler);
        } catch (HttpResponseException e) {
            errorStatusCode = e.getStatusCode();
            if (e.getStatusCode() == 403) {
                this.errorMessage = "The current user: (" + getInstance().getUser()
                        + ") is not authorized to access the resource: (" + url
                        + "). Properly entitle this user to have READ access and try again.";
            }
            else {
                this.errorMessage = "The current user: (" + getInstance().getUser() + ") could not execute REST request. Error Status Code: (" + errorStatusCode + ")";
            }

            logger.error(errorMessage);
            logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") = null (1)");
            return null;
        } catch (UnknownHostException e) {
            logger.error("Unknown Host. Please specific a valid host name. Error: (" + e.getLocalizedMessage() + ")");
            logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") = null (2)");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("Exit ServiceNowRESTService.executeGetRequest(" + url + ") = null (3)");
            return null;
        }

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
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            try {
                HttpDelete deleteRequest = getDeleteRequest(url);
                HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
                response = httpclient.execute(deleteRequest, responseHandler);
            } catch (HttpResponseException e) {
                errorStatusCode = e.getStatusCode();
                if (e.getStatusCode() == 403) {
                    this.errorMessage = "The current user: (" + getInstance().getUser()
                            + ") is not authorized to access the resource: (" + url
                            + "). Properly entitle this user to have READ access and try again.";
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
                e.printStackTrace();
                logger.debug("Exit ServiceNowRESTService.executeDeleteRequest(" + url + ") != null");
                return null;
            }
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

    private final static Logger logger = LoggerFactory.getLogger(ServiceNowRESTService.class);
}
