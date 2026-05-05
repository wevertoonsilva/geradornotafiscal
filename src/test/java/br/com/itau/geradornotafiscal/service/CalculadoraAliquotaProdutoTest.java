package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Item;
import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculadoraAliquotaProdutoTest {

    private final CalculadoraAliquotaProduto calculadora = new CalculadoraAliquotaProduto();

    @Test
    public void deveIsolarChamadasSequenciaisComItensDiferentes() {
        // Teste 1: Isolamento sequencial
        Item item1 = new Item("1", "Item 1", new BigDecimal("100.00"), 1);
        List<ItemNotaFiscal> resultado1 = calculadora.calcularAliquota(Arrays.asList(item1), new BigDecimal("0.10"));
        assertEquals(1, resultado1.size());
        assertEquals("1", resultado1.get(0).getIdItem());

        Item item2 = new Item("2", "Item 2", new BigDecimal("200.00"), 1);
        List<ItemNotaFiscal> resultado2 = calculadora.calcularAliquota(Arrays.asList(item2), new BigDecimal("0.10"));
        assertEquals(1, resultado2.size());
        assertEquals("2", resultado2.get(0).getIdItem());
    }

    @Test
    public void deveIsolarChamadasComQuantidadesDiferentes() {
        // Teste 2: Isolamento com quantidades diferentes
        Item item1 = new Item("1", "Item 1", new BigDecimal("100.00"), 1);
        List<ItemNotaFiscal> resultado1 = calculadora.calcularAliquota(Arrays.asList(item1), new BigDecimal("0.10"));
        assertEquals(1, resultado1.size());

        Item item2 = new Item("2", "Item 2", new BigDecimal("100.00"), 1);
        Item item3 = new Item("3", "Item 3", new BigDecimal("100.00"), 1);
        Item item4 = new Item("4", "Item 4", new BigDecimal("100.00"), 1);
        List<ItemNotaFiscal> resultado2 = calculadora.calcularAliquota(Arrays.asList(item2, item3, item4), new BigDecimal("0.10"));
        assertEquals(3, resultado2.size());
    }

    @Test
    public void deveIsolarChamadasConcorrentes() throws InterruptedException {
        // Teste 3: Isolamento concorrente
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        Callable<List<ItemNotaFiscal>> task = () -> {
            Item i1 = new Item(UUID.randomUUID().toString(), "Item", new BigDecimal("10.00"), 1);
            Item i2 = new Item(UUID.randomUUID().toString(), "Item", new BigDecimal("10.00"), 1);
            return calculadora.calcularAliquota(Arrays.asList(i1, i2), new BigDecimal("0.10"));
        };

        List<Future<List<ItemNotaFiscal>>> futures = IntStream.range(0, numThreads)
                .mapToObj(i -> executor.submit(task))
                .collect(Collectors.toList());

        for (Future<List<ItemNotaFiscal>> future : futures) {
            try {
                List<ItemNotaFiscal> resultado = future.get();
                assertEquals(2, resultado.size(), "Cada lista deve ter exatamente 2 itens");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();
    }
}
