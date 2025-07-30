package projetoA3;
import java.sql.*;



public class Categoria {
    private int id;
    private String nome;

    public Categoria(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public void criar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "INSERT INTO Categorias (nome) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, nome);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                        System.out.println("Categoria criada com sucesso! ID gerado: " + this.id);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao criar categoria: " + e.getMessage());
        }
    }

    public void atualizar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "UPDATE Categorias SET nome = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nome);
                pstmt.setInt(2, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Categoria atualizada com sucesso!");
                } else {
                    System.out.println("Categoria não encontrada!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar categoria: " + e.getMessage());
        }
    }

    public static void listar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM Categorias";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("Lista de Categorias:");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Nome: " + rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar categorias: " + e.getMessage());
        }
    }

    public void excluir() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "DELETE FROM Categorias WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Categoria excluída com sucesso!");
                } else {
                    System.out.println("Categoria não encontrada!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir categoria: " + e.getMessage());
        }
    }

    public void listarCursos() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            // Buscar o nome da categoria com base no id
            String sqlNome = "SELECT nome FROM Categorias WHERE id = ?";
            String nomeCategoria = "";
            try (PreparedStatement stmtNome = conn.prepareStatement(sqlNome)) {
                stmtNome.setInt(1, this.id);
                ResultSet rsNome = stmtNome.executeQuery();
                if (rsNome.next()) {
                    nomeCategoria = rsNome.getString("nome");
                } else {
                    System.out.println("Categoria com ID " + this.id + " não encontrada.");
                    return;
                }
            }

            // Listar os cursos associados
            String sqlCursos = "SELECT id, nome FROM Cursos WHERE categoria_id = ?";
            try (PreparedStatement stmtCursos = conn.prepareStatement(sqlCursos)) {
                stmtCursos.setInt(1, this.id);
                ResultSet rs = stmtCursos.executeQuery();
                System.out.println("Cursos da Categoria (ID: " + this.id + ", Nome: " + nomeCategoria + "):");
                boolean hasCursos = false;
                while (rs.next()) {
                    hasCursos = true;
                    System.out.println("ID: " + rs.getInt("id") + ", Nome: " + rs.getString("nome"));
                }
                if (!hasCursos) {
                    System.out.println("Nenhum curso encontrado para esta categoria.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar cursos: " + e.getMessage());
        }
    }

    public static boolean existeCategoriaComNome(String nome) {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Categorias WHERE LOWER(nome) = LOWER(?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nome);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar categoria: " + e.getMessage());
        }
        return false;
    }

    // Métodos getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}