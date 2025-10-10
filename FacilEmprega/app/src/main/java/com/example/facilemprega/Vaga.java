package com.example.facilemprega;

public class Vaga {
    private String id;
    private String nomeEmpresa;
    private String cargo;
    private Double salario; // MUDANÇA: de double para Double

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

    public Double getSalario() { // MUDANÇA: retorna Double
        // Retorna 0.0 se o salário for nulo para evitar quebrar o app depois
        return salario == null ? 0.0 : salario;
    }
    public void setSalario(Double salario) { // MUDANÇA: aceita Double
        this.salario = salario;
    }
}