package com.perfumelandiaspa.vendedores.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perfumelandiaspa.vendedores.Model.Vendedor;
import com.perfumelandiaspa.vendedores.Model.Entity.VendedorEntity;
import com.perfumelandiaspa.vendedores.Service.VendedorService;

import lombok.RequiredArgsConstructor;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController //el controlador trabaja con un REST
@RequestMapping("/vendedor")
@RequiredArgsConstructor
public class VendedorController {
    @Autowired
    private VendedorService vendedorService;

    @PostMapping("/crear")
    public ResponseEntity<String> crearVendedor(@RequestBody Vendedor vendedor) {
        String resultado = vendedorService.crearVendedor(vendedor);
        
        if (resultado.startsWith("Error")) {
            return ResponseEntity.badRequest().body(resultado); // HTTP 400 si hay error
        } else {
            return ResponseEntity.ok(resultado); // HTTP 200 si es exitoso
        }
    }


    @GetMapping("/buscarVendedor/{idVendedor}")
    public ResponseEntity<VendedorEntity> buscarClienteID(@PathVariable Integer idVendedor) {
        try {
            VendedorEntity cliente = vendedorService.buscarClienteID(idVendedor);
            return ResponseEntity.ok(cliente);  
        } catch (Exception e) {
            return ResponseEntity.notFound().build();  
        }
    }

    @DeleteMapping("/eliminarVendedorPorId/{idVendedor}")
    public ResponseEntity<String> eliminarPorId(@PathVariable int idVendedor ) {
    try {
        String resultado = vendedorService.eliminarPorId(idVendedor);
        if (resultado.equals("No existe un vendedor con el ID proporcionado")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultado);
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Error del servidor: " + e.getMessage());
    }
}

    
}
