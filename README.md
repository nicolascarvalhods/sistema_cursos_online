# 💻 API de Vendas de Cursos Online

Este é um projeto Java desenvolvido como trabalho final de disciplina na Universidade. A proposta era criar uma **API genérica de vendas**, e eu optei por um modelo voltado à **venda de cursos online**, inspirado em plataformas como **Alura** e **Hashtag Treinamentos**.

## 🚀 Objetivo

Desenvolver um sistema completo com **operações CRUD** (Create, Read, Update, Delete) e funcionalidades adicionais de acordo com regras de negócio definidas por mim. A aplicação permite:

- Cadastro e gerenciamento de clientes e endereços
- Gerenciamento de categorias e cursos
- Realização de pedidos com múltiplos itens
- Pagamentos por **boleto bancário** ou **cartão de crédito**
- Aplicação de códigos promocionais
- Consulta de pedidos e estados de pagamento

## 🛠️ Tecnologias Utilizadas

- **Java (JDK 17)**  
- **MySQL**  
- JDBC para conexão com o banco  
- **Paradigma orientado a objetos**  
- Console como interface principal (via `Scanner`)

## 🧩 Estrutura do Projeto

O projeto é composto por diversas classes principais, entre elas:

- `Cliente`, `Endereco`, `Categoria`, `Curso`
- `Pedido`, `ItemPedido`, `Pagamento` (abstrata), `Boleto`, `CartaoCredito`
- `DatabaseManager` (responsável pela conexão com o MySQL)
- `Main` (executável com menu interativo)

## 🗃️ Banco de Dados

O sistema cria automaticamente as tabelas necessárias ao iniciar. Os dados são persistidos em um banco **MySQL**, com suporte a integridade referencial via **chaves estrangeiras**.

**Arquivo de configuração:**  
`database.properties` → contém as credenciais de acesso ao banco.

## 🧠 Regras de Negócio

- Verificação de CPF e e-mail duplicados
- Validação de código promocional
- Associação entre cliente e endereço, pedidos e pagamentos
- Pagamentos precisam ser confirmados após a criação

## 🎯 Como executar

1. Configure seu banco de dados MySQL com o script `database.properties`.
2. Compile as classes com `javac`.
3. Execute o arquivo `Main.java` para iniciar o menu.

## 📌 Exemplo de Fluxo

1. Cadastro de um novo cliente  
2. Adição de endereço  
3. Criação de curso e categoria  
4. Realização de pedido com múltiplos cursos  
5. Escolha do método de pagamento  
6. Confirmação de pagamento posterior (via boleto/cartão)

## 📚 Inspiração

Inspirado nos modelos de negócio da **Alura** e **Hashtag Treinamentos**, focado em aprendizado prático e realista de sistemas de vendas.

## 🤝 Contribuição

Este projeto é pessoal e acadêmico, mas fico feliz com feedbacks ou sugestões.

---

### Autor

**Nícolas de Carvalho da Silva**  
Estudante de [Ciência da Computação] — [Uniritter - Zona Sul]  
[LinkedIn](https://www.linkedin.com/in/n%C3%ADcolas-de-carvalho/) • [GitHub](https://github.com/nicolascarvalhods)
