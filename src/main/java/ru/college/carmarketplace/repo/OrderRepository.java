package ru.college.carmarketplace.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.college.carmarketplace.model.entities.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Integer userId);

    @Query("SELECT o FROM Order o WHERE o.status = 'canceled' OR o.status = 'completed' ")
    List<Order> findCancelledAndReceivedOrders();

    @Query("SELECT o FROM Order o WHERE o.status IN ('inProgress', 'arrived', 'created')")
    List<Order> findAllByStatus();

    @Query("SELECT o FROM Order o WHERE o.status = 'onSubmit' ")
    List<Order> findAllCreated();

    @Query("SELECT o FROM Order o WHERE " +
            "(CAST(o.number AS string) LIKE %:search% OR CAST(o.id AS string) LIKE %:search%) " +
            "AND o.status IN ('В пути', 'Прибыл')")
    List<Order> findAllByStatusAndSearch(@Param("search") String search);

    @Query("SELECT o FROM Order o WHERE " +
            "(CAST(o.number AS string) LIKE %:search% OR CAST(o.id AS string) LIKE %:search%) " +
            "AND o.status = 'Создан'")
    List<Order> findCreatedOrdersBySearch(@Param("search") String search);

    @Query("SELECT o FROM Order o WHERE " +
            "(CAST(o.id AS string) LIKE %:search% OR CAST(o.number AS string) LIKE %:search%) " +
            "AND (o.status = 'Отменен' OR o.status = 'Получен')")
    List<Order> findCancelledAndReceivedOrdersBySearch(@Param("search") String search);
}
