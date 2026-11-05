package br.com.serralheriagarcia.exception;

// Violação de regra de negócio ou dado inválido (HTTP 400)
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
