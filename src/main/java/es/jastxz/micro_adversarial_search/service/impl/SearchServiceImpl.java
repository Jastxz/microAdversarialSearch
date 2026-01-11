package es.jastxz.micro_adversarial_search.service.impl;

import es.jastxz.engine.Minimax;
import es.jastxz.tipos.Mundo;
import es.jastxz.tipos.Tablero;
import org.springframework.stereotype.Service;

import es.jastxz.micro_adversarial_search.model.TableroResponse;
import es.jastxz.micro_adversarial_search.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

    @Override
    public TableroResponse calculaJugada(Mundo mundo) {
        Tablero tablero = Minimax.negamax(mundo);
        System.out.println(tablero.getMatrix().getData());
        return new TableroResponse(tablero.getMatrix().getData());
    }

}
