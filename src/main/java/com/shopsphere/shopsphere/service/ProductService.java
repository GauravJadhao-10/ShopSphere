package com.shopsphere.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.shopsphere.dto.ProductRequestDTO;
import com.shopsphere.shopsphere.dto.ProductResponseDTO;
import com.shopsphere.shopsphere.entity.Product;
import com.shopsphere.shopsphere.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDTO saveProduct(ProductRequestDTO productDTO) {

    Product product = new Product();

    product.setName(productDTO.getName());
    product.setDescription(productDTO.getDescription());
    product.setPrice(productDTO.getPrice());
    product.setStock(productDTO.getStock());

    Product savedProduct = productRepository.save(product);

    return convertToResponseDTO(savedProduct);
    }

    public List<ProductResponseDTO> getAllProducts() {

    List<Product> products = productRepository.findAll();

    return products.stream()
            .map(this::convertToResponseDTO)
            .toList();
    }

    public ProductResponseDTO getProductById(Long id) {

    Product product = productRepository.findById(id).orElse(null);

    if (product == null) {
        return null;
    }

    return convertToResponseDTO(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

   public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO) {

    Product existingProduct = productRepository.findById(id).orElse(null);

    if (existingProduct == null) {
        return null;
    }

    existingProduct.setName(productDTO.getName());
    existingProduct.setDescription(productDTO.getDescription());
    existingProduct.setPrice(productDTO.getPrice());
    existingProduct.setStock(productDTO.getStock());

    Product updatedProduct = productRepository.save(existingProduct);

    return convertToResponseDTO(updatedProduct);
   }
   public ProductResponseDTO convertToResponseDTO(Product product) {

    ProductResponseDTO responseDTO = new ProductResponseDTO();

    responseDTO.setId(product.getId());
    responseDTO.setName(product.getName());
    responseDTO.setDescription(product.getDescription());
    responseDTO.setPrice(product.getPrice());
    responseDTO.setStock(product.getStock());

    return responseDTO;
  }
}