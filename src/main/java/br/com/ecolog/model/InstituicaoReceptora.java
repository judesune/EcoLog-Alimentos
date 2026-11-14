package br.com.ecolog.model;

import java.util.Objects;

public class InstituicaoReceptora {

    private Long id;
    private String razaoSocial;
    private String cnpj;
    private String endereco;
    private Double capacidadeRefrigeradaKg;
    private String telefone;

    public InstituicaoReceptora() {
    }

    public InstituicaoReceptora(Long id, String razaoSocial, String cnpj, String endereco,
                                Double capacidadeRefrigeradaKg, String telefone) {
        this.id = id;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.capacidadeRefrigeradaKg = capacidadeRefrigeradaKg;
        this.telefone = telefone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Double getCapacidadeRefrigeradaKg() {
        return capacidadeRefrigeradaKg;
    }

    public void setCapacidadeRefrigeradaKg(Double capacidadeRefrigeradaKg) {
        this.capacidadeRefrigeradaKg = capacidadeRefrigeradaKg;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof InstituicaoReceptora)) {
            return false;
        }
        InstituicaoReceptora that = (InstituicaoReceptora) outro;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | CNPJ %s | %s | Refrigerado: %.2f kg | %s",
                id, razaoSocial, cnpj, endereco, capacidadeRefrigeradaKg, telefone);
    }
}
