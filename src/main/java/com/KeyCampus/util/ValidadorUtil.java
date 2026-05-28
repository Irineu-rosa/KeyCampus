package com.KeyCampus.util;

import java.util.ArrayList;
import java.util.List;

public class ValidadorUtil {

    private final List<String> erros = new ArrayList<>();

    public static ValidadorUtil de() {
        return new ValidadorUtil();
    }

    public ValidadorUtil campo(String valor, String mensagem) {
        if (valor == null || valor.isBlank())
            erros.add(mensagem);
        return this;
    }

    public ValidadorUtil combo(Object valor, String mensagem) {
        if (valor == null)
            erros.add(mensagem);
        return this;
    }

    public boolean validar() {
        if (erros.isEmpty()) return true;
        AlertUtil.atencao(erros.get(0));
        return false;
    }

    public boolean validarTodos() {
        if (erros.isEmpty()) return true;
        AlertUtil.atencao(String.join("\n", erros));
        return false;
    }
}