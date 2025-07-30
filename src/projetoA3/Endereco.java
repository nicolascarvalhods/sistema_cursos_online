package projetoA3;

import java.sql.*;

public class Endereco {
    private int id;
    private int clienteId;
    private String rua;
    private String cidade;

    public Endereco(int id, int clienteId, String rua, String cidade) {
        this.id = id;
        this.clienteId = clienteId;
        this.rua = rua;
        this.cidade = cidade;
    }

    public void atualizar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "INSERT INTO Enderecos (cliente_id, rua, cidade) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, clienteId);
                pstmt.setString(2, rua);
                pstmt.setString(3, cidade);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                        System.out.println("Endereço criado com sucesso! Novo ID: " + this.id);
                    }
                } else {
                    System.out.println("Falha ao criar endereço!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao criar endereço: " + e.getMessage());
        }
    }
        // Getters e setters
        public int getId () {
            return id;
        }
        public void setId ( int id){
            this.id = id;
        }
        public int getClienteId () {
            return clienteId;
        }
        public void setClienteId ( int clienteId){
            this.clienteId = clienteId;
        }
        public String getRua () {
            return rua;
        }
        public void setRua (String rua){
            this.rua = rua;
        }
        public String getCidade () {
            return cidade;
        }
        public void setCidade (String cidade){
            this.cidade = cidade;
        }
    }
