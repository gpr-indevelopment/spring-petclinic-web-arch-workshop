package org.springframework.samples.web.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.samples.web.owner.Owner;
import org.springframework.samples.web.owner.PetType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "owner-client", url = "${owner.host}")
public interface OwnerClient {

	@GetMapping("/owners/{ownerId}")
	Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId);

	@PostMapping("/owners")
	Owner saveOwner(@RequestBody Owner owner);

	@GetMapping("/owners")
	Page<Owner> findOwners(@RequestParam(defaultValue = "1") int page, @RequestParam String lastName);

	@GetMapping("/owners/pet-types")
	List<PetType> findPetTypes();
}
