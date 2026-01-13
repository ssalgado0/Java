package edu.uoc.epcsd.productcatalog.domain.mapper;

import edu.uoc.epcsd.productcatalog.application.rest.request.BookingRequest;
import edu.uoc.epcsd.productcatalog.application.rest.request.LineRequest;
import edu.uoc.epcsd.productcatalog.domain.Product;
import edu.uoc.epcsd.productcatalog.domain.booking.Booking;
import edu.uoc.epcsd.productcatalog.domain.booking.BookingLine;
import edu.uoc.epcsd.productcatalog.domain.booking.ItemAllocation;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.ProductEntity;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking.BookingEntity;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking.BookingLineEntity;
import edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking.ItemAllocationEntity;
import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import java.util.Collection;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

  public abstract Booking toDomain(BookingEntity entity);

  public abstract List<Booking> toDomain(Collection<BookingEntity> entity);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "product", source = "product", qualifiedByName = "toProduct")
  @Mapping(target = "quantity", source = "quantity")
  @Mapping(target = "pricePerUnit", source = "pricePerUnit")
  public abstract BookingLine toDomain(BookingLineEntity entity);

  @Mapping(target = "itemSerialNumber", source = "item.serialNumber")
  public abstract ItemAllocation toDomain(ItemAllocationEntity entity);

  @Mapping(target = "status", constant = "PENDING")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "allocations", ignore = true)
  public abstract Booking toDomain(BookingRequest request, @Context CurrentUser currentUser);

  @AfterMapping
  protected void afterMappingBookingRequestToDomain(BookingRequest request,
      @Context CurrentUser currentUser, @MappingTarget Booking booking) {
    booking.setUserId(Long.valueOf(currentUser.getId()));
  }

  @Mapping(target = "product.id", source = "productId")
  @Mapping(target = "id", ignore = true)
  public abstract BookingLine toDomain(LineRequest request);

  @Mapping(target = "lines", ignore = true)
  @Mapping(target = "allocations", ignore = true)
  @Mapping(target = "id", ignore = true)
  public abstract BookingEntity toEntity(Booking booking);

  @Mapping(target = "product", source = "product")
  @Mapping(target = "pricePerUnit", source = "product.dailyPrice")
  @Mapping(target = "booking", source = "booking")
  @Mapping(target = "id", ignore = true)
  public abstract BookingLineEntity toEntity(BookingLine bookingLine, BookingEntity booking,
      ProductEntity product);

  @Named("toProduct")
  protected Product toProduct(ProductEntity entity) {
    return entity.toDomain();
  }
}
