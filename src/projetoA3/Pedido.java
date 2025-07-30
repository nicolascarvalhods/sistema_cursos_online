package projetoA3;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private Cliente cliente;
    private Endereco endereco;
    private List<ItemPedido> itens;
    private double total;
    private int pagamentoId;
    private Timestamp dataHora;

    public Pedido(int id, Cliente cliente, Endereco endereco) {
        if (cliente == null || endereco == null) {
            throw new IllegalArgumentException("Cliente e endereço não podem ser nulos.");
        }
        this.id = id;
        this.cliente = cliente;
        this.endereco = endereco;
        this.itens = new ArrayList<>();
        this.total = 0.0;
        this.pagamentoId = 0;
        this.dataHora = new Timestamp(System.currentTimeMillis());
    }

    public void adicionarItem(ItemPedido item) {
        if (item == null) throw new IllegalArgumentException("Item não pode ser nulo.");
        itens.add(item);
    }

    public void setTotal(double total) {
        if (total < 0) throw new IllegalArgumentException("Total não pode ser negativo.");
        this.total = total;
    }

    public void setPagamentoId(int pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public void salvar() {
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            if (id == 0) {
                String sql = "INSERT INTO Pedidos (cliente_id, data_hora, endereco_id, total, pagamento_id) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, cliente.getId());
                    pstmt.setTimestamp(2, dataHora);
                    if (endereco.getId() == 0) {
                        if (endereco.getRua() == null || endereco.getRua().trim().isEmpty() ||
                                endereco.getCidade() == null || endereco.getCidade().trim().isEmpty()) {
                            throw new IllegalArgumentException("Rua e cidade do endereço não podem ser vazios.");
                        }
                        String enderecoSql = "INSERT INTO Enderecos (cliente_id, rua, cidade) VALUES (?, ?, ?)";
                        try (PreparedStatement enderecoStmt = conn.prepareStatement(enderecoSql, Statement.RETURN_GENERATED_KEYS)) {
                            enderecoStmt.setInt(1, cliente.getId());
                            enderecoStmt.setString(2, endereco.getRua().trim());
                            enderecoStmt.setString(3, endereco.getCidade().trim());
                            enderecoStmt.executeUpdate();
                            ResultSet generatedKeys = enderecoStmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                endereco.setId(generatedKeys.getInt(1));
                            }
                        }
                    }
                    pstmt.setInt(3, endereco.getId());
                    pstmt.setDouble(4, total);
                    pstmt.setInt(5, pagamentoId);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = pstmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            this.id = generatedKeys.getInt(1);
                            System.out.println("Pedido criado com sucesso! ID gerado: " + this.id);

                            // Salvar itens do pedido
                            if (itens == null) {
                                System.out.println("Erro: Lista de itens é nula. Nenhum item será salvo.");
                            } else if (itens.isEmpty()) {
                                System.out.println("Aviso: Nenhum item adicionado ao pedido.");
                            } else {
                                for (ItemPedido item : itens) {
                                    String itemSql = "INSERT INTO cursos_online.ItemPedido (pedido_id, curso_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

                                    double precoUnitario = (item.getPrecoUnitario() == 0.0) ? 1.0 : item.getPrecoUnitario();
                                    try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                                        itemStmt.setInt(1, this.id);
                                        itemStmt.setInt(2, item.getCursoId());
                                        itemStmt.setInt(3, item.getQuantidade());
                                        itemStmt.setDouble(4, precoUnitario);
                                        int rowsAffectedItem = itemStmt.executeUpdate();
                                        if (rowsAffectedItem == 0) {
                                            System.out.println("Falha ao inserir item para pedido_id: " + this.id + ". Verifique as restrições do banco.");
                                        } 
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                String sql = "UPDATE Pedidos SET pagamento_id = ?, total = ?, data_hora = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, pagamentoId);
                    pstmt.setDouble(2, total);
                    pstmt.setTimestamp(3, dataHora);
                    pstmt.setInt(4, id);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Pedido atualizado com sucesso!");
                    } else {
                        System.out.println("Pedido não encontrado!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar pedido: " + e.getMessage() + " (SQL State: " + e.getSQLState() + ")");
        }
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    public double getTotal() { return total; }
    public int getPagamentoId() { return pagamentoId; }
    public Timestamp getDataHora() { return dataHora; }
    public void setDataHora(Timestamp dataHora) { this.dataHora = dataHora; }
}