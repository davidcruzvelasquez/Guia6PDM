package com.example.reproductoraudioapp.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.reproductoraudioapp.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AudioAsincrono extends AsyncTask<Void, String,String> {

    Context context;
    TextView txvActual, txvFinal;
    Button btnIniciar;
    ProgressBar pbAudio;

    MediaPlayer reproductorMusica;

    boolean pause = false;
    boolean reiniciar = false;
    private String VIGILANTE = "vigilante";

    int duration;
    int amountToUpdate;

    public AudioAsincrono(Context context, TextView txvActual, TextView txvFinal, Button btnIniciar, ProgressBar pbAudio) {
        this.context = context;
        this.txvActual = txvActual;
        this.txvFinal = txvFinal;
        this.btnIniciar = btnIniciar;
        this.pbAudio = pbAudio;
    }

    @Override
    protected String doInBackground(Void... voids) {

        reproductorMusica.start();
        while( reproductorMusica.isPlaying() ) {
            esperaUnSegundo();
            publishProgress(tiempo(reproductorMusica.getCurrentPosition()));
            if ( pause == true ){
                synchronized (VIGILANTE){
                    try{
                        /** Realiza una pausa en el hilo */
                        reproductorMusica.pause();
                        VIGILANTE.wait();
                    }catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    pause = false;
                    reproductorMusica.start();
                }
            }
        }

        return null;
    }

    private void esperaUnSegundo() {
        try{
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignore){}
    }

    /** Notifica al vigilante en todas sus llamadas con syncronized **/
    public void reanudarAudio() {
        synchronized (VIGILANTE){
            VIGILANTE.notify();
        }
        btnIniciar.setText("Pausar");
    }

    public void pausarAudio() {
        pause = true;
        btnIniciar.setText("Reanudar");
    }

    public boolean esPause() {
        return pause;
    }

    public void reiniciarAudio() {
        reiniciar = true;
        onPreExecute();
    }

    private String tiempo(long tiempo) {
        long fin_min = TimeUnit.MILLISECONDS.toMinutes(tiempo);
        long fin_sec = TimeUnit.MILLISECONDS.toSeconds(tiempo) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tiempo));
        return fin_min + ":" + fin_sec;
    }

    @Override
    protected void onPreExecute() {
        reproductorMusica = MediaPlayer.create(context, R.raw.nokia_tune);
        long fin = reproductorMusica.getDuration();
        txvFinal.setText(tiempo(fin));
        super.onPreExecute();
        btnIniciar.setText("Iniciar");
        txvActual.setText("00:00");
        duration = reproductorMusica.getDuration();
        pbAudio.setMax(duration/1000);
        amountToUpdate = (1000);
        pbAudio.setProgress(0);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        txvActual.setText(values[0]);
        super.onProgressUpdate(values);
        if(!reiniciar){
            if(!pause){
                btnIniciar.setText("Pausar");
            }
            if (!(amountToUpdate * pbAudio.getProgress() >= duration)) {
                int p = pbAudio.getProgress();
                p += 1;
                pbAudio.setProgress(p);
            }
        }
    }
}
