import projetoA3.*;
import projetoA3.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.sql.*;


public class Main {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("\n=== Sistema de Cursos Online ===");
            System.out.println("1. Cadastrar Cliente");
            System.out.println("2. Atualizar Dados Cadastrais");
            System.out.println("3. Listar Clientes");
            System.out.println("4. Excluir Cliente");
            System.out.println("5. Cadastrar Curso");
            System.out.println("6. Atualizar Curso");
            System.out.println("7. Listar Cursos");
            System.out.println("8. Excluir Curso");
            System.out.println("9. Cadastrar Categorias de Produtos");
            System.out.println("10. Atualizar Categoria");
            System.out.println("11. Listar Categorias");
            System.out.println("12. Listar Cursos por Categoria");
            System.out.println("13. Excluir Categoria");
            System.out.println("14. Realizar Pedido");
            System.out.println("15. Listar Pedidos por Curso");
            System.out.println("16. Consultar Estado do Pagamento");
            System.out.println("17. Consultar Pedidos do Cliente");
            System.out.println("18. Confirmar Pagamento via Boleto");
            System.out.println("19. Confirmar Pagamento via Cartão");
            System.out.println("0. Sair");

            System.out.print("Escolha uma opção: ");
            int escolha = scanner.nextInt();
            scanner.nextLine();

            if (escolha == 0) {
                System.out.println("Saindo...");
                scanner.close();
                System.exit(0);
            }

            switch (escolha) {
                case 1:
                    cadastrarCliente(scanner);
                    break;
                case 2:
                    atualizarDadosCadastrais(scanner);
                    break;
                case 3:
                    listarClientes(scanner);
                    break;
                case 4:
                    excluirCliente(scanner);
                    break;
                case 5:
                    cadastrarCurso(scanner);
                    break;
                case 6:
                    atualizarCurso(scanner);
                    break;
                case 7:
                    listarCursos(scanner);
                    break;
                case 8:
                    excluirCurso(scanner);
                    break;
                case 9:
                    gerenciarCategorias(scanner);
                    break;
                case 10:
                    atualizarCategoria(scanner);
                    break;
                case 11:
                    listarCategorias(scanner);
                    break;
                case 12:
                    listarCursosPorCategoria(scanner);
                    break;
                case 13:
                    excluirCategoria(scanner);
                    break;
                case 14:
                    realizarPedido(scanner);
                    break;
                case 15:
                    listarPedidosPorCurso(scanner);
                    break;
                case 16:
                    consultarEstadoPagamento(scanner);
                    break;
                case 17:
                    consultarPedidosCliente(scanner);
                    break;
                case 18:
                    confirmarPagamentoBoleto(scanner);
                    break;
                case 19:
                    confirmarPagamentoCartao(scanner);
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }


    private static void cadastrarCliente(Scanner scanner) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Rua: ");
        String rua = scanner.nextLine();
        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();
        Cliente cliente = new Cliente(0, nome, email, cpf);
        cliente.criar(rua, cidade);
        if (!cpf.matches("\\d{11}")) {
            System.out.println("CPF inválido! Deve ter 10 dígitos.");
            return;
        }
        if (!email.contains("@")) {
            System.out.println("Email inválido!");
            return;
        }
    }

    private static void atualizarDadosCadastrais(Scanner scanner) {
        System.out.print("CPF do cliente: ");
        String cpf = scanner.nextLine();

        try (Connection conn = DatabaseManager.getConnection()) {
            // Verificar se o CPF existe
            String checkSql = "SELECT id, nome, email FROM Clientes WHERE cpf = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, cpf);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nomeAtual = rs.getString("nome");
                    String emailAtual = rs.getString("email");

                    System.out.println("Cliente encontrado - ID: " + id + ", Nome: " + nomeAtual + ", Email: " + emailAtual);
                    System.out.print("Novo nome (deixe em branco para manter '" + nomeAtual + "'): ");
                    String nome = scanner.nextLine();
                    if (nome.isEmpty()) nome = nomeAtual;

                    System.out.print("Novo email (deixe em branco para manter '" + emailAtual + "'): ");
                    String email = scanner.nextLine();
                    if (email.isEmpty()) email = emailAtual;

                    System.out.print("Novo CPF (deixe em branco para manter '" + cpf + "'): ");
                    String novoCpf = scanner.nextLine();
                    if (novoCpf.isEmpty()) novoCpf = cpf;

                    // Verificar duplicidade do novo CPF, se diferente
                    if (!novoCpf.equals(cpf)) {
                        String checkDupSql = "SELECT COUNT(*) FROM Clientes WHERE cpf = ?";
                        try (PreparedStatement dupStmt = conn.prepareStatement(checkDupSql)) {
                            dupStmt.setString(1, novoCpf);
                            ResultSet dupRs = dupStmt.executeQuery();
                            if (dupRs.next() && dupRs.getInt(1) > 0) {
                                System.out.println("Erro: O CPF " + novoCpf + " já está cadastrado!");
                                return;
                            }
                        }
                    }

                    // Atualizar o cliente
                    Cliente cliente = new Cliente(id, nome, email, novoCpf);
                    cliente.atualizar();

                    // Atualização de endereço
                    System.out.print("Deseja atualizar um endereço? (s/n): ");
                    String resposta = scanner.nextLine();
                    if (resposta.equalsIgnoreCase("s")) {
                        // Buscar endereços do cliente
                        String getEnderecosSql = "SELECT id, rua, cidade FROM Enderecos WHERE cliente_id = ?";
                        try (PreparedStatement enderecosStmt = conn.prepareStatement(getEnderecosSql)) {
                            enderecosStmt.setInt(1, id);
                            ResultSet enderecosRs = enderecosStmt.executeQuery();
                            System.out.println("Endereços encontrados:");
                            int count = 1;
                            while (enderecosRs.next()) {
                                int enderecoId = enderecosRs.getInt("id");
                                String rua = enderecosRs.getString("rua");
                                String cidade = enderecosRs.getString("cidade");
                                System.out.println(count + ". ID: " + enderecoId + ", Rua: " + rua + ", Cidade: " + cidade);
                                count++;
                            }

                            System.out.print("Selecione o ID do endereço a atualizar (ou 0 para criar novo): ");
                            int enderecoId = scanner.nextInt();
                            scanner.nextLine();

                            Endereco endereco;
                            if (enderecoId == 0) {
                                // Criar novo endereço
                                System.out.print("Nova rua: ");
                                String novaRua = scanner.nextLine();
                                System.out.print("Nova cidade: ");
                                String novaCidade = scanner.nextLine();
                                endereco = new Endereco(0, id, novaRua, novaCidade); // ID 0 para novo
                            } else {
                                // Verificar se o endereço existe
                                String checkEnderecoSql = "SELECT cliente_id, rua, cidade FROM Enderecos WHERE id = ? AND cliente_id = ?";
                                try (PreparedStatement checkEndStmt = conn.prepareStatement(checkEnderecoSql)) {
                                    checkEndStmt.setInt(1, enderecoId);
                                    checkEndStmt.setInt(2, id);
                                    ResultSet checkEndRs = checkEndStmt.executeQuery();
                                    if (checkEndRs.next()) {
                                        System.out.print("Nova rua (deixe em branco para manter '" + checkEndRs.getString("rua") + "'): ");
                                        String novaRua = scanner.nextLine();
                                        if (novaRua.isEmpty()) novaRua = checkEndRs.getString("rua");

                                        System.out.print("Nova cidade (deixe em branco para manter '" + checkEndRs.getString("cidade") + "'): ");
                                        String novaCidade = scanner.nextLine();
                                        if (novaCidade.isEmpty()) novaCidade = checkEndRs.getString("cidade");

                                        endereco = new Endereco(enderecoId, id, novaRua, novaCidade);
                                    } else {
                                        System.out.println("Endereço com ID " + enderecoId + " não encontrado para este cliente!");
                                        return;
                                    }
                                }
                            }

                            // Atualizar o endereço no banco
                            endereco.atualizar();
                            System.out.println("Endereço atualizado com sucesso!");
                        }
                    }
                } else {
                    System.out.println("Cliente com CPF " + cpf + " não encontrado!");
                }
            } catch (SQLException e) {
                System.out.println("Erro ao buscar cliente: " + e.getMessage());
            }
        } catch (SQLException e) {
                System.out.println("Erro ao processar dados cadastrais: " + e.getMessage());
            }
        }

    private static void listarClientes(Scanner scanner) {
        Cliente.listar();
    }

    private static void excluirCliente(Scanner scanner) {
        System.out.print("CPF do Cliente (somente números): ");
        String cpf = scanner.nextLine().trim();

        // Verifica se o cliente existe pelo CPF
        int idCliente = Cliente.buscarIdPorCpf(cpf);
        if (idCliente == -1) {
            System.out.println("Cliente com CPF " + cpf + " não encontrado!");
            return;
        }

        // Verifica se o cliente tem endereços associados a pedidos
        try (Connection conn = projetoA3.DatabaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Pedidos p " +
                    "JOIN Enderecos e ON p.endereco_id = e.id " +
                    "WHERE e.cliente_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idCliente);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Erro: O cliente possui endereços associados a pedidos. Exclua os pedidos primeiro.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar dependências: " + e.getMessage());
            return;
        }

        // Solicita confirmação
        System.out.print("Confirmar exclusão do cliente com CPF " + cpf + "? (s/n): ");
        String confirmacao = scanner.nextLine().trim();
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Exclusão cancelada.");
            return;
        }

        // Cria o objeto Cliente e exclui
        Cliente cliente = new Cliente(idCliente, "", "", "");
        try {
            cliente.excluir();
            System.out.println("Cliente excluído com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    private static void cadastrarCurso(Scanner scanner) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Preço: ");
        double preco = scanner.nextDouble();
        scanner.nextLine(); // Consumir nova linha
        System.out.print("ID da Categoria: ");
        int categoriaId = scanner.nextInt();
        Curso curso = new Curso(0, nome, preco, categoriaId);
        curso.criar();
    }

    private static void atualizarCurso(Scanner scanner) {
        System.out.print("ID do curso: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir nova linha
        System.out.print("Novo nome: ");
        String nome = scanner.nextLine();
        System.out.print("Novo preço: ");
        double preco = scanner.nextDouble();
        scanner.nextLine(); // Consumir nova linha
        System.out.print("Novo ID da Categoria: ");
        int categoriaId = scanner.nextInt();
        Curso curso = new Curso(id, nome, preco, categoriaId);
        curso.atualizar();
    }

    private static void listarCursos(Scanner scanner) {
        Curso.listar();
    }

    private static void excluirCurso(Scanner scanner) {
        System.out.print("ID do curso a excluir: ");
        int id = scanner.nextInt();
        Curso curso = new Curso(id, "", 0.0, 0); // Placeholder
        curso.excluir();
    }

    private static void gerenciarCategorias(Scanner scanner) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        // Verifica se já existe uma categoria com o mesmo nome
        if (Categoria.existeCategoriaComNome(nome)) {
            System.out.println("Erro: Já existe uma categoria com o nome '" + nome + "'.");
            return;
        }

        // Se não houver duplicata, cria a nova categoria
        Categoria categoria = new Categoria(0, nome);
        categoria.criar();
    }

    private static void atualizarCategoria(Scanner scanner) {
        System.out.print("ID da categoria: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir nova linha
        System.out.print("Novo nome: ");
        String nome = scanner.nextLine();
        Categoria categoria = new Categoria(id, nome);
        categoria.atualizar();
    }

    private static void listarCategorias(Scanner scanner) {
        Categoria.listar();
    }

    private static void listarCursosPorCategoria(Scanner scanner) {
        System.out.print("ID da Categoria: ");
        int idCategoria = scanner.nextInt();
        Categoria categoria = new Categoria(idCategoria, "");
        categoria.listarCursos();
    }

    private static void excluirCategoria(Scanner scanner) {
        System.out.print("ID da categoria a excluir: ");
        int id = scanner.nextInt();
        Categoria categoria = new Categoria(id, ""); // Placeholder
        categoria.excluir();
    }

    private static void listarPedidosPorCurso(Scanner scanner) {
        System.out.print("ID do Curso: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        // Buscar o nome do curso no banco de dados
        String nome = "";
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT nome FROM Cursos WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nome = rs.getString("nome");
                } else {
                    System.out.println("Curso com ID " + id + " não encontrado.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar nome do curso: " + e.getMessage());
            return;
        }

        // Criar instância de Curso com o nome recuperado
        Curso curso = new Curso(id, nome, 0.0, 0);
        curso.listarPedidos();
    }

    private static void realizarPedido(Scanner scanner) {
        System.out.print("CPF do Cliente (somente números): ");
        String cpf = scanner.nextLine().trim();

        int idCliente = Cliente.buscarIdPorCpf(cpf);
        String emailCliente = "";
        if (idCliente == -1) {
            System.out.println("Cliente com CPF " + cpf + " não encontrado!");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT email FROM Clientes WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idCliente);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    emailCliente = rs.getString("email");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar e-mail: " + e.getMessage());
        }

        Cliente cliente = new Cliente(idCliente, "", "", "");

        System.out.print("Rua (deixe em branco para usar o último endereço): ");
        String rua = scanner.nextLine();
        System.out.print("Cidade (deixe em branco para usar a última cidade): ");
        String cidade = scanner.nextLine();

        if (rua.isEmpty() || cidade.isEmpty()) {
            try (Connection conn = DatabaseManager.getConnection()) {
                String sql = "SELECT rua, cidade FROM Enderecos WHERE cliente_id = ? ORDER BY id DESC LIMIT 1";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idCliente);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        rua = rs.getString("rua");
                        cidade = rs.getString("cidade");
                    } else {
                        System.out.println("Nenhum endereço cadastrado para este cliente! Insira os dados:");
                        if (rua.isEmpty()) {
                            System.out.print("Rua: ");
                            rua = scanner.nextLine();
                        }
                        if (cidade.isEmpty()) {
                            System.out.print("Cidade: ");
                            cidade = scanner.nextLine();
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Erro ao buscar endereço: " + e.getMessage());
            }
        }

        Endereco endereco = new Endereco(0, idCliente, rua, cidade);
        Pedido pedido = new Pedido(0, cliente, endereco);

        System.out.print("Quantos itens? ");
        int numItens = scanner.nextInt();
        scanner.nextLine();
        double total = 0.0;

        for (int i = 0; i < numItens; i++) {
            System.out.print("Nome do Curso: ");
            String nomeCurso = scanner.nextLine();
            int idCurso = -1;
            double precoCurso = 0.0;

            try (Connection conn = DatabaseManager.getConnection()) {
                String sql = "SELECT id, preco FROM Cursos WHERE nome = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nomeCurso);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        idCurso = rs.getInt("id");
                        precoCurso = rs.getDouble("preco");
                    } else {
                        System.out.println("Curso " + nomeCurso + " não encontrado!");
                        continue;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Erro ao buscar curso: " + e.getMessage());
                continue;
            }

            System.out.print("Quantidade: ");
            int quantidade = scanner.nextInt();
            scanner.nextLine(); 

            System.out.print("Insira um código promocional (ou deixe em branco para nenhum desconto): ");
            String codigoPromocional = scanner.nextLine().trim();
            double desconto = validarCodigoPromocional(codigoPromocional);

            ItemPedido item = new ItemPedido(0, idCurso, quantidade, desconto, precoCurso);
            pedido.adicionarItem(item);

            double valorItem = precoCurso * quantidade * (1 - desconto);
            total += valorItem;
        }

        System.out.println("Total: R$" + String.format("%.2f", total));
        pedido.setTotal(total);

        System.out.println("Deseja realizar o pagamento agora? (1 - Sim, 2 - Não)");
        int opcaoPagamento = scanner.nextInt();
        if (opcaoPagamento == 1) {
            System.out.println("1. Boleto | 2. Cartão de Crédito");
            int tipo = scanner.nextInt();
            Pagamento pagamento = null;
            scanner.nextLine(); // consumir linha

            if (tipo == 1) {
                int codigoBoleto = (int) (Math.random() * 90000000) + 10000000;
                System.out.println("Código Boleto gerado: " + codigoBoleto);
                LocalDateTime dataVencimento = LocalDateTime.now().plusDays(5);
                pagamento = new Boleto(0, total, String.valueOf(codigoBoleto), dataVencimento);
            } else {
                System.out.print("Número do Cartão: ");
                String numero = scanner.nextLine();
                System.out.print("Titular: ");
                String titular = scanner.nextLine();
                System.out.print("Parcelas: ");
                int parcelas = scanner.nextInt();
                scanner.nextLine();

                LocalDateTime dataVencimento = LocalDateTime.now()
                        .with(TemporalAdjusters.lastDayOfMonth())
                        .withDayOfMonth(15);

                if (dataVencimento.isBefore(LocalDateTime.now())) {
                    dataVencimento = dataVencimento.plusMonths(1);
                }

                pagamento = new CartaoCredito(0, total, numero, titular, parcelas, dataVencimento);
            }

            try {
                int pagamentoId = pagamento.processarPagamento();
                if (pagamentoId > 0) {
                    pedido.setPagamentoId(pagamentoId);
                    pedido.salvar();
                    System.out.println("Acesso ao curso será enviado para o e-mail: " + emailCliente);
                } else {
                    System.out.println("Falha ao processar pagamento! Pedido não salvo.");
                }
            } catch (SQLException e) {
                System.out.println("Erro ao processar pagamento: " + e.getMessage());
            }
        } else {
            pedido.salvar();
            System.out.println("Pedido realizado com sucesso! Acesso ao curso será enviado para o e-mail: " + emailCliente);
        }
    }

    private static double validarCodigoPromocional(String codigo) {
        // Mapa de códigos promocionais fixos (chave: código, valor: desconto em decimal)
        Map<String, Double> codigosPromocionais = new HashMap<>();
        codigosPromocionais.put("NOVOALUNO10", 0.10); // 10% de desconto
        codigosPromocionais.put("VERAO15", 0.15); // 15% de desconto
        codigosPromocionais.put("BLACKF20", 0.20); // 20% de desconto

        if (codigo == null || codigo.isEmpty()) {
            System.out.println("Nenhum código promocional aplicado.");
            return 0.0; // Sem desconto se o código estiver em branco
        }

        Double desconto = codigosPromocionais.get(codigo.toUpperCase());
        if (desconto != null) {
            System.out.println("Código promocional aplicado: " + (desconto * 100) + "% de desconto.");
            return desconto; // Retorna o desconto como decimal (ex.: 0.10 para 10%)
        } else {
            System.out.println("Código promocional inválido.");
            return 0.0; // Retorna 0 se o código não for encontrado
        }
    }

    private static void consultarEstadoPagamento(Scanner scanner) {
        System.out.print("ID Pagamento: ");
        int idPag = scanner.nextInt();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT estado FROM Pagamentos WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idPag);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Estado do Pagamento: " + rs.getString("estado"));
            } else {
                System.out.println("Pagamento não encontrado!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar estado do pagamento: " + e.getMessage());
        }
    }

    private static void confirmarPagamentoBoleto(Scanner scanner) {
        System.out.print("ID do Pagamento (Boleto): ");
        int idPagamento = scanner.nextInt();
        scanner.nextLine(); // Consumir nova linha

        double valor = 0.0;
        LocalDateTime dataVencimento = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            // Consultar os dados do pagamento no banco
            String sql = "SELECT valor, data_vencimento FROM Pagamentos WHERE id = ? AND tipo = 'boleto'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idPagamento);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    valor = rs.getDouble("valor");
                    dataVencimento = rs.getObject("data_vencimento", LocalDateTime.class);
                    if (dataVencimento == null) {
                        System.out.println("Data de vencimento não encontrada para o pagamento ID: " + idPagamento);
                        return;
                    }
                } else {
                    System.out.println("Pagamento com ID " + idPagamento + " não encontrado ou não é um boleto!");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar dados do pagamento: " + e.getMessage());
            return;
        }

        // Criar objeto Boleto com os dados recuperados
        Boleto boleto = new Boleto(idPagamento, valor, "", dataVencimento);
        try {
            boleto.confirmarPagamento();
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void confirmarPagamentoCartao(Scanner scanner) {
        System.out.print("ID do Pagamento (Cartão): ");
        int idPagamento = scanner.nextInt();
        scanner.nextLine();

        double valor = 0.0;
        int parcelas = 0;
        LocalDateTime dataVencimento = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            // Consultar os dados do pagamento no banco
            String sql = "SELECT valor, parcelas, data_vencimento FROM Pagamentos WHERE id = ? AND tipo = 'cartao'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idPagamento);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    valor = rs.getDouble("valor");
                    parcelas = rs.getInt("parcelas");
                    dataVencimento = rs.getObject("data_vencimento", LocalDateTime.class);
                    if (dataVencimento == null) {
                        System.out.println("Data de vencimento não encontrada para o pagamento ID: " + idPagamento);
                        return;
                    }
                } else {
                    System.out.println("Pagamento com ID " + idPagamento + " não encontrado ou não é um cartão!");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar dados do pagamento: " + e.getMessage());
            return;
        }

        // Criar objeto CartaoCredito com os dados recuperados
        String numero = "0000-0000-0000-0000"; // Valor placeholder
        String titular = "Titular Desconhecido"; // Valor placeholder
        CartaoCredito cartao = new CartaoCredito(idPagamento, valor, numero, titular, parcelas, dataVencimento);
        try {
            cartao.confirmarPagamento();
            System.out.println("Pagamento confirmado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void consultarPedidosCliente(Scanner scanner) {
        System.out.print("ID Cliente: ");
        int idCliente = scanner.nextInt();
        Cliente cliente = Cliente.buscarPorId(idCliente);
        if (cliente == null) {
            System.out.println("Cliente não encontrado!");
            return;
        }
        cliente.listarPedidos();
    }

    private static void consultarProdutosPorCategoria(Scanner scanner) {
        System.out.print("ID Categoria: ");
        int idCategoria = scanner.nextInt();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT c.id, c.nome, c.preco FROM Cursos c WHERE c.categoria_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCategoria);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Nome: " + rs.getString("nome") + ", Preço: R$" + rs.getDouble("preco"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar produtos por categoria: " + e.getMessage());
        }
    }
}