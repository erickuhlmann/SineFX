package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("SineFX");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        // start the audio thread

    }

    private static final int SAMPLE_RATE = 16 * 1024;

    private void playSound() throws LineUnavailableException
    {
        final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        SourceDataLine line = AudioSystem.getSourceDataLine(af);
        line.open(af, SAMPLE_RATE);
        line.start();

        boolean forwardNotBack = true;

        for(double freq = 400; freq <= 800;)
        {
            byte [] toneBuffer = createSinWaveBuffer(freq);
            int count = line.write(toneBuffer, 0, toneBuffer.length);

            if(forwardNotBack)
            {
                freq += 20;
                forwardNotBack = false;
            }
            else
            {
                freq -= 10;
                forwardNotBack = true;
            }
        }

        line.drain();
        line.close();

    }

    public static byte[] createSinWaveBuffer(double freq)
    {
        double waveLen = 1.0/freq;
        int samples = (int) Math.round(waveLen * 5 * SAMPLE_RATE);
        byte[] output = new byte[samples];
        double period = SAMPLE_RATE / freq;
        for (int i = 0; i < output.length; i++)
        {
            double angle = 2.0 * Math.PI * i / period;
            output[i] = (byte)(Math.sin(angle) * 127f);
        }
        return output;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
