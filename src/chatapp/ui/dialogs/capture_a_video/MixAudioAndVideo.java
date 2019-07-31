package chatapp.ui.dialogs.capture_a_video;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.*;

import java.io.File;
import java.net.MalformedURLException;

public final class MixAudioAndVideo {
    public static void Mix(File video_file,File audio_file,File result_file){
        String inputVideoFilePath = null;
        String inputAudioFilePath =null;
        String outputVideoFilePath =null;
        try {
            inputVideoFilePath = video_file.toURI().toURL().toString();
            inputAudioFilePath=audio_file.toURI().toURL().toString();
            outputVideoFilePath =  result_file.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        IMediaWriter mWriter = ToolFactory.makeWriter(outputVideoFilePath);

        IContainer containerVideo = IContainer.make();
        IContainer containerAudio = IContainer.make();

        // check files are readable
        if (containerVideo.open(inputVideoFilePath, IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("Cant find " + inputVideoFilePath);
        if (containerAudio.open(inputAudioFilePath, IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("Cant find " + inputAudioFilePath);

        // read video file and create stream
        IStreamCoder coderVideo = containerVideo.getStream(0).getStreamCoder();
        if (coderVideo.open(null, null) < 0)
            throw new RuntimeException("Cant open video coder");
        IPacket packetvideo = IPacket.make();
        int width = coderVideo.getWidth();
        int height = coderVideo.getHeight();

        // read audio file and create stream
        IStreamCoder coderAudio = containerAudio.getStream(0).getStreamCoder();
        if (coderAudio.open(null, null) < 0)
            throw new RuntimeException("Cant open audio coder");
        IPacket packetaudio = IPacket.make();

        mWriter.addAudioStream(1, 0, coderAudio.getChannels(), coderAudio.getSampleRate());
        mWriter.addVideoStream(0, 0, width, height);

        while (containerVideo.readNextPacket(packetvideo) >= 0) {

            containerAudio.readNextPacket(packetaudio);

            // video packet
            IVideoPicture picture = IVideoPicture.make(coderVideo.getPixelType(), width, height);
            coderVideo.decodeVideo(picture, packetvideo, 0);
            if (picture.isComplete())
                mWriter.encodeVideo(0, picture);


            IAudioSamples samples;
            do {
                samples = IAudioSamples.make(512, coderAudio.getChannels(), IAudioSamples.Format.FMT_S32);
                containerAudio.readNextPacket(packetaudio);
                coderAudio.decodeAudio(samples, packetaudio, 0);
                mWriter.encodeAudio(1, samples);
            }while (samples.isComplete() );


        }

        coderAudio.close();
        coderVideo.close();
        containerAudio.close();
        containerVideo.close();
        mWriter.close();
        System.out.println("Done !");
    }

    /*public static void Mix(File video_file,File audio_file,File result_file) throws MalformedURLException {

        String filenamevideo =video_file.toURI().toURL().toString(); //video file on your disk
        String filenameaudio = audio_file.toURI().toURL().toString();//audio file on your disk


        IMediaWriter mWriter = ToolFactory.makeWriter(result_file.toURI().toURL().toString()); //output file

        IContainer containerVideo = IContainer.make();
        IContainer containerAudio = IContainer.make();

        if (containerVideo.open(filenamevideo, IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("Cant find " + filenamevideo);

        if (containerAudio.open(filenameaudio, IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("Cant find " + filenameaudio);

        int numStreamVideo = containerVideo.getNumStreams();
        int numStreamAudio = containerAudio.getNumStreams();


        int videostreamt = -1; //this is the video stream id
        int audiostreamt = -1;

        IStreamCoder videocoder = null;

        for(int i=0; i<numStreamVideo; i++){
            IStream stream = containerVideo.getStream(i);
            IStreamCoder code = stream.getStreamCoder();

            if(code.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
            {
                videostreamt = i;
                videocoder = code;
                break;
            }

        }

        for(int i=0; i<numStreamAudio; i++){
            IStream stream = containerAudio.getStream(i);
            IStreamCoder code = stream.getStreamCoder();

            if(code.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
            {
                audiostreamt = i;
                break;
            }

        }

        if (videostreamt == -1) throw new RuntimeException("No video steam found");
        if (audiostreamt == -1) throw new RuntimeException("No audio steam found");

        if(videocoder.open()<0 ) throw new RuntimeException("Cant open video coder");
        IPacket packetvideo = IPacket.make();

        IStreamCoder audioCoder = containerAudio.getStream(audiostreamt).getStreamCoder();

        if(audioCoder.open()<0 ) throw new RuntimeException("Cant open audio coder");
        mWriter.addAudioStream(0, 0, audioCoder.getChannels(), audioCoder.getSampleRate());

        mWriter.addVideoStream(1, 1, videocoder.getWidth(), videocoder.getHeight());

        IPacket packetaudio = IPacket.make();

        while(containerVideo.readNextPacket(packetvideo) >= 0 ||
                containerAudio.readNextPacket(packetaudio) >= 0){

            if(packetvideo.getStreamIndex() == videostreamt){

                //video packet
                IVideoPicture picture = IVideoPicture.make(videocoder.getPixelType(),
                        videocoder.getWidth(),
                        videocoder.getHeight());
                int offset = 0;
                while (offset < packetvideo.getSize()){
                    int bytesDecoded = videocoder.decodeVideo(picture,
                            packetvideo,
                            offset);
                    if(bytesDecoded < 0) throw new RuntimeException("bytesDecoded not working");
                    offset += bytesDecoded;

                    if(picture.isComplete()){

                        mWriter.encodeVideo(1, picture);

                    }
                }
            }

            if(packetaudio.getStreamIndex() == audiostreamt){
                //audio packet

                IAudioSamples samples = IAudioSamples.make(512,
                        audioCoder.getChannels(),
                        IAudioSamples.Format.FMT_S32);
                int offset = 0;
                while(offset<packetaudio.getSize())
                {
                    int bytesDecodedaudio = audioCoder.decodeAudio(samples,
                            packetaudio,
                            offset);
                    if (bytesDecodedaudio < 0)
                        throw new RuntimeException("could not detect audio");
                    offset += bytesDecodedaudio;

                    if (samples.isComplete()){
                        mWriter.encodeAudio(0, samples);

                    }
                }

            }

        }
    }*/
}
