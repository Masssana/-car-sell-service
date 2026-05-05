package ru.college.carmarketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.college.carmarketplace.model.requests.OrderRequest;
import ru.college.carmarketplace.model.dtos.OrderDTO;

import java.util.List;

public interface OrderService {

    void createOrder(OrderRequest orderRequest, HttpServletRequest request);
    List<OrderDTO> tracker(HttpServletRequest request);
    void updateOrder(OrderRequest orderRequest, HttpServletRequest request);
    OrderDTO getOrderById(Long id);
    void removeOrder(Long id);
}
