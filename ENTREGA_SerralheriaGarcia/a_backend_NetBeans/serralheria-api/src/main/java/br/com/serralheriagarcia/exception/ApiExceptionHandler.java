package br.com.serralheriagarcia.exception;

import br.com.serralheriagarcia.dto.Mensagem;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Converte as exceções da aplicação em respostas JSON {"mensagem": "..."}
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Mensagem> regraNegocio(RegraNegocioException e) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Mensagem> naoEncontrado(NaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NaoAutenticadoException.class)
    public ResponseEntity<Mensagem> naoAutenticado(NaoAutenticadoException e) {
        return resposta(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Mensagem> acessoNegado(AcessoNegadoException e) {
        return resposta(HttpStatus.FORBIDDEN, e.getMessage());
    }

    // Erros do Bean Validation (@NotBlank, @Email...): devolve a primeira mensagem
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Mensagem> validacao(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dados inválidos!");
        return resposta(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Mensagem> jsonInvalido(HttpMessageNotReadableException e) {
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos! Verifique os campos numéricos.");
    }

    // Restrição do banco (FK/UNIQUE) não tratada antes pelos services
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Mensagem> integridade(DataIntegrityViolationException e) {
        return resposta(HttpStatus.CONFLICT, "Operação não permitida: o registro está vinculado a outros dados!");
    }

    private ResponseEntity<Mensagem> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new Mensagem(mensagem));
    }
}
