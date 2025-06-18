package com.perfumelandiaspa.vendedores.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perfumelandiaspa.vendedores.Model.Vendedor;
import com.perfumelandiaspa.vendedores.Model.Entity.VendedorEntity;
import com.perfumelandiaspa.vendedores.Service.VendedorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController //el controlador trabaja con un REST
@RequestMapping("/api/v1/vendedor")
@RequiredArgsConstructor
public class VendedorController {
    @Autowired
    private VendedorService vendedorService;

    //Documentacion con Swagger de crear vendedor
    @Operation(
        summary = "Crear un nuevo vendedor",
        description = "Este EndPoint crea un nuevo vendedor con el nombre de la sucursal y la meta mensual correspondiente",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Vendedor creado exitosamente",
                content = @Content(schema = @Schema(implementation = String.class)) //Devuelve un cuerpo tipo Json
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Solicitud invalida",
                content = @Content() //no hay cuerpo en la respuesta por ende lo vacio
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Recurso no fue encontrado",
                content = @Content() //no hay cuerpo en la respuesta por ende lo vacio
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno en el servidor",
                content =  @Content() //no hay cuerpo en la respuesta por ende lo vacio
            )
        }
    )
    @PostMapping("/crear")
    public ResponseEntity<String> crearVendedor(@RequestBody Vendedor vendedor) {
        String resultado = vendedorService.crearVendedor(vendedor);
        
        if (resultado.startsWith("Error")) {
            return ResponseEntity.badRequest().body(resultado); // HTTP 400 si hay error
        } else {
            return ResponseEntity.ok(resultado); // HTTP 200 si es exitoso
        }
    }

    //Documentacion con Swagger de buscar un vendedor
    @Operation(
        summary = "Buscar Vendedor",
        description = "Este EndPoint se encarga de buscar un vendedor a través de su ID",
        responses = {
            @ApiResponse (
                responseCode = "200",
                description = "Vendedor encontrado exitosamente",
                content = @Content(schema = @Schema(implementation = VendedorEntity.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Solicitud invalida",
                content = @Content()
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Recurso no fue encontrado",
                content = @Content()
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno en el servidor",
                content = @Content()
            )       
        }
    )
    @GetMapping("/buscarVendedor/{idVendedor}")
    public ResponseEntity<VendedorEntity> buscarClienteID(@PathVariable Integer idVendedor) {
        try {
            VendedorEntity cliente = vendedorService.buscarClienteID(idVendedor);
            return ResponseEntity.ok(cliente);  
        } catch (Exception e) {
            return ResponseEntity.notFound().build();  
        }
    }

    //Documentacion con Swagger para eliminar un Vendedor por su ID
    @Operation(
        summary = "Eliminar Vendedor por su ID",
        description = "Eliminar vendedores que se encuentren en la base de datos por su ID",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Vendedor buscado y eliminado correctamente",
                content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Solicitud invalida",
                content = @Content()
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Recurso no fue encontrado",
                content = @Content()
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno en el servidor",
                content = @Content()
            )
        }
    )
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
