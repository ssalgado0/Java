package edu.uoc.epcsd.productcatalog.application.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.productcatalog.application.rest.request.CreateCategoryRequest;
import edu.uoc.epcsd.productcatalog.domain.Category;
import edu.uoc.epcsd.productcatalog.domain.service.CategoryService;
import edu.uoc.epcsd.productcatalog.testutils.IntegrationTest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
class CategoryRESTControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private Category categoryDemo1 = Category.builder()
            .id(1L)
            .name("Category 1")
            .description("Description category 1")
            .build();

    private Category categoryDemo2 = Category.builder()
            .id(1L)
            .name("Category 2")
            .description("Description category 2")
            .build();

    @Test
    void whenGetAllCategories_thenReturnsOk() throws Exception {

        when(categoryService.findAllCategories()).thenReturn(Arrays.asList(categoryDemo1, categoryDemo2));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody, "Response body should not be null");

        List<Category> categories = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Category.class));
        assertNotNull(categories, "Categories list should not be null");
        assertFalse(categories.isEmpty(), "Categories list should not be empty");
    }

    @Test
    void whenGetCategoryById_thenReturnsOk() throws Exception {
        when(categoryService.findCategoryById(1L)).thenReturn(Optional.of(categoryDemo1));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/1"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody, "Response body should not be null");

        Category returnedCategory = objectMapper.readValue(responseBody, Category.class);
        assertNotNull(returnedCategory, "Category should not be null");
        assertNotNull(returnedCategory.getId(), "Category ID should not be null");
    }

    @Test
    void whenGetCategoryByInvalidId_thenReturnsNotFound() throws Exception {
        when(categoryService.findCategoryById(99999L)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/99999"))
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void whenCreateCategory_withAdminRole_thenReturnsCreated() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest(
                null,
                "New Category",
                "Description new category"
        );

        when(categoryService.createCategory(any(Category.class))).thenReturn(5L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(201, result.getResponse().getStatus());
        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);
    }

    @Test
    void whenCreateCategory_withUserRole_thenReturnsForbidden() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest(
                null,
                "New Category",
                "Description new category"
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(403, result.getResponse().getStatus());
    }

    @Test
    void whenGetAllCategories_asUser_thenReturnsOk() throws Exception {
        when(categoryService.findAllCategories()).thenReturn(Arrays.asList(categoryDemo1));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .with(user("user").roles("USER")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenGetAllCategories_asAdmin_thenReturnsOk() throws Exception {

        when(categoryService.findAllCategories()).thenReturn(Arrays.asList(categoryDemo1));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .with(user("admin").roles("ADMIN")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenGetCategoryById_asUser_thenReturnsOk() throws Exception {

        when(categoryService.findCategoryById(1L)).thenReturn(Optional.of(categoryDemo1));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/1")
                        .with(user("user").roles("USER")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenGetCategoryById_asAdmin_thenReturnsOk() throws Exception {

        when(categoryService.findCategoryById(1L)).thenReturn(Optional.of(categoryDemo1));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/1").with(user("admin").roles("ADMIN")))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void whenSearchCategories_withDescription_thenReturnsOk() throws Exception {
        when(categoryService.findCategoriesByExample(any(Category.class)))
                .thenReturn(Arrays.asList(categoryDemo1));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/search")
                        .param("description", "Description category 1"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }


    @Test
    void whenSearchCategories_withInvalidParentId_thenReturnsInternalServerError() throws Exception {
        when(categoryService.findCategoriesByExample(any(Category.class)))
                .thenThrow(new IllegalArgumentException("Parent category not found"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/search")
                        .param("parentId", "99999"))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }


    @Test
    void whenCreateCategory_withNullName_thenReturnsBadRequest() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest(
                null,
                null,
                "Description new category"
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

      assertEquals(400, result.getResponse().getStatus());
    }

}
