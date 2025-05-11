package com.sismics.docs.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import com.sismics.docs.rest.resource.UserRequestResource;

@ApplicationPath("/api")
public class DocsRestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(UserRequestResource.class); // 注册资源类
        return resources;
    }
}
