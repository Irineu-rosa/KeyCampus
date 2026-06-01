package com.KeyCampus.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class SenhaUtil {

    private static final int SALT_BYTES = 16;

    public static String hashear(String senha) {
        try {
            byte[] salt = new byte[SALT_BYTES];
            new SecureRandom().nextBytes(salt);
            byte[] hash = digest(salt, senha);
            return Base64.getEncoder().encodeToString(salt)
                    + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash de senha", e);
        }
    }

    public static boolean verificar(String senha, String hashArmazenado) {
        try {
            String[] partes = hashArmazenado.split(":");
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
            byte[] hashTentativa = digest(salt, senha);
            return MessageDigest.isEqual(hashEsperado, hashTentativa);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] digest(byte[] salt, String senha) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        return md.digest(senha.getBytes("UTF-8"));
    }
}
