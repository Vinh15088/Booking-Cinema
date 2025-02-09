package com.vinhSeo.BookingCinema.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile({"dev", "test"})
public class OpenAPIConfig {

    @Bean
    public GroupedOpenApi groupedOpenApi(
            @Value("${openapi.service.api-docs}") String apidocs) {
        return GroupedOpenApi.builder()
                .group(apidocs)
                .packagesToScan("com.vinhSeo.BookingCinema.controller")
                .build();
    }

    @Bean
    public OpenAPI openAPI(
            @Value("${openapi.service.description}") String description,
            @Value("${openapi.service.title}") String title,
            @Value("${openapi.service.version}") String version,
            @Value("${openapi.service.server}") String server) {
        Server serverUrl = new Server();
        serverUrl.setUrl(server);
        serverUrl.setDescription("Server URL");

        Contact contact = new Contact();
        contact.setEmail("vinhmd.work@gmail.com");
        contact.setName("vinhSeo");
        contact.setUrl("https://github.com/vinhzz15088");

        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info();
        info.setTitle(title);
        info.setDescription(description);
        info.setVersion(version);
        info.setContact(contact);
        info.setLicense(license);

        Components components = new Components();
        components.addSecuritySchemes("bearerAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));

        List<SecurityRequirement> securityRequirements = new ArrayList<>();
        securityRequirements.add(new SecurityRequirement().addList("bearerAuth"));

        return new OpenAPI()
                .servers(List.of(new Server().url(server)))
                .info(info)
                .components(components)
                .security(securityRequirements);
    }
}
