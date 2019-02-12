package chatapp.ui.dialogs.capture_a_video;

import chatapp.ui.mainView.AudioRecording.JavaSoundRecorder;
import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXProgressBar;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.time.StopWatch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class VideoRecorder
{

    private static final int VIDEO_STREAM_INDEX = 0;
    private static final int AUDIO_STREAM_INDEX = 1;
    private static final TimeUnit DEFAULT_TIMEUNIT=TimeUnit.MILLISECONDS;


    public VideoRecorder(File result_file, ImageView feed_view, Webcam webcam, JFXProgressBar progress_bar){
        this.result_file=result_file;
        this.feed_view=feed_view;
        this.progressBar=progress_bar;
        this.webcam=webcam;
        init_recorder();
    }
    private ScheduledExecutorService executorService=Executors.newSingleThreadScheduledExecutor();
    private ExecutorService writer_excutor=Executors.newSingleThreadExecutor();
    private File result_file ;
    private ImageView feed_view;
    private JFXProgressBar progressBar;
    private volatile State state;
    private Webcam webcam;
    private IMediaWriter writer;

   /* // the line from which audio data is captured
    private TargetDataLine line;*/

    public void init_recorder() {
        Thread preparing_thread=new Thread(()->{
            try {
                writer = ToolFactory.makeWriter(result_file.toURI().toURL().toString());


                writer.addVideoStream(VIDEO_STREAM_INDEX, 0, ICodec.ID.CODEC_ID_H264, webcam.getViewSize().width, webcam.getViewSize().height);
                //----------------------------------------
               /* AudioFormat format = getAudioFormat();
                writer.addAudioStream(AUDIO_STREAM_INDEX, 0, ICodec.ID.CODEC_ID_MP3, format.getChannels(),(int)format.getSampleRate());

                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                if (!AudioSystem.isLineSupported(info)) {
                    throw new NotSupportedException("Data Line not supported");
                }
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format, line.getBufferSize());*/


                //short[] samples=get_audio_samples(line);
                // use audio samples

                //-------------------------------------
                webcam.open();
                state=State.Ready;
                //line.start();
                Platform.runLater(()->progressBar.setVisible(false));
                init_capturing_task();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        });
        preparing_thread.start();
    }

    /*private short[] get_audio_samples() {
        byte[] audioBytes = new byte[line.getBufferSize() / 2 ]; // best size?
        int numBytesRead = 0;
        numBytesRead = line.read(audioBytes, 0, audioBytes.length);
        // convert to signed shorts representing samples
        int numSamplesRead = numBytesRead / 2;
        short[] audioSamples = new short[ numSamplesRead ];
        if (line.getFormat().isBigEndian()) {
            for (int i = 0; i < numSamplesRead; i++) {
                audioSamples[i] = (short)((audioBytes[2*i] << 8) | audioBytes[2*i + 1]);
            }
        }
        else {
            for (int i = 0; i < numSamplesRead; i++) {
                audioSamples[i] = (short)((audioBytes[2*i + 1] << 8) | audioBytes[2*i]);
            }
        }
        return audioSamples;
    }

    private AudioFormat getAudioFormat()
    {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

*/
    private StopWatch stopWatch=new StopWatch();



    /*private SimpleLongProperty elpased_time=new SimpleLongProperty(0);



    public SimpleLongProperty getElpased_time() {
        return elpased_time;
    }
    private AnimationTimer timer=new AnimationTimer() {
        @Override
        public void handle(long now) {
            elpased_time.set( System.currentTimeMillis() - startTime );
        }
    };*/
    private ScheduledFuture capturing_task;
    private JavaSoundRecorder soundRecorder;


    void capture(){
        if(state.equals(State.Recording) || !(state.equals(State.Ready)||state.equals(State.Paused))){
            return;
        }
        state=State.Recording;
        if(state.equals(State.Paused)){
            stopWatch.resume();
            soundRecorder.resumeLine();
        }

    }

    void redo(){
        state=State.Stopped;
        if(result_file.exists())result_file.delete();
        writer_excutor.shutdownNow();
    }

    void save(){
        if(state.equals(State.Recording) || state.equals(State.Paused) ){
            state=State.Stopped;
            soundRecorder.finish();
            writer_excutor.shutdown();
            try {
                writer_excutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*line.stop();
            line.close();*/
            executorService.shutdownNow();
/*            line.stop();
            line.close();*/
            writer.flush();
            writer.close();
           /* capturing_task.cancel(true);
            writer_excutor.shutdownNow();*/

            //executorService.shutdownNow();
            webcam.close();
        }
    }

    void pause(){
        if(state.equals(State.Recording)){
            state=State.Paused;
            stopWatch.suspend();
            soundRecorder.pause();
        }
    }

    void clean_up(){
        if(capturing_task!=null)
            capturing_task.cancel(true);
        executorService.shutdownNow();
        writer_excutor.shutdownNow();
        try {
            writer.flush();
            writer.close();
        }catch (RuntimeException e){}
        if(stopWatch.isStarted())
            stopWatch.stop();
        webcam.close();

    }
    AtomicInteger  frame_count=new AtomicInteger(-1);

    private void init_capturing_task(){
        Runnable feed_process=new Runnable() {
            @Override
            public void run() {
                BufferedImage in_img=webcam.getImage();

                Platform.runLater(()->feed_view.setImage(SwingFXUtils.toFXImage(in_img, null)));
                if (state.equals(State.Recording)) {
                    if (frame_count.get() == -1) { // first running
                        frame_count.set(0);
                        //startTime = System.currentTimeMillis();
                        stopWatch.start();
                        if(soundRecorder==null){
                            File audio_file=new File(result_file.getParent(),result_file.getName().substring(0, result_file.getName().lastIndexOf('.'))+".wav");
                            soundRecorder=new JavaSoundRecorder(audio_file);
                            soundRecorder.start();
                        }
                    }
                    writer_excutor.submit(()->{

                    BufferedImage image = ConverterFactory.convertToType(in_img, BufferedImage.TYPE_3BYTE_BGR);
                    IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
                    IVideoPicture frame = converter.toPicture(image, stopWatch.getTime() * 1000);
                    frame.setKeyFrame(frame_count.get() == 0);
                    frame.setQuality(0);
                        System.out.println("Capture frame " + frame_count);
                        writer.encodeVideo(VIDEO_STREAM_INDEX, frame);
                        frame_count.incrementAndGet();
                    });




                    //writer.encodeVideo(VIDEO_STREAM_INDEX, in_img, System.currentTimeMillis() - startTime,TimeUnit.MILLISECONDS);
                    //writer.encodeAudio(AUDIO_STREAM_INDEX, audio_samples);


                }
            }
        };
                    /*writer_excutor.submit(new Runnable() {
                        @Override
                        public void run() {
             *//*
                 if you have audio chunks which are 440 samples long (at 44000
                Hz sample rate, 440 / 44000 = 0.01 seconds), and video at exactly 25fps (1 / 25 = 0.04 seconds),
            *//*
             *//*
             video0 @ 0.00 sec
audio0 @ 0.00 sec
audio1 @ 0.01 sec
audio2 @ 0.02 sec
audio3 @ 0.03 sec
video1 @ 0.04 sec
audio4 @ 0.04 sec
audio5 @ 0.05 sec
              *//*

                        }
                    });
                }
            }
        };*/
/*        Runnable audio_recording_process=new Runnable() {
            @Override
            public void run() {
                if (state.equals(State.Recording))
                    writer.encodeAudio(AUDIO_STREAM_INDEX ,get_audio_samples(),stopWatch.getTime(),DEFAULT_TIMEUNIT);
            }
        };
        short[] audio_samples=get_audio_samples();*/
        /*System.out.println(audio_samples.length);
        System.out.println(line.getFormat().getSampleRate());
        System.out.println(((long)((audio_samples.length/line.getFormat().getSampleRate()))));*/
        //audio_recording_task=executorService1.scheduleAtFixedRate(audio_recording_process,0 ,20 , TimeUnit.MILLISECONDS);
        capturing_task= executorService.scheduleAtFixedRate(feed_process, 0, 100, TimeUnit.MILLISECONDS );
    }



    private enum State{
        Ready,Recording,Paused,Stopped;
    }

}
