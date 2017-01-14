package com.gotit.questions.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		validateUser(user);
		if (user.getCreatedOn() == 0)
			user.setCreatedOn(System.currentTimeMillis());
		return elasticSearchService.saveUser(user);
	}

	private void validateUser(User user) throws ValidationException {
		if (ObjectUtils.isEmpty(user.getUserId()))
			user.setUserId(String.valueOf(System.currentTimeMillis()));
		if (ObjectUtils.isEmpty(user.getEmailId()))
			throw new ValidationException("user object does not have emailId");
		if (ObjectUtils.isEmpty(user.getUserName()))
			throw new ValidationException("user object does not have userName");
		Map<String, Object> userByEmail = loginUserByEmailId(user.getEmailId());
		if (userByEmail.get("success").toString().equals("true"))
			throw new ValidationException("user with email " + user.getEmailId() + " already linked to anohter user");

	}

	@RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
	public @ResponseBody User getUserById(@PathVariable(value = "userId") String userId) throws IndexNotFoundException {
		Map<String, Object> elasticResponse = elasticSearchService.searchById(INDEX_USER, TYPE_USER, userId);
		return questionUtil.createObjectFromMap(elasticResponse, User.class);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/user/login/{emailId}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> loginUserByEmailId(@PathVariable(value = "emailId") String emailId) {
		Map<String, Object> loginResponse = new HashMap<>();
		Map<String, Object> elasticResponse = elasticSearchService.searchByKeyword(INDEX_USER, TYPE_USER, "",
				"emailId=" + emailId, 0, 10, null, false);
		if (!ObjectUtils.isEmpty(elasticResponse.get(SEARCH_RESULT))) {
			User user = questionUtil.createObjectFromMap(
					((List<Map<String, Object>>) elasticResponse.get(SEARCH_RESULT)).get(0), User.class);
			loginResponse.put("success", true);
			loginResponse.put("user", user);
			return loginResponse;
		}
		loginResponse.put("success", false);
		loginResponse.put("error", "User with email " + emailId + " does not exist");
		return loginResponse;
	}

	@RequestMapping(value = "/ticket/generate", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> generateTicket(@RequestBody Map<String, Object> requestMap)
			throws ValidationException, IndexNotFoundException {
		Map<String, Object> responseMap = new HashMap<>();
		if (requestMap.containsKey("userId")) {
			User user = getUserById(requestMap.get("userId").toString());
			Map<String, Object> paymentResponseMap = paymentService.validateAndServicePayment(requestMap);
			if (Boolean.valueOf(paymentResponseMap.get("success").toString())) {
				Ticket ticket = generateNewTicket("paymentDetails");
				List<Ticket> ticketList = user.getTickets();
				ticketList.add(ticket);
				user.setTickets(ticketList);
				elasticSearchService.saveUser(user);

				responseMap.put("success", true);
				responseMap.put("userId", user.getUserId());
				responseMap.put("time", System.currentTimeMillis());
				responseMap.put("ticketId", ticket.getTicketId());
				return responseMap;
			}
			responseMap.put("success", false);
			responseMap.put("error", "payment failed");
			responseMap.put("errorCode", "PAYMENT");
			return responseMap;
		} else {
			responseMap.put("success", false);
			responseMap.put("error", "ticket request does not contain userId");
			responseMap.put("errorCode", "VALIDATION");
			return responseMap;
		}
	}

	private Ticket generateNewTicket(String paymentDetails) {
		Ticket ticket = new Ticket();
		ticket.setCreatedOn(System.currentTimeMillis());
		ticket.setPaymentInformation(paymentDetails);
		ticket.setTicketAvailable(true);
		ticket.setTicketId(String.valueOf((long) (Math.random() * 2737868362876L)));
		ticket.setTestPaperId(null);
		ticket.setTarget(null);
		return ticket;
	}
}
