package com.hotelaventura.config.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

// Servicio para subir y borrar fotos en el bucket publico de Supabase Storage
@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.storage.bucket:imagenes}")
    private String bucketName;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String uploadImage(MultipartFile file, String carpeta) throws IOException, InterruptedException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No se puede subir un archivo vacio");
        }

        // Armamos un nombre unico para no sobreescribir fotos existentes
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        String path = (carpeta != null && !carpeta.isBlank()) ? carpeta + "/" + fileName : fileName;

        String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .header(HttpHeaders.CONTENT_TYPE, file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return getPublicUrl(path);
        } else {
            throw new IOException("Fallo la subida a Supabase (" + response.statusCode() + "): " + response.body());
        }
    }

    public boolean deleteImage(String url) {
        try {
            String marker = "/storage/v1/object/public/" + bucketName + "/";
            if (!url.contains(marker)) {
                return false;
            }
            String path = url.substring(url.indexOf(marker) + marker.length());

            String deleteUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, path);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(deleteUrl))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            return false;
        }
    }

    public String getPublicUrl(String path) {
        return String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, path);
    }

    public String getBucketName() {
        return bucketName;
    }
}
