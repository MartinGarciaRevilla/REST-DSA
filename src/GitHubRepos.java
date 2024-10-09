import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GitHubRepos {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // 1. Pedir el nombre de usuario de GitHub
            System.out.print("Introduce el nombre de usuario de GitHub (o 'salir' para terminar): ");
            String username = scanner.nextLine();

            if (username.equalsIgnoreCase("salir")) {
                System.out.println("Saliendo...");
                break; // Salir del ciclo si el usuario introduce 'salir'
            }

            // 2. Llamar a la API de GitHub para obtener los repositorios
            String apiUrl = "https://api.github.com/users/" + username + "/repos";

            try {
                // Conexión HTTP a la API de GitHub
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                // Leer la respuesta
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // Cerrar las conexiones
                in.close();
                connection.disconnect();

                // Convertir la respuesta JSON a objetos Java con Gson
                Gson gson = new Gson();
                JsonArray reposArray = gson.fromJson(content.toString(), JsonArray.class);

                // Comprobar si el usuario tiene repositorios
                if (reposArray.size() > 0) {
                    System.out.println("\nRepositorios de " + username + ":");

                    // 3. Mostrar la lista de repositorios
                    for (JsonElement repoElement : reposArray) {
                        JsonObject repoObject = repoElement.getAsJsonObject();
                        String repoName = repoObject.get("name").getAsString();
                        String repoDescription = repoObject.get("description").isJsonNull() ? "Sin descripción" : repoObject.get("description").getAsString();

                        System.out.println("- " + repoName + ": " + repoDescription);
                    }

                    // 4. Preguntar al usuario por el nombre de un repositorio para ver más detalles
                    while (true) {
                        System.out.print("\nIntroduce el nombre del repositorio que quieres ver (o 'volver' para cambiar de usuario): ");
                        String repoNameInput = scanner.nextLine();

                        if (repoNameInput.equalsIgnoreCase("volver")) {
                            break; // Romper el ciclo para volver a pedir un nuevo usuario
                        }

                        // Buscar el repositorio seleccionado
                        boolean found = false;
                        for (JsonElement repoElement : reposArray) {
                            JsonObject repoObject = repoElement.getAsJsonObject();
                            String repoName = repoObject.get("name").getAsString();

                            if (repoName.equalsIgnoreCase(repoNameInput)) {
                                found = true;

                                // Mostrar detalles del repositorio
                                String repoUrl = repoObject.get("html_url").getAsString();
                                String repoCreationDate = repoObject.get("created_at").getAsString();
                                int stars = repoObject.get("stargazers_count").getAsInt();

                                System.out.println("\nDetalles del repositorio:");
                                System.out.println("Nombre: " + repoName);
                                System.out.println("URL: " + repoUrl);
                                System.out.println("Fecha de creación: " + repoCreationDate);
                                System.out.println("Estrellas: " + stars);
                                break;
                            }
                        }

                        if (!found) {
                            System.out.println("Repositorio no encontrado, por favor introduce un nombre válido.");
                        }
                    }

                } else {
                    System.out.println("Este usuario no tiene repositorios públicos.");
                }

            } catch (Exception e) {
                System.out.println("Error al obtener los repositorios: " + e.getMessage());
            }

            // Después de explorar los repositorios, el ciclo volverá a pedir otro usuario
        }

        scanner.close(); // Cerrar el escáner cuando terminemos el programa
    }
}
