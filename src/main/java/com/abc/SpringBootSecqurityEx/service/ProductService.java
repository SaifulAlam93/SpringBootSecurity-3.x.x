package com.abc.SpringBootSecqurityEx.service;

import com.abc.SpringBootSecqurityEx.dtos.ProductDTO;
import com.abc.SpringBootSecqurityEx.entity.ProductEntity;
import com.abc.SpringBootSecqurityEx.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findActiveProducts() {
        return productRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ProductDTO> findActiveProduct(Long id) {
        return productRepository.findById(id)
                .filter(product -> Boolean.TRUE.equals(product.getActive()))
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findActivePremiumProducts() {
        return productRepository.findAllByPremiumTrueAndActiveTrueOrderByNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public ProductDTO create(ProductDTO request) {
        ProductEntity product = new ProductEntity();
        apply(request, product);
        if (request.getActive() == null) {
            product.setActive(true);
        }
        if (request.getPremium() == null) {
            product.setPremium(false);
        }
        return toDto(productRepository.save(product));
    }

    public Optional<ProductDTO> update(Long id, ProductDTO request) {
        return productRepository.findById(id).map(product -> {
            apply(request, product);
            return toDto(productRepository.save(product));
        });
    }

    public boolean delete(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    private void apply(ProductDTO request, ProductEntity product) {
        product.setName(request.getName().trim());
        product.setCategory(request.getCategory().trim());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
        if (request.getPremium() != null) {
            product.setPremium(request.getPremium());
        }
    }

    private ProductDTO toDto(ProductEntity product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setStock(product.getStock());
        dto.setActive(product.getActive());
        dto.setPremium(product.getPremium());
        dto.setImageUrl(product.getImageUrl());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }
}
