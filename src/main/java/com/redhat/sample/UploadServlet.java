package com.redhat.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@MultipartConfig(maxFileSize = Integer.MAX_VALUE)
@WebServlet(urlPatterns = "/upload")
public class UploadServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String fileName = null;

    for (Part part : request.getParts()) {
      response.setHeader("Content-type", "text/plain");

      String dispositionHeader = part.getHeader("content-disposition");

      if (dispositionHeader != null) {
        for (String headerSegment : dispositionHeader.split(";")) {
          headerSegment = headerSegment.trim();
          String[] segmentPair = headerSegment.split("=");

          if (segmentPair.length == 2 && segmentPair[0].equals("filename")) {
            fileName = segmentPair[1];
            break;
          }
        }
      }

      if (fileName != null) {
        InputStream is = part.getInputStream();

        try {
          MessageDigest md5 = MessageDigest.getInstance("MD5");
          ByteBuffer byteBuffer = ByteBuffer.allocate((int) part.getSize());
          byteBuffer.put(is.readAllBytes());
          md5.update(byteBuffer);
          byte[] digest = md5.digest();

          StringBuilder digestString = new StringBuilder();

          for (byte b : digest) {
            digestString.append(String.format("%02X", b));
          }

          response.getWriter().print("File " + fileName + " uploaded. Read " +
              part.getSize() + " bytes, MD5 sum : " + digestString);
          response.setStatus(200);
          return;
        } catch (NoSuchAlgorithmException e) {
          throw new RuntimeException(e);
        }
      }
    }

    response.setStatus(500);
    response.getWriter().print("No filename found. Nothing was uploaded.");
  }
}
