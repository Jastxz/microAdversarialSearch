package org.javig.micro_adversarial_search.service.impl;

import org.javig.engine.Minimax;
import org.javig.micro_adversarial_search.model.TableroResponse;
import org.javig.micro_adversarial_search.service.TresEnRayaService;
import org.javig.tipos.Mundo;
import org.javig.tipos.Tablero;

public class TresEnRayaServiceImpl implements TresEnRayaService {

    @Override
    public TableroResponse calculaJugada(Mundo mundo) {
        Tablero tablero = Minimax.negamax(mundo);
        return new TableroResponse(tablero.getMatrix().getData());
    }

}
