package com.tyler58546.MesaMC.util;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    static void addFile(ZipOutputStream zipOut, String filePath, String outputPath) throws IOException {
        File input = new File(filePath);
        FileInputStream fis = new FileInputStream(input);
        ZipEntry ze = new ZipEntry(outputPath);
        zipOut.putNextEntry(ze);
        byte[] tmp = new byte[4*1024];
        int size = 0;
        while((size = fis.read(tmp)) != -1){
            zipOut.write(tmp, 0, size);
        }
        zipOut.flush();
        fis.close();
    }
    public static void zipWorld(String worldPath, String outputFileName){

        String outputPath = "maps/"+outputFileName;

        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;

        try {
            fos = new FileOutputStream(outputPath);
            zipOut = new ZipOutputStream(new BufferedOutputStream(fos));

            addFile(zipOut, worldPath+"/map.yml", "map.yml");

            String[] regionFiles = new File(worldPath+"/region").list();
            for (String rf:regionFiles) {
                addFile(zipOut, worldPath+"/region/"+rf, "region/"+rf);
            }
            zipOut.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try{
                if(fos != null) fos.close();
            } catch(Exception ex){

            }
        }
    }
}
