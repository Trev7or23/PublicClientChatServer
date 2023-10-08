package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class Main extends JFrame{
    
    private JTextArea mensajesTextArea;
    private JTextField mensajeTextField;
    private Gson gson = new Gson();

    private String osName = System.getProperty("os.name");
    
    public Main() {
        super("Cliente");

        mensajesTextArea = new JTextArea(10, 30);
        mensajesTextArea.setEditable(false);

        mensajeTextField = new JTextField(30);
        mensajeTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = osName + ": "+ mensajeTextField.getText();
                enviarMensaje(text);
                mensajeTextField.setText("");
            }
        });

        setLayout(new FlowLayout());
        add(new JScrollPane(mensajesTextArea));
        add(mensajeTextField);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setVisible(true);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                obtenerMensajes();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 1000);
    }

    private void enviarMensaje(String contenido) {
        Mensaje mensaje = new Mensaje(contenido);
        String mensajeJson = gson.toJson(mensaje);

        try {
            URL url = new URL("http://env-7530429.dal.togglebox.site:8080/json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(mensajeJson.getBytes());
            os.flush();
            os.close();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Error al enviar el mensaje. Código HTTP: " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void obtenerMensajes() {
        try {
            URL url = new URL("http://env-7530429.dal.togglebox.site:8080/json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Error al obtener los mensajes. Código HTTP: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                response.append(linea);
            }
            br.close();

            List<String> mensajes = parsearRespuesta(response.toString());
            mostrarMensajes(mensajes);

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> parsearRespuesta(String respuesta) {
        java.lang.reflect.Type tipoLista = new TypeToken<>() {}.getType();
        return gson.fromJson(respuesta, tipoLista);
    }

    private void mostrarMensajes(List<String> mensajes) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mensajesTextArea.setText("");
                for (String mensaje : mensajes) {
                    mensajesTextArea.append(mensaje + "\n");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}