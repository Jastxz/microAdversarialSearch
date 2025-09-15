package org.javig.micro_adversarial_search.service;

import org.javig.micro_adversarial_search.model.TableroResponse;
import org.javig.tipos.Mundo;

public interface TresEnRayaService {
    
    TableroResponse calculaJugada(Mundo mundo);

}
