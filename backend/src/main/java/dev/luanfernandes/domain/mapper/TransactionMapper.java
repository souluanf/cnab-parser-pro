package dev.luanfernandes.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import dev.luanfernandes.domain.entity.Transaction;
import dev.luanfernandes.domain.response.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface TransactionMapper {

    @Mapping(target = "description", source = "type.description")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "cpf", source = "cpf")
    @Mapping(target = "card", source = "card")
    @Mapping(target = "hour", source = "hour")
    @Mapping(target = "storeOwner", source = "storeOwner")
    @Mapping(target = "storeName", source = "storeName")
    TransactionResponse map(Transaction value);
}
