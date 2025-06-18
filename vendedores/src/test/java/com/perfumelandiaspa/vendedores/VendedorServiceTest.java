package com.perfumelandiaspa.vendedores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.perfumelandiaspa.vendedores.Model.Vendedor;
import com.perfumelandiaspa.vendedores.Model.Entity.VendedorEntity;
import com.perfumelandiaspa.vendedores.Repository.VendedorRepository;
import com.perfumelandiaspa.vendedores.Service.VendedorService;

public class VendedorServiceTest {

    @Mock
    private VendedorRepository vendedorRepository;

    @InjectMocks
    private VendedorService vendedorService;

    private Vendedor vendedor;
    private VendedorEntity vendedorEntity;

    @BeforeEach
    void setUp() {
        vendedor = new Vendedor();
        vendedor.setMetaMensual(1000000.0);
        vendedor.setSucursal("Sucursal Central");

        vendedorEntity = new VendedorEntity();
        vendedorEntity.setIdVendedor(1);
        vendedorEntity.setMetaMensual(1000000.0);
        vendedorEntity.setSucursal("Sucursal Central");
    }

    //Test para crear un vendedor --dara bueno
    @Test
    @DisplayName("Test para crear vendedor exitosamente")
    void testCrearVendedorExitoso(){
        when(vendedorRepository.save(any(VendedorEntity.class))).thenReturn(vendedorEntity);

        String resultado = vendedorService.crearVendedor(vendedor);

        assertEquals("Vendedor creado con éxito", resultado);
        verify(vendedorRepository).save(any(VendedorEntity.class));
    }

    //Test para crear vendedor --aca dara error
    @Test
    @DisplayName("Test para crear vendedor con error")
    void testCrearVendedorConError(){
        when(vendedorRepository.save(any(VendedorEntity.class))).thenThrow(new RuntimeException("Error de base de datos"));
   
        String resultado = vendedorService.crearVendedor(vendedor);

        assertEquals("Error al crear Vendedor: Error de base de datos", resultado);
    }

    // Test para buscarClienteID - Caso exitoso
    @Test
    @DisplayName("Test para buscar vendedor por ID exitosamente")
    void testBuscarClienteIDExitoso() {
        when(vendedorRepository.findById(1)).thenReturn(Optional.of(vendedorEntity));
        
        VendedorEntity resultado = vendedorService.buscarClienteID(1);
        
        assertEquals(vendedorEntity, resultado);
        verify(vendedorRepository).findById(1);
    }

    // Test para eliminarPorId - Caso exitoso
    @Test
    @DisplayName("Test para eliminar vendedor por ID exitosamente")
    void testEliminarPorIdExitoso() {
        when(vendedorRepository.existsById(1)).thenReturn(true);
        
        String resultado = vendedorService.eliminarPorId(1);
        
        assertEquals("Cliente eliminado correctamente", resultado);
        verify(vendedorRepository).deleteById(1);
    }

    // Test para eliminarPorId - Caso cuando no existe
    @Test
    @DisplayName("Test para eliminar vendedor cuando no existe")
    void testEliminarPorIdNoExiste() {
        when(vendedorRepository.existsById(1)).thenReturn(false);
        
        String resultado = vendedorService.eliminarPorId(1);
        
        assertEquals("No existe un cliente con el Id proporcionado", resultado);
        verify(vendedorRepository, never()).deleteById(any());
    }

    // Test para eliminarPorId - Caso con error
    @Test
    @DisplayName("Test para eliminar vendedor con error")
    void testEliminarPorIdConError() {
        when(vendedorRepository.existsById(1)).thenReturn(true);
        doThrow(new RuntimeException("Error de base de datos")).when(vendedorRepository).deleteById(1);;
        
        String resultado = vendedorService.eliminarPorId(1);
        
        assertEquals("Error al eliminar cliente: Error de base de datos", resultado);
    }
}
