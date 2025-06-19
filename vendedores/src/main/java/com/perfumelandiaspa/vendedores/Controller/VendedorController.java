package com.perfumelandiaspa.vendedores.Controller;

import java.util.Map;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perfumelandiaspa.vendedores.Model.Vendedor;
import com.perfumelandiaspa.vendedores.Model.Entity.VendedorEntity;
import com.perfumelandiaspa.vendedores.Service.VendedorService;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/vendedor")
@RequiredArgsConstructor
@Tag(name = "Vendedores", description = "API para gestión de vendedores con HATEOAS")
public class VendedorController {
    
    private final VendedorService vendedorService; 
    
    //Clase para la respuesta root con enlaces HATEOAS
    public static class VendedorRoot extends RepresentationModel<VendedorRoot> {
        private final String message;
        private final String version;
        
        public VendedorRoot(String message, String version) {
            this.message = message;
            this.version = version;
        }
        
        public String getMessage() { return message; }
        public String getVersion() { return version; }
    }

    @Operation(
        summary = "Endpoint raíz de la API de Vendedores",
        description = "Endpoint principal que proporciona enlaces HATEOAS a todas las operaciones disponibles",
        responses = {
            @ApiResponse(responseCode = "200", description = "Enlaces HATEOAS obtenidos exitosamente")
        }
    )
    @GetMapping
    public ResponseEntity<VendedorRoot> root() {
        VendedorRoot root = new VendedorRoot(
            "API de Gestión de Vendedores - PerfumeLandia SPA", 
            "v1.0"
        );
        
        // Enlaces HATEOAS optimizados
        root.add(linkTo(methodOn(VendedorController.class).root()).withSelfRel());
        root.add(linkTo(methodOn(VendedorController.class).crearVendedor(null))
            .withRel("crear-vendedor"));
        root.add(linkTo(VendedorController.class).slash("buscarVendedor/{idVendedor}")
            .withRel("buscar-vendedor"));
        root.add(linkTo(VendedorController.class).slash("eliminarVendedorPorId/{idVendedor}")
            .withRel("eliminar-vendedor"));
        
        return ResponseEntity.ok(root);
    }

    @Operation(summary = "Crear un nuevo vendedor")
    @PostMapping("/crear")
    public ResponseEntity<?> crearVendedor(@RequestBody Vendedor vendedor) {
        try {
            String resultado = vendedorService.crearVendedor(vendedor);
            
            if (resultado.startsWith("Error")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Solicitud inválida",
                    "message", resultado,
                    "timestamp", System.currentTimeMillis()
                ));
            }
            
            // Respuesta HATEOAS limpia
            RepresentationModel<?> response = new RepresentationModel<>();
            response.add(linkTo(methodOn(VendedorController.class).root())
                .withRel(IanaLinkRelations.INDEX));
            response.add(linkTo(methodOn(VendedorController.class).crearVendedor(null))
                .withSelfRel());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", resultado,
                "timestamp", System.currentTimeMillis(),
                "_links", response.getLinks()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error interno del servidor",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @Operation(summary = "Buscar vendedor por ID")
    @GetMapping("/buscarVendedor/{idVendedor}")
    public ResponseEntity<?> buscarClienteID(@PathVariable Integer idVendedor) {
        try {
            VendedorEntity vendedor = vendedorService.buscarClienteID(idVendedor);
            
            EntityModel<VendedorEntity> vendedorModel = EntityModel.of(vendedor);
            vendedorModel.add(linkTo(methodOn(VendedorController.class).buscarClienteID(idVendedor))
                .withSelfRel());
            vendedorModel.add(linkTo(methodOn(VendedorController.class).root())
                .withRel(IanaLinkRelations.INDEX));
            vendedorModel.add(linkTo(methodOn(VendedorController.class).crearVendedor(null))
                .withRel("crear-nuevo"));
            vendedorModel.add(linkTo(methodOn(VendedorController.class).eliminarPorId(idVendedor))
                .withRel("eliminar"));
            
            return ResponseEntity.ok(vendedorModel);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Recurso no encontrado",
                "message", "No se encontró el vendedor con ID: " + idVendedor,
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @Operation(summary = "Eliminar vendedor por ID")
    @DeleteMapping("/eliminarVendedorPorId/{idVendedor}")
    public ResponseEntity<?> eliminarPorId(@PathVariable int idVendedor) {
        try {
            String resultado = vendedorService.eliminarPorId(idVendedor);
            
            if (resultado.equals("No existe un vendedor con el ID proporcionado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Recurso no encontrado",
                    "message", resultado,
                    "timestamp", System.currentTimeMillis()
                ));
            }
            
            RepresentationModel<?> response = new RepresentationModel<>();
            response.add(linkTo(methodOn(VendedorController.class).root())
                .withRel(IanaLinkRelations.INDEX));
            response.add(linkTo(methodOn(VendedorController.class).crearVendedor(null))
                .withRel("crear-nuevo"));
            
            return ResponseEntity.ok(Map.of(
                "message", resultado,
                "idVendedor", idVendedor,
                "timestamp", System.currentTimeMillis(),
                "_links", response.getLinks()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error interno del servidor",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}
// package com.perfumelandiaspa.vendedores.Controller;

// import java.util.Map;
// //import java.util.NoSuchElementException;

// //import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.hateoas.EntityModel;
// import org.springframework.hateoas.IanaLinkRelations;
// import org.springframework.hateoas.RepresentationModel;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.perfumelandiaspa.vendedores.Model.Vendedor;
// import com.perfumelandiaspa.vendedores.Model.Entity.VendedorEntity;
// import com.perfumelandiaspa.vendedores.Service.VendedorService;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;

// import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
// import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// @RestController
// @RequestMapping("/api/v1/vendedor")
// @RequiredArgsConstructor
// @Tag(name = "Vendedores", description = "API para gestión de vendedores con HATEOAS")
// public class VendedorController {
    
//     //@Autowired
//     private VendedorService vendedorService;

//     /**
//      * Clase interna para representar la respuesta del endpoint root con enlaces HATEOAS
//      */
//     public static class VendedorRoot extends RepresentationModel<VendedorRoot> {
//         private final String message;
//         private final String version;
        
//         public VendedorRoot(String message, String version) {
//             this.message = message;
//             this.version = version;
//         }
        
//         public String getMessage() {
//             return message;
//         }
        
//         public String getVersion() {
//             return version;
//         }
//     }

//     @Operation(
//         summary = "Endpoint raíz de la API de Vendedores",
//         description = "Endpoint principal que proporciona enlaces HATEOAS a todas las operaciones disponibles para vendedores",
//         responses = {
//             @ApiResponse(
//                 responseCode = "200",
//                 description = "Enlaces HATEOAS obtenidos exitosamente",
//                 content = @Content(schema = @Schema(implementation = VendedorRoot.class))
//             )
//         }
//     )
//     @GetMapping
//     public ResponseEntity<VendedorRoot> root() {
//         VendedorRoot root = new VendedorRoot(
//             "API de Gestión de Vendedores - PerfumeLandia SPA", 
//             "v1.0"
//         );
        
//         // Agregar enlaces HATEOAS
//         root.add(linkTo(methodOn(VendedorController.class).root()).withSelfRel());
//         root.add(linkTo(methodOn(VendedorController.class).crearVendedor(null))
//             .withRel("crear-vendedor")
//             .withType("POST")
//             .withTitle("Crear un nuevo vendedor"));
//         root.add(linkTo(VendedorController.class).slash("buscarVendedor").slash("{idVendedor}")
//             .withRel("buscar-vendedor")
//             .withType("GET")
//             .withTitle("Buscar vendedor por ID"));
//         root.add(linkTo(VendedorController.class).slash("eliminarVendedorPorId").slash("{idVendedor}")
//             .withRel("eliminar-vendedor")
//             .withType("DELETE")
//             .withTitle("Eliminar vendedor por ID"));
        
//         return ResponseEntity.ok(root);
//     }

//     @Operation(
//         summary = "Crear un nuevo vendedor",
//         description = "Este EndPoint crea un nuevo vendedor con el nombre de la sucursal y la meta mensual correspondiente. Devuelve el resultado con enlaces HATEOAS.",
//         responses = {
//             @ApiResponse(
//                 responseCode = "201",
//                 description = "Vendedor creado exitosamente",
//                 content = @Content(schema = @Schema(implementation = RepresentationModel.class))
//             ),
//             @ApiResponse(
//                 responseCode = "400",
//                 description = "Solicitud inválida",
//                 content = @Content()
//             ),
//             @ApiResponse(
//                 responseCode = "500",
//                 description = "Error interno del servidor",
//                 content = @Content()
//             )
//         }
//     )
//     @PostMapping("/crear")
//     public ResponseEntity<?> crearVendedor(@RequestBody Vendedor vendedor) {
//         try {
//             String resultado = vendedorService.crearVendedor(vendedor);
            
//             if (resultado.startsWith("Error")) {
//                 return ResponseEntity.badRequest().body(Map.of(
//                     "error", "Solicitud inválida",
//                     "message", resultado,
//                     "timestamp", System.currentTimeMillis()
//                 ));
//             }
            
//             // Crear respuesta con enlaces HATEOAS
//             RepresentationModel<?> response = new RepresentationModel<>();
//             response.add(linkTo(methodOn(VendedorController.class).root())
//                 .withRel(IanaLinkRelations.INDEX)
//                 .withTitle("Volver al inicio"));
//             response.add(linkTo(methodOn(VendedorController.class).crearVendedor(null))
//                 .withSelfRel()
//                 .withTitle("Crear otro vendedor"));
            
//             return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
//                 "message", resultado,
//                 "timestamp", System.currentTimeMillis(),
//                 "_links", response.getLinks()
//             ));
            
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of(
//                 "error", "Error interno del servidor",
//                 "message", "Error al crear vendedor: " + e.getMessage(),
//                 "timestamp", System.currentTimeMillis()
//             ));
//         }
//     }

//     @Operation(
//         summary = "Buscar Vendedor",
//         description = "Este EndPoint se encarga de buscar un vendedor a través de su ID y devuelve enlaces HATEOAS relacionados.",
//         responses = {
//             @ApiResponse(
//                 responseCode = "200",
//                 description = "Vendedor encontrado exitosamente",
//                 content = @Content(schema = @Schema(implementation = EntityModel.class))
//             ),
//             @ApiResponse(
//                 responseCode = "404",
//                 description = "Recurso no fue encontrado",
//                 content = @Content()
//             ),
//             @ApiResponse(
//                 responseCode = "500",
//                 description = "Error interno en el servidor",
//                 content = @Content()
//             )
//         }
//     )
//     @GetMapping("/buscarVendedor/{idVendedor}")
//     public ResponseEntity<?> buscarClienteID(@PathVariable Integer idVendedor) {
//         try {
//             VendedorEntity vendedor = vendedorService.buscarClienteID(idVendedor);
            
//             // Crear EntityModel con enlaces HATEOAS
//             EntityModel<VendedorEntity> vendedorModel = EntityModel.of(vendedor);
//             vendedorModel.add(linkTo(methodOn(VendedorController.class).buscarClienteID(idVendedor))
//                 .withSelfRel()
//                 .withTitle("Este vendedor"));
//             vendedorModel.add(linkTo(methodOn(VendedorController.class).root())
//                 .withRel(IanaLinkRelations.INDEX)
//                 .withTitle("Volver al inicio"));
//             vendedorModel.add(linkTo(methodOn(VendedorController.class).crearVendedor(null))
//                 .withRel("crear-nuevo")
//                 .withType("POST")
//                 .withTitle("Crear nuevo vendedor"));
//             vendedorModel.add(linkTo(methodOn(VendedorController.class).eliminarPorId(idVendedor))
//                 .withRel("eliminar")
//                 .withType("DELETE")
//                 .withTitle("Eliminar este vendedor"));
            
//             return ResponseEntity.ok(vendedorModel);
            
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                 "error", "Recurso no encontrado",
//                 "message", "No se encontró el vendedor con ID: " + idVendedor,
//                 "timestamp", System.currentTimeMillis()
//             ));
//         }
//     }

//     @Operation(
//         summary = "Eliminar Vendedor por su ID",
//         description = "Elimina un vendedor que se encuentra en la base de datos por su ID y proporciona enlaces HATEOAS para navegación.",
//         responses = {
//             @ApiResponse(
//                 responseCode = "200",
//                 description = "Vendedor encontrado y eliminado correctamente",
//                 content = @Content(schema = @Schema(implementation = RepresentationModel.class))
//             ),
//             @ApiResponse(
//                 responseCode = "404",
//                 description = "Recurso no fue encontrado",
//                 content = @Content()
//             ),
//             @ApiResponse(
//                 responseCode = "500",
//                 description = "Error interno en el servidor",
//                 content = @Content()
//             )
//         }
//     )
//     @DeleteMapping("/eliminarVendedorPorId/{idVendedor}")
//     public ResponseEntity<?> eliminarPorId(@PathVariable int idVendedor) {
//         try {
//             String resultado = vendedorService.eliminarPorId(idVendedor);
            
//             if (resultado.equals("No existe un vendedor con el ID proporcionado")) {
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                     "error", "Recurso no encontrado",
//                     "message", "No se encontró el vendedor con ID: " + idVendedor,
//                     "timestamp", System.currentTimeMillis()
//                 ));
//             }
            
//             // Crear respuesta con enlaces HATEOAS
//             RepresentationModel<?> response = new RepresentationModel<>();
//             response.add(linkTo(methodOn(VendedorController.class).root())
//                 .withRel(IanaLinkRelations.INDEX)
//                 .withTitle("Volver al inicio"));
//             response.add(linkTo(methodOn(VendedorController.class).crearVendedor(null))
//                 .withRel("crear-nuevo")
//                 .withType("POST")
//                 .withTitle("Crear nuevo vendedor"));
            
//             return ResponseEntity.ok(Map.of(
//                 "message", resultado,
//                 "idVendedor", idVendedor,
//                 "timestamp", System.currentTimeMillis(),
//                 "_links", response.getLinks()
//             ));
            
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of(
//                 "error", "Error interno del servidor",
//                 "message", "Error del servidor: " + e.getMessage(),
//                 "timestamp", System.currentTimeMillis()
//             ));
//         }
//     }
// }
// package com.perfumelandiaspa.vendedores.Controller;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.perfumelandiaspa.vendedores.Model.Vendedor;
// import com.perfumelandiaspa.vendedores.Model.Entity.VendedorEntity;
// import com.perfumelandiaspa.vendedores.Service.VendedorService;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import lombok.RequiredArgsConstructor;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;

// @RestController //el controlador trabaja con un REST
// @RequestMapping("/api/v1/vendedor")
// @RequiredArgsConstructor
// public class VendedorController {
//     @Autowired
//     private VendedorService vendedorService;

//     //Documentacion con Swagger de crear vendedor
//     @Operation(
//         summary = "Crear un nuevo vendedor",
//         description = "Este EndPoint crea un nuevo vendedor con el nombre de la sucursal y la meta mensual correspondiente",
//         responses = {
//             @ApiResponse(
//                 responseCode = "200",
//                 description = "Vendedor creado exitosamente",
//                 content = @Content(schema = @Schema(implementation = String.class)) //Devuelve un cuerpo tipo Json
//             ),
//             @ApiResponse(
//                 responseCode = "400",
//                 description = "Solicitud invalida",
//                 content = @Content() //no hay cuerpo en la respuesta por ende lo vacio
//             ),
//             @ApiResponse(
//                 responseCode = "404",
//                 description = "Recurso no fue encontrado",
//                 content = @Content() //no hay cuerpo en la respuesta por ende lo vacio
//             ),
//             @ApiResponse(
//                 responseCode = "500",
//                 description = "Error interno en el servidor",
//                 content =  @Content() //no hay cuerpo en la respuesta por ende lo vacio
//             )
//         }
//     )
//     @PostMapping("/crear")
//     public ResponseEntity<String> crearVendedor(@RequestBody Vendedor vendedor) {
//         String resultado = vendedorService.crearVendedor(vendedor);
        
//         if (resultado.startsWith("Error")) {
//             return ResponseEntity.badRequest().body(resultado); // HTTP 400 si hay error
//         } else {
//             return ResponseEntity.ok(resultado); // HTTP 200 si es exitoso
//         }
//     }

//     //Documentacion con Swagger de buscar un vendedor
//     @Operation(
//         summary = "Buscar Vendedor",
//         description = "Este EndPoint se encarga de buscar un vendedor a través de su ID",
//         responses = {
//             @ApiResponse (
//                 responseCode = "200",
//                 description = "Vendedor encontrado exitosamente",
//                 content = @Content(schema = @Schema(implementation = VendedorEntity.class))
//             ),
//             @ApiResponse(
//                 responseCode = "400",
//                 description = "Solicitud invalida",
//                 content = @Content()
//             ),
//             @ApiResponse(
//                 responseCode = "404",
//                 description = "Recurso no fue encontrado",
//                 content = @Content()
//             ),
//             @ApiResponse(
//                 responseCode = "500",
//                 description = "Error interno en el servidor",
//                 content = @Content()
//             )       
//         }
//     )
//     @GetMapping("/buscarVendedor/{idVendedor}")
//     public ResponseEntity<VendedorEntity> buscarClienteID(@PathVariable Integer idVendedor) {
//         try {
//             VendedorEntity cliente = vendedorService.buscarClienteID(idVendedor);
//             return ResponseEntity.ok(cliente);  
//         } catch (Exception e) {
//             return ResponseEntity.notFound().build();  
//         }
//     }

//     //Documentacion con Swagger para eliminar un Vendedor por su ID
//     @Operation(
//         summary = "Eliminar Vendedor por su ID",
//         description = "Eliminar vendedores que se encuentren en la base de datos por su ID",
//         responses = {
//             @ApiResponse(
//                 responseCode = "200",
//                 description = "Vendedor buscado y eliminado correctamente",
//                 content = @Content(schema = @Schema(implementation = String.class))
//             ),
//             @ApiResponse(
//                 responseCode = "400",
//                 description = "Solicitud invalida",
//                 content = @Content()
//             ),
//             @ApiResponse(
//                 responseCode = "404",
//                 description = "Recurso no fue encontrado",
//                 content = @Content()
//             ),
//             @ApiResponse(
//                 responseCode = "500",
//                 description = "Error interno en el servidor",
//                 content = @Content()
//             )
//         }
//     )
//     @DeleteMapping("/eliminarVendedorPorId/{idVendedor}")
//     public ResponseEntity<String> eliminarPorId(@PathVariable int idVendedor ) {
//     try {
//         String resultado = vendedorService.eliminarPorId(idVendedor);
//         if (resultado.equals("No existe un vendedor con el ID proporcionado")) {
//             return ResponseEntity.notFound().build();
//         }
//         return ResponseEntity.ok(resultado);
//     } catch (Exception e) {
//         return ResponseEntity.internalServerError().body("Error del servidor: " + e.getMessage());
//     }
//     }
// }
