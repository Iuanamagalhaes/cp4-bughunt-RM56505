package br.com.fiap.streamfiap.exception;

public class ConteudoInvalidoException extends RuntimeException {
    public ConteudoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
