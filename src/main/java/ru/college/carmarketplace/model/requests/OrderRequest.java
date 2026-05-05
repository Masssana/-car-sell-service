package ru.college.carmarketplace.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.dtos.DeliveryMarkDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long id;
    private Integer number;
    private String status;
    private String whereFrom;
    private String whereTo;
    private CarDTO car;
    private Long carId;
    private String phoneNumber;
    private List<DeliveryMarkDTO> marks;
}
