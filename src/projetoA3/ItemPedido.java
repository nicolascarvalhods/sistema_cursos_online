package projetoA3;

public class ItemPedido {
    private int id;
    private int cursoId;
    private int quantidade;
    private double desconto;
    private double precoUnitario;

    public ItemPedido(int id, int cursoId, int quantidade, double desconto, double precoUnitario) {
        this.id = id;
        this.cursoId = cursoId;
        this.quantidade = quantidade;
        this.desconto = desconto;
        this.precoUnitario = precoUnitario;
    }

    public int getId() { return id; }
    public int getCursoId() { return cursoId; }
    public int getQuantidade() { return quantidade; }
    public double getDesconto() { return desconto; }
    public double getPrecoUnitario() { return precoUnitario; }
}