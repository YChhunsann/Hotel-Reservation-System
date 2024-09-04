package sample.manager.ManagerPages;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample._BackEnd.CommonTask;
import sample._BackEnd.DBConnection;
import sample._BackEnd.TableView.ManagerCheckInDetailsTable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class ManagerCheckOut extends DBConnection implements Initializable {

    public TableView<ManagerCheckInDetailsTable> checkInInfoTable;
    public TableColumn<ManagerCheckInDetailsTable, String> siCol;
    public TableColumn<ManagerCheckInDetailsTable, String> nameCol;
    public TableColumn<ManagerCheckInDetailsTable, String> roomNoCol;
    public TableColumn<ManagerCheckInDetailsTable, String> priceDayCol;
    public TableColumn<ManagerCheckInDetailsTable, String> checkedInCol;
    public TableColumn<ManagerCheckInDetailsTable, String> addressCol;
    public Label nameField;
    public Label checkedInField;
    public Label priceDayField;
    public Label daysTotalField;
    public Label totalPriceField;
    public DatePicker checkOutDatepicker;
    public Label roomNoField;
    public Label siNoField;

    private ObservableList<ManagerCheckInDetailsTable> TABLEROW = FXCollections.observableArrayList();
    public int selectIndex = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        System.out.println("Initializing ManagerCheckOut...");
        TABLEROW.clear();
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        siCol.setCellValueFactory(new PropertyValueFactory<>("sino"));
        roomNoCol.setCellValueFactory(new PropertyValueFactory<>("roomno"));
        priceDayCol.setCellValueFactory(new PropertyValueFactory<>("priceday"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        checkedInCol.setCellValueFactory(new PropertyValueFactory<>("checkedin"));
        showCheckedInTable();
    }

    @FXML
    private void onCheckOutPick(ActionEvent event) throws ParseException {
        LocalDate myDate = checkOutDatepicker.getValue();
//        System.out.println("Check-out Date Selected: " + myDate);
        String checkInDate = checkedInField.getText();
//        System.out.println("Checked-In Date: " + checkInDate);
        if(!checkInDate.isEmpty()) {
            String checkOutDate = myDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            System.out.println("Formatted Check-out Date: " + checkOutDate);
            long days = (dayDifference(checkOutDate, checkInDate)) + 1;
            daysTotalField.setText(days + "");
//            System.out.println("Total Days Calculated: " + days);
            String priceDay = priceDayField.getText();
//            System.out.println("Price Per Day: " + priceDay);
            boolean isNumeric = priceDay.chars().allMatch(Character::isDigit);
            if(isNumeric) {
                long pricePerDay = Long.parseLong(priceDay);
                long totalPrice = pricePerDay * days;
                totalPriceField.setText(totalPrice + "");
//                System.out.println("Total Price Calculated: " + totalPrice);
            }
        } else {
            CommonTask.showAlert(Alert.AlertType.WARNING, "Warning", "Checked-In Date is empty!");
        }
    }

    private long dayDifference(String checkOut, String checkIn) throws ParseException {
        SimpleDateFormat obj = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = obj.parse(checkIn);
        Date date2 = obj.parse(checkOut);
        long time_difference = date2.getTime() - date1.getTime();
        long days_difference = (time_difference / (1000 * 60 * 60 * 24)) % 365;
//        System.out.println("Day Difference: " + days_difference);
        return days_difference;
    }

    private void showCheckedInTable() {
        TABLEROW.clear();
        Connection connection = getConnections();
        try {
            if (!connection.isClosed()) {
                String sql = "SELECT * FROM hms0.checkinoutinfo WHERE CHECKEDOUT IS NULL ORDER BY SI_NO DESC";
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String SI_NO = resultSet.getString("SI_NO");
                    String NAME = resultSet.getString("NAME");
                    String ROOMNO = resultSet.getString("ROOMNO");
                    String PRICEDAY = resultSet.getString("PRICEDAY");
                    String CHECKEDIN = resultSet.getString("CHECKEDIN");
                    String ADDRESS = resultSet.getString("ADDRESS");

                    ManagerCheckInDetailsTable checkInTable = new ManagerCheckInDetailsTable(SI_NO, NAME, ROOMNO, PRICEDAY, CHECKEDIN, ADDRESS);
                    TABLEROW.add(checkInTable);
//                    System.out.println("Added to Table: " + checkInTable);
                }
                checkInInfoTable.getItems().setAll(TABLEROW);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    public void onTableRowSelect(MouseEvent mouseEvent) {
        selectIndex = checkInInfoTable.getSelectionModel().getSelectedIndex();
//        System.out.println("Row Selected: Index " + selectIndex);
        if (selectIndex <= -1) {
            return;
        }
        // Populate the fields
        checkedInField.setText(checkedInCol.getCellData(selectIndex).toString());
        nameField.setText(nameCol.getCellData(selectIndex).toString());
        priceDayField.setText(priceDayCol.getCellData(selectIndex).toString());
        roomNoField.setText(roomNoCol.getCellData(selectIndex).toString());
        siNoField.setText(siCol.getCellData(selectIndex).toString());

        // Print the values for debugging
//        System.out.println("Selected Row Data:");
//        System.out.println("SI No: " + siCol.getCellData(selectIndex));
//        System.out.println("Name: " + nameCol.getCellData(selectIndex));
//        System.out.println("Room No: " + roomNoCol.getCellData(selectIndex));
//        System.out.println("Price Day: " + priceDayCol.getCellData(selectIndex));
//        System.out.println("Checked-In: " + checkedInCol.getCellData(selectIndex));
    }

    public void checkOutBtn(ActionEvent actionEvent) throws SQLException {
        Connection connection = DBConnection.getConnections();
        String checkOutDate = checkOutDatepicker.getValue() + "";
        String daysTotal = daysTotalField.getText();
        String totalPrice = totalPriceField.getText();
        String roomNo = roomNoField.getText();
        String siNo = siNoField.getText();

//        System.out.println("Check-out Button Pressed:");
//        System.out.println("Check-out Date: " + checkOutDate);
//        System.out.println("Days Total: " + daysTotal);
//        System.out.println("Total Price: " + totalPrice);
//        System.out.println("Room No: " + roomNo);
//        System.out.println("SI No: " + siNo);

        if (siNo.equals("") || checkOutDate.equals("null") || daysTotal.isEmpty() || totalPrice.isEmpty()) {
            CommonTask.showAlert(Alert.AlertType.WARNING, "Error", "Field can't be empty!");
        } else {
            String sql = "UPDATE hms0.checkinoutinfo SET CHECKEDOUT = ?, TOTALDAYS = ?, TOTALPRICE = ? WHERE SI_NO = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, checkOutDate);
            preparedStatement.setString(2, daysTotal);
            preparedStatement.setString(3, totalPrice);
            preparedStatement.setString(4, siNo);
            try {
                preparedStatement.execute();
                CommonTask.showAlert(Alert.AlertType.INFORMATION, "Successful", "Check-Out Successful!");
                String sql1 = "UPDATE hms0.roominfo SET STATUS = ? WHERE ROOMNO = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
                preparedStatement1.setString(1, "Available");
                preparedStatement1.setString(2, roomNo);
                preparedStatement1.execute();
                showCheckedInTable();
                showBillPopup();  // Display the bill popup here
            } catch (SQLException e) {
                CommonTask.showAlert(Alert.AlertType.ERROR, "Error", "Exception detected. Probably Sql!");
            } finally {
                DBConnection.closeConnections();
            }
        }
        clearTextFields();
    }

    private void showBillPopup() {
        Stage billStage = new Stage();
        billStage.setTitle("Bill Details");
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));

        Label billDetails = new Label("Bill Details");
        Label name = new Label("Name: " + nameField.getText());
        Label roomNo = new Label("Room No: " + roomNoField.getText());
        Label checkedInDate = new Label("Checked-In Date: " + checkedInField.getText());
        Label checkedOutDate = new Label("Checked-Out Date: " + checkOutDatepicker.getValue().toString());
        Label pricePerDay = new Label("Price per Day: " + priceDayField.getText());
        Label totalDays = new Label("Total Days: " + daysTotalField.getText());
        Label totalPrice = new Label("Total Price: " + totalPriceField.getText());

        Button saveBtn = new Button("Save as PDF");
        saveBtn.setOnAction(e -> {
            try {
                saveBillAsPDF(name.getText(), roomNo.getText(), checkedInDate.getText(),
                        checkedOutDate.getText(), pricePerDay.getText(),
                        totalDays.getText(), totalPrice.getText());
            } catch (FileNotFoundException | DocumentException ex) {
                ex.printStackTrace();
            }
        });

        vbox.getChildren().addAll(billDetails, name, roomNo, checkedInDate, checkedOutDate, pricePerDay, totalDays, totalPrice, saveBtn);
        Scene scene = new Scene(vbox, 400, 400);
        billStage.setScene(scene);
        billStage.show();
    }

    private void saveBillAsPDF(String name, String roomNo, String checkedInDate, String checkedOutDate,
                               String pricePerDay, String totalDays, String totalPrice)
            throws FileNotFoundException, DocumentException {

        if (name.isEmpty() || roomNo.isEmpty() || checkedInDate.isEmpty() ||
                checkedOutDate.isEmpty() || pricePerDay.isEmpty() || totalDays.isEmpty() || totalPrice.isEmpty()) {
            CommonTask.showAlert(Alert.AlertType.WARNING, "Error", "Some fields are missing. Ensure all fields are filled before saving.");
            return;
        }

        // Generate a unique file name with a timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(Calendar.getInstance().getTime());
        String fileName = "Bill_Details_" + timestamp + ".pdf";

        // Create a new document
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Add a header
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font subHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        Paragraph header = new Paragraph("Hotel Spring Mountain", headerFont);
        header.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(header);

        Paragraph address = new Paragraph("372 Street, PP, Cambodia\nContact: 071 953 3007", bodyFont);
        address.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(address);

        document.add(new Paragraph(" ")); // Add some space

        // Add the bill title
        Paragraph billTitle = new Paragraph("Bill Details", subHeaderFont);
        billTitle.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(billTitle);

        document.add(new Paragraph(" ")); // Add some space

        // Create a table for the bill details
        PdfPTable table = new PdfPTable(2); // 2 columns
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Table header
        PdfPCell cell = new PdfPCell(new Paragraph("Description", subHeaderFont));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Details", subHeaderFont));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);

        // Add table rows
        addTableRow(table, "Name", name, bodyFont);
        addTableRow(table, "Room No", roomNo, bodyFont);
        addTableRow(table, "Checked-In Date", checkedInDate, bodyFont);
        addTableRow(table, "Checked-Out Date", checkedOutDate, bodyFont);
        addTableRow(table, "Price per Day", pricePerDay, bodyFont);
        addTableRow(table, "Total Days", totalDays, bodyFont);
        addTableRow(table, "Total Price", totalPrice, bodyFont);

        // Add the table to the document
        document.add(table);

        // Close the document
        document.close();

        System.out.println("PDF Saved Successfully as " + fileName);
        CommonTask.showAlert(Alert.AlertType.INFORMATION, "Success", "PDF saved successfully!");
    }


    private void addTableRow(PdfPTable table, String description, String details, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(description, font));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(details, font));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        table.addCell(cell);
    }



    private void clearTextFields() {
        nameField.setText("");
        checkedInField.setText("");
        priceDayField.setText("");
        daysTotalField.setText("");
        totalPriceField.setText("");
        roomNoField.setText("");
        siNoField.setText("");
    }
}
