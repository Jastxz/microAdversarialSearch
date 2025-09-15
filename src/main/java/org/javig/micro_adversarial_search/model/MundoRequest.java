package org.javig.micro_adversarial_search.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MundoRequest {
    
    private int[][] data;
    private int posicionFila, posicionColumna;
    private int dificultad;
    private int profundidad;
    private int marca;
    private int turno;
    private int seleccionado;

}
