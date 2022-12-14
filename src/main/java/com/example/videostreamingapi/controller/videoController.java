package com.example.videostreamingapi.controller;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.videostreamingapi.dto.VideoUploadRequest;
import com.example.videostreamingapi.dto.VideoUploadResponse;
import com.example.videostreamingapi.model.Video;
import com.example.videostreamingapi.dto.VideoRes;
import com.example.videostreamingapi.service.AwsFileService;
import com.example.videostreamingapi.service.CustomUserDetailsService;
import com.example.videostreamingapi.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.videostreamingapi.constant.VideoConstants.*;

@Controller
public class videoController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    AwsFileService awsFileService;
    @Autowired
    VideoService videoService;

    @Autowired
    CustomUserDetailsService userService;

    @RequestMapping(value = "videos",method = RequestMethod.GET)
    @ResponseBody
    public List<VideoRes> getAll(){
         var videoList=videoService.getAll();
         var res=new ArrayList<VideoRes>();
         for(var video:videoList){
             var v=new VideoRes();
             v.setTitle(video.getTitle());
             v.setViews(video.getViews());
             v.setDescription(video.getDescription());
             v.setUrl(video.getUrl());
             v.setThumbnailUrl(video.getThumbnailUrl());
             v.setChannelName(video.getUser().getChannelName());
             res.add(v);
         }
         return res;
    }

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public ResponseEntity<VideoUploadResponse> upload(Principal principal,@RequestParam("video") MultipartFile video,
    @RequestParam("thumbnail") MultipartFile thumbnail,
    @RequestParam("title") String title, @RequestParam("description") String description)
    {
        System.out.println(title+" "+description+" "+video.getContentType()+" "+thumbnail.getContentType());
//        return ResponseEntity.ok(new VideoUploadResponse("success"));
        var videoUrl=(new Date()).getTime()+".mp4";
        var thumbnailUrl=""+(new Date()).getTime()+".jpg";
        try {
            if(awsFileService.isExist(videoUrl)|| awsFileService.isExist(thumbnailUrl)){
                throw new Exception("Duplicate Key");}
        awsFileService.save(video, videoUrl);
        awsFileService.save(thumbnail, thumbnailUrl);
        var user=userService.getUserByName(principal.getName());
        videoService.save(new Video(title,description,thumbnailUrl,0,videoUrl,user));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoUploadResponse(e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new VideoUploadResponse("success"));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @RequestMapping(value = "delete/{videoname}",method = RequestMethod.GET)
    public String delete(Model model,@PathVariable String videoname){
        try {
            if(!awsFileService.isExist(videoname)){
                model.addAttribute("error","Key Does Not Exist");
                throw new Exception("Key Does Not Exist");}
            awsFileService.delete(videoname);
        }catch (Exception e){
            return "error";
        }
        model.addAttribute("message","Deleted");
        return "success";
    }

    @RequestMapping(value = "video/{videoname}" , method = RequestMethod.GET)
    public ResponseEntity<?> download(@RequestHeader(value = "Range", required = false) String rangeHeader,@PathVariable String videoname,Model model) throws IOException {
        System.out.println(rangeHeader);
        if(!awsFileService.isExist(videoname))
            return ResponseEntity.ok("error key does not exist");
        try {
            final String fileKey = videoname;
            long rangeStart = 0;
            final Long fileSize = awsFileService.findSizeByName(videoname);
            long rangeEnd =CHUNK_SIZE;
            if (rangeHeader == null) {
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).
                    contentType(MediaType.valueOf("video/mp4"))
                    .header(ACCEPT_RANGES, BYTES)
                    .header(CONTENT_LENGTH, String.valueOf(rangeEnd))
                    .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                    .header(CONTENT_LENGTH, String.valueOf(fileSize))
                    .body(readByteRangeNew(fileKey, rangeStart, rangeEnd));
            }
            String[] ranges = rangeHeader.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = rangeStart + CHUNK_SIZE;
            }
            rangeEnd = Math.min(rangeEnd, fileSize - 1);
            final byte[] data = readByteRangeNew(fileKey, rangeStart, rangeEnd);
            final String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
            HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;
            if (rangeEnd >= fileSize) {
                httpStatus = HttpStatus.OK;
            }
            return ResponseEntity.status(httpStatus)
                    .contentType(MediaType.valueOf("video/mp4"))
                    .header(ACCEPT_RANGES, BYTES)
                    .header(CONTENT_LENGTH, contentLength)
                    .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                    .body(data);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    public byte[] readByteRangeNew(String filename, long start, long end) throws IOException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, filename);
        S3ObjectInputStream s3Object = awsFileService.findByNameWithRange(filename,start,end);
        byte[] bytes = IOUtils.toByteArray(s3Object);
        return bytes;
    }

    @RequestMapping(value = "thumbnail/{thumbnailName}" , method = RequestMethod.GET)
    public ResponseEntity<?> thumbnail(@PathVariable String thumbnailName,Model model) throws IOException {
        if(!awsFileService.isExist(thumbnailName))
            return ResponseEntity.ok("error key does not exist");
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, thumbnailName);
        S3ObjectInputStream s3Object = awsFileService.findByName(thumbnailName);
        byte[] bytes = IOUtils.toByteArray(s3Object);
        String fileName = URLEncoder.encode(thumbnailName, "UTF-8").replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("image/jpg"));
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

}