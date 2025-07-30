package projetoA3;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Boleto extends Pagamento {
    private String codigo;

    public Boleto(int id, double valor, String codigo, LocalDateTime dataVencimento) {
        super(id, valor, dataVencimento);
        this.codigo = codigo;
    }

    @Override
    public int processarPagamento() throws SQLException {
            try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
                String sql = "INSERT INTO Pagamentos (tipo, valor, estado, parcelas, data_vencimento) VALUES (?, ?, 'pendente', 1, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, "boleto");
                    pstmt.setDouble(2, valor);
                    pstmt.setObject(3, dataVencimento); // Usa o dataVencimento passado pelo construtor
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = pstmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            this.id = generatedKeys.getInt(1);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            String dataVencimentoFormatada = dataVencimento.format(formatter);
                            System.out.println("Pagamento via boleto processado! ID: " + this.id + ", Vencimento: " + dataVencimento);
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

        public String getCodigo() { return codigo; }


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
                    System.out.println("Pagamento via boleto (ID: " + this.id + ") confirmado com sucesso! Data: " + LocalDateTime.now());
                } else {
                    System.out.println("Falha ao confirmar pagamento (ID: " + this.id + ").");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao confirmar pagamento: " + e.getMessage());
            throw e;
        }
    }
}