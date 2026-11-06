package com.edwin.escuela.controller;

import com.edwin.escuela.dto.aulas.AulaRequest;
import com.edwin.escuela.dto.aulas.AulaResponse;
import com.edwin.escuela.entities.Aula;
import com.edwin.escuela.services.aulas.AulaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aulas")
@Tag(name = "API Aulas", description = "Metodos para gestion de aulas")
public class AulaController extends CrudController<AulaRequest, AulaResponse, AulaService>{
    public AulaController(AulaService service) {
        super(service);
    }
}
