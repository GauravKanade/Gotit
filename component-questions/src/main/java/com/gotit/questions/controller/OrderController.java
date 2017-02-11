package com.gotit.questions.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gotit.entity.Order;
import com.gotit.entity.Ticket;
import com.gotit.entity.User;
import com.gotit.exceptions.ValidationException;
import com.gotit.questions.elasticservice.ElasticSearchConstants;
import com.gotit.questions.elasticservice.ElasticSearchService;
import com.gotit.questions.util.QuestionUtil;
import com.gotit.util.Log;

@RestController
@RequestMapping(value = "api/orders")
public class OrderController implements ElasticSearchConstants {

	@Autowired
	QuestionUtil questionUtil;

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	UserController userController;

	@RequestMapping(value = "/order/save", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveOrder(@RequestBody Order order)
			throws ValidationException, IndexNotFoundException {
		Log.i(">>OrderController.saveOrder() - order: " + order);
		if (ObjectUtils.isEmpty(order.getUserId())) {
			ValidationException validationException = new ValidationException("order does not contain userId");
			Log.e("Exception in prcessing order", validationException);
			throw validationException;
		}

		User user = userController.getUserById(order.getUserId());
		if (ObjectUtils.isEmpty(user)) {
			ValidationException validationException = new ValidationException("userId is invalid. No such user");
			Log.e("Exception in prcessing order", validationException);
			throw validationException;
		}

		int numberOfTickets = (int) (order.getPayment().getAmount() / 10);
		if (numberOfTickets > 0) {
			List<Ticket> ticketsGenerated = generateNewTicket(order, numberOfTickets);
			user.getTickets().addAll(ticketsGenerated);
			elasticSearchService.saveUser(user);
			return elasticSearchService.saveOrder(order);
		} else {
			ValidationException validationException = new ValidationException("invalid Amount");
			Log.e("Exception in prcessing order", validationException);
			throw validationException;
		}

	}

	@RequestMapping(value = "/api/order/{orderId}", method = RequestMethod.GET)
	public @ResponseBody Order getOrderById(@PathVariable String orderId) {
		Log.i(">>OrderController.getOrderById() - orderId: " + orderId);
		Order order = elasticSearchService.searchById(INDEX_ORDER, TYPE_ORDER, orderId, Order.class);
		return order;
	}

	@RequestMapping(value = "/api/order/userId/{userId}", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> getOrderByUserId(@PathVariable String userId,
			@RequestParam String template) throws IndexNotFoundException, ValidationException {
		Log.i(">>OrderController.getOrderByUserId() - userId: " + userId);
		User user = userController.getUserById(userId);
		if (ObjectUtils.isEmpty(user)) {
			ValidationException validationException = new ValidationException("userId is invalid. No such user");
			Log.e("Exception in prcessing order", validationException);
			throw validationException;
		}
		List<String> orderIds = new ArrayList<>();
		for (Ticket ticket : user.getTickets()) {
			if (!orderIds.contains(ticket.getOrderId()))
				orderIds.add(ticket.getOrderId());
		}
		List<Map<String, Object>> orderListMap = new ArrayList<>();
		for (String orderId : orderIds) {
			Map<String, Object> orderMap = elasticSearchService.searchById(INDEX_ORDER, TYPE_ORDER, orderId);
			// TODO: add logic for template
			orderListMap.add(orderMap);
		}
		return orderListMap;
	}

	private List<Ticket> generateNewTicket(Order order, int numberOfTickets) {
		List<Ticket> ticketList = new ArrayList<>();
		List<String> ticketIdList = new ArrayList<>();
		for (int i = 1; i <= numberOfTickets; i++) {
			Ticket ticket = new Ticket();
			ticket.setTicketAvailable(true);
			ticket.setCompleted(false);
			ticket.setTicketId(RandomStringUtils.randomAlphanumeric(15));
			ticket.setTestPaperId(null);
			ticket.setTarget(null);
			ticket.setOrderId(order.getOrderId());
			ticketList.add(ticket);
			ticketIdList.add(ticket.getTicketId());
		}
		String[] ticketArray = questionUtil.convertToArray(ticketIdList, String.class);
		order.setTicketIds(ticketArray);
		return ticketList;
	}
}
