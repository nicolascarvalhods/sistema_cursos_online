package projetoA3;

import java.sql.*;


public class Curso {
    private int id;
    private String nome;
    private double preco;
    private int categoriaId;

    public Curso(int id, String nome, double preco, int categoriaId) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.categoriaId = categoriaId;
    }

    public void criar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "INSERT INTO Cursos (nome, preco, categoria_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, nome);
                pstmt.setDouble(2, preco);
                pstmt.setInt(3, categoriaId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1); // Atualiza o ID com o gerado
                        System.out.println("Curso criado com sucesso! ID gerado: " + this.id);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao criar curso: " + e.getMessage());
        }
    }

    public void atualizar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "UPDATE Cursos SET nome = ?, preco = ?, categoria_id = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nome);
                pstmt.setDouble(2, preco);
                pstmt.setInt(3, categoriaId);
                pstmt.setInt(4, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Curso atualizado com sucesso!");
                } else {
                    System.out.println("Curso não encontrado!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar curso: " + e.getMessage());
        }
    }

    public static void listar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM Cursos";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("Lista de Cursos:");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Nome: " + rs.getString("nome") +
                            ", Preço: " + rs.getDouble("preco") + ", Categoria ID: " + rs.getInt("categoria_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar cursos: " + e.getMessage());
        }
    }

    public void excluir() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "DELETE FROM Cursos WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Curso excluído com sucesso!");
                } else {
                    System.out.println("Curso não encontrado!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir curso: " + e.getMessage());
        }
    }

    public void listarPedidos() {
            try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
                String sql = "SELECT p.id AS pedido_id, p.cliente_id, p.data_hora, p.total " +
                        "FROM Pedidos p " +
                        "JOIN ItemPedido ip ON p.id = ip.pedido_id " +
                        "WHERE ip.curso_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.id);
                    ResultSet rs = stmt.executeQuery();
                    System.out.println("Pedidos associados ao Curso (ID: " + this.id + ", Nome: " + this.nome + "):");
                    boolean hasPedidos = false;
                    while (rs.next()) {
                        hasPedidos = true;
                        int pedidoId = rs.getInt("pedido_id");
                        int clienteId = rs.getInt("cliente_id");
                        Timestamp dataHora = rs.getTimestamp("data_hora");
                        double total = rs.getDouble("total");
                        System.out.println("Pedido ID: " + pedidoId +
                                ", Cliente ID: " + clienteId +
                                ", Data/Hora: " + (dataHora != null ? dataHora.toString() : "Sem data") +
                                ", Total: " + total);
                    }
                    if (!hasPedidos) {
                        System.out.println("Nenhum pedido encontrado para este curso.");
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
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
}