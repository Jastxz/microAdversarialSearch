package org.javig.micro_adversarial_search.controller;

import org.javig.micro_adversarial_search.model.MundoRequest;
import org.javig.micro_adversarial_search.model.TableroResponse;
import org.javig.micro_adversarial_search.service.TresEnRayaService;
import org.javig.tipos.Movimiento;
import org.javig.tipos.Mundo;
import org.javig.tipos.Posicion;
import org.javig.tipos.SmallMatrix;
import org.javig.tipos.Tablero;
import org.javig.util.Util;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/tresenraya")
public class TresEnRayaController {
    
    private final TresEnRayaService tresEnRayaService;

    public TresEnRayaController(TresEnRayaService tresEnRayaService) {
        this.tresEnRayaService = tresEnRayaService;
    }

    public ResponseEntity<?> calculaJugada(MundoRequest request) {
        SmallMatrix matrix = new SmallMatrix(request.getData());
        Tablero tablero = new Tablero(matrix);
        Posicion posicion = new Posicion(request.getPosicionFila(), request.getPosicionColumna());
        Movimiento movimiento = new Movimiento(tablero, posicion);
        Mundo mundo = new Mundo(movimiento, Util.juego3enRaya, request.getDificultad(), request.getProfundidad(), 
            request.getMarca(), request.getTurno(), request.getSeleccionado(), true);
        TableroResponse response = tresEnRayaService.calculaJugada(mundo);
        return ResponseEntity.ok(response);
    }

}
