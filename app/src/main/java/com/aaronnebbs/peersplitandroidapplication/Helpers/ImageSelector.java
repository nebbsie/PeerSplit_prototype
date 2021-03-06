package com.aaronnebbs.peersplitandroidapplication.Helpers;

import com.aaronnebbs.peersplitandroidapplication.R;

import java.util.Arrays;
import java.util.regex.Pattern;


public class ImageSelector {

    private static String[] videoFormats = {"mp4", "avi", "flv", "wmv", "mov", "avchd"};
    private static String[] pictureFormats = {"jpg", "png", "gif", "jpeg", "bmp","tiff"};
    private static String[] textFormats = {"txt", "doc", "docx"};
    private static String[] pdfFormats = {"pdf"};
    private static String[] codeFormats = {"c", "java", "cpp", "py", "class"};

    // Returns an image for a certain file type.
    public static int getTypeImage(String str){
        String[] strings = str.split(Pattern.quote("."));
        if(Arrays.asList(videoFormats).contains(strings[1]) || Arrays.asList(videoFormats).contains(strings[1].toLowerCase())){
            return R.drawable.ic_videocam_black_24dp;
        }else if(Arrays.asList(pictureFormats).contains(strings[1])||Arrays.asList(pictureFormats).contains(strings[1].toLowerCase())){
            return R.drawable.ic_image_black_24dp;
        }else if(Arrays.asList(textFormats).contains(strings[1])||Arrays.asList(textFormats).contains(strings[1].toLowerCase())){
            return R.drawable.ic_mode_edit_black_24dp;
        }else if(Arrays.asList(pdfFormats).contains(strings[1])||Arrays.asList(pdfFormats).contains(strings[1].toLowerCase())){
            return R.drawable.file_pdf;
        } else if(Arrays.asList(codeFormats).contains(strings[1])||Arrays.asList(codeFormats).contains(strings[1].toLowerCase())){
            return R.drawable.code_braces;
        }
        return R.drawable.ic_cloud_white_48px;
    }

    public static boolean isImage(String str){
        String[] strings = str.split(Pattern.quote("."));
        if(Arrays.asList(pictureFormats).contains(strings[1]) || Arrays.asList(pictureFormats).contains(strings[1].toLowerCase())){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isVideo(String str){
        String[] strings = str.split(Pattern.quote("."));
        if(Arrays.asList(videoFormats).contains(strings[1]) || Arrays.asList(videoFormats).contains(strings[1].toLowerCase())){
            return true;
        }else {
            return false;
        }
    }

}
