package com.example.facilemprega.model;

public class Vaga {
    private String id;
    private String nomeEmpresa;
    private String cargo;
    private Double salario;
    private String link; // CAMPO ADICIONADO

    // Construtor vazio
    public Vaga() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }
    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Double getSalario() {
        return salario == null ? 0.0 : salario;
    }
    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public String getLink() { return link; } // GETTER ADICIONADO
    public void setLink(String link) { this.link = link; } // SETTER ADICIONADO
}