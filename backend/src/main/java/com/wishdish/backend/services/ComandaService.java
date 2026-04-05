package com.wishdish.backend.services;

import com.wishdish.backend.dtos.ComandaResponseDTO;
import com.wishdish.backend.models.*;
import com.wishdish.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComandaService {

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ItemComandaRepository itemComandaRepository;

    @Transactional
    public Comanda crearComanda(Long numeroMesa, List<Long> productosIds) {
        // Buscar mesa por número de mesa (no por ID)
        Mesa mesa = mesaRepository.findByNumeroMesa(numeroMesa.intValue())
                .orElseThrow(() -> new RuntimeException("Error: La mesa número " + numeroMesa + " no existe."));

        Comanda nuevaComanda = new Comanda();
        nuevaComanda.setMesa(mesa);
        nuevaComanda.setEstado(Comanda.EstadoComanda.en_cocina);

        Comanda comandaGuardada = comandaRepository.save(nuevaComanda);

        for (Long productoId : productosIds) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Error: El producto " + productoId + " no existe."));

            ItemComanda item = new ItemComanda();
            item.setComanda(comandaGuardada);
            item.setProducto(producto);
            item.setCantidad(1);
            item.setEstado(ItemComanda.EstadoItem.en_cocina);

            itemComandaRepository.save(item);
        }

        return comandaRepository.findById(comandaGuardada.getId()).get();
    }

    public List<ComandaResponseDTO> getComandasActivas() {
        List<Comanda.EstadoComanda> estadosActivos = Arrays.asList(
                Comanda.EstadoComanda.en_cocina,
                Comanda.EstadoComanda.servida
        );

        List<Comanda> comandas = comandaRepository.findByEstadoIn(estadosActivos);

        return comandas.stream()
                .map(ComandaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemComanda avanzarEstadoItem(Long itemId) {
        ItemComanda item = itemComandaRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Error: El item " + itemId + " no existe."));

        item.avanzarEstado();
        itemComandaRepository.save(item);

        verificarYAvanzarComanda(item.getComanda());

        return item;
    }

    private void verificarYAvanzarComanda(Comanda comanda) {
        boolean todosPreparados = comanda.getItems().stream()
                .allMatch(item -> item.getEstado() == ItemComanda.EstadoItem.preparado);

        if (todosPreparados && comanda.getEstado() == Comanda.EstadoComanda.en_cocina) {
            comanda.avanzarEstado();
            comandaRepository.save(comanda);
        }
    }
}
