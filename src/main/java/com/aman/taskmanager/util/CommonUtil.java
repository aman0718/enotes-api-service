package com.aman.taskmanager.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.aman.taskmanager.handler.GenericResponse;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtil {

    public static ResponseEntity<?> createBuildResponse(Object data, HttpStatus status) {

        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("success")
                .message("success")
                .data(data)
                .build();
        return response.create();
    }

    public static ResponseEntity<?> createBuildResponseMessage(String message, HttpStatus status) {

        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("success")
                .message(message)
                .build();
        return response.create();
    }

    public static ResponseEntity<?> createErrorResponse(Object data, HttpStatus status) {

        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("failed")
                .message("failed")
                .data(data)
                .build();

        return response.create();
    }

    public static ResponseEntity<?> createErrorResponseMessage(String message, HttpStatus status) {

        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("failed")
                .message(message)
                .build();

        return response.create();
    }

    public static String getContentType(String originalFileName) {

        String extension = FilenameUtils.getExtension(originalFileName);

        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheettml.sheet";
            case "txt":
                return "text/plan";
            case "png":
                return "image/png";
            case "jpg":
                return "application/jpg";
            default:
                return "application/octet-stream";
        }
    }

    public static String getUrl(HttpServletRequest httpRequest) {
        String hostURL = httpRequest.getRequestURL().toString();
        httpRequest.getServletPath();
        hostURL = hostURL.replace(httpRequest.getServletPath(), "");
        return hostURL;
    }

}
