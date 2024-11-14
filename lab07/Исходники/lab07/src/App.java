import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class App extends Component {
    static JTable table;
    public static void CarService() {
        JFrame frame = new JFrame("Car Service Management");
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(App.class.getResource("icons\\car.png")));
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().setBackground(new Color(61, 64, 82));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(61, 64, 82));

        String[] icons = {"save", "open", "print"};
        String[] buttonsName = {"Сохранить", "Открыть", "Печать списка",};
        JButton[] buttons = new JButton[icons.length];

        for (int i = 0; i < icons.length; i++) {
            ImageIcon iconImage = new ImageIcon(new
                    ImageIcon(Objects.requireNonNull(App.class.getResource("icons\\" + icons[i] + ".png")))
                    .getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));
            buttons[i] = new JButton(buttonsName[i], iconImage);
            buttonPanel.add(buttons[i]);
        }

        buttons[2].addActionListener(e -> {exportFile(frame);
        });

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(61, 64, 82));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Поиск");
        searchField.setText("Поиск");
        searchField.setForeground(Color.GRAY);

        /**
         * Добавляет обработчик событий фокуса для текстового поля поиска.
         * Когда фокус получен, плейсхолдер удаляется, и текст становится
         черным.
         * Когда фокус потерян, если поле пустое, плейсхолдер возвращается.
         *
         * @param FocusAdapter - адаптерный класс, который предоставляет
        пустую реализацию методов интерфейса FocusListener.
         * Данный интерфейс включает методы focusGained() и focusLost().
         */
        searchField.addFocusListener(new FocusAdapter() {
            /**
             * Метод вызывается, когда фокус на текстовое поле поиска
             получен.
             * Если текст равен "Поиск" (плейсхолдер), он удаляется, и цвет
             текста меняется на черный.
             *
             * @param e - событие фокуса.
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Поиск")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            /**
             * Метод вызывается, когда текстовое поле поиска теряет фокус.
             * Если поле пустое, возвращается текст плейсхолдера "Поиск", и
             цвет текста меняется на серый.
             *
             * @param e - событие фокуса.
             */
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Поиск");
                }
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.EAST);

        String[] columnNames = {"ФИО владельца", "Марка авто", "Характеристики", "Неисправности", "ФИО работника"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setBackground(new Color(206, 246, 184));
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(61, 64, 82));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel actionPanel = new JPanel();
        JButton addRowButton = new JButton("Добавить строку");
        JButton deleteRowButton = new JButton("Удалить строку");
        /**
         * Добавляет пустую строку в таблицу.
         *
         * @param e событие ActionEvent, возникающее при нажатии кнопки добавления строки.
         */
        addRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{"", "", "", "", ""});
            }
        });
        /**
         * Удаляет последнюю строку из таблицы, если она существует.
         * Если строк нет, выводит сообщение об ошибке.
         *
         * @param e событие ActionEvent, возникающее при нажатии кнопки удаления строки.
         */

        deleteRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int lastRow = model.getRowCount() - 1; // Получаем индекс последней строки
                if (lastRow >= 0) { // Проверяем, что строка существует
                    model.removeRow(lastRow); // Удаляем последнюю строку
                } else {
                    JOptionPane.showMessageDialog(frame, "Отсутствует строка.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JFileChooser fileChooser = new JFileChooser();
        /**
         *
         * Сохранение таблицы в формате xml файла
         * @param e- событие ActionListener, возникающее при нажатии кнопки сохранения таблицы.
         */
        buttons[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Save_To_XML(table, frame, Get_Name(fileChooser, frame));
                } catch (MyException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        /**
         *
         * Обработка второй кнопки: открытие (парсинг) xml файла
         * @param e- событие ActionListener, возникающее при нажатии кнопки открытия таблицы.
         */

        buttons[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Open_XML(model, frame, Get_Name(fileChooser, frame));
                } catch (MyException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionPanel.add(addRowButton);
        actionPanel.add(deleteRowButton);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(actionPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static String Get_Name(JFileChooser fileChooser, JFrame frame) {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extFilter = new FileNameExtensionFilter("XML Files", "xml");
        fileChooser.addChoosableFileFilter(extFilter);

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.toString();
        } else {
            JOptionPane.showMessageDialog(frame, "Файл не выбран.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    /**
     *
     * @param table - таблица, передаваемая в метод, для получения из нее данных
     * @param frame - окно
     * @throws MyException - возникает при ошибке создания XML файла, выбрасывается при возникновении ParserConfigurationException
     */

    private static void Save_To_XML(JTable table, Frame frame, String name_to_file) throws MyException {
        TableModel data = table.getModel();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;
        String message = "Ошибка при сохранении данных.";

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new MyException(message);
        }

        Element rootElement = doc.createElement("carService");
        doc.appendChild(rootElement);

        for (int i = 0; i < data.getRowCount(); i++) {
            Element car = doc.createElement("car");
            rootElement.appendChild(car);

            Element owner = doc.createElement("owner");
            owner.appendChild(doc.createTextNode(data.getValueAt(i, 0).toString()));
            car.appendChild(owner);

            Element brand = doc.createElement("brand");
            brand.appendChild(doc.createTextNode(data.getValueAt(i, 1).toString()));
            car.appendChild(brand);

            Element characteristics = doc.createElement("characteristics");
            characteristics.appendChild(doc.createTextNode(data.getValueAt(i, 2).toString()));
            car.appendChild(characteristics);

            Element issues = doc.createElement("issues");
            issues.appendChild(doc.createTextNode(data.getValueAt(i, 3).toString()));
            car.appendChild(issues);

            Element worker = doc.createElement("worker");
            worker.appendChild(doc.createTextNode(data.getValueAt(i, 4).toString()));
            car.appendChild(worker);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(name_to_file));

        /**
         * @param e- исключение, возникающее при ошибке сохранения XML файла
         */
        try {
            transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new MyException(message);
        }

        JOptionPane.showMessageDialog(frame, "Данные успешно сохранены в XML файл.", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     *
     * @param model - Динамическая таблица
     * @param frame - окно
     * @throws MyException - возникает при ошибке парсинга XML файла, выбрасывается при возникновении ParserConfigurationException | IOException | SAXException
     */

    private static void Open_XML(DefaultTableModel model, JFrame frame, String name_of_file) throws MyException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(name_of_file);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new MyException("Ошибка при загрузке данных.");
        }

        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();

        model.setRowCount(0);

        NodeList carList = root.getElementsByTagName("car");

        for (int i = 0; i < carList.getLength(); i++) {
            Node carNode = carList.item(i);

            if (carNode.getNodeType() == Node.ELEMENT_NODE) {
                Element carElement = (Element) carNode;

                String owner = carElement.getElementsByTagName("owner").item(0).getTextContent();
                String brand = carElement.getElementsByTagName("brand").item(0).getTextContent();
                String characteristics = carElement.getElementsByTagName("characteristics").item(0).getTextContent();
                String issues = carElement.getElementsByTagName("issues").item(0).getTextContent();
                String worker = carElement.getElementsByTagName("worker").item(0).getTextContent();

                model.addRow(new Object[]{owner, brand, characteristics, issues, worker});
            }
        }

        JOptionPane.showMessageDialog(frame, "Данные успешно распакованы.", "Успех", JOptionPane.INFORMATION_MESSAGE);

        frame.setVisible(true);
    }

    private static Document saveFromTable(JTable table, JFrame frame) {
        if (table == null || table.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "Таблица пустая или не инициализирована.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException e) {
            JOptionPane.showMessageDialog(frame, "Ошибка при создании документа XML.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Element rootElement = doc.createElement("carService");
        doc.appendChild(rootElement);

        for (int i = 0; i < table.getModel().getRowCount(); i++) {
            Element car = doc.createElement("car");
            rootElement.appendChild(car);

            Element owner = doc.createElement("owner");
            owner.appendChild(doc.createTextNode(String.valueOf(table.getModel().getValueAt(i, 0))));
            car.appendChild(owner);

            Element brand = doc.createElement("brand");
            brand.appendChild(doc.createTextNode(String.valueOf(table.getModel().getValueAt(i, 1))));
            car.appendChild(brand);

            Element characteristics = doc.createElement("characteristics");
            characteristics.appendChild(doc.createTextNode(String.valueOf(table.getModel().getValueAt(i, 2))));
            car.appendChild(characteristics);

            Element issues = doc.createElement("issues");
            issues.appendChild(doc.createTextNode(String.valueOf(table.getModel().getValueAt(i, 3))));
            car.appendChild(issues);

            Element worker = doc.createElement("worker");
            worker.appendChild(doc.createTextNode(String.valueOf(table.getModel().getValueAt(i, 4))));
            car.appendChild(worker);
        }

        return doc;
    }

    public static void exportFile(JFrame frame) {
        try {
            Document doc = saveFromTable(table, frame);
            if (doc == null) return;

            String template = "lab07.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport(template);
            Map<String, Object> params = new HashMap<>();
            params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, doc);

            JasperPrint print = JasperFillManager.fillReport(jasperReport, params);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Сохранить PDF файл");
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

            int userSelection = fileChooser.showSaveDialog(frame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                if (!outputFile.getAbsolutePath().endsWith(".pdf")) {
                    outputFile = new File(outputFile + ".pdf"); // Ensure the file has a .pdf extension
                }

                JasperExportManager.exportReportToPdfFile(print, outputFile.getAbsolutePath());

                JOptionPane.showMessageDialog(frame, "Таблица успешно сохранена в PDF файл.", "Информация", JOptionPane.INFORMATION_MESSAGE);

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(outputFile);
                } else {
                    JOptionPane.showMessageDialog(frame, "Автоматическое открытие файла не поддерживается на этой системе.", "Предупреждение", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Сохранение отменено.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (JRException e) {
            JOptionPane.showMessageDialog(frame, "Ошибка при компиляции или экспорте отчета.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Не удалось открыть PDF файл.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class MyException extends Exception {
        /**
         *
         * @param message - String значения для обозначения exception
         */
        public MyException(String message) {
            super(message);
        }
    }
}