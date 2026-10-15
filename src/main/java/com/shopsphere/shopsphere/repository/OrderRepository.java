package com.shopsphere.shopsphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.shopsphere.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
