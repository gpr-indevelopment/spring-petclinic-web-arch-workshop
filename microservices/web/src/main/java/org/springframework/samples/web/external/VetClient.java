package org.springframework.samples.web.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.samples.web.vet.Vet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@FeignClient(value = "vet-client", url = "${vet.host}")
public interface VetClient {

	@RequestMapping(method = RequestMethod.GET, value = "/vets")
	Page<Vet> findPaginated(@RequestParam int page);

	@RequestMapping(method = RequestMethod.GET, value = "/all-vets")
	Collection<Vet> findAllVets();
}
