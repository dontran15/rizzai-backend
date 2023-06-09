package com.nighthawk.spring_portfolio.database.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Scanner;

/*
 * This is the original Java implementation of the ChatGPT API call
 * 
 * It creates an HttpURLConnection to the OpenAI API and sends a POST request with the prompt, similarly to how it's done on frontend
 * 
 * While this works fine, unfortunately it can only access the davinci 003 model for whatever reason, so the python implementation is preferred.
 */
public class Chat {

    private static String secret = System.getenv("SECRET");

    public static String daVinciTest(String text) throws MalformedURLException, IOException {
        String url = "https://api.openai.com/v1/completions";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + KeyFileReader.getKey());

        JSONObject data = new JSONObject();
        data.put("model", "text-davinci-003");
        data.put("prompt", text);
        data.put("max_tokens", 4000);
        data.put("temperature", 1.0);

        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes());

        String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
                .reduce((a, b) -> a + b).get();

        return (new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text"));
    }

    // actual function to send queries based on bot personality
    public static String gptRizz(String text, String botName) throws MalformedURLException, IOException {
        String response = "";

        // checks bot name
        switch (botName) {
            case "Chad":
                response = ChatPyReader
                        .pythonReader("Act like a toxic masculine manly chad in popular culture and answer the query: "
                                + "'" + text + "'");
                break;
            case "Stacey":
                response = ChatPyReader
                        .pythonReader(
                                "Act like an entitled popular high school cheerleader named Stacey and answer the query: "
                                        + "'" + text + "'");
                break;
            default:
                response = daVinciTest(text);
        }

        String check = response.substring(0, 1);

        // checks for GPT's inappropriate responses
        if (response.indexOf("Apologies") != -1 || response.indexOf("Sorry") != -1 || response.indexOf("sorry") != -1) {
            // circumvents ai model ethics thing :eyeroll: (just responds using davinci
            // w/out 'chad' tone)
            return daVinciTest(text);
        }

        return response;
    }

    // checks model being used (for now in apicontroller being a constant passed
    // through)
    public static String callModel(String text, String model, String botName)
            throws MalformedURLException, IOException {
        if (text == null || text.equals("")) {
            return "";
        }

        if (model.equals("gpt")) {
            return gptRizz(text, botName);
        }

        return daVinciTest(text);
    }

    // checks if love advice prompt (removed checks for now since it makes responses
    // too
    // long)
    public static String loveAdvice(String text, String model, String botName)
            throws MalformedURLException, IOException {
        // check using GPT text analysis on if prompt is dating advice
        // returns DaVinci answer to prompt or error based on 'Yes'/'No' response

        // String check = ChatPyReader
        // .pythonReader("is this query about love advice or being an alpha male:" + "'"
        // + text + "'")
        // .substring(0, 1);
        // if (check.equals("Y") || check.equals("C")) {
        // return chatRizz(text, model, botName);
        // } else if (check.equals("N")) {
        // return "Sorry bro, this prompt just isn't about love advice. Ask me a more
        // CHADLY question that I can give advice on chump. (Note: you might need to ask
        // again to get a good response)";
        // }

        // NOTE: only way to get around non-yes/no response due to 'inappropriate'
        // prompt, this still means DaVinci will respond to all actual inappropriate
        // prompts
        return callModel(text, model, botName);
    }

    // Original pickup line generator

    public String chatGPTPickUpLines(String text) throws MalformedURLException, IOException {
        String url = "https://api.openai.com/v1/completions";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + KeyFileReader.getKey());

        JSONObject data = new JSONObject();
        data.put("model", "text-davinci-003");
        data.put("prompt", "Please generate one pickup line related to the topic of " + text + ".");
        data.put("max_tokens", 4000);
        data.put("temperature", 1.0);

        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes());

        String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
                .reduce((a, b) -> a + b).get();

        return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text");
    }

    // Generates multiple pickup lines based on the number of lines requested

    public ArrayList<String> generateMultiple(int responses, String prompt) throws MalformedURLException, IOException {
        ArrayList<String> response = new ArrayList<String>();
        String topic = prompt;

        if (prompt == null || prompt.equals("")) {
            topic = "anything";
        }

        for (int i = 0; i < responses; i++) {
            response.add(chatGPTPickUpLines(topic));
        }
        return response;
    }

    public static void main(String[] args) throws Exception {
        // NOTE: you need to create a file called key.txt that has key (get from me or
        // Bailey) AND MAKE SURE it's in .gitignore or bailey will be very angry
        // System.out.println(chatGPTTest("Generate a list of pickup lines."));

        System.out.println(loveAdvice("who are you", "gpt", "Chad"));
    }
}