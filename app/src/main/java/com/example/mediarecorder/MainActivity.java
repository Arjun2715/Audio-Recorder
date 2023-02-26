package com.example.mediarecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder;// Objeto MediaRecorder para grabar audio
    private MediaPlayer mediaPlayer;// Objeto MediaPlayer para reproducir audio
    private String fileName;// Nombre del archivo de audio
    private boolean isRecording = false; // Indica si se está grabando actualmente
    private Button recordButton,stopButton,playButton,stopPlayButton;
    private TextView messageTextView; // TextView para mostrar mensajes de estado
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private final String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordButton = findViewById(R.id.record_button);// Botón para iniciar la grabación
        stopButton = findViewById(R.id.stop_button);// Botón para detener la grabación
        playButton = findViewById(R.id.play_button);// Botón para reproducir la grabación
        stopPlayButton = findViewById(R.id.stop_play_button);// Botón para detener la reproducción
        messageTextView = findViewById(R.id.message_text_view);// TextView para mostrar mensajes de estado
        stopButton.setEnabled(false);
        playButton.setEnabled(false);
        stopPlayButton.setEnabled(false);
        fileName = getExternalCacheDir().getAbsolutePath();// Ruta donde se almacenará el archivo de audio
        fileName += "/audiotest.3gp";// Nombre del archivo de audio
        // Verificar si se tienen permisos para grabar audio y escribir en el almacenamiento externo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);// Solicitar permisos
        } else {
            permissionToRecordAccepted = true;
        }
        // Listener para el botón de grabación
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    if (checkMicrophoneAvailability()) {
                        startRecording();// Iniciar la grabación
                    } else {
                        Toast.makeText(MainActivity.this, "Microphone no desponible", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Grabación ya en curso", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Listener para el botón de detener la grabación
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
        // Listener para el botón de reproducir la grabación
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecording();
            }
        });
        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayback();
            }
        });
    }
    // Comprueba si el micrófono está disponible en el dispositivo
    private boolean checkMicrophoneAvailability() {
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }
    // Inicia la grabación
    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e("MediaRecorder", "prepare() failed");
        }
        mediaRecorder.start();
        isRecording = true;
        messageTextView.setText(R.string.r1);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        stopPlayButton.setEnabled(false);
    }
    // Detiene la grabación
    private void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            messageTextView.setText(R.string.r2);
            recordButton.setEnabled(true);
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
            stopPlayButton.setEnabled(false);
        }
    }
    // Reproduce el archivo de audio grabado
    private void playRecording() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            messageTextView.setText(R.string.r3);
            recordButton.setEnabled(false);
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            stopPlayButton.setEnabled(true);
        // Configura la acción a tomar cuando la reproducción se completa
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    messageTextView.setText(R.string.r4);
                    recordButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    playButton.setEnabled(true);
                    stopPlayButton.setEnabled(false);
                }
            });
        } catch (IOException e) {
            Log.e("MediaRecorder", "prepare() failed");
        }
    }
    // Detiene la reproducción del archivo de audio
    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            messageTextView.setText(R.string.r5);
            recordButton.setEnabled(true);
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
            stopPlayButton.setEnabled(false);
        }
    }
    // Maneja la respuesta de permiso de grabación de audio del usuario
    @Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;}
    if (!permissionToRecordAccepted ) finish();
    }
}
