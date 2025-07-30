package projetoA3;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;


public class DatabaseManager {

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream is = DatabaseManager.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is == null) {
                throw new SQLException("Arquivo database.properties não encontrado!");
            }
            props.load(is);
        } catch (Exception e) {
            throw new SQLException("Erro ao carregar propriedades: " + e.getMessage());
        }
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS Clientes (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(255), email VARCHAR(255), cpf VARCHAR(14))");
                stmt.execute("CREATE TABLE IF NOT EXISTS Enderecos (id INT AUTO_INCREMENT PRIMARY KEY, cliente_id INT, rua VARCHAR(255), cidade VARCHAR(255), FOREIGN KEY (cliente_id) REFERENCES Clientes(id))");
                stmt.execute("CREATE TABLE IF NOT EXISTS Categorias (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(255))");
                stmt.execute("CREATE TABLE IF NOT EXISTS Cursos (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(255), preco DOUBLE, categoria_id INT, FOREIGN KEY (categoria_id) REFERENCES Categorias(id))");
                stmt.execute("CREATE TABLE IF NOT EXISTS Pagamentos (id INT AUTO_INCREMENT PRIMARY KEY, tipo VARCHAR(50), valor DOUBLE, estado VARCHAR(50), data_vencimento DATETIME, data_pagamento DATETIME, parcelas INT)");
                stmt.execute("CREATE TABLE IF NOT EXISTS Pedidos (id INT AUTO_INCREMENT PRIMARY KEY, cliente_id INT, data_hora DATETIME, endereco_id INT, pagamento_id INT, total DOUBLE, FOREIGN KEY (cliente_id) REFERENCES Clientes(id), FOREIGN KEY (endereco_id) REFERENCES Enderecos(id), FOREIGN KEY (pagamento_id) REFERENCES Pagamentos(id))");
                stmt.execute("CREATE TABLE IF NOT EXISTS ItensPedido (pedido_id INT, curso_id INT, quantidade INT, desconto DOUBLE, PRIMARY KEY (pedido_id, curso_id), FOREIGN KEY (pedido_id) REFERENCES Pedidos(id), FOREIGN KEY (curso_id) REFERENCES Cursos(id))");



                System.out.println("Banco de dados inicializado com sucesso!");
            } else {
                System.out.println("Falha na conexão com o banco de dados!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao inicializar banco: " + e.getMessage());
        }
    }

}