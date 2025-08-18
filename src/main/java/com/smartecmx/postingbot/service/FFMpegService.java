package com.smartecmx.postingbot.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Slf4j
@Service
@NoArgsConstructor
public class FFMpegService {

    private static final String STATIC_RESOURCE_PATH_IMAGES = "src/main/resources/static/images";
    private static final String STATIC_RESOURCE_PATH_VIDEOS = "src/main/resources/static/videos";
    private static final double EXTRA_VIDEO_DURATION = 2.5;

    public void generateVideo(String destinationPathFolder, String fileName) throws IOException {
        String[] destinationFolderParts = destinationPathFolder.split("/");
        String fileId = destinationFolderParts[destinationFolderParts.length - 1];

        Path speechPath = Path.of(destinationPathFolder, "speech_" + fileId + ".mp3");
        Path subtitlesPath = Path.of(destinationPathFolder, "subtitles_" + fileId + ".srt");
        Path imagePath = Path.of(destinationPathFolder, "backgroundImage_" + fileId + ".jpg");
        Path musicPath = Path.of(destinationPathFolder, "backgroundMusic_" + fileId + ".mp3");
        Path logoPath = Path.of(STATIC_RESOURCE_PATH_IMAGES, "SmarTec_Mx_Logo_Watermark.png");

        FFprobe ffprobe = new FFprobe("ffprobe");
        double speechLenght = ffprobe.probe(speechPath.toString()).getFormat().duration + EXTRA_VIDEO_DURATION;

        Path videoFinalPath = Path.of(destinationPathFolder, fileName);

        String subtitlesFilter = subtitlesPath.toString().replace("\\", "\\\\");
        if (subtitlesFilter.contains(" ")) {
            subtitlesFilter = "'" + subtitlesFilter + "'";
        }

        String filter = String.format(
            "[0:v]loop=loop=-1:size=1:start=0,trim=duration=%.2f,fade=t=out:st=%.2f:d=2[vf];" +
            "[1:a]volume=4.0[voice];" +
            "[2:a]volume=0.3[bgmusic];" +
            "[voice][bgmusic]amix=inputs=2:duration=longest[mix];" +
            "[mix]afade=t=out:st=%.2f:d=2[audio];" +
            "[vf][3:v]overlay=main_w-overlay_w-90:60[with_logo];" +
            "[with_logo]subtitles=%s:force_style='FontSize=15,BorderStyle=3,BackColour=&H00008F39&,PrimaryColour=&HFFFFFF&'[final_video]",
            speechLenght,
            (speechLenght - EXTRA_VIDEO_DURATION),
            (speechLenght - EXTRA_VIDEO_DURATION),
            subtitlesPath.toString().replace("\\", "\\\\")
        );

        FFmpeg ffmpeg = new FFmpeg("ffmpeg");
        FFmpegBuilder builder = new FFmpegBuilder()
                .addInput(imagePath.toString())   // [0:v] Image
                .addInput(speechPath.toString())   // [1:a] Speech
                .addInput(musicPath.toString())   // [2:a] Music
                .addInput(logoPath.toString())     // [3:v] Logo

                .setComplexFilter(filter)
                .addOutput(videoFinalPath.toString())
                .setVideoCodec("libx264")
                .setAudioCodec("aac")
                .setVideoFrameRate(30)
                .addExtraArgs("-map", "[final_video]")
                .addExtraArgs("-map", "[audio]")
                .addExtraArgs("-shortest")
                .done();

        new FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run();

        Path introsDir = Path.of(STATIC_RESOURCE_PATH_VIDEOS, "Intros");
        Path randomIntro;
        try (var filesStream = Files.list(introsDir)) {
            var introVideos = filesStream.filter(Files::isRegularFile).toList();
            randomIntro = introVideos.get((int) (Math.random() * introVideos.size()));
        }
        Path followUsVideoPath = Path.of(STATIC_RESOURCE_PATH_VIDEOS, "Follow_Us.mp4");
        concatVideos(randomIntro, videoFinalPath, followUsVideoPath);
        log.info("Video generated successfully at: " + videoFinalPath.toString());
    }

    public void concatVideos(Path introVideoPath, Path generatedVideoPath, Path outroVideoPath) throws IOException {
        FFmpeg ffmpeg = new FFmpeg("ffmpeg");
        FFprobe ffprobe = new FFprobe("ffprobe");

        Path temporayOutputPath = Path.of(generatedVideoPath.getParent().toString(), "temp_" + generatedVideoPath.getFileName().toString());

        normalizeVideo(introVideoPath);
        normalizeVideo(generatedVideoPath);
        normalizeVideo(outroVideoPath);

        FFmpegBuilder builder = new FFmpegBuilder()
                .addInput(introVideoPath.toString())
                .addInput(generatedVideoPath.toString())
                .addInput(outroVideoPath.toString())
                .setComplexFilter("[0:v][0:a][1:v][1:a][2:v][2:a]concat=n=3:v=1:a=1[outv][outa]")
                .addOutput(temporayOutputPath.toString())
                .addExtraArgs("-map", "[outv]", "-map", "[outa]")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();
        Files.move(temporayOutputPath, generatedVideoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void normalizeVideo(Path filePath) throws IOException {
        FFmpeg ffmpeg = new FFmpeg("ffmpeg");
        FFprobe ffprobe = new FFprobe("ffprobe");

        Path temporaryOutputPath = Path.of(filePath.getParent().toString(), "normalized_" + filePath.getFileName().toString());
        
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(filePath.toString())
                .overrideOutputFiles(true)
                .addOutput(temporaryOutputPath.toString())
                .setFormat("mp4")
                .setVideoCodec("libx264")
                .setVideoFrameRate(30, 1)
                .setVideoResolution(1080, 1920)
                .setAudioCodec("aac")
                .setAudioChannels(2)
                .setAudioBitRate(128_000)
                .setAudioSampleRate(44_100)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();
        Files.move(temporaryOutputPath, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
