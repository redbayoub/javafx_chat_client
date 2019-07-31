package chatapp.ui.mainView.AudioRecording;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleLongProperty;

import javax.sound.sampled.*;
import javax.ws.rs.NotSupportedException;
import java.io.File;
import java.io.IOException;

/**
 * A sample program is to demonstrate how to record sound in Java author:
 * www.codejava.net
 * http://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
 */
public class JavaSoundRecorder extends Thread
{
    public JavaSoundRecorder(File result_file){
        this.wavFile=result_file;
        AudioFormat format = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        // checks if system supports the data line
        if (!AudioSystem.isLineSupported(info)) {
            throw new NotSupportedException("Data Line not supported");
        }
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);

            line.open(format);
            line.start();   // start capturing
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // path of the wav file
    private File wavFile ;

    // format of audio file
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    private TargetDataLine line;

    @Override
    public void run() {
        try {

            AudioInputStream ais = new AudioInputStream(line);
            // start recording
            startTime=System.currentTimeMillis();
            timer.start();

            AudioSystem.write(ais, fileType, wavFile);

        }
        catch ( IOException ex) {
            ex.printStackTrace();
        }
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
        // audio in recoding video
        float sampleRate = 44100;// 44100
        int sampleSizeInBits = 16; // 16
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false; //false
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    public void finish()
    {
        if(line!=null){
            line.stop();
            line.close();
        }
        if(timer!=null){
            timer.stop();
        }

    }
}
