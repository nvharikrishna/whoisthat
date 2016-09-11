package com.nvharikrishna.whoisthat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class MainActivity extends AppCompatActivity {

    private final int CHECK_CODE = 0x1;
    private final int LONG_DURATION = 5000;
    private final int SHORT_DURATION = 1200;

    private static Speaker speaker;
    private static SpeechRecognizer speechRecognizer;
    private static BlockingDeque<String> messages = new LinkedBlockingDeque<>();
    private static boolean speechRecognizerRunning = false;

    private ToggleButton toggle;
    private CompoundButton.OnCheckedChangeListener toggleListener;

    private TextView smsText;
    private TextView smsSender;

    private BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, NotificationListenerService.class));
        setContentView(R.layout.activity_main);
        speaker = new Speaker(getApplicationContext());
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        VoiceCommandListener2 voiceCommandListener = new VoiceCommandListener2();
        speechRecognizer.setRecognitionListener(voiceCommandListener);

        IntentFilter recognizeFilter = new IntentFilter();
        recognizeFilter.addAction("whoisthat.Recognize");
        RecognizeReceiver recognizeReceiver = new RecognizeReceiver();
        registerReceiver(recognizeReceiver, recognizeFilter);

        IntentFilter speakFilter = new IntentFilter();
        speakFilter.addAction(("whoisthat.Speak"));
        SpeakReceiver speakReceiver = new SpeakReceiver();
        registerReceiver(speakReceiver, speakFilter);
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                speaker = new Speaker(this);
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }
    private void initializeSMSReceiver(){
        smsReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if(bundle!=null){
                    Object[] pdus = (Object[])bundle.get("pdus");
                    for(int i=0;i<pdus.length;i++){
                        byte[] pdu = (byte[])pdus[i];
                        SmsMessage message = SmsMessage.createFromPdu(pdu);
                        String text = message.getDisplayMessageBody();
                        String sender = getContactName(message.getOriginatingAddress());
                        speaker.pause(LONG_DURATION);
                        speaker.speak("You have a new message from" + sender + "!");
                        speaker.pause(SHORT_DURATION);
                        speaker.speak(text);
                        smsSender.setText("Message from " + sender);
                        smsText.setText(text);
                    }
                }

            }
        };
    }

    private String getContactName(String phone){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        String projection[] = new String[]{ContactsContract.Data.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor.moveToFirst()){
            return cursor.getString(0);
        }else {
            return "unknown number";
        }
    }

    private void registerSMSReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
        speaker.destroy();
    }


    public static class RecognizeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String title_to_speak = intent.getStringExtra("title_to_speak");
            String message_to_speak = intent.getStringExtra("message_to_speak");
            launchSpeechRecognizer(context, title_to_speak, message_to_speak);
        }

        public void launchSpeechRecognizer(Context context, String title_to_speak, String message_to_speak){
            if(!SpeechRecognizer.isRecognitionAvailable(context)){
                Log.w("Recognizer", "Sorry! speech recognizer is not available now. Please tray again after some time");
                return;
            }
            messages.addFirst(title_to_speak);
            if(speechRecognizerRunning) {
                Log.d("Recoginzer", "SpeechRecognizer is already running... It will not be invoked again");
                return;
            }

            speechRecognizerRunning = true;
            speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));

        }
    }

    public static class SpeakReceiver extends BroadcastReceiver {

        private Speaker speaker;


        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("SPEAK RECEIVER", "received message ******");
            speaker.speak(intent.getStringExtra("message_to_speak"));

        }
    }

    public static class VoiceCommandListener2 implements RecognitionListener {

        private static final String TAG = "VoiceCommandListener2";
        String message;

        public VoiceCommandListener2(){

        }

        public VoiceCommandListener2(String message){
            this.message = message;
        }



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
            if(error == SpeechRecognizer.ERROR_NO_MATCH){
                speaker.speak("Sorry! cannot recognize your input. Lets talk later.");
            }
            speechRecognizerRunning = false;
        }

        private boolean isValidRequest(List<String> speech)
        {
            Log.d(TAG, "speech ===== " + speech);
            if(null != speech) {
                for (String s : speech){
                    try
                    {
                        Log.d(TAG, "SPEECH " + s.toString());
                        if(sanitize(s).equals("whoisthat") || sanitize(s).equals("whosthat") )
                        {
                            return true;
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "onResults: Caught Exception while sanitizing string");
                    }
                }
            }
            return false;
        }

        private String sanitize(String s)
        {
            return s.toLowerCase().replace(" ","").replace("'","").replace(",","");
        }
        @Override
        public void onResults(Bundle results) {
            speechRecognizerRunning = false;
            Log.d(TAG, "Received results : " + results.toString());

            List<String> speech = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if(!isValidRequest(speech))
                return;

            if(!messages.isEmpty()) {
                speaker.speak(messages.pollFirst());
            }

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


}
