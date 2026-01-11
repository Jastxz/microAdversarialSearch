package es.jastxz.micro_adversarial_search.service;

import es.jastxz.tipos.Mundo;

import es.jastxz.micro_adversarial_search.model.TableroResponse;

public interface SearchService {

    TableroResponse calculaJugada(Mundo mundo);

}
