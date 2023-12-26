package com.example.examenfinal;

import android.os.CountDownTimer;

public class RegistroFechaCronometro {

    private CountDownTimer countDownTimer;
    private boolean cronometroCorriendo = false;
    private long tiempoRestante = 0;

    public interface OnCronometroTickListener {
        void onTick(String tiempoRestante);
        void onFinish();
    }

    private OnCronometroTickListener onCronometroTickListener;

    public RegistroFechaCronometro(OnCronometroTickListener listener) {
        this.onCronometroTickListener = listener;
    }

    public void iniciarCronometro() {
        countDownTimer = new CountDownTimer(tiempoRestante, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestante = millisUntilFinished;
                if (onCronometroTickListener != null) {
                    onCronometroTickListener.onTick(obtenerTiempoFormato());
                }
            }

            @Override
            public void onFinish() {
                detenerCronometro();
                if (onCronometroTickListener != null) {
                    onCronometroTickListener.onFinish();
                }
            }
        }.start();

        cronometroCorriendo = true;
    }

    public void detenerCronometro() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        cronometroCorriendo = false;
    }

    public boolean isCronometroCorriendo() {
        return cronometroCorriendo;
    }

    public String obtenerTiempoFormato() {
        int minutos = (int) (tiempoRestante / 1000) / 60;
        int segundos = (int) (tiempoRestante / 1000) % 60;

        return String.format("%02d:%02d", minutos, segundos);
    }
}
