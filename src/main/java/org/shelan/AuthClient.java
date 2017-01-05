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


    public static void main(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            AuthClient client = new AuthClient();

            Options options = new Options();
            Option usernameOption = Option.builder("u")
                    .hasArg()
                    .desc("username to register")
                    .build();
            Option passwordOption = Option.builder("p")
                    .desc("password to register")
                    .hasArg()
                    .build();

            Option tokenOption = Option.builder("t")
                    .desc("JWT token to be sent with request")
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
            options.addOption("register", false, "register a new user");
            options.addOption("auth", false, "authenticate a user");
            options.addOption("lastLogins", false, "Get last 5 successful attempts");

            CommandLine line = parser.parse(options, args);


            String action = line.getOptionValue('a');

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
                    client.authenticate(line.getOptionValue('u'), line.getOptionValue('p'));
                }
                client.getLastAccess();
            }


            //  client.printHelp(options);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
    }

    public String register(String username, String password) throws IOException {
        HttpPost registerPost = new HttpPost("http://localhost:4567/register");
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
        HttpPost authPost = new HttpPost("http://localhost:4567/authenticate");
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

    public String getLastAccess() throws IOException {
        HttpGet lastAccessGet = new HttpGet("http://localhost:4567/getLogins");
        List<NameValuePair> nvps = new ArrayList<>();
        CloseableHttpResponse response = httpclient.execute(lastAccessGet);
        System.out.println(EntityUtils.toString(response.getEntity()));
        try {
            return response.getStatusLine().toString();
        } finally {
            response.close();
        }
    }

    public void printHelp(Options options) {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("Main", options);
        System.exit(0);
    }
}
