package sample.manager.ManagerPages.RoomInfoEdit;

import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static sample._BackEnd.DBConnection.closeConnections;
import static sample._BackEnd.DBConnection.getConnections;

@SuppressWarnings("unused")
public class RoomInfoEdit implements Initializable {
    public Button UserConfirm;

    public TextField roomNoField;
    public TextField roomTypeField;
    public TextField capacityField;
    public TextField priceDayField;
    @SuppressWarnings("rawtypes")
    public JFXComboBox statusCbox;
    private String[] roomStats = { "Available", "Unavailable" };

    public void closeBtn(ActionEvent event) {
        Stage stage = (Stage) UserConfirm.getScene().getWindow();
        stage.close();
    }

    public void saveBtn(ActionEvent event) {
        Connection connection = getConnections();
        try {
            if (!connection.isClosed()) {
                String sql = "UPDATE hms0.roominfo SET ROOMTYPE = ?, CAPACITY = ?, PRICEDAY = ?, STATUS = ? where ROOMNO = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, roomTypeField.getText());
                statement.setString(2, capacityField.getText());
                statement.setString(3, priceDayField.getText());
                statement.setString(4, statusCbox.getValue() + "");
                statement.setString(5, roomNoField.getText());
                statement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeConnections();
        }
        Stage stage = (Stage) UserConfirm.getScene().getWindow();
        stage.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusCbox.getItems().setAll(roomStats);
    }

    @SuppressWarnings("unchecked")
    public void setRoomInfo(String roomNo, String type, String capacity, String priceDay, String status) {
        roomNoField.setText(roomNo);
        roomNoField.setDisable(true);
        roomTypeField.setText(type);
        capacityField.setText(capacity);
        priceDayField.setText(priceDay);
        statusCbox.setValue(status);
    }
}
