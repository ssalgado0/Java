package edu.uoc.epcsd.productcatalog.application.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.productcatalog.application.rest.request.CreateProductRequest;
import edu.uoc.epcsd.productcatalog.domain.Product;
import edu.uoc.epcsd.productcatalog.domain.service.ProductService;
import edu.uoc.epcsd.productcatalog.testutils.IntegrationTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
class ProductRESTControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private final Product mockProduct = Product.builder().id(1L).name("Camera A").build();
    private final Pageable defaultPageable = PageRequest.of(0, 10);

  private final Product productDemo1 = Product.builder()
            .id(1L)
            .name("Product name 1")
            .description("Product description 1")
      .dailyPrice(new BigDecimal(10))
            .brand("Product brand")
            .model("Product model")
            .categoryId(1L)
            .build();

  private final Product productDemo2 = Product.builder()
            .id(2L)
            .name("Product name 2")
            .description("Product description 2")
      .dailyPrice(new BigDecimal(15))
            .brand("Product brand")
            .model("Product model")
            .categoryId(1L)
            .build();


    @Test
    void findProductsByCriteria_shouldReturnAllProductsPage_whenFilterTermIsEmpty() throws Exception {

        PageImpl<Product> mockPage = new PageImpl<>(List.of(mockProduct), defaultPageable, 1);
        when(productService.findAllProducts(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/products/search")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Camera A"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void findProductsByCriteria_shouldReturnFilteredPage_whenFilterTermIsPresent() throws Exception {
        String searchTerm = "camera";
        PageImpl<Product> mockPage = new PageImpl<>(List.of(mockProduct), PageRequest.of(0, 5), 1);

        when(productService.findProductsByText(eq(searchTerm), any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/products/search")
                        .param("filterTerm", searchTerm)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void whenGetAllProducts_thenReturnsOk() throws Exception {
      when(productService.findAllProducts()).thenReturn(List.of(productDemo1, productDemo2));

        MvcResult result = mockMvc.perform(get("/products"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody, "Response body should not be null");

        List<Product> products = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
        assertNotNull(products, "Products list should not be null");
        assertEquals(2, products.size(), "Products list should have 2 items");
    }

    @Test
    void whenGetProductById_thenReturnsOk() throws Exception {
        when(productService.findProductById(anyLong())).thenReturn(Optional.of(productDemo1));

        MvcResult result = mockMvc.perform(get("/products/1"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody, "Response body should not be null");
    }

    @Test
    void whenGetProductByInvalidId_thenReturnsNotFound() throws Exception {
        when(productService.findProductById(anyLong())).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/products/99999"))
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void whenCreateProduct_withAdminRole_thenReturnsCreated() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "New product name",
                "New product description",
            new BigDecimal(20),
                "Product brand",
                "Product model",
                1L
        );

        when(productService.createProduct(any(Product.class))).thenReturn(10L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(201, result.getResponse().getStatus());
        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);
    }

    @Test
    void whenCreateProduct_withUserRole_thenReturnsForbidden() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "New product name",
                "New product description",
            new BigDecimal(20),
                "Product brand",
                "Product model",
                1L
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(403, result.getResponse().getStatus());
    }

    @Test
    void whenCreateProduct_withoutAuthentication_thenReturnsForbidden() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "New product name",
                "New product description",
            new BigDecimal(20),
                "Product brand",
                "Product model",
                1L
        );


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(403, result.getResponse().getStatus());
    }

    @Test
    void whenGetAllProducts_asUser_thenReturnsOk() throws Exception {
      when(productService.findAllProducts()).thenReturn(List.of(productDemo1));

        MvcResult result = mockMvc.perform(get("/products")
                        .with(user("user").roles("USER")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenGetAllProducts_asAdmin_thenReturnsOk() throws Exception {
      when(productService.findAllProducts()).thenReturn(List.of(productDemo1));

        MvcResult result = mockMvc.perform(get("/products")
                        .with(user("admin").roles("ADMIN")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenGetProductById_asUser_thenReturnsOk() throws Exception {
        when(productService.findProductById(1L)).thenReturn(Optional.of(productDemo1));

        MvcResult result = mockMvc.perform(get("/products/1")
                        .with(user("user").roles("USER")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenGetProductById_asAdmin_thenReturnsOk() throws Exception {
        when(productService.findProductById(1L)).thenReturn(Optional.of(productDemo1));

        MvcResult result = mockMvc.perform(get("/products/1")
                        .with(user("admin").roles("ADMIN")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }




    @Test
    void whenDeleteProduct_withAdminRole_thenReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/1")
                        .with(user("admin").roles("ADMIN")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String responseBody = result.getResponse().getContentAsString();
        assertEquals("true", responseBody, "Should return true on successful deletion");
    }

    @Test
    void whenDeleteProduct_withInvalidId_thenReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Product not found"))
                .when(productService).deleteProduct(anyLong());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/99999")
                        .with(user("admin").roles("ADMIN")))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    void whenCreateProduct_withInvalidCategoryId_thenReturnsBadRequest() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "New product name",
                "New product description",
            new BigDecimal(20),
                "Product brand",
                "Product model",
                99999L
        );

        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Category not found"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }
}
