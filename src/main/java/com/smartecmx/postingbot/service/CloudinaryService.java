package com.smartecmx.postingbot.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    @Value("${com.smartecmx.postingbot.util.cloudinary.logo_public_id}")
    private String logoPublicId;

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
}
