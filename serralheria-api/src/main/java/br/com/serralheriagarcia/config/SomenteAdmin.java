package br.com.serralheriagarcia.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Marca métodos (ou controllers inteiros) que apenas usuários com perfil ADMIN podem acessar
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SomenteAdmin {
}
