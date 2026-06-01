package com.KeyCampus.util;

import javafx.scene.control.TextField;

public class MascaraUtil {

    public static void hora(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {

            String digits = novo.replaceAll("[^\\d]", "");

            if (digits.length() > 4) digits = digits.substring(0, 4);

            String formatado = switch (digits.length()) {
                case 0 -> "";
                case 1, 2 -> digits;
                default -> digits.substring(0, 2) + ":" + digits.substring(2);
            };

            if (!formatado.equals(novo)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }
}