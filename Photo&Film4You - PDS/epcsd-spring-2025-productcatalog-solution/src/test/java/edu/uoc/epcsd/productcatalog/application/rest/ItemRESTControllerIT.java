package edu.uoc.epcsd.productcatalog.application.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.productcatalog.domain.Item;
import edu.uoc.epcsd.productcatalog.domain.ItemStatus;
import edu.uoc.epcsd.productcatalog.domain.service.ItemService;
import edu.uoc.epcsd.productcatalog.testutils.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
class ItemRESTControllerIT extends IntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ItemService itemService;

  private final Item item1 = Item.builder().serialNumber("SN1").productId(1L).status(ItemStatus.OPERATIONAL).build();
  private final Item item2 = Item.builder().serialNumber("SN2").productId(1L).status(ItemStatus.NON_OPERATIONAL).build();

  @Test
  @WithMockUser(roles = "ADMIN")
  void whenGetItemsByProductId_thenReturnsListAndOk() throws Exception {
    when(itemService.findByProductId(anyLong())).thenReturn(List.of(item1, item2));

    MvcResult result = mockMvc.perform(get("/items/product/1")).andReturn();

    assertEquals(200, result.getResponse().getStatus());
    String responseBody = result.getResponse().getContentAsString();
    assertNotNull(responseBody, "Response body should not be null");

    List<Item> items = objectMapper.readValue(responseBody,
        objectMapper.getTypeFactory().constructCollectionType(List.class, Item.class));
    assertNotNull(items, "Items list should not be null");
    assertEquals(2, items.size());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void whenGetItemsByProductId_andNoItems_thenReturnsEmptyList() throws Exception {
    when(itemService.findByProductId(anyLong())).thenReturn(List.of());

    MvcResult result = mockMvc.perform(get("/items/product/999")).andReturn();

    assertEquals(200, result.getResponse().getStatus());
    String responseBody = result.getResponse().getContentAsString();
    assertNotNull(responseBody, "Response body should not be null");

    List<Item> items = objectMapper.readValue(responseBody,
        objectMapper.getTypeFactory().constructCollectionType(List.class, Item.class));
    assertNotNull(items, "Items list should not be null");
    assertEquals(0, items.size());
  }
}
