package com.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController // Bean
public class OrderController {// singleton, dependent
	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	IOrderService orderService; // inject
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public void createOrder( @RequestBody Orders order) {
		orderService.saveOrder(order);
	}

	@GetMapping
	List<Orders> getOrders() {
		return orderService.getOrders();
	}

	@GetMapping("/{id}")
	@Cacheable(value = "orders", key="#id"  )
	ResponseEntity<Orders> getOrdersById(@PathVariable Integer id) {
		ResponseEntity<Orders> responseEntity;
		try {
			Orders order = orderService.getOrders(id).get();
			responseEntity = new ResponseEntity<>(order, HttpStatus.OK);
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return responseEntity;
	}

	/*
	 * @GetMapping("/mobile/{mobile}") Orders getOrdersByMobile(@PathVariable String
	 * mobile) { return orderService.getOrdersByMobile(mobile); }
	 */

	@DeleteMapping("/{id}")
	@CacheEvict(value="users", allEntries = true)
	void deleteOrders(@PathVariable Integer id) {
		orderService.deleteOrders(id);
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
		Map<String, String> errorMessages = new HashMap<>();
		List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
		allErrors.forEach((error) -> {
			FieldError fieldError = ((FieldError) error);
			String fieldName = fieldError.getField();
			String errorMessage = fieldError.getDefaultMessage();
			errorMessages.put(fieldName, errorMessage);
		});
		return errorMessages;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	String handleAllExceptions(Exception ex) {
		ex.printStackTrace();
		return ex.getMessage();
	}
}
