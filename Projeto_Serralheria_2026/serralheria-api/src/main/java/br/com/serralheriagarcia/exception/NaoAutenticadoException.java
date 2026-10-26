package br.com.serralheriagarcia.exception;

// Usuário não logado, token inválido ou expirado (HTTP 401)
public class NaoAutenticadoException extends RuntimeException {

    public NaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}
