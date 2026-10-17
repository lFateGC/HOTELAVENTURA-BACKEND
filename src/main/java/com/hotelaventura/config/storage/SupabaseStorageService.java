package com.hotelaventura.config.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url:https://tu-proyecto.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key:tu-key}")
    private String supabaseKey;

    @Value("${supabase.storage.bucket:imagenes}")
    private String bucketName;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Sube la foto al bucket y nos regresa la URL publica directa
    public String uploadImage(MultipartFile file, String carpeta) throws IOException, InterruptedException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se envio ningun archivo para subir");
        }

        // Sacamos la extension original (.png, .jpg, etc)
        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Le asignamos un UUID para evitar sobreescribir archivos con el mismo nombre
        String subCarpeta = (carpeta != null && !carpeta.isBlank()) ? carpeta.trim().replaceAll("[^a-zA-Z0-9_-]", "") + "/" : "";
        String uniquePath = subCarpeta + UUID.randomUUID() + extension;

        String cleanUrl = supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;
        URI uploadUri = URI.create(cleanUrl + "/storage/v1/object/" + bucketName + "/" + uniquePath);

        // Hacemos el POST directo a la API de Supabase Storage con HttpClient nativo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uploadUri)
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apiKey", supabaseKey)
                .header("Content-Type", file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            // Retornamos el link publico para que el front lo use directamente en <img src=... />
            return cleanUrl + "/storage/v1/object/public/" + bucketName + "/" + uniquePath;
        } else {
            throw new RuntimeException("Error de Supabase al subir archivo: " + response.body());
        }
    }

    // Para borrar la foto del bucket si se elimina un producto o habitacion
    public boolean deleteImage(String publicUrl) {
        try {
            if (publicUrl == null || publicUrl.isBlank()) return false;
            String cleanUrl = supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;
            String prefix = cleanUrl + "/storage/v1/object/public/" + bucketName + "/";
            if (!publicUrl.startsWith(prefix)) {
                return false;
            }
            String path = publicUrl.substring(prefix.length());
            URI deleteUri = URI.create(cleanUrl + "/storage/v1/object/" + bucketName + "/" + path);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(deleteUri)
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apiKey", supabaseKey)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            return false;
        }
    }

    public String getBucketName() {
        return bucketName;
    }
}
