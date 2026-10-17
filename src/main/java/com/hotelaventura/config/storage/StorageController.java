package com.hotelaventura.config.storage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/storage")
@CrossOrigin(origins = "*")
public class StorageController {

    private final SupabaseStorageService storageService;

    public StorageController(SupabaseStorageService storageService) {
        this.storageService = storageService;
    }

    // Endpoint para que el front nos mande la foto en form-data y le respondamos la URL
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirImagen(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "carpeta", defaultValue = "general") String carpeta) {
        try {
            String urlPublica = storageService.uploadImage(file, carpeta);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "mensaje", "Imagen subida exitosamente",
                    "url", urlPublica,
                    "bucket", storageService.getBucketName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "mensaje", "No se pudo subir la imagen a Supabase",
                    "detalle", e.getMessage()
            ));
        }
    }

    // Para borrar fotos si se cancela o elimina un registro
    @DeleteMapping("/delete")
    public ResponseEntity<?> eliminarImagen(@RequestParam("url") String url) {
        boolean eliminada = storageService.deleteImage(url);
        if (eliminada) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "mensaje", "Imagen eliminada correctamente"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "mensaje", "No se pudo eliminar la imagen"
            ));
        }
    }
}
