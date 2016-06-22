package info.doseamigos.meds;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Speechlet for adding medication.
 */
public class MedSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(MedSpeechlet.class);
    private MedService medService;

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        Injector injector = Guice.createInjector(
            new MedGuiceModule()
        );
        medService = injector.getInstance(MedService.class);
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info(session.getUser().getUserId());
        if (session.getUser().getAccessToken() == null) {
            log.info("The access token is null.");
            throw new NullPointerException();
        }
        String accessToken = session.getUser().getAccessToken();
        Map<String, String> userInfo = null;
        try {
            log.info("Attempting to get user from access token");
            userInfo = getUserInfo(accessToken);

        } catch (IOException e) {
            log.error("error occurred grabbing user info from google", e);
        }
        log.info("Received user from google.");
        String name = userInfo == null ? "John Doe" : userInfo.get("name");
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Welcome to Dose Amigos " + name + ", you can add a new medication.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        SimpleCard card = new SimpleCard();
        card.setTitle("Add New Medication");
        card.setContent("Welcome to Dose Amigos, you can add a new medication.");
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    Map<String, String> getUserInfo(String accessToken) throws IOException {
//        URL url = new URL("https://www.googleapis.com/userinfo/v2/me?access_token=" + "ya29.CjAJAxFJy-WXjpWv6HRIbYy1Exv6suL5diA4JdwTa7xLJqOaC4CAkNKvJ2h_KzJzkWM");
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//        urlConnection.setRequestMethod("GET");
//        urlConnection.connect();
        HttpGet httpGet = new HttpGet("https://www.googleapis.com/userinfo/v2/me?access_token=" + "ya29.CjAJAxFJy-WXjpWv6HRIbYy1Exv6suL5diA4JdwTa7xLJqOaC4CAkNKvJ2h_KzJzkWM");
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(httpGet);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getEntity().getContent(), objectMapper.getTypeFactory()
            .constructMapType(HashMap.class, String.class, String.class));

    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        if ("AddMed".equals(intentName)) {
            String medName = intent.getSlot("MedName").getValue();
            Med newMed = medService.addByName(medName);
            log.info("Added new med: " + newMed);
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText(String.format("You've added %s to your list of medications.", newMed.getName()));
            SimpleCard card = new SimpleCard();
            card.setTitle("Added Medication");
            card.setContent("You added the following Medication: " + newMed);

            return SpeechletResponse.newTellResponse(outputSpeech, card);
        } else {
            throw new RuntimeException("Intent is invalid.");
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {

    }
}
