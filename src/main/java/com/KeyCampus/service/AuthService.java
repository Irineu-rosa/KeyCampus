package com.KeyCampus.service;

import com.KeyCampus.dao.UsuarioDao;
import com.KeyCampus.model.Usuario;
import com.KeyCampus.util.SenhaUtil;

public class AuthService {

    private final UsuarioDao usuarioDao = new UsuarioDao();

    public enum ResultadoLogin {
        SUCESSO,
        PRIMEIRO_ACESSO,
        CREDENCIAL_INVALIDA
    }

    public ResultadoLogin login(String nome, String matricula) {
        Usuario usuario = usuarioDao.buscarPorMatricula(matricula);

        if (usuario == null) return ResultadoLogin.CREDENCIAL_INVALIDA;

        if (!usuario.getNome().equalsIgnoreCase(nome.trim())) {
            return ResultadoLogin.CREDENCIAL_INVALIDA;
        }

        if (usuario.isPrimeiroAcesso() || usuario.getSenhaHash() == null) {
            return ResultadoLogin.PRIMEIRO_ACESSO;
        }

        return ResultadoLogin.SUCESSO;
    }

    public Usuario autenticar(String matricula, String senha) {
        Usuario usuario = usuarioDao.buscarPorMatricula(matricula);
        if (usuario == null) return null;
        if (!SenhaUtil.verificar(senha, usuario.getSenhaHash())) return null;
        return usuario;
    }

    public void definirSenha(String matricula, String novaSenha) {
        Usuario usuario = usuarioDao.buscarPorMatricula(matricula);
        if (usuario == null) throw new RuntimeException("Usuário não encontrado");
        String hash = SenhaUtil.hashear(novaSenha);
        usuarioDao.atualizarSenha(usuario.getId(), hash);
    }

    public void resetarSenha(Long usuarioId) {
        usuarioDao.resetarSenha(usuarioId);
    }
}
