package ru.college.carmarketplace.model.dtos;

import lombok.Data;
import ru.college.carmarketplace.model.entities.DeliveryMark;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class DeliveryMarkDTO {
    private Long id;
    private String title;
    private String date;

    public static DeliveryMarkDTO fromEntity(DeliveryMark deliveryMark) {
        DeliveryMarkDTO dto = new DeliveryMarkDTO();
        dto.setId(deliveryMark.getId());
        dto.setTitle(deliveryMark.getTitle());
        dto.setDate(formatDate(deliveryMark.getDate()));
        return dto;
    }

    private static String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM HH:mm").withLocale(new Locale("ru"));
        return date.format(formatter);
    }
}
