package br.com.ecolog.view;

import java.io.Console;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Leitura e escrita no terminal, com conversão e nova tentativa em caso de
 * valor inválido. Os métodos "Opcional" mantêm o valor atual quando o usuário
 * só aperta Enter (usados nas telas de edição).
 */
public class EntradaConsole {

    public static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Scanner scanner;
    private final PrintStream saida;

    public EntradaConsole(Scanner scanner, PrintStream saida) {
        this.scanner = scanner;
        this.saida = saida;
    }

    public void escrever(String texto) {
        saida.println(texto);
    }

    public void titulo(String texto) {
        saida.println();
        saida.println("=".repeat(60));
        saida.println("  " + texto);
        saida.println("=".repeat(60));
    }

    public void sucesso(String texto) {
        saida.println("[OK] " + texto);
    }

    public void erro(String texto) {
        saida.println("[ERRO] " + texto);
    }

    public String lerTexto(String rotulo) {
        while (true) {
            String valor = lerLinha(rotulo + ": ");
            if (!valor.isBlank()) {
                return valor.trim();
            }
            erro("Campo obrigatório.");
        }
    }

    public String lerTextoOpcional(String rotulo, String atual) {
        String valor = lerLinha(String.format("%s [%s]: ", rotulo, atual));
        return valor.isBlank() ? atual : valor.trim();
    }

    public int lerInteiro(String rotulo, int minimo, int maximo) {
        while (true) {
            String valor = lerLinha(rotulo + ": ");
            try {
                int numero = Integer.parseInt(valor.trim());
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
            } catch (NumberFormatException ignorado) {
                // cai na mensagem abaixo
            }
            erro(String.format("Digite um número entre %d e %d.", minimo, maximo));
        }
    }

    public Long lerId(String rotulo) {
        while (true) {
            String valor = lerLinha(rotulo + ": ");
            try {
                long numero = Long.parseLong(valor.trim());
                if (numero > 0) {
                    return numero;
                }
            } catch (NumberFormatException ignorado) {
                // cai na mensagem abaixo
            }
            erro("Digite um código numérico válido.");
        }
    }

    public Long lerIdOpcional(String rotulo, Long atual) {
        while (true) {
            String valor = lerLinha(String.format("%s [%d]: ", rotulo, atual));
            if (valor.isBlank()) {
                return atual;
            }
            try {
                long numero = Long.parseLong(valor.trim());
                if (numero > 0) {
                    return numero;
                }
            } catch (NumberFormatException ignorado) {
                // cai na mensagem abaixo
            }
            erro("Digite um código numérico válido.");
        }
    }

    public Double lerDecimal(String rotulo) {
        while (true) {
            Double numero = converterDecimal(lerLinha(rotulo + ": "));
            if (numero != null) {
                return numero;
            }
            erro("Digite um número (ex.: 12,5).");
        }
    }

    public Double lerDecimalOpcional(String rotulo, Double atual) {
        while (true) {
            String valor = lerLinha(String.format("%s [%s]: ", rotulo, formatarDecimal(atual)));
            if (valor.isBlank()) {
                return atual;
            }
            Double numero = converterDecimal(valor);
            if (numero != null) {
                return numero;
            }
            erro("Digite um número (ex.: 12,5).");
        }
    }

    public LocalDate lerData(String rotulo) {
        while (true) {
            LocalDate data = converterData(lerLinha(rotulo + " (dd/mm/aaaa): "));
            if (data != null) {
                return data;
            }
            erro("Data inválida. Use o formato dd/mm/aaaa.");
        }
    }

    public LocalDate lerDataOpcional(String rotulo, LocalDate atual) {
        while (true) {
            String valor = lerLinha(String.format("%s (dd/mm/aaaa) [%s]: ", rotulo, formatarData(atual)));
            if (valor.isBlank()) {
                return atual;
            }
            LocalDate data = converterData(valor);
            if (data != null) {
                return data;
            }
            erro("Data inválida. Use o formato dd/mm/aaaa.");
        }
    }

    public boolean confirmar(String pergunta) {
        while (true) {
            String valor = lerLinha(pergunta + " (s/n): ").trim().toLowerCase();
            if (valor.equals("s") || valor.equals("sim")) {
                return true;
            }
            if (valor.equals("n") || valor.equals("nao") || valor.equals("não")) {
                return false;
            }
            erro("Responda s ou n.");
        }
    }

    /**
     * Lê uma senha (pode ser vazia). No terminal interativo os caracteres não
     * aparecem na tela; com entrada redirecionada, lê a linha normalmente.
     */
    public String lerSenha(String rotulo) {
        Console terminal = System.console();
        if (terminal == null) {
            return lerLinha(rotulo + ": ");
        }
        char[] senha = terminal.readPassword("%s: ", rotulo);
        if (senha == null) {
            throw new EntradaEncerradaException();
        }
        String valor = new String(senha);
        Arrays.fill(senha, ' ');
        return valor;
    }

    public void pausar() {
        lerLinha("Pressione Enter para continuar...");
    }

    public static String formatarData(LocalDate data) {
        return data == null ? "-" : data.format(FORMATO_DATA);
    }

    public static String formatarDecimal(Double valor) {
        return valor == null ? "-" : String.format("%.2f", valor);
    }

    private String lerLinha(String prompt) {
        saida.print(prompt);
        saida.flush();
        if (!scanner.hasNextLine()) {
            throw new EntradaEncerradaException();
        }
        // Remove o BOM que alguns terminais/redirecionamentos inserem no início da entrada
        return scanner.nextLine().replace("﻿", "");
    }

    private static Double converterDecimal(String valor) {
        try {
            double numero = Double.parseDouble(valor.trim().replace(',', '.'));
            return Double.isFinite(numero) ? numero : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static LocalDate converterData(String valor) {
        try {
            return LocalDate.parse(valor.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
