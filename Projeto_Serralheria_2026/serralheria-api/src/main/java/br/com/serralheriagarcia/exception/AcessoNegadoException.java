package br.com.serralheriagarcia.exception;

// Usuário logado, mas sem permissão para a operação (HTTP 403)
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
