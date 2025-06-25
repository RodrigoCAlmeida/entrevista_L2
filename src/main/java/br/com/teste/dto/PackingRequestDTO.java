package br.com.teste.dto;

import java.util.List;

public class PackingRequestDTO {
    private List<OrderDTO> pedidos;

    public List<OrderDTO> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<OrderDTO> pedidos) {
        this.pedidos = pedidos;
    }
}