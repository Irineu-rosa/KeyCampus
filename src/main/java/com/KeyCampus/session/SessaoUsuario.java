package com.KeyCampus.session;

import com.KeyCampus.model.Usuario;

public class SessaoUsuario {

    private static Usuario usuarioAtual;

    private SessaoUsuario() {}

    public static void iniciar(Usuario usuario) {
        usuarioAtual = usuario;
    }

    public static Usuario get() {
        return usuarioAtual;
    }

    public static boolean estaLogado() {
        return usuarioAtual != null;
    }

    public static void encerrar() {
        usuarioAtual = null;
    }
}
