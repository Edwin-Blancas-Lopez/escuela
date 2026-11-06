package com.edwin.escuela.controller;

import com.edwin.escuela.dto.grupos.GrupoRequest;
import com.edwin.escuela.dto.grupos.GrupoResponse;
import com.edwin.escuela.services.grupos.GrupoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grupos")
@Tag(name = "API Grupos", description = "Metodos para gestion de grupos")
public class GrupoController extends CrudController<GrupoRequest, GrupoResponse, GrupoService>{
    public GrupoController(GrupoService service) {
        super(service);
    }
}
