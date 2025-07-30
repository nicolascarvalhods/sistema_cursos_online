package projetoA3;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CartaoCredito extends Pagamento {
    private String numero;
    private String titular;
    private int parcelas;

    public CartaoCredito(int id, double valor, String numero, String titular, int parcelas, LocalDateTime dataVencimento) {
        super(id, valor, dataVencimento);
        this.numero = numero;
        this.titular = titular;
        this.parcelas = parcelas;
    }

    @Override
    public int processarPagamento() throws SQLException {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "INSERT INTO Pagamentos (tipo, valor, estado, parcelas, data_vencimento) VALUES (?, ?, 'pendente', ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "cartao");
                pstmt.setDouble(2, valor);
                pstmt.setInt(3, parcelas);
                pstmt.setObject(4, dataVencimento);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        String dataVencimentoFormatada = dataVencimento.format(formatter);
                        System.out.println("Pagamento via cartão processado! ID: " + this.id + ", Vencimento: " + dataVencimento);
                        return this.id;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao processar pagamento: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    @Override
    public void confirmarPagamento() throws SQLException {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String checkSql = "SELECT estado FROM Pagamentos WHERE id = ? AND estado = 'pendente'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, this.id);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("Pagamento (ID: " + this.id + ") não está pendente ou não existe!");
                    return;
                }
            }

            String updateSql = "UPDATE Pagamentos SET estado = 'pago', data_pagamento = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setObject(1, LocalDateTime.now());
                pstmt.setInt(2, this.id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Pagamento via cartão (ID: " + this.id + ") confirmado com sucesso! Data: " + LocalDateTime.now());
                } else {
                    System.out.println("Falha ao confirmar pagamento (ID: " + this.id + ").");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao confirmar pagamento: " + e.getMessage());
            throw e;
        }
    }

    public String getNumero() { return numero; }
    public String getTitular() { return titular; }
    public int getParcelas() { return parcelas; }
}