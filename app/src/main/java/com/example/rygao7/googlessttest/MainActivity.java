package com.example.rygao7.googlessttest;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import android.widget.TextView;

import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import org.w3c.dom.Text;

import java.io.IOException;

import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private VoiceRecorder mVoiceRecorder;

    private String recognizeResult;

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {

        }

        @Override
        public void onVoice(byte[] data, int size) {
            try (SpeechClient speechClient = SpeechClient.create()) {
                ByteString audioBytes = ByteString.copyFrom(data, 0, size);

                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(AudioEncoding.LINEAR16)
                        .setSampleRateHertz(16000)
                        .setLanguageCode("en-US")
                        .build();
                RecognitionAudio audio = RecognitionAudio.newBuilder()
                        .setContent(audioBytes)
                        .build();

                // Performs speech recognition on the audio file
                RecognizeResponse response = speechClient.recognize(config, audio);
                List<SpeechRecognitionResult> results = response.getResultsList();

                for (SpeechRecognitionResult result : results) {
                    // There can be several alternative transcripts for a given chunk of speech. Just use the
                    // first (most likely) one here.
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    recognizeResult = alternative.getTranscript();

                    Toast.makeText(MainActivity.this, "This is my Toast message!",
                            Toast.LENGTH_LONG).show();



                }
            }
            catch (IOException e) {}
        }

        @Override
        public void onVoiceEnd() {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecorder();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    protected void onStop() {
        // Stop listening to voice
        stopVoiceRecorder();

        super.onStop();
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }


}