package org.shelan;

import org.apache.commons.cli.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shelan on 1/5/17.
 */

public class AuthClient {

    private CloseableHttpClient httpclient = HttpClients.createDefault();
    private static Logger logger = LoggerFactory.getLogger(AuthClient.class);
    private String url;

    public AuthClient(String url) {
        this.url = url;
    }

    public static void main(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();

            Options options = new Options();

            Option urlOption = Option.builder("url")
                    .hasArg()
                    .desc("Base URL of the API : Eg :-url http://localhost:4567")
                    .required()
                    .build();

            Option usernameOption = Option.builder("u")
                    .hasArg()
                    .desc("username to register Eg: -u test1")
                    .build();
            Option passwordOption = Option.builder("p")
                    .desc("password to register Eg: -p pw1")
                    .hasArg()
                    .build();

            Option tokenOption = Option.builder("t")
                    .desc("JWT token to be sent with request eg: -t=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsIn" +
                            "VzZXJuYW1lIjoidXNlcjEifQ.zlEe2AZn-qRhodQhttMnOJfqLGgdc1BIkYNd01kAins")
                    .hasArg()
                    .build();

            Option actionOption = Option.builder("a")
                    .desc("Action to perform")
                    .hasArg(true)
                    .required(true)
                    .build();

            options.addOption(usernameOption);
            options.addOption(passwordOption);
            options.addOption(actionOption);
            options.addOption(tokenOption);
            options.addOption(urlOption);

            CommandLine line = parser.parse(options, args);

            String url = line.getOptionValue("url");
            String action = line.getOptionValue('a');

            AuthClient client = new AuthClient(url);

            if ("register".equalsIgnoreCase(action)) {
                if (!(line.hasOption('u') && line.hasOption('p'))) {
                    client.printHelp(options);
                } else {
                    client.register(line.getOptionValue('u'), line.getOptionValue('p'));
                }
            } else if ("auth".equalsIgnoreCase(action)) {
                if (!(line.hasOption('u') && line.hasOption('p'))) {
                    client.printHelp(options);
                } else {
                    client.authenticate(line.getOptionValue('u'), line.getOptionValue('p'));
                }
            } else if (("lastLogins".equalsIgnoreCase(action))) {
                if (!line.hasOption('t')) {
                    client.printHelp(options);
                } else {
                    client.getLastAccess(line.getOptionValue('t'));
                }

            }


            //  client.printHelp(options);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            logger.error(e.getMessage());

        }
    }

    public String register(String username, String password) throws IOException {
        HttpPost registerPost = new HttpPost(this.url + "/register");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("username", username));
        nvps.add(new BasicNameValuePair("password", password));
        registerPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = httpclient.execute(registerPost);
        System.out.println(EntityUtils.toString(response.getEntity()));
        try {
            return response.getStatusLine().toString();
        } finally {
            response.close();
        }
    }

    public String authenticate(String username, String password) throws IOException {
        HttpPost authPost = new HttpPost(this.url + "/authenticate");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("username", username));
        nvps.add(new BasicNameValuePair("password", password));
        authPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = httpclient.execute(authPost);
        System.out.println(EntityUtils.toString(response.getEntity()));
        try {
            return response.getStatusLine().toString();
        } finally {
            response.close();
        }
    }

    public String getLastAccess(String token) throws IOException {
        HttpGet lastAccessGet = new HttpGet(this.url + "/getLogins");
        List<NameValuePair> nvps = new ArrayList<>();
        lastAccessGet.setHeader("Authorization","bearer "+token);
        CloseableHttpResponse response = httpclient.execute(lastAccessGet);
        System.out.println(EntityUtils.toString(response.getEntity()));
        try {
            return response.getStatusLine().toString();
        } finally {
            response.close();
        }
    }

    public static void printHelp(Options options) {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("java -jar auth-client-1.0-SNAPSHOT-jar-with-dependencies.jar" +
                        "-url http://localhost:4567  -a auth -u user1 -p pass1" +
                        "\n" +
                        "actions available are register,auth,lastLogins",
                options);
        System.exit(0);
    }
}
