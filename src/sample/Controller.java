package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Controller
{
    @FXML
    Button soundButton;

    private AudioThread audioThread;

    public Controller()
    {
        audioThread = new AudioThread();
        audioThread.setDaemon(true);
        audioThread.start();
    }

    @FXML
    private void onSoundButtonAction(ActionEvent event)
    {
        try
        {
            playSound();
        }
        catch (LineUnavailableException e)
        {;}

        soundButton.setText("Clicked");
    }

    @FXML
    private void onStartButtonAction(ActionEvent event)
    {
        audioThread.startSound();
    }

    @FXML
    private void onStopButtonAction(ActionEvent event)
    {
        audioThread.stopSound();
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


}
