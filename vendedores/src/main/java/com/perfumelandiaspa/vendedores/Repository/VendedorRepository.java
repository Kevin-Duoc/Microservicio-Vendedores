package com.perfumelandiaspa.vendedores.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perfumelandiaspa.vendedores.Model.Entity.VendedorEntity;

public interface VendedorRepository extends JpaRepository<VendedorEntity, Integer>{
    List<VendedorEntity> findBySucursal(String sucursal); //para el endpoint de sucursal en postman

    boolean existsById(int idVendedor);


}