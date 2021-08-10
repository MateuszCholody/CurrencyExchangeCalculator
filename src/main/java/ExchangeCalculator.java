import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class ExchangeCalculator implements Initializable {
    public TableColumn<Currency, String> currencyColumn;
    public TableColumn<Currency, Double> rateColumn;
    public ComboBox<String> currencyList;
    public ComboBox<String> inputCurrencyComboBox;
    public ComboBox<String> outputCurrencyComboBox;
    public javafx.scene.control.TextField amountTextField;
    public javafx.scene.control.TextField resultTextField;
    public LineChart<String, Double> currencyChart;
    public ComboBox<String> xaxisList;
    public ComboBox<String> yaxisList;
    public RadioButton weekHistoryRadioButton;
    public RadioButton monthHistoryRadioButton;
    public RadioButton customHistoryRadioButton;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public TableView<Currency> tableView;
    public Text exchangeRateText;
    public Text alertText;
    public NumberAxis yAxis;
    private ObservableList<Currency> list = FXCollections.observableArrayList();
    private ToggleGroup radioGroup;

    private ExchangeRateHandler handler;

    public void setList(ActionEvent actionEvent) {
        try {
            list.clear();
            handler.getExchangeRates(currencyList.getValue().substring(0,3)).forEach((k,v) -> {
                try {
                    list.add(new Currency(k, v));
                }
                catch (ClassCastException e) {
                    System.out.println(5678);
                    list.add(new Currency(k, Double.parseDouble(String.valueOf(v))));
                }
            });
            tableView.setItems(list);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void convertCurrency(ActionEvent actionEvent) {
        String inputCurrency = inputCurrencyComboBox.getValue().substring(0,3);
        String outputCurrency = outputCurrencyComboBox.getValue().substring(0,3);
        Double amount, result, rate;
        try {
            amount = Double.parseDouble(amountTextField.getText());
        }
        catch (NumberFormatException e) {
            alertText.setText("Wrong input in amount field. Please try again.");
            amountTextField.setText("0.00");
            amount = 0.0;
        }
        try {
            rate = handler.getExchangeRates(inputCurrency, outputCurrency).get(outputCurrency);
            exchangeRateText.setText(rate.toString());
        } catch (IOException e) {
            e.printStackTrace();
            alertText.setText("Service currently unavailable. Please try again later.");
            rate = 0.0;
        }
        result = amount * rate;
        resultTextField.setText(result.toString());

    }

    public void plotData(ActionEvent mouseEvent) {
        Map<String, Double> rates;
        XYChart.Series series = new XYChart.Series();
        currencyChart.getData().clear();
        series.setName("Rate");
        try {
            List<LocalDate> range = getDateRange();
            rates = handler.getExchangeRates(xaxisList.getValue().substring(0,3),
                    yaxisList.getValue().substring(0,3), range.get(1), range.get(0));
            Double maxRate = 0.0;
            Double minRate = 10000.0;
            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
                if (maxRate < entry.getValue()) {
                    maxRate = entry.getValue();
                } else if (minRate > entry.getValue()) {
                    minRate = entry.getValue();
                }
            }
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(minRate - 0.05 * minRate);
            yAxis.setUpperBound(maxRate + 0.05 * maxRate);
            currencyChart.getData().add(series);

        } catch (Exception e) {
            System.out.println("err");
        }
    }

    private List<LocalDate> getDateRange() {
        List<LocalDate> range = new ArrayList<>();
        range.add(LocalDate.now());
        if (weekHistoryRadioButton.isSelected()) {
            System.out.println(2343);
            range.add(LocalDate.now().minusWeeks(1));
        }
        if (monthHistoryRadioButton.isSelected()) {
            range.add(LocalDate.now().minusMonths(1));
        }
        if (customHistoryRadioButton.isSelected()) {
            range.add(startDatePicker.getValue());
        }
        System.out.println(range);
        return range;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handler = ExchangeRateHandler.getInstance();
        initializeComboBoxes();
        initializeColumns();
        initializeRadioGroup();
    }

    private void initializeRadioGroup() {
        radioGroup = new ToggleGroup();
        weekHistoryRadioButton.setToggleGroup(radioGroup);
        monthHistoryRadioButton.setToggleGroup(radioGroup);
        customHistoryRadioButton.setToggleGroup(radioGroup);
    }

    private void initializeColumns() {
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currencyCode"));
        rateColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
    }

    private void initializeComboBoxes() {
        currencyList.getItems().addAll(handler.getCurrencyCodes());
        inputCurrencyComboBox.getItems().addAll(handler.getCurrencyCodes());
        outputCurrencyComboBox.getItems().addAll(handler.getCurrencyCodes());
        xaxisList.getItems().addAll(handler.getCurrencyCodes());
        yaxisList.getItems().addAll(handler.getCurrencyCodes());
    }

    public void setRadioButton(ActionEvent actionEvent) {
        if (customHistoryRadioButton.isArmed()) {
            startDatePicker.setDisable(false);
            endDatePicker.setDisable(false);
            endDatePicker.setValue(LocalDate.now());
            startDatePicker.setValue(LocalDate.now().minusWeeks(1));
        } else {
            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);
        }
    }

    public static class Currency {
        private final SimpleStringProperty currencyCode;
        private final SimpleDoubleProperty rate;


        public Currency(String currencyCode, Double rate) {
            this.currencyCode = new SimpleStringProperty(currencyCode);
            this.rate = new SimpleDoubleProperty(rate);
        }

        public String getCurrencyCode() {
            return currencyCode.get();
        }

        public SimpleStringProperty currencyCodeProperty() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode.set(currencyCode);
        }

        public double getRate() {
            return rate.get();
        }

        public SimpleDoubleProperty rateProperty() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate.set(rate);
        }
    }
}
