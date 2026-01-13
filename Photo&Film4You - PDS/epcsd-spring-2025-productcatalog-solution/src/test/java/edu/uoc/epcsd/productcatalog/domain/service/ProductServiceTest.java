package edu.uoc.epcsd.productcatalog.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.uoc.epcsd.productcatalog.domain.Product;
import edu.uoc.epcsd.productcatalog.domain.exception.ProductHasActiveBookingsException;
import edu.uoc.epcsd.productcatalog.domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {


    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingLineService bookingLineService;

    @InjectMocks
    private ProductServiceImpl productService;


    @Test
    void findProductsByText_shouldDelegateToRepository () {
        String filterTerm = "test";
        Pageable pageable = PageRequest.of(1, 20);
        Product mockProduct = Product.builder().id(1L).name("Test Product").build();
        Page<Product> mockPage = new PageImpl<>(List.of(mockProduct), pageable, 1);

        when(productRepository.findProductsByText(filterTerm, pageable)).thenReturn(mockPage);

        Page<Product> result = productService.findProductsByText(filterTerm, pageable);

        verify(productRepository).findProductsByText(filterTerm, pageable);
        assertEquals(1, result.getNumberOfElements());
        assertEquals("Test Product", result.getContent().get(0).getName());

    }

    @Test
    void updateProduct_shouldDelegateToRepository() {
        Product mockProduct = Product.builder()
            .id(1L)
            .name("Test Update Product")
            .description("Test Update Product Description")
            .dailyPrice(new BigDecimal(100))
            .brand("Test Update Product Brand")
            .model("Test Update Product Model")
            .categoryId(1L)
            .build();

        when(productRepository.updateProduct(mockProduct)).thenReturn(mockProduct);
        Product result = productService.updateProduct(mockProduct);
        verify(productRepository).updateProduct(mockProduct);
        assertEquals("Test Update Product", result.getName());
    }

    @Test
    void updateProduct_whenProductNotFound_shouldThrowException() {
        Product mockProduct = Product.builder()
            .id(1L)
            .name("Test Update Product")
            .build();

        when(productRepository.updateProduct(mockProduct))
            .thenThrow(new IllegalArgumentException("Update Product Exception"));

        assertThrows(IllegalArgumentException.class,
            () -> productService.updateProduct(mockProduct));

        verify(productRepository).updateProduct(mockProduct);
    }

    @Test
    void deleteProduct_whenNoBookings_shouldDeleteItemsAndProduct() {
        Long productId = 1L;
        Product product = Product.builder().id(productId).build();

        when(productRepository.findProductById(productId)).thenReturn(
            java.util.Optional.of(product));
        when(bookingLineService.existsAnyBookingForProduct(productId)).thenReturn(false);

        productService.deleteProduct(productId);

        verify(bookingLineService).existsAnyBookingForProduct(productId);
        verify(itemService).findByProductId(productId);
        verify(productRepository).deleteProduct(product);
    }

    @Test
    void deleteProduct_whenBookingsExist_shouldThrowExceptionAndNotDelete() {
        Long productId = 1L;
        Product product = Product.builder().id(productId).build();

        when(productRepository.findProductById(productId)).thenReturn(
            java.util.Optional.of(product));
        when(bookingLineService.existsAnyBookingForProduct(productId)).thenReturn(true);

        assertThrows(ProductHasActiveBookingsException.class,
            () -> productService.deleteProduct(productId));

        verify(bookingLineService).existsAnyBookingForProduct(productId);
        verify(itemService, never()).findByProductId(productId);
        verify(productRepository, never()).deleteProduct(product);
    }

}