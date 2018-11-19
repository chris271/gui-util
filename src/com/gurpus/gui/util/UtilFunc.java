package com.gurpus.gui.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class UtilFunc {

    //Zip The Files...
    public static boolean zipIt(String outputZipFileName, List<String> fileList, String outputFolder) throws IOException {
        byte[] buffer = new byte[1024];
        try (FileOutputStream fos = new FileOutputStream(outputZipFileName); ZipOutputStream zos = new ZipOutputStream(fos)) {
            Logger.info("Output to Zip: " + outputZipFileName);
            for (String file : fileList) {
                Logger.info("File Added: " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream(outputFolder + File.separator + file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    //Example Http Request Method
    private void makeHttpRequest(final String url, final List<NameValuePair> urlParameters) {
        CloseableHttpClient client = null;
        try {
            Logger.info("Making HTTP Request To URL => " + url);
            client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            // add header
            post.setHeader("User-Agent", "Mozilla/5.0");
            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse response = client.execute(post);
            Logger.info("The Request Has Finished With Response Code : "
                    + response.getStatusLine().getStatusCode());
            byte[] bytes = IOUtils.toByteArray(response.getEntity().getContent());
            Logger.info("Response = " + new String(bytes, StandardCharsets.UTF_8));
            Logger.info("Finished Writing Response.");
        } catch (Exception ex) {
            Logger.severe("Problem With Request/Response...");
            ex.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private int[] getRange(String rangeToParse) {
        final int[] ranges = {0, 0};
        if (rangeToParse.contains("-")) {
            final String[] subColumnRange = rangeToParse.split("-");
            ranges[0] = Integer.parseInt(subColumnRange[0].trim());
            ranges[1] = Integer.parseInt(subColumnRange[1].trim());
        } else {
            ranges[0] = Integer.parseInt(rangeToParse);
            ranges[1] = Integer.parseInt(rangeToParse);
        }
        return ranges;
    }

}
