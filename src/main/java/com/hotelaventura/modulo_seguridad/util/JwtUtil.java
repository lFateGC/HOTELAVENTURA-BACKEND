package com.hotelaventura.modulo_seguridad.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;

// Manejo de tokens JWT con HMAC-SHA256 nativo sin librerias externas pesadas
@Component
public class JwtUtil {

    @Value("${jwt.secret:hotel_aventura_secret_key_super_segura_2026_spring_boot}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // dura 24 horas (en milisegundos)
    private long expiration;

    // Crea el token con el username, los roles y la fecha de vencimiento
    public String generarToken(String username, Set<String> roles) {
        long ahora = System.currentTimeMillis();
        long expira = ahora + expiration;

        // Header estandar
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String headerBase64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));

        // Payload con los datos que nos interesan
        String rolesJson = (roles != null && !roles.isEmpty()) ? String.join("\",\"", roles) : "";
        String payloadJson = String.format(
                "{\"sub\":\"%s\",\"roles\":[\"%s\"],\"iat\":%d,\"exp\":%d}",
                username, rolesJson, ahora / 1000, expira / 1000
        );
        String payloadBase64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        // Firmamos con la clave secreta
        String dataToSign = headerBase64 + "." + payloadBase64;
        String signature = hmacSha256(dataToSign, secret);

        return dataToSign + "." + signature;
    }

    // Revisa si la firma coincide y si el token aun no ha vencido
    public boolean validarToken(String token) {
        try {
            if (token == null || token.isBlank()) return false;
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String dataToSign = parts[0] + "." + parts[1];
            String expectedSignature = hmacSha256(dataToSign, secret);
            if (!expectedSignature.equals(parts[2])) {
                return false; // Firma alterada o no coincide
            }

            // Comprobamos la fecha de expiracion en el payload
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            if (payloadJson.contains("\"exp\":")) {
                int expIdx = payloadJson.indexOf("\"exp\":") + 6;
                int endIdx = payloadJson.indexOf("}", expIdx);
                if (endIdx == -1) endIdx = payloadJson.indexOf(",", expIdx);
                long expSeconds = Long.parseLong(payloadJson.substring(expIdx, endIdx).trim());
                if (System.currentTimeMillis() / 1000 > expSeconds) {
                    return false; // Ya vencio el token
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Saca el username del token para saber quien hace la peticion
    public String obtenerUsername(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String[] parts = token.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int subIdx = payloadJson.indexOf("\"sub\":\"") + 7;
            int endIdx = payloadJson.indexOf("\"", subIdx);
            return payloadJson.substring(subIdx, endIdx);
        } catch (Exception e) {
            return null;
        }
    }

    // Criptografia HMAC-SHA256 usando librerias estandar de Java
    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error calculando firma HMAC", e);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
