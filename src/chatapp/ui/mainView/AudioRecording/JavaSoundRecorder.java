package chatapp.ui.mainView.AudioRecording;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * A sample program is to demonstrate how to record sound in Java author:
 * www.codejava.net
 * http://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
 */
public class JavaSoundRecorder extends Task<Void>
{
    public JavaSoundRecorder(File result_file){
        this.wavFile=result_file;
    }

    // path of the wav file
    private File wavFile ;

    // format of audio file
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    private TargetDataLine line;

    @Override
    protected Void call() throws Exception
    {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing

            System.out.println("Start capturing...");

            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Start recording...");

            // start recording
            startTime=System.currentTimeMillis();
            timer.start();
            AudioSystem.write(ais, fileType, wavFile);

        }
        catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private SimpleLongProperty elpased_time=new SimpleLongProperty(0);
    private long startTime;
    public SimpleLongProperty getElpased_time() {
        return elpased_time;
    }

    private AnimationTimer timer=new AnimationTimer() {
        @Override
        public void handle(long now) {
            elpased_time.set( System.currentTimeMillis() - startTime );
        }
    };



    /**
     * Defines an audio format
     */
    private AudioFormat getAudioFormat()
    {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish()
    {
        if(line!=null){
            line.stop();
            line.close();
        }
        if(timer!=null){
            timer.stop();
        }
        System.out.println("Finished");
    }

}
