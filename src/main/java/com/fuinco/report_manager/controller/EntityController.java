package com.fuinco.report_manager.controller;

import com.fuinco.report_manager.dto.ApiResponse;
import com.fuinco.report_manager.report.entity.Entity;
import com.fuinco.report_manager.report.entity.EntityField;
import com.fuinco.report_manager.service.EntityFieldsService;
import com.fuinco.report_manager.service.EntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;

@RestController
@RequestMapping("/entity")
public class EntityController {
    private final EntityService entityService;
    private final EntityFieldsService entityFieldsService;

    public EntityController(EntityService entityService, EntityFieldsService entityFieldsService) {
        this.entityService = entityService;
        this.entityFieldsService = entityFieldsService;
    }
    @PostMapping("")
    public ResponseEntity<Entity> createEntity(@RequestBody Entity entity) {
        if(entity == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Entity savedEntity = entityService.createEntity(entity);
        return ResponseEntity.ok(savedEntity);
    }
    @GetMapping("/all")
    public ApiResponse<List<Entity>> getAllEntities() {
        return entityService.findAll();
    }
    @GetMapping("/{id}")
    public ApiResponse<Entity> getEntityById(@PathVariable String id) {
        return entityService.findById(id);
    }
    @PutMapping("/update/{id}")
    public ApiResponse<Entity> updateEntity( @RequestBody Entity entity) {
        return entityService.update(entity);
    }
    @GetMapping("/{name}")
    public Entity getEntityByName(@PathVariable String name) {
        return entityService.getEntityByName(name);
    }
    @PostMapping("/field")
    public EntityField createEntityField(@RequestBody EntityField field) {
        return entityFieldsService.create(field);
    }
    @GetMapping("/operator/{entity}")
    public ApiResponse<List<String>> getOperators(@PathVariable String entity, @RequestBody Field field) {
        return entityService.getEntityFieldOperator(entity, field.getName());
    }

}
