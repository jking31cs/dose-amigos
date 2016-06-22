package info.doseamigos.meds;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Welcome to Dose Amigos, you can add a new medication.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        SimpleCard card = new SimpleCard();
        card.setTitle("Add New Medication");
        card.setContent("Welcome to Dose Amigos, you can add a new medication.");
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
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
