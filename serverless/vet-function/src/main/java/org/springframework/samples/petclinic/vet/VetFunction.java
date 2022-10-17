package org.springframework.samples.petclinic.vet;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;

@Component
public class VetFunction implements Function<APIGatewayV2HTTPEvent, String> {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private VetRepository vetRepository;

	@Override
	public String apply(APIGatewayV2HTTPEvent event) {
		if (event == null || event.getRawPath() == null) {
			return "";
		}
		switch (event.getRawPath()) {
			case "/vets":
				int pageSize = 5;
				String pageStr = event.getQueryStringParameters().get("page");
				if (pageStr == null || pageStr.isBlank()) {
					pageStr = "1";
				}
				Integer page = Integer.parseInt(pageStr);
				Pageable pageable = PageRequest.of(page - 1, pageSize);
				return serializeResponse(vetRepository.findAll(pageable));
			case "/all-vets":
				return serializeResponse(this.vetRepository.findAll());
			default:
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	private String serializeResponse(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
