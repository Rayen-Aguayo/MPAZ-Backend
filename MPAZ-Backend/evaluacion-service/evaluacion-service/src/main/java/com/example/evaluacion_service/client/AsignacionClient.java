package com.example.evaluacion_service.client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
/** Cliente hacia el servicio que administra asignaciones de unidades. */
@Component
public class AsignacionClient {
 private final RestClient client;
 public AsignacionClient(RestClient.Builder builder,@Value("${clients.asignacion-service.url:http://localhost:8083}") String url){this.client=builder.baseUrl(url).build();}
 public AsignacionRemota obtenerAsignacion(Long id){return client.get().uri("/api/asignaciones/{id}",id).retrieve().body(AsignacionRemota.class);}
 public record AsignacionRemota(Long id,Long unidadId,Integer maxIntentos,String estado) {}
}
