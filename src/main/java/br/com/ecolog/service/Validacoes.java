package br.com.ecolog.service;

import java.util.List;
import java.util.regex.Pattern;

/** Validações de formato reutilizadas pelos serviços de cadastro. */
final class Validacoes {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validacoes() {
    }

    static void texto(List<String> erros, String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            erros.add("Informe " + campo + ".");
        } else if (valor.length() > tamanhoMaximo) {
            erros.add(primeiraMaiuscula(campo) + " deve ter no máximo " + tamanhoMaximo + " caracteres.");
        }
    }

    /** Aceita CNPJ com ou sem pontuação, desde que tenha 14 dígitos. */
    static void cnpj(List<String> erros, String cnpj) {
        String digitos = somenteDigitos(cnpj);
        if (digitos.length() != 14 || digitos.chars().distinct().count() == 1) {
            erros.add("CNPJ inválido: informe os 14 dígitos (ex.: 11.222.333/0001-81).");
        }
    }

    static void email(List<String> erros, String email) {
        texto(erros, email, "o e-mail", 100);
        if (email != null && !email.isBlank() && !EMAIL.matcher(email).matches()) {
            erros.add("E-mail inválido.");
        }
    }

    static void telefone(List<String> erros, String telefone) {
        texto(erros, telefone, "o telefone", 20);
        int quantidadeDigitos = somenteDigitos(telefone).length();
        if (telefone != null && !telefone.isBlank() && (quantidadeDigitos < 10 || quantidadeDigitos > 13)) {
            erros.add("Telefone inválido: informe DDD e número.");
        }
    }

    /** Formata o CNPJ no padrão 00.000.000/0000-00 para gravar sempre igual. */
    static String formatarCnpj(String cnpj) {
        String d = somenteDigitos(cnpj);
        if (d.length() != 14) {
            return cnpj;
        }
        return String.format("%s.%s.%s/%s-%s",
                d.substring(0, 2), d.substring(2, 5), d.substring(5, 8), d.substring(8, 12), d.substring(12));
    }

    static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private static String primeiraMaiuscula(String campo) {
        String semArtigo = campo.replaceFirst("^(o|a) ", "");
        return Character.toUpperCase(semArtigo.charAt(0)) + semArtigo.substring(1);
    }
}
