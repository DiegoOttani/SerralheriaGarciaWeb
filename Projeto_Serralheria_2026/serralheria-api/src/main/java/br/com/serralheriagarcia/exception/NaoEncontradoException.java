package br.com.serralheriagarcia.exception;

// Registro não encontrado (HTTP 404)
public class NaoEncontradoException extends RuntimeException {

    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
