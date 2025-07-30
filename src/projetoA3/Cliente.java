package projetoA3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Cliente {
    private int id;
    private String nome;
    private String email;
    private String cpf;

    public Cliente(int id, String nome, String email, String cpf) {
        this.id = id; // ID será sobrescrito pelo gerado, se 0
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
    }

    public void criar(String rua, String cidade) {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            // Verificar se o CPF já existe
            String checkSql = "SELECT COUNT(*) FROM Clientes WHERE cpf = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, cpf);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Erro: O CPF " + cpf + " já está cadastrado!");
                    return;
                }
            }

            // Inserir novo cliente
            String sql = "INSERT INTO Clientes (nome, email, cpf) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, nome);
                pstmt.setString(2, email);
                pstmt.setString(3, cpf);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                        System.out.println("Cliente criado com sucesso! ID gerado: " + this.id);

                        // Criar endereço associado
                        String enderecoSql = "INSERT INTO Enderecos (cliente_id, rua, cidade) VALUES (?, ?, ?)";
                        try (PreparedStatement enderecoStmt = conn.prepareStatement(enderecoSql, Statement.RETURN_GENERATED_KEYS)) {
                            enderecoStmt.setInt(1, this.id);
                            enderecoStmt.setString(2, rua);
                            enderecoStmt.setString(3, cidade);
                            enderecoStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao criar cliente: " + e.getMessage());
        }
    }

    public void atualizar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "UPDATE Clientes SET nome = ?, email = ?, cpf = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nome);
                pstmt.setString(2, email);
                pstmt.setString(3, cpf);
                pstmt.setInt(4, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Cliente atualizado com sucesso!");
                } else {
                    System.out.println("Cliente não encontrado!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    public static void listar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM Clientes";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("Lista de Clientes:");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Nome: " + rs.getString("nome") +
                            ", Email: " + rs.getString("email") + ", CPF: " + rs.getString("cpf"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
    }

    public void excluir() throws SQLException {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            // Excluir endereços associados (se não houver pedidos vinculados)
            String enderecoSql = "DELETE FROM Enderecos WHERE cliente_id = ?";
            try (PreparedStatement enderecoStmt = conn.prepareStatement(enderecoSql)) {
                enderecoStmt.setInt(1, id);
                enderecoStmt.executeUpdate();
            }

            // Excluir cliente
            String sql = "DELETE FROM Clientes WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Cliente com ID " + id + " não encontrado.");
                }
            }
        }
    }

    public static Cliente buscarPorId(int id) {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM Clientes WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Cliente(rs.getInt("id"), rs.getString("nome"), rs.getString("email"), rs.getString("cpf"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        }
        return null;
    }

    public static int buscarIdPorCpf(String cpf) {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT id FROM Clientes WHERE cpf = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, cpf);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por CPF: " + e.getMessage());
        }
        return -1; // Retorna -1 se o cliente não for encontrado ou ocorrer um erro
    }

    public void listarPedidos() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT p.id, p.data_hora, p.total, ip.curso_id, ip.quantidade, ip.desconto, c.nome AS nome_curso, pg.tipo AS tipo_pagamento " +
                    "FROM Pedidos p " +
                    "LEFT JOIN ItensPedido ip ON p.id = ip.pedido_id " +
                    "LEFT JOIN Cursos c ON ip.curso_id = c.id " +
                    "LEFT JOIN Pagamentos pg ON p.pagamento_id = pg.id " +
                    "WHERE p.cliente_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, this.id);
                ResultSet rs = stmt.executeQuery();
                System.out.println("Pedidos do Cliente (ID: " + this.id + ", Nome: " + this.nome + "):");
                int currentPedidoId = -1;
                while (rs.next()) {
                    int pedidoId = rs.getInt("id");
                    if (pedidoId != currentPedidoId) {
                        if (currentPedidoId != -1) {
                            System.out.println(); // Separar pedidos
                        }
                        System.out.println("ID Pedido: " + pedidoId +
                                ", Data/Hora: " + rs.getTimestamp("data_hora") +
                                ", Total: R$" + rs.getDouble("total") +
                                ", Forma de Pagamento: " + (rs.getString("tipo_pagamento") != null ? rs.getString("tipo_pagamento") : "Não pago"));
                        currentPedidoId = pedidoId;
                    }
                    String nomeCurso = rs.getString("nome_curso");
                    int quantidade = rs.getInt("quantidade");
                    double desconto = rs.getDouble("desconto");
                    if (nomeCurso != null) {
                        System.out.println("  - Curso: " + nomeCurso +
                                ", Quantidade: " + quantidade +
                                ", Desconto: " + desconto + "%");
                    }
                }
                if (currentPedidoId == -1) {
                    System.out.println("Nenhum pedido encontrado para este cliente.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
        }
    }

    // Métodos getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public static boolean existe(int id) {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Clientes WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar existência de cliente: " + e.getMessage());
        }
        return false;
    }
}