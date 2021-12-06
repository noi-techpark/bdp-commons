package it.bz.odh.spreadsheets.utils;

import java.io.File;

import org.springframework.stereotype.Component;


/**
 * Images on Sharepoint might be saved in different formats like PDF, JPEG,
 * SVG etc. So a conversion to PNG is needed to have a unified format.
 */
@Component
public class ImageUtil {

    
    private File convertToPng(File file, String name) {
        return null;
    }

    private String guessFiletype(String name) {
        return "";
    }
}
