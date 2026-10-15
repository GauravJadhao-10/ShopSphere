package com.shopsphere.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.shopsphere.dto.ProductRequestDTO;
import com.shopsphere.shopsphere.dto.ProductResponseDTO;
import com.shopsphere.shopsphere.service.ProductService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE
   @PostMapping
    public ProductResponseDTO createProduct(
        @Valid @RequestBody ProductRequestDTO productDTO) {

       return productService.saveProduct(productDTO);
   }

    // READ ALL
    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
   }

    // READ ONE
    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
       return productService.getProductById(id);
   }

    // UPDATE
    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(
        @PathVariable Long id,
        @Valid @RequestBody ProductRequestDTO productDTO) {

    return productService.updateProduct(id, productDTO);
   }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}