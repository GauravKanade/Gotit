package com.gotit.questions.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gotit.entity.Ticket;
import com.gotit.entity.User;
import com.gotit.exceptions.ValidationException;
import com.gotit.questions.elasticservice.ElasticSearchConstants;
import com.gotit.questions.elasticservice.ElasticSearchService;
import com.gotit.questions.payment.PaymentService;
import com.gotit.questions.util.QuestionUtil;
import com.gotit.util.Log;

@RestController
@RequestMapping(value = "/api/users")
public class UserController implements ElasticSearchConstants {

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	QuestionUtil questionUtil;

	@Autowired
	PaymentService paymentService;

	@RequestMapping(value = "/user/save", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveUser(@RequestBody User user) throws ValidationException {
		Log.i(">>UserController.saveUser() - user: " + user);
		validateUser(user);
		if (isNewUser(user)) {
			elasticSearchService.applyIndexIfIndexDoesNotExist(INDEX_USER, "USER");
			List<Ticket> ticketList = generateNewTicket("new user", "3");
			user.setTickets(ticketList);
			user.setCreatedOn(System.currentTimeMillis());
		}
		return elasticSearchService.saveUser(user);
	}

	private boolean isNewUser(User user) throws ValidationException {
		if (ObjectUtils.isEmpty(user.getUserId())) {
			try {
				user.setUserId(RandomStringUtils.randomAlphabetic(8) + "-"
						+ String.valueOf(System.currentTimeMillis()).substring(5));
				Map<String, Object> userByEmail = loginUserByEmailId(user.getEmailId());
				if (userByEmail.get("success").toString().equals("true"))
					throw new ValidationException("user with email " + user.getEmailId()
							+ " already linked to another user, or userId is null");
				return true;
			} catch (IndexNotFoundException e) {
				return true;
			}
		}
		return false;
	}

	private void validateUser(User user) throws ValidationException {
		if (ObjectUtils.isEmpty(user.getEmailId()))
			throw new ValidationException("user object does not have emailId");
		if (ObjectUtils.isEmpty(user.getUserFirstName()))
			throw new ValidationException("user object does not have userFirstName");

	}

	@RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
	public @ResponseBody User getUserById(@PathVariable(value = "userId") String userId)
			throws IndexNotFoundException, ValidationException {
		Log.i(">>UserController.getUserById() - userId: " + userId);
		Map<String, Object> elasticResponse = elasticSearchService.searchById(INDEX_USER, TYPE_USER, userId);
		if (ObjectUtils.isEmpty(elasticResponse))
			throw new ValidationException("user with userId: " + userId + " does not exist in database");
		return questionUtil.createObjectFromMap(elasticResponse, User.class);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/user/login/{emailId}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> loginUserByEmailId(@PathVariable(value = "emailId") String emailId)
			throws IndexNotFoundException {
		Log.i(">>UserController.loginUserByEmailId() - emailId=" + emailId);
		elasticSearchService.applyIndexIfIndexDoesNotExist(INDEX_USER, "USER");
		Map<String, Object> loginResponse = new HashMap<>();
		try {
			Map<String, Object> elasticResponse = elasticSearchService.searchByKeyword(INDEX_USER, TYPE_USER, "",
					"emailId=" + emailId, 0, 10, null, false);
			if (!ObjectUtils.isEmpty(elasticResponse.get(SEARCH_RESULT))) {
				User user = questionUtil.createObjectFromMap(
						((List<Map<String, Object>>) elasticResponse.get(SEARCH_RESULT)).get(0), User.class);
				loginResponse.put("success", true);
				loginResponse.put("user", user);
				return loginResponse;
			}
		} catch (IndexNotFoundException e) {
			Log.e("GotitServer", e);
		}
		loginResponse.put("success", false);
		loginResponse.put("error", "User with email " + emailId + " does not exist");
		return loginResponse;
	}

	@RequestMapping(value = "/ticket/generate", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> generateTicket(@RequestBody Map<String, Object> requestMap)
			throws ValidationException, IndexNotFoundException {
		Log.i(">>UserController.generateTicket() - requestMap=" + requestMap);
		Map<String, Object> responseMap = new HashMap<>();
		if (requestMap.containsKey("userId")) {
			if (requestMap.containsKey("numberOfTickets")) {
				User user = getUserById(requestMap.get("userId").toString());
				Map<String, Object> paymentResponseMap = paymentService.validateAndServicePayment(requestMap);
				if (Boolean.valueOf(paymentResponseMap.get("success").toString())) {
					List<Ticket> ticketsGenerated = generateNewTicket("paymentDetails",
							requestMap.get("numberOfTickets").toString());
					List<Ticket> ticketList = user.getTickets();
					ticketList.addAll(ticketsGenerated);
					user.setTickets(ticketList);
					elasticSearchService.saveUser(user);

					responseMap.put("success", true);
					responseMap.put("userId", user.getUserId());
					responseMap.put("time", System.currentTimeMillis());
					return responseMap;
				}
				responseMap.put("success", false);
				responseMap.put("error", "payment failed");
				responseMap.put("errorCode", "PAYMENT");
				return responseMap;
			} else {
				responseMap.put("success", false);
				responseMap.put("error", "ticket request does not contain numberOfTickets");
				responseMap.put("errorCode", "VALIDATION");
				return responseMap;
			}
		} else {
			responseMap.put("success", false);
			responseMap.put("error", "ticket request does not contain userId");
			responseMap.put("errorCode", "VALIDATION");
			return responseMap;
		}

	}

	private List<Ticket> generateNewTicket(String paymentDetails, String numberOfTickets) {
		List<Ticket> ticketList = new ArrayList<>();

		int ticketCount = Integer.parseInt(numberOfTickets);
		for (int i = 1; i <= ticketCount; i++) {
			Ticket ticket = new Ticket();
			ticket.setTicketAvailable(true);
			ticket.setTicketId(RandomStringUtils.randomAlphanumeric(15));
			ticket.setTestPaperId(null);
			ticket.setTarget(null);
			ticket.setOrderId("newUserOrder");
			ticketList.add(ticket);
		}
		return ticketList;
	}
}
