import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.Objects;

public class App {
    /**
     * Инициализирует и отображает окно приложения "Управление автосервисом".
     * Окно содержит панель с кнопками, поле для поиска и таблицу для
     * отображения данных по автосервису.
     */
    public static void CarService() {
        // Создание главного окна приложения
        JFrame frame = new JFrame("Car Service Management");
        // Установка иконки для окна
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(App.class.getResource("icons\\car.png")));
        frame.setIconImage(icon.getImage());
        // Настройка действия при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Установка размера окна
        // Установка цвета фона окна
        frame.getContentPane().setBackground(new Color(61, 64, 82));

        // Главная панель, которая содержит кнопки и панель поиска
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout()); // Используем BorderLayout для организации кнопок и поиска
        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(61, 64, 82));

        // Массивы с названиями и иконками для кнопок
        String[] icons = {"save", "open", "add", "edit", "bin", "print"};
        String[] buttonsName = {"Сохранить", "Открыть", "Добавить", "Редактировать", "Удалить", "Печать списка"};
        JButton[] buttons = new JButton[icons.length];

        // Добавляем кнопки на панель кнопок
        for (int i = 0; i < icons.length; i++) {
            ImageIcon iconImage = new ImageIcon(new
                    ImageIcon(Objects.requireNonNull(App.class.getResource("icons\\" + icons[i] + ".png")))
                    .getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));
            buttons[i] = new JButton(buttonsName[i], iconImage);
            buttonPanel.add(buttons[i]);
        }

        for(int i = 2; i < buttons.length; i++ ) {
            buttons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // При нажатии на кнопку открываем диалоговое окно с сообщением
                    JOptionPane.showMessageDialog(frame, "Пока в работе", "Информация", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Создание панели для поиска
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Выравниваем по правому краю
        searchPanel.setBackground(new Color(61, 64, 82));
        // Поле для ввода текста поиска
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Поиск"); // Кнопка для поиска
        // Добавляем текст плейсхолдера в поле поиска
        searchField.setText("Поиск");
        searchField.setForeground(Color.GRAY); // По умолчанию серый текст
        // Добавляем FocusListener для обработки фокуса поля

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
            @Override
            /**
             * Метод вызывается, когда фокус на текстовое поле поиска
             получен.
             * Если текст равен "Поиск" (плейсхолдер), он удаляется, и цвет
             текста меняется на черный.
             *
             * @param e - событие фокуса.
             */
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Поиск")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK); // Цвет текста черный при вводе
                }
            }
            @Override
            /**
             * Метод вызывается, когда текстовое поле поиска теряет фокус.
             * Если поле пустое, возвращается текст плейсхолдера "Поиск", и
             цвет текста меняется на серый.
             *
             * @param e - событие фокуса.
             */
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Поиск"); // Возвращаем плейсхолдер
                }
            }
        });

        // Добавляем поле поиска и кнопку в панель поиска
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Добавляем панели кнопок и поиска в верхнюю панель
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.EAST); // Панель поиска справа

        // Заголовки столбцов таблицы
        String[] columnNames = {"ФИО владельца", "Марка авто", "Характеристики", "Неисправности", "ФИО работника"};

        // Модель таблицы с возможностью добавления/удаления строк
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model); // Таблица с динамической моделью
        table.setBackground(new Color(206, 246, 184));
        table.setFillsViewportHeight(true); // Растягиваем таблицу на всю доступную высоту
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Настраиваем заголовок таблицы
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(61, 64, 82));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));

        // Добавляем таблицу в JScrollPane для прокрутки
        JScrollPane scrollPane = new JScrollPane(table);

        // Панель для добавления/удаления строк
        JPanel actionPanel = new JPanel();
        JButton addRowButton = new JButton("Добавить строку");
        JButton deleteRowButton = new JButton("Удалить строку");

        // Обработчик добавления строки
        /**
         * Добавляет пустую строку в таблицу.
         *
         * @param e событие ActionEvent, возникающее при нажатии кнопки добавления строки.
         */
        addRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Добавляем пустую строку
                model.addRow(new Object[]{"", "", "", "", ""});
            }
        });

        // Обработчик удаления строки
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


        // Обработчик для кнопки "Сохранить"
        /**
         *
         * Сохранение таблицы в формате xml файла
         * @param e- событие ActionListener, возникающее при нажатии кнопки сохранения таблицы.
         */
        buttons[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TableModel data = table.getModel();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.newDocument();

                    // Создаем корневой элемент
                    Element rootElement = doc.createElement("carService");
                    doc.appendChild(rootElement);

                    // Проходим по каждой строке таблицы и создаем элементы XML
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

                    // Сохранение XML в файл
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(new File("car_service_data.xml"));

                    transformer.transform(source, result);
                    JOptionPane.showMessageDialog(frame, "Данные успешно сохранены в XML файл.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Ошибка при сохранении данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
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
                    // Создаем DocumentBuilder для парсинга XML
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new File("car_service_data.xml")); // Заменяем xmlFile на путь к файлу

                    // Нормализуем XML структуру
                    doc.getDocumentElement().normalize();

                    // Получаем корневой элемент <carService>
                    Element root = doc.getDocumentElement();

                    // Очищаем таблицу перед загрузкой новых данных
                    model.setRowCount(0);

                    // Получаем список всех элементов <car>
                    NodeList carList = root.getElementsByTagName("car");

                    // Проходим по каждому элементу <car>
                    for (int i = 0; i < carList.getLength(); i++) {
                        Node carNode = carList.item(i);

                        if (carNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element carElement = (Element) carNode;

                            // Извлекаем подэлементы <owner>, <brand>, <characteristics>, <issues>, <worker>
                            String owner = carElement.getElementsByTagName("owner").item(0).getTextContent();
                            String brand = carElement.getElementsByTagName("brand").item(0).getTextContent();
                            String characteristics = carElement.getElementsByTagName("characteristics").item(0).getTextContent();
                            String issues = carElement.getElementsByTagName("issues").item(0).getTextContent();
                            String worker = carElement.getElementsByTagName("worker").item(0).getTextContent();

                            // Добавляем данные в таблицу
                            model.addRow(new Object[]{owner, brand, characteristics, issues, worker});
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Данные успешно распакованы.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Ошибка при загрузке данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Добавляем кнопки на панель действий
        actionPanel.add(addRowButton);
        actionPanel.add(deleteRowButton);

        // Добавляем верхнюю панель, таблицу и панель действий в окно
        frame.add(topPanel, BorderLayout.NORTH); // Верхняя панель
        frame.add(scrollPane, BorderLayout.CENTER); // Таблица в центре
        frame.add(actionPanel, BorderLayout.SOUTH); // Панель действий снизу

        // Делаем окно видимым
        frame.setVisible(true);
    }
}
