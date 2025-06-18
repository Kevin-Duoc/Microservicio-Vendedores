package com.perfumelandiaspa.vendedores.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vendedor {

    private int idVendedor;
    private String sucursal;
    private Double metaMensual;

}
