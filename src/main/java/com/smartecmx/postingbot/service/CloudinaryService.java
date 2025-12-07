package com.smartecmx.postingbot.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.transformation.TextLayer;
import com.cloudinary.utils.ObjectUtils;
import com.smartecmx.postingbot.common.CommonMethod;
import com.smartecmx.postingbot.common.OverlayConfig;
import com.smartecmx.postingbot.exception.CloudinaryException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    @Value("${com.smartecmx.postingbot.util.cloudinary.logo_public_id}")
    private String logoPublicId;

    @Value("${com.smartecmx.postingbot.util.cloudinary.cloud_name}")
    private String cloudName;

    private final Cloudinary cloudinary;

    public String uploadAndTransformMemeFromUrl(String imgflipUrl, String publicId) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(imgflipUrl, ObjectUtils.asMap(
            "public_id", publicId,
            "folder", "memes",
            "overwrite", true,
            "type", "upload",
            "transformation", new Transformation()
                .width(1080)
                .height(1080)
                .crop("pad")
                .background("auto")
                .chain()
                .overlay(logoPublicId)
                .gravity("south_east")
                .width(150)
                .opacity(80)
                .flags("relative")
                .effect("brightness:10")
        ));

        return uploadResult.get("secure_url").toString();
    }

    public void listFolders() throws Exception {
        Map result = cloudinary.api().rootFolders(ObjectUtils.emptyMap());
        List<Map<String, String>> folders = (List<Map<String, String>>) result.get("folders");

        System.out.println("📂 Carpetas raíz:");
        for (Map<String, String> folder : folders) {
            System.out.println("- " + folder.get("name") + " (path: " + folder.get("path") + ")");
        }
    }

    public void downloadRandomItemFromFolder(String sourceFolderName, String destinationFolder, String name ) throws Exception {

        ApiResponse result = cloudinary.search()
            .expression("folder:\"" + sourceFolderName + "\"")
            .maxResults(100)
            .execute();

        List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");

        if (resources == null || resources.isEmpty()) {
            throw new CloudinaryException("No items found in folder: " + sourceFolderName);
        }

        List<String> urls = resources.stream()
                .map(resource -> (String) resource.get("secure_url"))
                .collect(Collectors.toList());

        Random random = new Random();
        CommonMethod.downloadFile(destinationFolder, urls.get(random.nextInt(urls.size())), name);
        log.info("File downloaded from Cloudinary: " + destinationFolder + "/" + name);
    }

    public String getSmartecLogoUrl(){
        String extension = "png";
        String transform = "w_150,o_80,e_brightness:10";
        return String.format("https://res.cloudinary.com/%s/image/upload/%s/%s.%s", cloudName, transform, logoPublicId, extension);
    }

    public String uploadVideo(Path videoPath) throws Exception {
        File videoFile = videoPath.toFile();
        Map <String, Object> uploadResult = cloudinary.uploader().uploadLarge(videoFile, ObjectUtils.asMap(
                "resource_type", "video",
                "folder", "curiousFacts",
                "public_id", videoFile.getName().replace(".mp4", "")
        ));
    return uploadResult.get("secure_url").toString();
    }

    public Map<String, String> getRandomItemFromFolder(String folderName) throws Exception {

        ApiResponse result = cloudinary.search()
            .expression("folder:\"" + folderName + "\"")
            .maxResults(100)
            .execute();
        List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");
        
        if (resources == null || resources.isEmpty()) {
            throw new CloudinaryException("No items found in folder: " + folderName);
        }

        Random random = new Random();
        Map<String, Object> resource = resources.get(random.nextInt(resources.size()));
        return resource.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof String)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> (String) entry.getValue()
                ));
    }
    
public String generateTechnicalTipUrl(String publicIdTemplate, String textTip, OverlayConfig tipConfig, String textCTA, OverlayConfig ctaConfig) {

    TextLayer tipLayer = new TextLayer()
            .fontFamily(tipConfig.getFont())
            .fontSize(tipConfig.getFontSize())
            .fontWeight(tipConfig.getWeight())
            .textAlign(tipConfig.getAlign())
            .text(textTip);

    TextLayer ctaLayer = new TextLayer()
            .fontFamily(ctaConfig.getFont())
            .fontSize(ctaConfig.getFontSize())
            .fontWeight(ctaConfig.getWeight())
            .textAlign(ctaConfig.getAlign())
            .text(textCTA);

    Transformation transformation = new Transformation()
            .quality("auto")
            .fetchFormat("auto")

            // TIP
            .overlay(tipLayer)
            .color(tipConfig.getColorHex())
            .gravity(tipConfig.getGravity())
            .x(tipConfig.getX())
            .y(tipConfig.getY())
            .width(tipConfig.getMaxWidth())
            .crop("fit")
            .flags("layer_apply")
            .chain()

            // CTA
            .overlay(ctaLayer)
            .color(tipConfig.getColorHex())
            .gravity(ctaConfig.getGravity())
            .x(ctaConfig.getX())
            .y(ctaConfig.getY())
            .width(ctaConfig.getMaxWidth())
            .crop("fit")
            .flags("layer_apply");

    String finalUrl = cloudinary.url()
            .transformation(transformation)
            .generate(publicIdTemplate);

    return finalUrl;
}



}
