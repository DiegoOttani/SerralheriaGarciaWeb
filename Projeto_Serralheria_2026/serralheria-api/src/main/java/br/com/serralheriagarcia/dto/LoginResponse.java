package br.com.serralheriagarcia.dto;

import br.com.serralheriagarcia.domain.Perfil;

public record LoginResponse(String token, Integer id, String nome, String login, Perfil perfil) {
}
