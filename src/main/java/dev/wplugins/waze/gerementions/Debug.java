package dev.wplugins.waze.gerementions;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Debug {

    public static void main(String[] args) {
        SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Long expire = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(System.currentTimeMillis() >= expire) {
                    System.out.println("[" + SDF.format(System.currentTimeMillis()) + " INFO] Check turned to expired!");
                    System.exit(0);
                } else {
                    System.out.println("[" + SDF.format(System.currentTimeMillis()) + " INFO] It doesnt expired yet.");
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 1000, 500);

    }
}
