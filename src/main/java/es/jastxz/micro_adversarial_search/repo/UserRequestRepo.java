package es.jastxz.micro_adversarial_search.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import es.jastxz.micro_adversarial_search.model.UserRequest;

public interface UserRequestRepo extends JpaRepository<UserRequest, String> {
}
