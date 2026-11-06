package com.edwin.escuela.controller;

import com.edwin.escuela.dto.cursos.CursoRequest;
import com.edwin.escuela.dto.cursos.CursoResponse;
import com.edwin.escuela.services.cursos.CursoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "API Cursos", description = "Metodos para gestion de cursos")
public class CursosController extends CrudController<CursoRequest, CursoResponse, CursoService> {
    public CursosController(CursoService service) {
        super(service);
    }
}
