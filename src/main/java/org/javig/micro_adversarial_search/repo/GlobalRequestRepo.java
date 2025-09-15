package org.javig.micro_adversarial_search.repo;

import org.javig.micro_adversarial_search.model.GlobalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalRequestRepo extends JpaRepository<GlobalRequest, Long>{
}
