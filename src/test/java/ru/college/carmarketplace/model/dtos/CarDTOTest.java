package ru.college.carmarketplace.model.dtos;

import org.junit.jupiter.api.Test;
import ru.college.carmarketplace.model.entities.Car;
import ru.college.carmarketplace.model.entities.CarImage;
import ru.college.carmarketplace.model.entities.CarParameter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarDTOTest {

    @Test
    void getDto_mapsScalarFields() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("BMW");
        car.setModel("X5");
        car.setPrice(BigDecimal.valueOf(2_500_000));
        car.setEngineType("Бензин");
        car.setTransmission("Автомат");
        car.setDriveType("Полный");
        car.setCapacity((short) 5);
        car.setBodyType("Кроссовер");
        car.setYear(2020);
        car.setEngineVolume(3.0);
        car.setMileage(50_000);
        car.setLocation("Москва");
        car.setTitle("В отличном состоянии");
        car.setExclusives("Люк");
        car.setRecommends("Зимняя резина");
        car.setCreateAt(LocalDateTime.parse("2024-01-15T10:00:00"));
        car.setBooked(true);

        CarDTO dto = CarDTO.getDto(car);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBrand()).isEqualTo("BMW");
        assertThat(dto.getModel()).isEqualTo("X5");
        assertThat(dto.getPrice()).isEqualByComparingTo("2500000");
        assertThat(dto.getEngineType()).isEqualTo("Бензин");
        assertThat(dto.getTransmission()).isEqualTo("Автомат");
        assertThat(dto.getDriveType()).isEqualTo("Полный");
        assertThat(dto.getCapacity()).isEqualTo((short) 5);
        assertThat(dto.getBodyType()).isEqualTo("Кроссовер");
        assertThat(dto.getYear()).isEqualTo(2020);
        assertThat(dto.getEngineVolume()).isEqualTo(3.0);
        assertThat(dto.getMileage()).isEqualTo(50_000);
        assertThat(dto.getLocation()).isEqualTo("Москва");
        assertThat(dto.getTitle()).isEqualTo("В отличном состоянии");
        assertThat(dto.getExclusives()).isEqualTo("Люк");
        assertThat(dto.getRecommends()).isEqualTo("Зимняя резина");
        assertThat(dto.getCreateAt()).isEqualTo(LocalDateTime.parse("2024-01-15T10:00:00"));
        assertThat(dto.isBooked()).isTrue();
    }

    @Test
    void getDto_groupsImagesAndMapsParameters() {
        Car car = new Car();
        car.setId(2L);
        car.setBrand("Audi");
        car.setModel("A4");
        car.setPrice(BigDecimal.ONE);

        CarImage main = new CarImage();
        main.setImageType("main");
        main.setImageUrl("https://example.com/a.jpg");
        CarImage main2 = new CarImage();
        main2.setImageType("main");
        main2.setImageUrl("https://example.com/b.jpg");
        car.setImageUrl(List.of(main, main2));

        CarParameter param = new CarParameter();
        param.setId(10L);
        param.setImage("p.jpg");
        param.setTitle("Круиз");
        param.setAvailable(true);
        car.setParameters(List.of(param));

        CarDTO dto = CarDTO.getDto(car);

        assertThat(dto.getImageUrl()).containsEntry("main", List.of(
                "https://example.com/a.jpg",
                "https://example.com/b.jpg"
        ));
        assertThat(dto.getParameters()).hasSize(1);
        assertThat(dto.getParameters().getFirst().getId()).isEqualTo(10L);
        assertThat(dto.getParameters().getFirst().getTitle()).isEqualTo("Круиз");
        assertThat(dto.getParameters().getFirst().isAvailable()).isTrue();
    }

    @Test
    void getDto_nullCollections_yieldsNullFields() {
        Car car = new Car();
        car.setId(3L);
        car.setBrand("Lada");
        car.setModel("Vesta");
        car.setPrice(BigDecimal.TEN);

        CarDTO dto = CarDTO.getDto(car);

        assertThat(dto.getImageUrl()).isNull();
        assertThat(dto.getParameters()).isNull();
    }
}
