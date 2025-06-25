package br.com.teste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDTO {
    @JsonProperty("produto_id")
    private String produtoId;

    private DimensionsDTO dimensoes;

    public String getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(String produtoId) {
        this.produtoId = produtoId;
    }

    public DimensionsDTO getDimensoes() {
        return dimensoes;
    }

    public void setDimensoes(DimensionsDTO dimensoes) {
        this.dimensoes = dimensoes;
    }
}