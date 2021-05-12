package com.example.dowload.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CreateFilename {
    @Value("${download.filepath}")
    private String FILE_PATH;

    public String getFilename(String url) {
        String reg = "[^/]*$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url);
        String filename="";
        if (matcher.find()) {
             filename= matcher.group(0);
        }
        int filenameCopyCount=1;
        while (new File(FILE_PATH+filename).isFile()){
            String filenameCopy=filename;
            filenameCopy=filenameCopy.substring(0, filenameCopy.indexOf(".")) + "_" + filenameCopyCount + filenameCopy.substring(filenameCopy.indexOf("."));
            if(!new File(FILE_PATH+filenameCopy).isFile()){
                filename = filenameCopy;
                break;
            }
            filenameCopyCount++;
        }
        return filename;
    }
}
