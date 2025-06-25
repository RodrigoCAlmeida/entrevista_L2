package br.com.teste.service;

import br.com.teste.dto.OrderDTO;
import br.com.teste.dto.PackingRequestDTO;
import com.github.skjolber.packing.api.*;
import com.github.skjolber.packing.packer.bruteforce.BruteForcePackager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PackingService {

    private final BruteForcePackager packager;
    private final List<ContainerItem> availableContainers;

    public PackingService() {
        this.packager = BruteForcePackager.newBuilder().build();

        Container caixa1 = Container.newBuilder().withDescription("Caixa 1").withSize(30, 40, 80).withEmptyWeight(1).withMaxLoadWeight(100).build();
        Container caixa2 = Container.newBuilder().withDescription("Caixa 2").withSize(80, 50, 40).withEmptyWeight(1).withMaxLoadWeight(100).build();
        Container caixa3 = Container.newBuilder().withDescription("Caixa 3").withSize(50, 80, 60).withEmptyWeight(1).withMaxLoadWeight(100).build();

        this.availableContainers = ContainerItem.newListBuilder()
                .withContainer(caixa1)
                .withContainer(caixa2)
                .withContainer(caixa3)
                .build();
    }

    public Map<String, Object> processAndPackOrders(PackingRequestDTO request) {
        List<Map<String, Object>> processedOrders = new ArrayList<>();

        for (OrderDTO order : request.getPedidos()) {
            List<StackableItem> itemsToPack = order.getProdutos().stream()
                    .map(product -> new StackableItem(
                            Box.newBuilder()
                                    .withId(product.getProdutoId())
                                    .withSize(
                                            product.getDimensoes().getAltura(),
                                            product.getDimensoes().getLargura(),
                                            product.getDimensoes().getComprimento())
                                    .withRotate3D()
                                    .withWeight(1)
                                    .build(),
                            1))
                    .collect(Collectors.toList());

            PackagerResult result = packager.newResultBuilder()
                    .withContainers(this.availableContainers)
                    .withStackables(itemsToPack)
                    .build();

            Map<String, Object> processedOrder = new LinkedHashMap<>();
            processedOrder.put("pedido_id", order.getPedidoId());

            List<Map<String, Object>> caixasList = new ArrayList<>();
            adicionarCaixasEmbaladas(result, caixasList);
            adicionarProdutosNaoEmbalados(result, itemsToPack, caixasList);

            processedOrder.put("caixas", caixasList);
            processedOrders.add(processedOrder);
        }

        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("pedidos", processedOrders);

        return finalResponse;
    }

    private void adicionarCaixasEmbaladas(PackagerResult result, List<Map<String, Object>> listaDeCaixas) {
        for (Container container : result.getContainers()) {
            if (container.getStack() != null && !container.getStack().getPlacements().isEmpty()) {
                Map<String, Object> caixaMap = new LinkedHashMap<>();
                caixaMap.put("caixa_id", container.getDescription());
                List<String> produtosNomes = container.getStack().getPlacements().stream()
                        .map(placement -> placement.getStackable().getId())
                        .collect(Collectors.toList());
                caixaMap.put("produtos", produtosNomes);
                listaDeCaixas.add(caixaMap);
            }
        }
    }

    private void adicionarProdutosNaoEmbalados(
            PackagerResult result,
            List<StackableItem> allOriginalItems,
            List<Map<String, Object>> caixasList) {

        Set<String> packedProductIds = new HashSet<>();
        for (Container container : result.getContainers()) {
            if (container.getStack() != null) {
                for (StackPlacement placement : container.getStack().getPlacements()) {
                    packedProductIds.add(placement.getStackable().getId());
                }
            }
        }

        List<String> unpackedProductIds = allOriginalItems.stream()
                .map(item -> item.getStackable().getId())
                .filter(id -> !packedProductIds.contains(id))
                .collect(Collectors.toList());

        if (!unpackedProductIds.isEmpty()) {
            Map<String, Object> caixaNaoEmbalada = new LinkedHashMap<>();
            caixaNaoEmbalada.put("caixa_id", null);
            caixaNaoEmbalada.put("produtos", unpackedProductIds);
            caixaNaoEmbalada.put("observacao", "Produto não cabe em nenhuma caixa disponível.");
            caixasList.add(caixaNaoEmbalada);
        }
    }
}