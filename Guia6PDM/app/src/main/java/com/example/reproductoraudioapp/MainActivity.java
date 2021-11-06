package com.example.reproductoraudioapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.reproductoraudioapp.utils.AudioAsincrono;

public class MainActivity extends AppCompatActivity {

    private Button btnIniciar, btnReiniciar;
    private TextView txvActual, txvFinal;
    private AudioAsincrono audioAsincrono;
    private ProgressBar pbAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvActual = findViewById(R.id.txvActual);
        txvFinal  = findViewById(R.id.txvFinal);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        pbAudio = findViewById(R.id.pbAudio);
    }

    private void iniciar() {
        if ( audioAsincrono == null ) {
            audioAsincrono = new AudioAsincrono(MainActivity.this, txvActual, txvFinal, btnIniciar, pbAudio);
            audioAsincrono.execute();
        } else if ( audioAsincrono.getStatus() == AsyncTask.Status.FINISHED ) {
            audioAsincrono = new AudioAsincrono(MainActivity.this, txvActual, txvFinal, btnIniciar, pbAudio);
            audioAsincrono.execute();
        } else if (audioAsincrono.getStatus() == AsyncTask.Status.RUNNING && !audioAsincrono.esPause() ) { // En caso de que este corriendo y no este pausado; entonces se pausa.
            audioAsincrono.pausarAudio();
        } else { // En caso de que este pausado; entonces se debe reanudar
            audioAsincrono.reanudarAudio();
        }
    }

    public void onClickBtnIniciar(View view ) {
        iniciar();
    }

    public void onClickBtnReiniciar(View view ) {
        audioAsincrono.reiniciarAudio();
    }

}