package com.edwin.escuela.controller;


import com.edwin.escuela.dto.mestros.MaestroRequest;
import com.edwin.escuela.dto.mestros.MaestroResponse;
import com.edwin.escuela.services.maestros.MaestroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maestros")
@Tag(name = "API Maestros", description = "Metodos para gestion de maestros")
public class MaestroController extends CrudController<MaestroRequest, MaestroResponse, MaestroService>{
    public MaestroController(MaestroService service) {
        super(service);
    }
}
