package br.com.teste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OrderDTO {
    @JsonProperty("pedido_id")
    private int pedidoId;

    private List<ProductDTO> produtos;

    // Getters e Setters
    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public List<ProductDTO> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProductDTO> produtos) {
        this.produtos = produtos;
    }
}
