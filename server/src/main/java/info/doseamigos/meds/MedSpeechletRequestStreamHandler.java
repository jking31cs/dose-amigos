package info.doseamigos.meds;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.google.common.collect.Sets;

/**
 * Handler to be used by Lambda to process Amazon Echo requests.
 */
public class MedSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    /**
     * Simple RequestStreamHandler Constructor for MedSpeechlet.
     */
    public MedSpeechletRequestStreamHandler() {
        super(new MedSpeechlet(), Sets.newHashSet("amzn1.echo-sdk-ams.app.ee78b839-b473-4a3c-9e64-2cda87bf93ac"));
    }
}
