package com.yakubovskiy.project.service.interfaces;

import org.springframework.stereotype.Service;

@Service
public interface MapperService<Entity, Dto> {
    Dto toDto(final Entity obj);

    Entity toEntity(final Dto objDto);
}
