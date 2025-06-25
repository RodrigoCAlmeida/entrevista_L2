package br.com.teste.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import br.com.teste.dto.PackingRequestDTO;
import br.com.teste.service.PackingService;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PackingService packingService;

    @InjectMocks
    private PublicController publicController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
    }

    @Test
    void quandoChamarPackOrders_comJsonValido_deveRetornarStatus200() throws Exception {
        String requestJson = lerJsonDoArquivo("request-packing-multi-pedido.json");
        Map<String, Object> mockServiceResponse = new LinkedHashMap<>();
        mockServiceResponse.put("pedidos", Collections.emptyList()); // Resposta simples
        when(packingService.processAndPackOrders(any(PackingRequestDTO.class)))
                .thenReturn(mockServiceResponse);
        mockMvc.perform(post("/protected/packOrders") // Verifique se o path da API está correto no seu PublicController
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidos").exists());
    }

    private String lerJsonDoArquivo(String nomeArquivo) throws Exception {
        try (InputStream inputStream = PublicControllerTest.class.getResourceAsStream("/" + nomeArquivo)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Arquivo não encontrado nos resources: " + nomeArquivo);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}