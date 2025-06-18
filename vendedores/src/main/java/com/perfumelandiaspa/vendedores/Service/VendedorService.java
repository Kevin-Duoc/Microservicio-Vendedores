package com.perfumelandiaspa.vendedores.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfumelandiaspa.vendedores.Model.Vendedor;
import com.perfumelandiaspa.vendedores.Model.Entity.VendedorEntity;
import com.perfumelandiaspa.vendedores.Repository.VendedorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendedorService {

    @Autowired
    private final VendedorRepository vendedorRepository;

    //Crear vendedor
public String crearVendedor(Vendedor vendedor) {
    try {


        VendedorEntity vendedorNuevo = new VendedorEntity();
        // vendedorNuevo.setIdVendedor(vendedor.getIdVendedor()); // Asigna el ID manual
        vendedorNuevo.setMetaMensual(vendedor.getMetaMensual());
        vendedorNuevo.setSucursal(vendedor.getSucursal());
        
        vendedorRepository.save(vendedorNuevo);
        return "Vendedor creado con éxito";
    } catch (Exception e) {
        return "Error al crear Vendedor: " + e.getMessage();
    }
}

    //Buscar por id
    public VendedorEntity buscarClienteID(int idVendedor){
        return vendedorRepository.findById(idVendedor).get();
    }


    //Metodo para Eliminar vendedor por id
    public String eliminarPorId(int idVendedor) {

        //En caso de error
        try {
            if (!vendedorRepository.existsById(idVendedor)) {
                return "No existe un cliente con el Id proporcionado";
            }
            
            vendedorRepository.deleteById(idVendedor);
            return "Cliente eliminado correctamente";
        } catch (Exception e) {
            return "Error al eliminar cliente: " + e.getMessage();
        }
    }


    
    
}
