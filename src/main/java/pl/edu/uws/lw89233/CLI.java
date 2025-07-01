package pl.edu.uws.lw89233;

import pl.edu.uws.lw89233.managers.EnvManager;
import pl.edu.uws.lw89233.managers.MessageManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

public class CLI {

    private static final String SERVER_ADDRESS = EnvManager.getEnvVariable("SERVER_ADDRESS");
    private static final int SERVER_PORT = Integer.parseInt(EnvManager.getEnvVariable("SERVER_PORT"));
    private static int messageIdCounter = 1;
    private static String userLogin;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            boolean isLoggedIn = false;
            boolean isRunning = true;

            while (isRunning) {
                showMenu(isLoggedIn);

                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        if (!isLoggedIn) {
                            handleRegistration(scanner, out, in);
                        } else {
                            System.out.println("Nie możesz wykonać tej opeeracji będąc zalogowanym.");
                        }
                        break;

                    case 2:
                        if (!isLoggedIn) {
                            isLoggedIn = handleLogin(scanner, out, in);
                        } else {
                            System.out.println("Nie możesz wykonać tej opeeracji będąc zalogowanym.");
                        }
                        break;

                    case 3:
                        if (isLoggedIn) {
                            handleRetrieveLast10Posts(out, in);
                        } else {
                            System.out.println("Musisz być zalogowany, aby wykonać tę operację.");
                        }
                        break;

                    case 4:
                        if (isLoggedIn) {
                            handleSendPost(scanner, out, in);
                        } else {
                            System.out.println("Musisz być zalogowany, aby wykonać tę operację.");
                        }
                        break;

                    case 5:
                        if (isLoggedIn) {
                            handleSendFile(scanner, out, in);
                        } else {
                            System.out.println("Musisz być zalogowany, aby wykonać tę operację.");
                        }
                        break;

                    case 6:
                        if (isLoggedIn) {
                            handleGetFile(scanner, out, in);
                        } else {
                            System.out.println("Musisz być zalogowany, aby wykonać tę operację.");
                        }
                        break;

                    case 7:
                        if (isLoggedIn) {
                            isLoggedIn = false;
                            userLogin = null;
                            System.out.println("Wylogowano pomyślnie.");
                        } else {
                            System.out.println("Nie jesteś zalogowany.");
                        }
                        break;

                    case 0:
                        isRunning = false;
                        System.out.println("Zakończono działanie aplikacji.");
                        break;

                    default:
                        System.out.println("Nieprawidłowa opcja, spróbuj ponownie.");
                        break;
                }
                messageIdCounter++;
            }
        } catch (IOException e) {
            System.err.println("Błąd komunikacji: " + e.getMessage());
        }
    }

    private static void showMenu(boolean isLoggedIn) {
        System.out.println("Wybierz opcję:");
        if (!isLoggedIn) {
            System.out.println("1: Rejestracja");
            System.out.println("2: Logowanie");
        } else {
            System.out.println("3: Pobierz ostatnie 10 postów");
            System.out.println("4: Wyślij post");
            System.out.println("5: Wyślij plik");
            System.out.println("6: Pobierz plik");
            System.out.println("7: Wyloguj się");
        }
        System.out.println("0: Wyjdź");
    }

    private static void handleRegistration(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("Podaj login:");
        String login = scanner.nextLine();
        System.out.println("Podaj hasło:");
        String password = scanner.nextLine();

        String registrationRequest = "type:registration_request#" + "message_id:" + messageIdCounter + "#" +
                "login:" + login + "#" +
                "password:" + password + "#";

        out.println(registrationRequest);
        MessageManager responseManager = new MessageManager(in.readLine());

        while(!responseManager.getAttribute("message_id").equals(String.valueOf(messageIdCounter))){
            responseManager = new MessageManager(in.readLine());
        }

        if (responseManager.getAttribute("status").equals("200")) {
            System.out.println("Pomyślnie zarejestrowano użytkownika.");
        } else {
            System.out.println("Istnieje użytkownik z podanym loginem.");
        }
    }

    private static boolean handleLogin(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("Podaj login:");
        String login = scanner.nextLine();
        System.out.println("Podaj hasło:");
        String password = scanner.nextLine();

        String loginRequest = "type:login_request#" + "message_id:" + messageIdCounter + "#" +
                "login:" + login + "#" +
                "password:" + password + "#";

        out.println(loginRequest);
        MessageManager responseManager = new MessageManager(in.readLine());

        while(!responseManager.getAttribute("message_id").equals(String.valueOf(messageIdCounter))){
            responseManager = new MessageManager(in.readLine());
        }

        if (responseManager.getAttribute("status").equals("200")) {
            System.out.println("Pomyślnie zalogowano użytkownika.");
            userLogin = login;
            return true;
        } else {
            System.out.println("Niepoprawny login lub hasło.");
            return false;
        }
    }

    private static void handleRetrieveLast10Posts(PrintWriter out, BufferedReader in) throws IOException {

        out.println("type:retrive_last_10_posts_request#" + "message_id:" + messageIdCounter + "#");
        MessageManager responseManager = new MessageManager(in.readLine());

        while(!responseManager.getAttribute("message_id").equals(String.valueOf(messageIdCounter))){
            responseManager = new MessageManager(in.readLine());
        }

        String postsResponse = responseManager.getAttribute("posts");
        if (postsResponse != null && !postsResponse.isEmpty()) {
            System.out.println("Ostatnie 10 postów:");

            String[] posts = postsResponse.split("&");

            for (String post : posts) {
                String[] postDetails = post.split(";");
                String postId = "", content = "", author = "", createdAt = "";

                for (String detail : postDetails) {
                    if (detail.startsWith("post_id=")) {
                        postId = detail.split("=")[1];
                    } else if (detail.startsWith("content=")) {
                        content = detail.split("=")[1];
                    } else if (detail.startsWith("author=")) {
                        author = detail.split("=")[1];
                    } else if (detail.startsWith("created_at=")) {
                        createdAt = detail.split("=")[1];
                    }
                }

                System.out.println("Post ID: " + postId);
                System.out.println("Autor: " + author);
                System.out.println("Treść: " + content);
                System.out.println("Data utworzenia: " + createdAt);
                System.out.println("---------------------------------------------------");
            }
        } else {
            System.out.println("Brak postów.");
        }
    }

    private static void handleSendPost(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("Wpisz treść posta:");
        String postContent = scanner.nextLine();

        String sendPostRequest = "type:send_post_request#" + "message_id:" + messageIdCounter + "#" +
                "post:" + postContent + "#" +
                "login:" + userLogin + "#";

        out.println(sendPostRequest);
        MessageManager responseManager = new MessageManager(in.readLine());

        while(!responseManager.getAttribute("message_id").equals(String.valueOf(messageIdCounter))){
            responseManager = new MessageManager(in.readLine());
        }

        if (responseManager.getAttribute("status").equals("200")) {
            System.out.println("Pomyślnie dodano post.");
        } else {
            System.out.println("Wystąpił problem podczas dodawania posta.");
        }
    }

    private static void handleSendFile(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("Podaj ścieżkę do pliku do wysłania:");
        String filePath = scanner.nextLine();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Plik nie istnieje.");
            return;
        }

        String fileName = file.getName();

        String initRequest = "type:send_file_request#message_id:" + messageIdCounter
                + "#login:" + userLogin + "#file_name:" + fileName + "#action:init#";
        out.println(initRequest);

        MessageManager response = new MessageManager(in.readLine());

        while(!response.getAttribute("message_id").equals(String.valueOf(messageIdCounter))){
            response = new MessageManager(in.readLine());
        }

        if (!response.getAttribute("status").equals("200")) {
            System.out.println("Nie można rozpocząć transferu.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[512];
            int bytesRead;
            int packageNumber = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] partOfFileBytes = Arrays.copyOf(buffer, bytesRead);
                String partOfFileString = Base64.getEncoder().encodeToString(partOfFileBytes);

                String packageRequest = "type:send_file_request#message_id:" + messageIdCounter
                        + "#login:" + userLogin + "#file_name:" + fileName
                        + "#action:package#package_number:" + packageNumber
                        + "#content:" + partOfFileString + "#";

                out.println(packageRequest);

                do {
                    response = new MessageManager(in.readLine());
                } while (!response.getAttribute("message_id").equals(String.valueOf(messageIdCounter)));

                if (!response.getAttribute("status").equals("200")) {
                    System.out.println("Błąd podczas wysyłania pakietu " + response.getAttribute("packageNumber"));
                    return;
                }

                packageNumber++;
            }

            String finishRequest = "type:send_file_request#message_id:" + messageIdCounter
                    + "#login:" + userLogin + "#file_name:" + fileName + "#action:finish#";
            out.println(finishRequest);

            do {
                response = new MessageManager(in.readLine());
            } while (!response.getAttribute("message_id").equals(String.valueOf(messageIdCounter)));

            if (response.getAttribute("status").equals("200")) {
                System.out.println("Pomyślnie wysłano plik.");
            }
        }
    }

    private static void handleGetFile(Scanner scanner, PrintWriter out, BufferedReader in) {
        System.out.println("Podaj nazwę pliku do pobrania:");
        String fileName = scanner.nextLine();

        File filesDir = new File("files");
        filesDir.mkdirs();
        File downloadedFile = new File(filesDir, fileName);

        String request = "type:get_file_request#message_id:" + messageIdCounter
                + "#login:" + userLogin + "#file_name:" + fileName + "#";
        out.println(request);

        try (FileOutputStream fos = new FileOutputStream(downloadedFile)) {
            int expectedPackageNumber = 0;

            while (true) {
                String responseStr = in.readLine();

                if (responseStr == null) {
                    throw new IOException("Przerwano połączenie");
                }

                MessageManager response = new MessageManager(responseStr);

                while(!response.getAttribute("message_id").equals(String.valueOf(messageIdCounter))){
                    response = new MessageManager(in.readLine());
                }

                String status = response.getAttribute("status");
                if (!"200".equals(status)) {
                    throw new IOException("Błąd pobierania pliku");
                }

                String packageNumberStr = response.getAttribute("package_number");
                int packageNumber = Integer.parseInt(packageNumberStr);

                if (packageNumber == -1) {
                    System.out.println("Pomyślnie pobrano plik");
                    break;
                } else if (packageNumber != expectedPackageNumber) {
                    throw new Exception("Błąd numeracji pakietów");
                }

                String content = response.getAttribute("content");
                if (content == null) {
                    throw new IOException("Brak zawartości pakietu");
                }

                byte[] partOfFileBytes = Base64.getDecoder().decode(content);
                fos.write(partOfFileBytes);

                String ack = "type:get_file_request#message_id:" + messageIdCounter
                        + "#login:" + userLogin + "#file_name:" + fileName
                        + "#action:ack#package_number:" + packageNumber + "#";
                out.println(ack);

                expectedPackageNumber++;
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas pobierania pliku: " + e.getMessage());
            downloadedFile.delete();
        }
    }
}