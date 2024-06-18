package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AirPollutionApp {
    private JFrame frame; // Główne okno aplikacji
    private JTextArea textArea; // Obszar tekstowy do wyświetlania wyników
    private JTextField cityTextField; // Pole tekstowe do wprowadzania nazwy miasta
    private JLabel imageLabel; // Etykieta do wyświetlania obrazka stanu AQI
    private APIClient apiClient; // Obiekt do obsługi zapytań do API

    // Konstruktor inicjalizujący aplikację z kluczem API
    public AirPollutionApp(String apiKey) {
        apiClient = new APIClient(apiKey);
        createUI();
    }

    // Metoda tworząca interfejs użytkownika
    private void createUI() {
        frame = new JFrame("Air Pollution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tytuł
        JLabel titleLabel = new JLabel("Poznaj zanieczyszczenie powietrza na całym świecie", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(titleLabel, gbc);

        // Pole tekstowe do wprowadzania nazwy miasta
        cityTextField = new JTextField();
        cityTextField.setToolTipText("Podaj nazwę miasta albo państwa");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        cityTextField.setPreferredSize(new Dimension(200, 30)); // Ustawienie preferowanego rozmiaru pola tekstowego
        panel.add(cityTextField, gbc);

        // Przycisk do wywołania zapytania o zanieczyszczenie
        JButton button = new JButton("Sprawdź zanieczyszczenie");
        button.setPreferredSize(new Dimension(100, 30));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchAndDisplayData();
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(button, gbc);

        // Obszar tekstowy do wyświetlania wyników
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Ustawienie czcionki i stylu
        textArea.setForeground(Color.BLUE); // Ustawienie koloru tekstu
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        // Obrazek do wyświetlania stanu AQI
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(400, 400));
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(imageLabel, gbc);

        // Legenda wyjaśniająca skróty i skalę AQI
        JTextArea legendArea = new JTextArea();
        legendArea.setEditable(false);
        legendArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        legendArea.setText("Legenda:\n"
                + "AQI - skala jakości powietrza (1 - Dobra, 2 - Okej, 3 - Średnia, 4 - Słaba, 5 - Zła)\n"
                + "CO - Tlenek węgla\n"
                + "NO₂ - Dwutlenek azotu\n"
                + "O₃ - Ozon\n"
                + "SO₂ - Dwutlenek siarki\n"
                + "PM₂.₅ i PM₁₀ - Cząstki stałe");
        legendArea.setBackground(frame.getBackground()); // Ustawienie tła legendy
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(legendArea, gbc);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Metoda pobierająca dane i wyświetlająca je w interfejsie
    private void fetchAndDisplayData() {
        try {
            String cityName = cityTextField.getText();
            CityCoordinates coordinates = apiClient.getCoordinates(cityName);
            AirPollutionData data = apiClient.getAirPollutionData(coordinates.lat, coordinates.lon);

            StringBuilder sb = new StringBuilder();
            for (AirPollutionData.ListElement element : data.list) {
                sb.append("AQI: ").append(element.main.aqi).append(" - ").append(getAQIStatus(element.main.aqi)).append("\n");
                sb.append("CO: ").append(element.components.co).append(" µg/m3\n");
                sb.append("NO₂: ").append(element.components.no2).append(" µg/m3\n");
                sb.append("O₃: ").append(element.components.o3).append(" µg/m3\n");
                sb.append("SO₂: ").append(element.components.so2).append(" µg/m3\n");
                sb.append("PM₂.₅: ").append(element.components.pm2_5).append(" µg/m3\n");
                sb.append("PM₁₀: ").append(element.components.pm10).append(" µg/m3\n");
                sb.append("\n");

                // Ustawienie odpowiedniego obrazka w zależności od AQI
                setImage(element.main.aqi);
            }

            textArea.setText(sb.toString());
        } catch (Exception e) {
            textArea.setText("Error fetching data: " + e.getMessage());
        }
    }

    // Metoda ustawiająca odpowiedni obrazek w zależności od AQI
    private void setImage(int aqi) {
        String imagePath = "";
        switch (aqi) {
            case 1:
                imagePath = "/goodFace.png";
                break;
            case 2:
                imagePath = "/fairFace.png";
                break;
            case 3:
                imagePath = "/moderateFace.png";
                break;
            case 4:
                imagePath = "/poorFace.png";
                break;
            case 5:
                imagePath = "/verypoorFace.png";
                break;
            default:
                imagePath = "";
                break;
        }
        if (!imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImg));
        } else {
            imageLabel.setIcon(null);
        }
    }

    // Metoda zwracająca opisowy status AQI
    private String getAQIStatus(int aqi) {
        switch (aqi) {
            case 1:
                return "Dobra jakość powietrza";
            case 2:
                return "Okej jakość powietrza";
            case 3:
                return "Średnia jakość powietrza";
            case 4:
                return "Słaba jakość powietrza";
            case 5:
                return "Zła jakość powietrza";
            default:
                return "Nieznane";
        }
    }

    // Główna metoda uruchamiająca aplikację
    public static void main(String[] args) {
        String apiKey = "bc295a4bc8727b69c6df749ba9cc88be";  // Podaj swój klucz API
        new AirPollutionApp(apiKey);
    }
}
