package com.redis;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("order-service")
public class OrderService implements IOrderService {
	@Autowired
	IOrderRepository repository;
	Logger logger = LoggerFactory.getLogger(getClass());
//	static String field1;
	public OrderService() {
		System.out.println("Order service bean initializing");
	}
	static int method1() {
		return 2;
	}
	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Integer saveOrder(Orders order) {
		repository.save(order);//mock
		return order.getId();
	}

	@Override
	public List<Orders> getOrders() {
		logger.debug("getting all records");
		return repository.findAll();
	}

	@Override
	public Optional<Orders> getOrders(Integer id) {
		return repository.findById(id);
	}

	@Override
	@Transactional
	public void deleteOrders(Integer id) {
		repository.deleteById(id);
	}
}
