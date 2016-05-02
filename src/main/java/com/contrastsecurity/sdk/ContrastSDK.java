/*
 * Copyright (c) 2015, Contrast Security, LLC.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * Neither the name of the Contrast Security, LLC. nor the names of its contributors may
 * be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.contrastsecurity.sdk;

import com.contrastsecurity.exceptions.ResourceNotFoundException;
import com.contrastsecurity.exceptions.UnauthorizedException;
import com.contrastsecurity.http.FilterForm;
import com.contrastsecurity.http.RequestConstants;
import com.contrastsecurity.http.UrlBuilder;
import com.contrastsecurity.models.*;
import com.contrastsecurity.utils.ContrastSDKUtils;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Entry point for using the Contrast REST API. Make an instance of this class
 * and call methods. Easy!
 */
public class ContrastSDK {

    private String apiKey;
    private String serviceKey;
    private String user;
    private String restApiURL;
    private UrlBuilder urlBuilder;
    private Gson gson;

    public ContrastSDK() {

    }

    /**
     * Create a ContrastSDK object that will attempt to use the Contrast V3 API
     *
     * @param user       Username (e.g., joe@acme.com)
     * @param serviceKey User service key
     * @param apiKey     API Key
     * @param restApiURL the base Contrast API URL
     * @throws IllegalArgumentException if the API URL is malformed
     */
    public ContrastSDK(String user, String serviceKey, String apiKey, String restApiURL) throws IllegalArgumentException {
        this.user = user;
        this.serviceKey = serviceKey;
        this.apiKey = apiKey;
        this.restApiURL = restApiURL;

        ContrastSDKUtils.validateUrl(this.restApiURL);

        this.urlBuilder = UrlBuilder.getInstance();
        this.gson = new Gson();
    }

    /**
     * Create a ContrastSDK object that attempts to use the Contrast V3 API.
     *
     * This will use the default api url which is https://app.contrastsecurity.com/Contrast/api
     */
    public ContrastSDK(String user, String serviceKey, String apiKey) {
        this.user = user;
        this.serviceKey = serviceKey;
        this.apiKey = apiKey;
        this.restApiURL = DEFAULT_API_URL;

        ContrastSDKUtils.validateUrl(this.restApiURL);

        this.urlBuilder = UrlBuilder.getInstance();
        this.gson = new Gson();
    }

    /**
     * Get summary information about a single app.
     *
     * @param organizationId the ID of the organization
     * @param appId          the ID of the application
     * @return Applications object that contains one Application; wrapper
     * @throws UnauthorizedException if the Contrast account failed to authorize
     * @throws IOException           if there was a communication problem
     */
    public Applications getApplication(String organizationId, String appId) throws IOException, UnauthorizedException, ResourceNotFoundException {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = makeRequest(GET_REQUEST, this.urlBuilder.getApplicationUrl(organizationId, appId));
            reader = new InputStreamReader(is);

            return this.gson.fromJson(reader, Applications.class);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Get the list of applications being monitored by Contrast.
     *
     * @param organizationId the ID of the organization
     * @return Applications object that contains the list of Application's
     * @throws UnauthorizedException if the Contrast account failed to authorize
     * @throws IOException           if there was a communication problem
     */
    public Applications getApplications(String organizationId) throws UnauthorizedException, IOException {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = makeRequest(GET_REQUEST, urlBuilder.getApplicationsUrl(organizationId));
            reader = new InputStreamReader(is);

            return this.gson.fromJson(reader, Applications.class);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Return coverage data about the monitored Contrast application.
     *
     * @param organizationId the ID of the organization
     * @param appId          the ID of the application
     * @return Coverage object for the given app
     * @throws UnauthorizedException if the Contrast account failed to authorize
     * @throws IOException           if there was a communication problem
     */
    public Coverage getCoverage(String organizationId, String appId) throws IOException, UnauthorizedException {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = makeRequest(GET_REQUEST, urlBuilder.getCoverageUrl(organizationId, appId));
            reader = new InputStreamReader(is);

            return this.gson.fromJson(reader, Coverage.class);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Return the libraries of the monitored Contrast application.
     *
     * @param organizationId the ID of the organization
     * @param appId          the ID of the application
     * @return Libraries object that contains the list of Library objects
     * @throws UnauthorizedException if the Contrast account failed to authorize
     * @throws IOException           if there was a communication problem
     */
    public Libraries getLibraries(String organizationId, String appId) throws IOException, UnauthorizedException {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = makeRequest(GET_REQUEST, urlBuilder.getLibrariesUrl(organizationId, appId));
            reader = new InputStreamReader(is);

            return this.gson.fromJson(reader, Libraries.class);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Get the vulnerabilities in the application whose ID is passed in.
     *
     * @param organizationId the ID of the organization
     * @param appId          the ID of the application
     * @return Traces object that contains the list of Trace's
     * @throws UnauthorizedException if the Contrast account failed to authorize
     * @throws IOException           if there was a communication problem
     */
    public Traces getTraces(String organizationId, String appId) throws IOException, UnauthorizedException {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = makeRequest(GET_REQUEST, urlBuilder.getTracesUrl(organizationId, appId));
            reader = new InputStreamReader(is);

            return this.gson.fromJson(reader, Traces.class);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Get the vulnerabilities in the application whose ID is passed in with a filter.
     *
     * @param organizationId the ID of the organization
     * @param appId          the ID of the application
     * @param form           FilterForm query parameters
     * @return Traces object that contains the list of Trace's
     * @throws UnauthorizedException if the Contrast account failed to authorize
     * @throws IOException           if there was a communication problem
     */
    public Traces getTracesWithFilter(String organizationId, String appId, FilterForm form) throws IOException, UnauthorizedException {
        InputStream is = null;
        InputStreamReader reader = null;

        try {
            is = makeRequest(GET_REQUEST, urlBuilder.getTracesWithFilterUrl(organizationId, appId, form));
            reader = new InputStreamReader(is);

            return this.gson.fromJson(reader, Traces.class);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Download a contrast.jar agent associated with this account. The user should save
     * this byte array to a file named 'contrast.jar'. This signature takes a parameter
     * which contains the name of the saved engine profile to download.
     *
     * @param type           the type of agent you want to download; Java, Java 1.5, .NET, or Node
     * @param profileName    the name of the saved engine profile to download,
     * @param organizationId the ID of the organization,
     * @return a byte[] array of the contrast.jar file contents, which the user should convert to a new File
     * @throws IOException if there was a communication problem
     */
    public byte[] getAgent(AgentType type, String organizationId, String profileName) throws IOException, UnauthorizedException {
        InputStream is = null;
        try {
            is = makeRequest(GET_REQUEST, urlBuilder.getAgentUrl(type, organizationId, profileName));

            return IOUtils.toByteArray(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Download a contrast.jar agent associated with this account. The user should save
     * this byte array to a file named 'contrast.jar'. This signature takes a parameter
     * which contains the name of the saved engine profile to download.
     * <p>
     * This uses 'default' as the profile name.
     *
     * @param type           the type of agent you want to download; Java, Java 1.5, .NET, or Node
     * @param organizationId the ID of the organization,
     * @return a byte[] array of the contrast.jar file contents, which the user should convert to a new File
     * @throws IOException if there was a communication problem
     */
    public byte[] getAgent(AgentType type, String organizationId) throws IOException, UnauthorizedException {
        return getAgent(type, organizationId, DEFAULT_AGENT_PROFILE);
    }

    public static void main(String[] args) throws UnauthorizedException, IOException, ResourceNotFoundException {
        ContrastSDK conn = new ContrastSDK("username", "demo", "demo", LOCALHOST_API_URL);

        String orgId = "add-your-org-id";
        String appId = "add-your-app-id";

        Gson gson = new Gson();

        // System.out.println(gson.toJson(conn.getApplication(orgId, appId)));
        // System.out.println(gson.toJson(conn.getApplications(orgId)));
        // System.out.println(gson.toJson(conn.getCoverage(orgId, appId)));
        // System.out.println(gson.toJson(conn.getTraces(orgId, appId)));
        // System.out.println(gson.toJson(conn.getLibraries(orgId, appId)));
        // FileUtils.writeByteArrayToFile(new File("contrast.jar"), conn.getAgent(AgentType.JAVA, orgId));
    }

    // ------------------------ Utilities -----------------------------------------------

    public InputStream makeRequest(String method, String path) throws IOException, UnauthorizedException {
        String url = restApiURL + path;
        HttpURLConnection connection = makeConnection(url, method);
        InputStream is = connection.getInputStream();
        int rc = connection.getResponseCode();
        if (rc >= BAD_REQUEST && rc < SERVER_ERROR) {
            IOUtils.closeQuietly(is);
            throw new UnauthorizedException(rc);
        }
        return is;
    }

    public HttpURLConnection makeConnection(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty(RequestConstants.AUTHORIZATION, ContrastSDKUtils.makeAuthorizationToken(user, serviceKey));
        connection.setRequestProperty(RequestConstants.API_KEY, apiKey);
        connection.setUseCaches(false);
        return connection;
    }

    private static final String GET_REQUEST = "GET";
    private static final String POST_REQUEST = "POST";

    private static final int BAD_REQUEST = 400;
    private static final int SERVER_ERROR = 500;

    private static final String DEFAULT_API_URL = "https://app.contrastsecurity.com/Contrast/api";
    private static final String LOCALHOST_API_URL = "http://localhost:19080/Contrast/api";
    private static final String DEFAULT_AGENT_PROFILE = "default";
}