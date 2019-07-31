package chatapp.ui.dialogs.capture_a_video;

import chatapp.ui.mainView.AudioRecording.JavaSoundRecorder;
import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXProgressBar;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.video.ConverterFactory;
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
    AtomicInteger  frame_count=new AtomicInteger(-1);
    private ScheduledExecutorService executorService=Executors.newSingleThreadScheduledExecutor();
    private ExecutorService writer_excutor=Executors.newSingleThreadExecutor();
    private File result_file ;
    private File video_file;
    private File audio_file;

    private ImageView feed_view;
    private JFXProgressBar progressBar;
    private volatile State state;
    private Webcam webcam;
    private IMediaWriter writer;
    private StopWatch stopWatch=new StopWatch();
    private ScheduledFuture capturing_task;
    private JavaSoundRecorder soundRecorder;
    public VideoRecorder(File result_file, ImageView feed_view, Webcam webcam, JFXProgressBar progress_bar){
        this.result_file=result_file;
        this.feed_view=feed_view;
        this.progressBar=progress_bar;
        this.webcam=webcam;
        init_recorder();
    }

    public void init_recorder() {
        Thread preparing_thread=new Thread(()->{
            try {
                String video_filename=result_file.getName().substring(0,result_file.getName().lastIndexOf('.') );
                video_file=new File(result_file.getParentFile(),video_filename+"_vo.mp4");
                writer = ToolFactory.makeWriter(video_file.toURI().toURL().toString());


                writer.addVideoStream(VIDEO_STREAM_INDEX, 0, ICodec.ID.CODEC_ID_H264, webcam.getViewSize().width, webcam.getViewSize().height);
                //----------------------------------------
                webcam.open();
                state=State.Ready;

                Platform.runLater(()->progressBar.setVisible(false));
                init_capturing_task();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        });
        preparing_thread.start();
    }

    void capture(){
        if(state.equals(State.Recording) || !state.equals(State.Ready)){
            return;
        }
        state=State.Recording;


    }

    void save(){
        if(state.equals(State.Recording) ){
            state=State.Stopped;
            soundRecorder.finish();
            writer_excutor.shutdown();
            try {
                writer_excutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            executorService.shutdownNow();
            writer.flush();
            writer.close();
            webcam.close();
            MixAudioAndVideo.Mix(video_file,audio_file ,result_file );
            if(video_file.exists())video_file.delete();
            if(audio_file.exists())audio_file.delete();
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

    private void init_capturing_task(){
        Runnable feed_process=new Runnable() {
            @Override
            public void run() {
                BufferedImage in_img=webcam.getImage();

                Platform.runLater(()->feed_view.setImage(SwingFXUtils.toFXImage(in_img, null)));
                if (state.equals(State.Recording)) {
                    if (frame_count.get() == -1) { // first running
                        frame_count.set(0);
                        if(stopWatch.isStarted()){
                            stopWatch.reset();
                        }
                        stopWatch.start();
                        if(soundRecorder==null){
                            audio_file=new File(result_file.getParent(),result_file.getName().substring(0, result_file.getName().lastIndexOf('.'))+"_ao.wav");
                            soundRecorder=new JavaSoundRecorder(audio_file);
                            soundRecorder.start();

                        }
                    }
                    writer_excutor.submit(()->{

                    BufferedImage converted_image = ConverterFactory.convertToType(in_img, BufferedImage.TYPE_3BYTE_BGR);
                    long time=stopWatch.getTime();
                    /*IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
                    IVideoPicture frame = converter.toPicture(image, stopWatch.getTime() * 1000);
                    frame.setKeyFrame(frame_count.get() == 0);
                    frame.setQuality(0);
                        System.out.println("Capture frame " + frame_count);*/
                        writer.encodeVideo(VIDEO_STREAM_INDEX, converted_image,time,TimeUnit.MILLISECONDS);
                        frame_count.incrementAndGet();
                    });


                }
            }
        };
                        capturing_task= executorService.scheduleAtFixedRate(feed_process, 0, 100, TimeUnit.MILLISECONDS );
    }



    private enum State{
        Ready,Recording,Stopped;
    }

}
