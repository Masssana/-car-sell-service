package ru.college.carmarketplace.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.college.carmarketplace.model.requests.OrderRequest;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.dtos.CarParameterDTO;
import ru.college.carmarketplace.model.dtos.DeliveryMarkDTO;
import ru.college.carmarketplace.model.dtos.OrderDTO;
import ru.college.carmarketplace.model.entities.*;
import ru.college.carmarketplace.repo.CarRepository;
import ru.college.carmarketplace.repo.OrderRepository;
import ru.college.carmarketplace.repo.UserRepository;
import ru.college.carmarketplace.service.JwtService;
import ru.college.carmarketplace.service.OrderService;
import ru.college.carmarketplace.utils.TokenExtractUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final JwtService jwtService;

    public List<OrderDTO> tracker(HttpServletRequest request) {
        return returnListOfOrdersConvertedToDTO(request);
    }


    private List<OrderDTO> returnListOfOrdersConvertedToDTO(HttpServletRequest request) {
        List<Order> userOrders = orderRepository.findByUserId(getCurrentUserId(request));
        List<OrderDTO> result = new ArrayList<>();
        Integer userId = getCurrentUserId(request);
        AppUser user = userRepository.findById(userId).orElse(null);
        for (Order order : userOrders) {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setNumber(order.getNumber());
            dto.setCar(convertToCarDTO(order.getCar()));
            dto.setStatus(order.getStatus());
            dto.setWhereFrom(order.getWhereFrom());
            dto.setWhereTo(order.getWhereTo());
            dto.setName(user.getName());
            dto.setPhoneNumber(user.getPhoneNumber());

            result.add(dto);
        }
        return result;
    }

    public void removeOrder(Long id) {
        orderRepository.deleteById(id);
    }


    private Integer getCurrentUserId(HttpServletRequest request) {
        
        Integer userId = TokenExtractUtil.extractIdFromToken(request, jwtService);

        validateToken(userId);

        return userId;
    }

    private void validateToken(Integer userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный токен: ID пользователя не найден");
        }
    }

    public Car getProduct(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    private CarDTO convertToCarDTO(Car car) {

        CarDTO dto = CarDTO.getDto(car);

        // Группируем изображения по типам
        if (car.getImageUrl() != null) {
            dto.setImageUrl(car.getImageUrl().stream()
                    .collect(Collectors.groupingBy(
                            CarImage::getImageType,
                            Collectors.mapping(CarImage::getImageUrl, Collectors.toList())
                    )));
        }

        if (car.getParameters() != null && !car.getParameters().isEmpty()) {
            dto.setParameters(car.getParameters().stream()
                    .map(param -> new CarParameterDTO(
                            param.getId(),
                            param.getImage(),
                            param.getTitle(),
                            param.isAvailable()
                    ))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public void createOrder(OrderRequest orderRequest, HttpServletRequest req) {
        Integer userId = getCurrentUserId(req);
        AppUser user = userRepository.findById(userId).orElse(null);
        Car car = getProduct(orderRequest.getId());
        Order order = new Order();
        car.setBooked(true);
        order.setUser(user);
        order.setCar(car);
        order.setStatus("onSubmit");
        orderRepository.save(order);
    }

    @Override
    public void updateOrder(OrderRequest orderRequest, HttpServletRequest request) {
        Order order = orderRepository.findById(orderRequest.getId()).orElse(null);
        order.setStatus(orderRequest.getStatus());
        order.setWhereTo(orderRequest.getWhereTo());
        order.setWhereFrom(orderRequest.getWhereFrom());
        order.setNumber(orderRequest.getNumber());
        if (orderRequest.getMarks() != null) {
            order.getMarks().clear();

            for (DeliveryMarkDTO markDto : orderRequest.getMarks()) {
                DeliveryMark mark = new DeliveryMark();
                mark.setTitle(markDto.getTitle());
                mark.setOrder(order);
                order.getMarks().add(mark);
            }
        }
        orderRepository.save(order);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id).map(OrderDTO::toDTO).orElse(null);
    }

}
