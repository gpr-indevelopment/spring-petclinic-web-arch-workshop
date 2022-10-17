package org.springframework.samples.petclinic.owner;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;

@Component
public class OwnerFunction implements Function<APIGatewayV2HTTPEvent, String> {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OwnerRepository ownerRepository;

	@Override
	public String apply(APIGatewayV2HTTPEvent event) {
		if (event == null || event.getRawPath() == null) {
			return "";
		}
		if (event.getRawPath().equals("/owners/pet-types")) {
			return serializeResponse(ownerRepository.findPetTypes());
		} else if (event.getRawPath().contains("/owners")) {
			if (event.getRequestContext().getHttp().getMethod().equals("POST")) {
				Owner owner = deserializeRequestBody(event, Owner.class);
				ownerRepository.save(owner);
				return serializeResponse(owner);
			} else if (event.getRequestContext().getHttp().getMethod().equals("GET")) {
				if (event.getQueryStringParameters() == null || event.getQueryStringParameters().get("lastName") == null) {
					return findOwner(event);
				} else {
					return findOwners(event);
				}
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	private String findOwner(APIGatewayV2HTTPEvent event) {
		String ownerIdStr = event.getRawPath().replaceAll("/", "").replaceAll("owners", "");
		if (ownerIdStr.isBlank()) {
			return serializeResponse(new Owner());
		}
		Integer ownerId = Integer.parseInt(ownerIdStr);
		return serializeResponse(ownerRepository.findById(ownerId));
	}

	private String findOwners(APIGatewayV2HTTPEvent event) {
		String pageStr = event.getQueryStringParameters().get("page");
		if (pageStr == null || pageStr.isBlank()) {
			pageStr = "1";
		}
		String lastName = event.getQueryStringParameters().get("lastName");
		int page = Integer.parseInt(pageStr);
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return serializeResponse(ownerRepository.findByLastName(lastName, pageable));
	}

	private <T> T deserializeRequestBody(APIGatewayV2HTTPEvent event, Class<T> clazz) {
		try {
			return objectMapper.readValue(event.getBody(), clazz);
		} catch (JsonProcessingException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while deserializing request body.");
		}
	}

	private String serializeResponse(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while serializing response.");
		}
	}
}
