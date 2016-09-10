package com.nvharikrishna.whoisthat;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Harikrishna on 10/09/16.
 */
public class VoiceCommandListener implements RecognitionListener {

    private static final String TAG = "VoiceCommandListener";

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "READY FOR SPEECH");

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "BEGINNING OF SPEECH");

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.v(TAG, "recevied buffer");

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "End of speech");

    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "ERROR while recoginzing command. Received error code: " + error);

    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "Received results : " + results.toString());
        Log.d(TAG, "bundle  score" + results.getString(SpeechRecognizer.CONFIDENCE_SCORES));
        Log.d(TAG, "bundle " + results.getString(SpeechRecognizer.RESULTS_RECOGNITION));

        List<String> speech = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(TAG, "speech ===== " + speech);
        if(null != speech)
            for(String s : speech)
            Log.d(TAG, "SPEECH " + s.toString());
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "Received partial results " + partialResults);

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.v(TAG, "received event");

    }
}
