package com.example.unidad_service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UnidadServiceApplicationTests {

    @Autowired private MockMvc mockMvc;

    @Test
    void deberiaListarUnidadesAutorizadasCargadasPorLiquibase() throws Exception {
        mockMvc.perform(get("/api/unidades/autorizadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoOA").value("OA 3"))
                .andExpect(jsonPath("$[0].estado").value("AUTORIZADA"));
    }

    @Test
    void deberiaCrearUnidadYRecorrerFlujoDeEstados() throws Exception {
        String nuevaUnidadJson =
                """
                {
                  "asignatura": "Ciencias Naturales",
                  "titulo": "Ciclo de vida de los animales",
                  "codigoOA": "oa3",
                  "objetivo": "Observar y comparar las características de distintas etapas del ciclo de vida.",
                  "explicacion": "Todos los seres vivos nacen, crecen, se reproducen y mueren cumpliendo un ciclo natural.",
                  "ejemplo": "La mariposa pasa por huevo, oruga, crisálida y mariposa adulta.",
                  "instrucciones": "Observa las etapas y escucha la explicación guiada.",
                  "creadorId": 1
                }
                """;

        mockMvc.perform(
                        post("/api/unidades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(nuevaUnidadJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoOA").value("OA 3"))
                .andExpect(jsonPath("$.estado").value("BORRADOR"));

        // Transición BORRADOR -> EN_REVISION
        mockMvc.perform(
                        patch("/api/unidades/3/estado")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"estado\": \"EN_REVISION\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_REVISION"));

        // Transición EN_REVISION -> AUTORIZADA
        mockMvc.perform(
                        patch("/api/unidades/3/estado")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"estado\": \"AUTORIZADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("AUTORIZADA"));
    }
}
