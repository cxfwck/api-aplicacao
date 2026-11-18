package br.com.cotiinformatica.api_autenticacao.services;

import br.com.cotiinformatica.api_autenticacao.dtos.CriarUsuarioRequest;
import br.com.cotiinformatica.api_autenticacao.dtos.CriarUsuarioResponse;
import br.com.cotiinformatica.api_autenticacao.entities.Perfil;
import br.com.cotiinformatica.api_autenticacao.entities.Usuario;
import br.com.cotiinformatica.api_autenticacao.repositories.PerfilRepository;
import br.com.cotiinformatica.api_autenticacao.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;


    public CriarUsuarioResponse criarUsuario(CriarUsuarioRequest request) {

        if(usuarioRepository.findByEmail(request.email()) != null) {
            throw new IllegalArgumentException("O email informado já está cadastrado. Tente outro.");
        }

        var usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(criptografarSenha(request.senha()));

        var perfil = perfilRepository.findByNome("Operador");

        if(perfil == null) {
            perfil = new Perfil();
            perfil.setNome("Operador");
            perfilRepository.save(perfil);
        }

        usuario.setPerfil(perfil);

        usuarioRepository.save(usuario);

        return new CriarUsuarioResponse(
                "Usuário cadastrado com sucesso",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().getNome(),
                LocalDateTime.now()
        );
    }


    public void autenticarUsuario() {

    }

    private String criptografarSenha(String senha) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(senha.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível no ambiente Java.", e);
        }
    }
}
