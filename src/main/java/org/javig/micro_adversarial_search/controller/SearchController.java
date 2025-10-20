package org.javig.micro_adversarial_search.controller;

import java.util.Arrays;

import org.javig.engine.FuncionesGato;
import org.javig.micro_adversarial_search.model.ErrorResponse;
import org.javig.micro_adversarial_search.model.MundoRequest;
import org.javig.micro_adversarial_search.model.TableroResponse;
import org.javig.micro_adversarial_search.service.SearchService;
import org.javig.tipos.Movimiento;
import org.javig.tipos.Mundo;
import org.javig.tipos.Posicion;
import org.javig.tipos.SmallMatrix;
import org.javig.tipos.Tablero;
import org.javig.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/v0")
public class SearchController {
    
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/tresenraya")
    public ResponseEntity<?> calculaJugadaRaya(@RequestBody MundoRequest request) {
        return calculaJugada(request, Util.juego3enRaya);
    }

    @PostMapping("/gatos")
    public ResponseEntity<?> calculaJugadaGatos(@RequestBody MundoRequest request) {
        return calculaJugada(request, Util.juegoGato);
    }

    @PostMapping("/damas")
    public ResponseEntity<?> calculaJugadaDamas(@RequestBody MundoRequest request) {
        return calculaJugada(request, Util.juegoDamas);
    }

    private ResponseEntity<?> calculaJugada(MundoRequest request, String juego) {
        try {
            // Comprueba si los datos son inválidos
            if (datosNoValidos(request, juego)) {
                ErrorResponse error = new ErrorResponse(
                    400, 
                    "Bad Request", 
                    "Petición mal formulada"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Procesamiento normal
            SmallMatrix matrix = new SmallMatrix(request.getData());
            Tablero tablero = new Tablero(matrix);
            Posicion posicion = new Posicion(request.getPosicionFila(), request.getPosicionColumna());
            Movimiento movimiento = new Movimiento(tablero, posicion);
            Mundo mundo = new Mundo(movimiento, Util.juego3enRaya, request.getDificultad(), request.getProfundidad(), 
                request.getMarca(), request.getTurno(), true);
            TableroResponse response = searchService.calculaJugada(mundo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Error 500 para cualquier otro problema no controlado
            ErrorResponse error = new ErrorResponse(
                500, 
                "Internal Server Error", 
                "Error interno del servidor"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private boolean datosNoValidos(MundoRequest request, String juego) {
        boolean raya = juego.equals(Util.juego3enRaya);
        boolean gato = juego.equals(Util.juegoGato);
        boolean damas = juego.equals(Util.juegoDamas);
        boolean turnoNoValido = request.getTurno() < 1 || request.getTurno() > 2;
        boolean dificultadNoValida = request.getDificultad() < 1 || request.getDificultad() > 4;

        boolean matrizNoValida = true;
        boolean posicionNoValida = true;
        if (raya) {
            matrizNoValida = request.getData().length != 3 || request.getData()[0].length != 3 
                || request.getData()[1].length != 3 || request.getData()[2].length != 3;
            posicionNoValida = request.getPosicionFila() < 0 || request.getPosicionFila() > 2 
            || request.getPosicionColumna() < 0 || request.getPosicionColumna() > 2;
        } else if (gato || damas) {
            matrizNoValida = request.getData().length != 8 || request.getData()[0].length != 8 
                || request.getData()[1].length != 8 || request.getData()[2].length != 8;
            posicionNoValida = request.getPosicionFila() < 0 || request.getPosicionFila() > 7 
                || request.getPosicionColumna() < 0 || request.getPosicionColumna() > 7;
        }
        
        boolean marcaNoValida = true;
        boolean profundidadNoValida = true;
        if (raya) {
            marcaNoValida = request.getMarca() < 1 || request.getMarca() > 2;
            profundidadNoValida = request.getProfundidad() != 1 && request.getProfundidad() != 9;
        } else if (gato) {
            marcaNoValida = Arrays.stream(FuncionesGato.nombresGatos).noneMatch(m -> m == request.getMarca()) 
                && FuncionesGato.nombreRaton != request.getMarca();
            profundidadNoValida = request.getProfundidad() != 1 && request.getProfundidad() != 2
                && request.getProfundidad() != 4 && request.getProfundidad() != 6
                && request.getProfundidad() != 8;
        } else if (damas) {
            marcaNoValida = request.getMarca() < 1 || request.getMarca() > 2;
            profundidadNoValida = request.getProfundidad() < 1 || request.getProfundidad() > 5;
        }
        
        return matrizNoValida || posicionNoValida || marcaNoValida || turnoNoValido 
                || dificultadNoValida || profundidadNoValida;
    }

}
