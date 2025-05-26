package Java;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Nodo {
    String partida;
    List<Integer> cuerpo;
    String firmaDigital;
    Nodo siguiente;

    public Nodo(String partidaVal, int k) {
        this.partida = partidaVal;
        this.cuerpo = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            this.cuerpo.add(random.nextInt(100000) + 1); // 1 a 100,000
        }
        this.firmaDigital = calcularFirmaDigital();
        this.siguiente = null;
    }

    private String calcularFirmaDigital() {
        // Convertir los elementos del cuerpo a string y unirlos con espacios
        String elementosCuerpoStr = this.cuerpo.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

        // Concatenar partida con la cadena de elementos del cuerpo
        String contenidoParaHash = this.partida + elementosCuerpoStr; // No necesita espacio extra si partida es el hash
                                                                      // y cuerpo ya tiene sus espacios

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(contenidoParaHash.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // Esta excepción no debería ocurrir para SHA-256, que es estándar
            throw new RuntimeException("Error al calcular SHA-256", e);
        }
    }

    // Helper para convertir array de bytes a String hexadecimal
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String generarSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular SHA-256", e);
        }
    }

    @Override
    public String toString() {
        String cuerpoPreview;
        if (cuerpo.size() > 5) {
            cuerpoPreview = cuerpo.subList(0, 5).toString() + "... (total " + cuerpo.size() + " elems)";
        } else {
            cuerpoPreview = cuerpo.toString();
        }
        return "  Partida: " + partida + "\n" +
                "  Cuerpo: " + cuerpoPreview + "\n" +
                "  Firma Digital: " + firmaDigital + "\n";
    }
}