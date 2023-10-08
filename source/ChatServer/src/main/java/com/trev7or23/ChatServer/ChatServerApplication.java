package com.trev7or23.ChatServer;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;


@SpringBootApplication
@RestController
public class ChatServerApplication {

    private List<String> mensajes = new ArrayList<>();
    private Gson gson = new Gson();
    
   
    @GetMapping("/json")
    public String obtenerMensajes() {
        return gson.toJson(mensajes);
    }

    @PostMapping("/json")
    public void recibirMensaje(@RequestBody String mensajeJson) {
        Mensaje mensaje = gson.fromJson(mensajeJson, Mensaje.class);
        mensajes.add(mensaje.getContenido());
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }
    
}
