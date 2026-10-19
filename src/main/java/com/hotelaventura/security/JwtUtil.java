package com.hotelaventura.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;

// Manejo de tokens JWT con HMAC-SHA256
@Component
public class JwtUtil {

    @Value("${jwt.secret:hotel_aventura_secret_key_super_segura_2026_spring_boot}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 horas (en milisegundos)
    private long expiration;

    // Crea el token con username, roles y fecha de expiracion
    public String generarToken(String username, Set<String> roles) {
        long ahora = System.currentTimeMillis();
        long expira = ahora + expiration;

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String headerBase64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));

        String rolesJson = (roles != null && !roles.isEmpty()) ? String.join("\",\"", roles) : "";
        String payloadJson = String.format(
                "{\"sub\":\"%s\",\"roles\":[\"%s\"],\"iat\":%d,\"exp\":%d}",
                username, rolesJson, ahora / 1000, expira / 1000
        );
        String payloadBase64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        String dataToSign = headerBase64 + "." + payloadBase64;
        String signature = hmacSha256(dataToSign, secret);

        return dataToSign + "." + signature;
    }

    // Valida la firma y la fecha de expiracion
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
                return false;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            if (payloadJson.contains("\"exp\":")) {
                int expIdx = payloadJson.indexOf("\"exp\":") + 6;
                int endIdx = payloadJson.indexOf("}", expIdx);
                if (endIdx == -1) endIdx = payloadJson.indexOf(",", expIdx);
                long expSeconds = Long.parseLong(payloadJson.substring(expIdx, endIdx).trim());
                if (System.currentTimeMillis() / 1000 > expSeconds) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extrae el username guardado en el token
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

    // Extrae los roles guardados en el payload del token
    public Set<String> obtenerRoles(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String[] parts = token.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int rolesIdx = payloadJson.indexOf("\"roles\":[");
            if (rolesIdx == -1) return java.util.Collections.emptySet();
            int start = rolesIdx + 9;
            int end = payloadJson.indexOf("]", start);
            if (end <= start) return java.util.Collections.emptySet();
            String rolesStr = payloadJson.substring(start, end).replace("\"", "").trim();
            if (rolesStr.isEmpty()) return java.util.Collections.emptySet();
            Set<String> set = new java.util.HashSet<>();
            for (String r : rolesStr.split(",")) {
                if (!r.trim().isEmpty()) {
                    set.add(r.trim());
                }
            }
            return set;
        } catch (Exception e) {
            return java.util.Collections.emptySet();
        }
    }

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
