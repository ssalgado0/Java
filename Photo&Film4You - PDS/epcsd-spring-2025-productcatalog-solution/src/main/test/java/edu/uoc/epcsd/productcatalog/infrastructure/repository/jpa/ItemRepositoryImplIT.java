package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import edu.uoc.epcsd.productcatalog.domain.repository.ItemRepository;
import edu.uoc.epcsd.productcatalog.testutils.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class ItemRepositoryImplIT extends IntegrationTest {

  @Autowired
  private ItemRepository itemRepositoryImpl;

  @ParameterizedTest
  @ValueSource(longs = {
      999L, // Non-existent product ID
      6, // "Difusor Universal 3000" has items but none are operational in the test data
      7, // "Trípode Manfrotto 055XPROB" has no items
  })
  void givenNonExistentProductId_whenFindByProductId_thenReturnEmptyList(Long productId) {

    List<ItemsPerProductCount> result = itemRepositoryImpl.countTotalItemsPerProduct(
        List.of(productId));

    assertThat(result).isEmpty();
  }

  @Test
  void givenExistingProductIdsWithItems_whenFindByProductId_thenReturnCorrectCounts() {
    Long productId1 = 1L; // "Canon 500D" has 2 operational items in the test data
    Long productId2 = 3L; // "Canon EOS R5 C" has 1 operational item in the test data

    List<ItemsPerProductCount> result = itemRepositoryImpl.countTotalItemsPerProduct(
        List.of(productId1, productId2));

    assertThat(result).hasSize(2)
        .anySatisfy(count -> {
          assertThat(count.getProductId()).isEqualTo(productId1);
          assertThat(count.getQuantity()).isEqualTo(2);
        })
        .anySatisfy(count -> {
          assertThat(count.getProductId()).isEqualTo(productId2);
          assertThat(count.getQuantity()).isEqualTo(1);
        });
  }
}