package com.gotit.questions.payment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PaymentService {

	public Map<String, Object> validateAndServicePayment(Map<String, Object> requestMap) {
		// TODO: call third party payment gateway to process payment request
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("success", true);
		return responseMap;
	}
}
