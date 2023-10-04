package com.redis;

import java.util.List;
import java.util.Optional;

public interface IOrderService {
	Integer saveOrder(Orders order);

	List<Orders> getOrders();

	Optional<Orders> getOrders(Integer id);

	void deleteOrders(Integer id);

}
