package com.shopsphere.shopsphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.shopsphere.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}