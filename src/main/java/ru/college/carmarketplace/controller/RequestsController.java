package ru.college.carmarketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.college.carmarketplace.annotations.AdminOnly;
import ru.college.carmarketplace.model.requests.OrderRequest;
import ru.college.carmarketplace.model.dtos.OrderDTO;
import ru.college.carmarketplace.service.AdminService;
import ru.college.carmarketplace.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/requests")
public class RequestsController {
    private final OrderService orderService;
    private final AdminService adminService;

    @GetMapping("/get")
    public ResponseEntity<List<OrderDTO>> getTracker(HttpServletRequest request) {
        return ResponseEntity.ok(orderService.tracker(request));
    }

    @PostMapping("/add")
    public void addOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest request){
        orderService.createOrder(orderRequest, request);
    }

    @GetMapping("/active")
    @AdminOnly
    public ResponseEntity<List<OrderDTO>> getReadyProducts(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminService.getReadyOrders(search));
    }

    @GetMapping("/inactive")
    @AdminOnly
    public ResponseEntity<List<OrderDTO>> getNotReadyProducts(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminService.getNotReadyOrders(search));
    }

    @GetMapping("/new")
    @AdminOnly
    public ResponseEntity<List<OrderDTO>> getNewProducts(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminService.getNewOrders(search));
    }

    @PostMapping("/update")
    @AdminOnly
    public void updateOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest request){
        orderService.updateOrder(orderRequest, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
