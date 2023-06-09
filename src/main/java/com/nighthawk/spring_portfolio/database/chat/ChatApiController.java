package com.nighthawk.spring_portfolio.database.chat;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.database.dating.DatingProfile;
import com.nighthawk.spring_portfolio.database.dating.DatingProfileJpaRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/chat")
public class ChatApiController {

    private Chat chatGPT = new Chat();

    @PostMapping("/generateLines") // to do generate response based on profile
    public ResponseEntity<Object> generatePickupLines(@RequestBody final Map<String, Object> map)
            throws MalformedURLException, IOException {

        String prompt = (String) map.get("prompt");
        int responses = Integer.valueOf((String) map.get("responses"));

        ArrayList<String> response;

        // If there's nothing in the prompt, generate a random response
        if (prompt == null || prompt.equals("")) {
            response = (ArrayList<String>) (chatGPT.generateMultiple(responses, null));
        } else {
            response = (ArrayList<String>) (chatGPT.generateMultiple(responses, prompt));
        }

        // Add the response to a JSON object
        ArrayList<Map<String, Object>> jsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < response.size(); i++) {
            HashMap<String, Object> js = new HashMap<String, Object>();
            js.put("id", i);
            js.put("line", response.get(i));
            js.put("topic", prompt);
            js.put("wordCount", response.get(i).split(" ").length);
            jsList.add(js);
        }

        return new ResponseEntity<>(jsList, HttpStatus.OK);
    }

    // to do, do an overall api on any prompt, as long as it's related to love
    // (requires two prompts to check using gpt)
    @PostMapping("/generate")
    public ResponseEntity<Object> generateLoveAdvice(@RequestBody final Map<String, Object> map)
            throws MalformedURLException, IOException {

        String prompt = (String) map.get("prompt");
        String model = "gpt"; // change to configure if response is to davinci (generic) or gpt (sentiment)
        String botName = (String) map.get("botName");
        String response = Chat.loveAdvice(prompt, model, botName);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Checks if the prompt is a love question
    @PostMapping("/queryTest")
    public ResponseEntity<Object> queryTest(@RequestBody final Map<String, Object> map)
            throws MalformedURLException, IOException {

        String prompt = (String) map.get("prompt");
        String response = ChatPyReader.pythonReader(prompt);

        System.out.println(response);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // Unused, use generateLines() instead to generate pickup lines
    @PostMapping("/pickUpLines") // to do generate response based on profile
    public ResponseEntity<Object> generateProfilePickUpLines(@RequestBody final Map<String, Object> map)
            throws MalformedURLException, IOException {

        String prompt = (String) map.get("prompt");
        int responses = Integer.valueOf((String) map.get("responses"));

        long profileId = Long.valueOf((String) map.get("profileId"));

        // DatingProfile profile = profilerep.findById(profileId).orElse(null);

        ArrayList<String> response;

        if (prompt == null || prompt.equals("")) {
            response = (ArrayList<String>) (chatGPT.generateMultiple(responses, null));
        } else {
            response = (ArrayList<String>) (chatGPT.generateMultiple(responses, prompt));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
