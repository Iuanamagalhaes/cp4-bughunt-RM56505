package br.com.fiap.streamfiap.model;

import jakarta.persistence.Entity;

@Entity
public class Filme extends Conteudo implements Promocionavel {

    private boolean estreia;
    private static final double PRECO_ALUGUEL = 9.90;
    private static final double ADICIONAL_ESTREIA = 5.00;
    private static final double DESCONTO_PROMOCAO = 0.8;


    public Filme() {
    }

    public Filme(String titulo, String categoria, int duracaoMinutos, int classificacaoEtaria, boolean disponivel, boolean estreia) {
        super(titulo, categoria, duracaoMinutos, classificacaoEtaria, disponivel);
        this.estreia = estreia;
    }

    @Override
    public double calcularPrecoAluguel() {
        return PRECO_ALUGUEL + (estreia ? ADICIONAL_ESTREIA : 0.0);
    }

    @Override
    public double aplicarPromocao(double preco) {
        return preco * DESCONTO_PROMOCAO;
    }

    public boolean isEstreia() { return estreia; }
    public void setEstreia(boolean estreia) { this.estreia = estreia; }
}
