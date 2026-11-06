package com.edwin.escuela.controller;

import com.edwin.escuela.dto.inscripciones.InscripcionRequest;
import com.edwin.escuela.dto.inscripciones.InscripcionResponse;
import com.edwin.escuela.services.inscripciones.InscripcionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "API Inscripciones", description = "Metodos para gestion de inscripciones")
public class InscripcionController extends CrudController<InscripcionRequest, InscripcionResponse, InscripcionService>{
    public InscripcionController(InscripcionService service) {
        super(service);
    }
}
