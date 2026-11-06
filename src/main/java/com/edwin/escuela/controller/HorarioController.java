package com.edwin.escuela.controller;

import com.edwin.escuela.dto.horarios.HorarioRequest;
import com.edwin.escuela.dto.horarios.HorarioResponse;
import com.edwin.escuela.services.horarios.HorarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/horarios")
@Tag(name = "API Horarios", description = "Metodos para gestion de horarios")
public class HorarioController extends CrudController<HorarioRequest, HorarioResponse, HorarioService>{
    public HorarioController(HorarioService service) {
        super(service);
    }
}
