package ru.college.carmarketplace.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.college.carmarketplace.model.entities.Order;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private Integer number;
    private String status;
    private String whereFrom;
    private String whereTo;
    private CarDTO car;
    private String phoneNumber;
    private String name;
    private List<DeliveryMarkDTO> marks;

    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;

        return OrderDTO.builder()
                .id(order.getId())
                .number(order.getNumber())
                .status(order.getStatus())
                .whereFrom(order.getWhereFrom())
                .whereTo(order.getWhereTo())
                .car(order.getCar() != null ? CarDTO.getDto(order.getCar()) : null)
                .phoneNumber(order.getUser() != null ? order.getUser().getPhoneNumber() : "")
                .name(order.getUser() != null ? order.getUser().getName() : "")
                .marks(order.getMarks() != null ? order.getMarks()
                        .stream()
                        .map(DeliveryMarkDTO::fromEntity)
                        .collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

}
