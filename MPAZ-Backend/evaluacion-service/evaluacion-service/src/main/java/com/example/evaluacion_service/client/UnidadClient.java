package com.example.evaluacion_service.client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
/** Cliente HTTP hacia unidad-service. La respuesta esperada contiene preguntas finales y sus claves. */
@Component
public class UnidadClient {
 private final RestClient client;
 public UnidadClient(RestClient.Builder builder,@Value("${clients.unidad-service.url:http://localhost:8082}") String url){this.client=builder.baseUrl(url).build();}
 public List<PreguntaRemota> obtenerPreguntasPruebaFinal(Long unidadId){
  PreguntaRemota[] result=client.get().uri("/api/unidades/{id}/preguntas?esPruebaFinal=true",unidadId).retrieve().body(PreguntaRemota[].class);
  return result==null?List.of():List.of(result);
 }
 public record PreguntaRemota(Long id,Integer correcta) {}
}
