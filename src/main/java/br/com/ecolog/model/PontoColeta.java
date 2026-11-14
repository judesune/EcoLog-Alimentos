package br.com.ecolog.model;

import java.util.Objects;

public class PontoColeta {

    private Long id;
    private String razaoSocial;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String email;

    public PontoColeta() {
    }

    public PontoColeta(Long id, String razaoSocial, String cnpj, String endereco, String telefone, String email) {
        this.id = id;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof PontoColeta)) {
            return false;
        }
        PontoColeta that = (PontoColeta) outro;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | CNPJ %s | %s | %s | %s",
                id, razaoSocial, cnpj, endereco, telefone, email);
    }
}
