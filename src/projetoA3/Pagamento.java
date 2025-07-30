package projetoA3;

import java.sql.SQLException;
import java.time.LocalDateTime;

public abstract class Pagamento {
    protected int id;
    protected double valor;
    protected LocalDateTime dataVencimento;

    public Pagamento(int id, double valor, LocalDateTime dataVencimento) {
        this.id = id;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
    }

    public abstract int processarPagamento() throws SQLException;

    public abstract void confirmarPagamento() throws SQLException;

    public int getId() { return id; }
    public double getValor() { return valor; }
    public LocalDateTime getDataVencimento() { return dataVencimento; }

}