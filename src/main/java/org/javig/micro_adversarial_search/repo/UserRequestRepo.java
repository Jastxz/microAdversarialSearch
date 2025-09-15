package org.javig.micro_adversarial_search.repo;

import org.javig.micro_adversarial_search.model.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRequestRepo extends JpaRepository<UserRequest, String>{
}
