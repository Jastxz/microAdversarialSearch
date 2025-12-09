package org.javig.micro_adversarial_search.service.impl;

import org.javig.engine.Minimax;
import org.javig.micro_adversarial_search.model.TableroResponse;
import org.javig.micro_adversarial_search.service.SearchService;
import org.javig.tipos.Mundo;
import org.javig.tipos.Tablero;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    @Override
    public TableroResponse calculaJugada(Mundo mundo) {
        Tablero tablero = Minimax.negamax(mundo);
        System.out.println(tablero.getMatrix().getData());
        return new TableroResponse(tablero.getMatrix().getData());
    }

}
