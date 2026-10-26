package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.exception.RegraNegocioException;

// Validação e formatação de CPF, CNPJ e telefone (regras herdadas da versão desktop)
public final class Documentos {

    private Documentos() {
    }

    public static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    // 000.000.000-00
    public static String formatarCpf(String valor) {
        String d = somenteDigitos(valor);
        if (d.length() != 11) {
            throw new RegraNegocioException("CPF inválido! Digite 11 números.");
        }
        return d.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    // 00.000.000/0000-00
    public static String formatarCnpj(String valor) {
        String d = somenteDigitos(valor);
        if (d.length() != 14) {
            throw new RegraNegocioException("CNPJ inválido! Digite 14 números.");
        }
        return d.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    // Aceita CPF (11 dígitos) ou CNPJ (14 dígitos)
    public static String formatarCpfCnpj(String valor) {
        String d = somenteDigitos(valor);
        if (d.length() == 11) {
            return formatarCpf(d);
        }
        if (d.length() == 14) {
            return formatarCnpj(d);
        }
        throw new RegraNegocioException("CPF/CNPJ inválido! Digite 11 ou 14 números.");
    }

    // (00) 0000-0000 ou (00) 00000-0000
    public static String formatarTelefone(String valor) {
        String d = somenteDigitos(valor);
        if (d.length() == 10) {
            return d.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        if (d.length() == 11) {
            return d.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        throw new RegraNegocioException("Telefone inválido! Digite DDD + número (10 ou 11 dígitos).");
    }
}
